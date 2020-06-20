package com.hj.server.business.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gjing
 * <p>
 * 服务启动监听器
 **/
@Slf4j
@Component
public class NettyServer {

    /**
     * 接收程序socket端口
     **/
    @Value("${hj.server.address}")
    public String socketAddress;

    /**
     * 接收程序socket端口
     **/
    @Value("${hj.server.port}")
    public int socketPort;

    //记录服务端已有的连接
    public static Map<String, ChannelHandlerContext> map = new HashMap<>();

    @Autowired
    ServerChannelInitializer serverChannelInitializer;

    /**
     * PostConstruct注解用于方法上，该方法在初始化的依赖注入操作之后被执行。
     * 这个方法必须在class被放到service之后被执行，这个注解所在的类必须支持依赖注入。
     */
    @PostConstruct
    public void initial() {
        Thread server = new Thread(new Runnable() {
            public void run() {
                //TODO 这边可能需要用到线程去分配报文
                SocketAddress address = new InetSocketAddress(socketAddress, socketPort);
                //new 一个主线程组
                EventLoopGroup bossGroup = new NioEventLoopGroup(1);
                //new 一个工作线程组
                EventLoopGroup workGroup = new NioEventLoopGroup(200);
                ServerBootstrap bootstrap = new ServerBootstrap()
                        .group(bossGroup, workGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(serverChannelInitializer)
                        .localAddress(address)
                        //设置队列大小
                        .option(ChannelOption.SO_BACKLOG, 1024)
                        // 两小时内没有数据的通信时,TCP会自动发送一个活动探测数据报文
                        .childOption(ChannelOption.SO_KEEPALIVE, true);
                try {
                    ChannelFuture future = bootstrap.bind(socketPort).sync();
                    log.info("服务器启动开始监听socket端口: {}", socketPort);
                    future.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    //关闭主线程组
                    bossGroup.shutdownGracefully();
                    //关闭工作线程组
                    workGroup.shutdownGracefully();
                }
            }
        });
        server.start();
    }

}