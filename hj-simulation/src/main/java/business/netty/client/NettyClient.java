package business.netty.client;

import business.cache.DataCache;
import business.receiver.entity.ReverseBean;
import business.service.ReverseService;
import cn.hutool.core.date.DateUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.util.CharsetUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

@Slf4j
@Data
public class NettyClient {
    private String id;
    private String ip;
    private int port;

    private Thread nettyClientThread = null;
    private ChannelHandlerContext ctx = null;
    private Boolean isConnecting = false;
    private boolean active = false;
    private boolean connected = false;
    private boolean isIntervalSend = false;

    public NettyClient(String ip, int port, String id) {
        this.ip = ip;
        this.port = port;
        this.id = id;
        this.initial();
    }

    public void closed() {
        if (ctx != null && isActive()) {
            ctx.close();
            if (nettyClientThread != null) {
                nettyClientThread.stop();
            }
        }

        active = false;
        connected = false;
        isIntervalSend = false;
    }

    public boolean isActive() {
        if (ctx != null) {
            active = ctx.channel().isActive();
        }

        return active;
    }

    public boolean isConnected() {
        return this.connected;
    }

    /**
     * 初始化Netty客户端
     */
    public void initial() {
        synchronized (this.isConnecting) {
            if (!this.isConnecting) {
                this.isConnecting = true;
                this.nettyClientThread = new Thread() {
                    public void run() {
                        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

                        try {
                            Bootstrap b = new Bootstrap();
                            b.group(workerGroup);
                            b.channel(NioSocketChannel.class);
                            b.option(ChannelOption.SO_KEEPALIVE, true);
                            b.handler(new ChannelInitializer<SocketChannel>() {
                                public void initChannel(SocketChannel ch) throws Exception {
                                    ByteBuf delimiter = Unpooled.copiedBuffer("\r\n".getBytes());
                                    ChannelPipeline pl = ch.pipeline();
                                    pl.addLast(new ChannelHandler[]{new DelimiterBasedFrameDecoder(2048, delimiter)});
                                    pl.addLast(new ChannelHandler[]{new StringDecoder(Charset.forName("UTF-8"))});
                                    pl.addLast(new ChannelHandler[]{new StringEncoder(Charset.forName("UTF-8"))});
                                    pl.addLast(new ChannelHandler[]{new WriteTimeoutHandler(600)});
                                    pl.addLast(new ChannelHandler[]{new NettyClientHandler(NettyClient.this)});
                                }
                            });
                            ChannelFuture f = b.connect(NettyClient.this.ip, NettyClient.this.port).sync();
                            f.channel().closeFuture().sync();
                        } catch (Exception e) {
                            log.error(e.getMessage());
                        } finally {
                            workerGroup.shutdownGracefully();
                            NettyClient.this.isConnecting = false;
                        }

                    }
                };
                this.nettyClientThread.start();

                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException var4) {
                    var4.printStackTrace();
                }
            }

        }
    }

    /**
     * 发送报文到服务端
     *
     * @param msg
     * @return
     */
    public boolean send(String msg) {
        if (!isActive()) {
            initial();
        }
        if (ctx != null && isActive()) {
            connected = true;
            //发送报文
            ctx.writeAndFlush(Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
            if (this.id != null) {
                String date = DateUtil.formatDateTime(new Date());
                msg = "[" + date + "]发送数据:" + msg;
                log.warn(msg);
                final String broadcastMsg = this.id + "," + msg;
                DataCache.THREAD_POOL_CACHE.execute(() -> {
                            for (String sseId : DataCache.TRANSFER_CLIENT_SEND.keySet()) {
                                DataCache.TRANSFER_CLIENT_SEND.get(sseId).add(broadcastMsg);
                            }
                        }
                );
            }
            return true;
        } else {
            this.connected = false;
            this.isIntervalSend = false;
            return false;
        }
    }

    public void read(final String msg) {
        if (this.id != null) {
            if (this.id.equals("ANALOG-DATA")) {
                this.send(ReverseService.excute(msg));
            } else {
                String date = DateUtil.formatDateTime(new Date());
                String msg_ = "[" + date + "]接收数据:" + msg;
                String broadcastMsg = this.id + "," + msg_;
                log.warn(msg_);
                DataCache.THREAD_POOL_CACHE.execute(() -> {
                    Iterator var2 = DataCache.TRANSFER_CLIENT_REV.keySet().iterator();
                    while (var2.hasNext()) {
                        String sseId = (String) var2.next();
                        (DataCache.TRANSFER_CLIENT_REV.get(sseId)).add(broadcastMsg);
                    }

                });
            }
        } else {
            DataCache.THREAD_POOL_CACHE.execute(() -> {
                int mn_index = msg.indexOf("MN=");
                if (mn_index != -1) {
                    try {
                        String mn = msg.substring(mn_index + 3, msg.indexOf(";", mn_index));
                        ChannelHandlerContext ctx = DataCache.REVERSE_CTX_CACHE.get(mn);
                        if (ctx != null && ctx.channel().isActive()) {
                            ctx.writeAndFlush(Unpooled.copiedBuffer(msg + "\r\n", CharsetUtil.UTF_8));
                        } else {
                            ReverseBean v = new ReverseBean();
                            v.setContent(msg);
                            v.setCreateTime(new Date());
                            (DataCache.REVERSE_CMD_CACHE.computeIfAbsent(mn, (k) -> {
                                return new ArrayList();
                            })).add(v);
                        }
                    } catch (Exception var5) {
                        var5.printStackTrace();
                    }
                }

            });
        }

    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
}
