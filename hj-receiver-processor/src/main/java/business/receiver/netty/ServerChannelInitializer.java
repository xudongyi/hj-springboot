package business.receiver.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Gjing
 *
 * netty服务初始化器
 **/
@Component
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Autowired
    NettyServerHandler nettyServerHandler;
    @Override
    protected void initChannel(SocketChannel socketChannel){
        //添加编解码
        ByteBuf delimiter = Unpooled.copiedBuffer("\r\n".getBytes());
        //socketChannel.pipeline().addLast(new ChannelHandler[]{new DelimiterBasedFrameDecoder(2048, delimiter)});
        socketChannel.pipeline().addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
        socketChannel.pipeline().addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
        socketChannel.pipeline().addLast(nettyServerHandler);
    }
}