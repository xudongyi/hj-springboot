package business.service;

import business.cache.DataCache;
import business.receiver.entity.BlackList;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

/**
 * 反控
 */
public class RevService {
    public static boolean isRefuse(ChannelHandlerContext ctx) {
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIP = insocket.getAddress().getHostAddress();
        if (DataCache.BLACK_LIST_CACHE.get(clientIP) != null && DataCache.WHITE_LIST_CACHE.get(clientIP) == null) {
            return true;
        } else {
            //根据IP获取对应的黑名单
            BlackList bl = DataCache.BLACK_LIST_TMP.get(clientIP);
            return true;
        }
    }

    public static void reply(ChannelHandlerContext ctx, String msg, int port) {

    }
}
