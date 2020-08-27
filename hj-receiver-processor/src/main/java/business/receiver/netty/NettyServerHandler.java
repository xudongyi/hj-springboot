package business.receiver.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xudy
 * <p>
 * netty服务端处理器
 **/
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {


    private NettyServer server;

    public NettyServerHandler(NettyServer server){
        this.server = server;
    }
    /**
     * 客户端连接会触发
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx){
        log.info("Channel active......");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s){
        server.read(s,channelHandlerContext);
    }

    /**
     * 发生异常触发
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 客户端主动断开服务端的链接,关闭流
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx)  {
        log.info(ctx.channel().localAddress().toString() + " 通道不活跃！");
        removeChannnelMap(ctx);
        // 关闭流
        ctx.close();
    }

    /**
     * 删除map中ChannelHandlerContext
     */
    private void removeChannnelMap(ChannelHandlerContext ctx) {
        for (String key : NettyServer.map.keySet()) {
            if (NettyServer.map.get(key) != null && NettyServer.map.get(key).equals(ctx)) {
                NettyServer.map.remove(key);
            }
        }

    }


}