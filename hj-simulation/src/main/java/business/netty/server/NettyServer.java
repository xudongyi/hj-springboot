package business.netty.server;

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
                    protected void initChannel(SocketChannel arg0) throws Exception {
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
            } catch (Exception var6) {
                var6.printStackTrace();
            } finally {
                NettyServer.this.bossGroup.shutdownGracefully();
                NettyServer.this.workerGroup.shutdownGracefully();
            }
        });
        server.start();
    }

    public void read(String msg, ChannelHandlerContext ctx) {

    }

}
