package business.netty.server;

import business.cache.DataCache;
import business.entity.ReverseBean;
import business.service.RevService;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 用于接收反控指令所需要使用的服务端。
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {
    private NettyServer ns;

    public NettyServerHandler(NettyServer ns) {
        this.ns = ns;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final String msg) {
        if (StringUtils.isNotEmpty(msg) && !RevService.isRefuse(ctx)) {
            this.ns.read(msg, ctx);
            RevService.reply(ctx, msg, this.ns.getPort());
            DataCache.THREAD_POOL_CACHE.execute(() -> {
                int mn_index = msg.indexOf("MN=");
                if (mn_index != -1) {
                    try {
                        String mn = msg.substring(mn_index + 3, msg.indexOf(";", mn_index));
                        List<ReverseBean> list = DataCache.REVERSE_CMD_CACHE.get(mn);
                        if (list != null && list.size() > 0) {
                            for (ReverseBean v : list) {
                                ctx.writeAndFlush(Unpooled.copiedBuffer(v.getContent() + "\r\n", CharsetUtil.UTF_8));
                            }
                        }

                        synchronized (DataCache.REVERSE_CMD_CACHE) {
                            DataCache.REVERSE_CMD_CACHE.remove(mn);
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        e.printStackTrace();
                    }
                }

            });
        }
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("发生未知的异常", cause);
        ctx.close();
    }

    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
    }

}