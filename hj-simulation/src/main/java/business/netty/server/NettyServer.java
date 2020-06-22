package business.netty.server;

import business.cache.DataCache;
import business.entity.Server;
import business.netty.client.NettyClient;
import business.service.ConvertMessageService;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.Data;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Data
public class NettyServer {
    private int port = 6000;
    private boolean isOpen = false;
    private Thread server = null;
    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workerGroup = null;

    public NettyServer(int port) {
        this.port = port;
        this.initial();
    }

    public void initial() {
        isOpen = true;
        server = new Thread(() -> {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(NettyServer.this.bossGroup, NettyServer.this.workerGroup);
                b.channel(NioServerSocketChannel.class);
                b.option(ChannelOption.SO_BACKLOG, 1024);
                b.childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel arg0) {
                        ByteBuf delimiter = Unpooled.copiedBuffer("\r\n".getBytes());
                        ChannelPipeline pl = arg0.pipeline();
                        pl.addLast(new ChannelHandler[]{new DelimiterBasedFrameDecoder(2048, delimiter)});
                        pl.addLast(new ChannelHandler[]{new StringDecoder(Charset.forName("UTF-8"))});
                        pl.addLast(new ChannelHandler[]{new StringEncoder(Charset.forName("UTF-8"))});
                        pl.addLast(new ChannelHandler[]{new ReadTimeoutHandler(3600)});
                        pl.addLast(new ChannelHandler[]{new NettyServerHandler(NettyServer.this)});
                    }
                });
                ChannelFuture f = b.bind(NettyServer.this.port).sync();
                f.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                NettyServer.this.bossGroup.shutdownGracefully();
                NettyServer.this.workerGroup.shutdownGracefully();
            }
        });
        server.start();
    }

    public void closed() {
        this.isOpen = false;
        if (this.bossGroup != null) {
            this.bossGroup.shutdownGracefully();
        }

        if (this.workerGroup != null) {
            this.workerGroup.shutdownGracefully();
        }

        if (this.server != null) {
            this.server.stop();
        }

    }

    public void read(String msg, ChannelHandlerContext ctx) {
        boolean isforward = false;
        Server server = DataCache.SERVER_CACHE.get(this.port);
        if (server != null) {
            String forward = DataCache.SERVER_CACHE.get(this.port).getForward();
            if (StringUtils.isNotEmpty(forward)) {
                int mn_index = msg.indexOf("MN=");
                if (mn_index != -1) {
                    try {
                        String mn = msg.substring(mn_index + 3, msg.indexOf(";", mn_index));
                        DataCache.REVERSE_CTX_CACHE.put(mn, ctx);
                    } catch (Exception var13) {
                        var13.printStackTrace();
                    }
                }

                msg = ConvertMessageService.convert(this.port, msg);
                isforward = true;
                String[] ipPorts = forward.split(",");
                for (String ipPort : ipPorts) {
                    NettyClient nc = DataCache.getForwardClient(ipPort);
                    if (nc != null) {
                        nc.send(msg + "\r\n");
                    }
                }
            }
        }

        SimpleDateFormat sf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        if (!isforward) {
            msg = "[" + sf.format(new Date()) + "]接收数据:" + msg;
        } else {
            msg = "[" + sf.format(new Date()) + "]接收数据(已转发):" + msg;
        }

        final String broadcastMsg = this.port + "," + msg;
        DataCache.THREAD_POOL_CACHE.execute(() -> {
            Iterator var2 = DataCache.TRANSFER_SERVER_REV.keySet().iterator();

            while (var2.hasNext()) {
                String sseId = (String) var2.next();
                 DataCache.TRANSFER_SERVER_REV.get(sseId).add(broadcastMsg);
            }

        });
    }
}
