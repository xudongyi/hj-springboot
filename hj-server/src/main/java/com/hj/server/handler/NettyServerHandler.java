package com.hj.server.handler;

import com.hj.server.server.NettyServer;
import com.xy.format.hbt212.core.T212Mapper;
import com.xy.format.hbt212.model.Data;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gjing
 * <p>
 * netty服务端处理器
 **/
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 客户端连接会触发
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel active......");
    }

    /**
     * 客户端发消息会触发
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("服务器收到消息: {}", msg.toString());
        //TODO 接收到服务端消息后对数据进行处理。
        if (msg != null) {
            T212Mapper mapper = new T212Mapper()
                    .enableDefaultParserFeatures()
                    .enableDefaultVerifyFeatures();
            Data data = mapper.readData(msg.toString());
            String mn = data.getMn();
            if (NettyServer.map.get(mn) != null && NettyServer.map.get(mn).equals(ctx)) {
                log.info("mn: {}", mn);
            } else {
                NettyServer.map.put(mn, ctx);
            }
            Map<String,String> cp = new HashMap<>();
            if(data.getQn()!=null){
                cp.put("QN",data.getQn());
            }
            cp.put("ST",data.getSt());
            cp.put("CN","9012");
            cp.put("PW",data.getPw());
            cp.put("MN",mn);
            cp.put("Flag","4");
            cp.put("CP","&&");
            cp.put("ExeRtn","1");
            String returnStr = mapper.writeMapAsString(cp);
            ctx.write(returnStr);
            ctx.flush();
        }
    }

    /**
     * 发生异常触发
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 客户端主动断开服务端的链接,关闭流
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().localAddress().toString() + " 通道不活跃！");
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