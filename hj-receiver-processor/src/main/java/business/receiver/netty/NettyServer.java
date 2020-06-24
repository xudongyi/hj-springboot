package business.receiver.netty;

import business.config.MybatisPlusConfig;
import business.processor.task.UpdateTableFieldTask;
import business.receiver.entity.SysDeviceMessage;
import business.receiver.entity.SysDeviceMessageEnum;
import business.receiver.mapper.CommonMapper;
import business.receiver.mapper.SysDeviceMessageMapper;
import business.receiver.service.GpsDataService;
import business.receiver.service.HwStoreDataService;
import business.receiver.service.OnlineDataService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
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
import java.util.Date;
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

    @Value("${receive.online}")
    private boolean receiveOnline = true;
    @Value("${receive.gps}")
    private boolean receiveGps = true;
    @Value("${receive.hwstore}")
    private boolean receiveHwstore = true;

    @Autowired
    private OnlineDataService onlineDataService;
    @Autowired
    private GpsDataService gpsDataService;
    @Autowired
    private HwStoreDataService hwStoreDataService;

    //记录服务端已有的连接
    public static Map<String, ChannelHandlerContext> map = new HashMap<>();

    @Autowired
    ServerChannelInitializer serverChannelInitializer;

    @Autowired(required=false)
    private SysDeviceMessageMapper sysDeviceMessageMapper;

    @Autowired(required=false)
    private CommonMapper commonMapper;

    /**
     * PostConstruct注解用于方法上，该方法在初始化的依赖注入操作之后被执行。
     * 这个方法必须在class被放到service之后被执行，这个注解所在的类必须支持依赖注入。
     */
    @PostConstruct
    public void initial() {
        Thread server = new Thread(() -> {
            //月初时如果更新表还没有结束，则一直等待初始化结束
            while(!UpdateTableFieldTask.isInitial()) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException var11) {
                    var11.printStackTrace();
                }
            }
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
        });
        server.start();
    }

    public void read(String msg, ChannelHandlerContext ctx) {

        if (msg != null) {
            int index = msg.indexOf("MN=");
            if(index==-1){
                log.error("数据报文格式错误[MN错误]：" + msg);
            }else{
                String mn = msg.substring(index + 3, msg.indexOf(59, index));
                if (mn.equals("")) {
                    log.error("数据报文格式错误[MN错误]：" + msg);
                }else{
                    //插入数据到数据库
                    String thisMonth = DateUtil.format(new Date(),"yyMM");
                    String tableName = "sys_device_message_" + thisMonth;
                    if(commonMapper.checkTableExistsWithSchema(tableName)==0){
                        sysDeviceMessageMapper.createSysDeviceMessageTable(tableName);
                    }
                    SysDeviceMessage deviceMessage = new SysDeviceMessage();
                    deviceMessage.setContent(msg);
                    deviceMessage.setFlag(SysDeviceMessageEnum.IS_RECIEVE.code());
                    deviceMessage.setMn(mn);
                    //动态表名设置
                    MybatisPlusConfig.tableName.set("sys_device_message_"+DateUtil.format(new Date(),"yyMM"));
                    int result = sysDeviceMessageMapper.insert(deviceMessage);
                    if(result==0){
                        log.error("报文插入数据库失败！");
                    }
                }
            }
        }
        try {
            if (StringUtils.isNotEmpty(msg)) {
                if (msg.startsWith("$")) {
                    if (this.receiveGps) {
                        this.gpsDataService.accept(msg);
                    } else {
                        log.error("GPS数据接收器未开启[receive.gps]，报文丢弃：" + msg);
                    }
                } else if (msg.startsWith("#HWSTORE#")) {
                    if (this.receiveHwstore) {
                        this.hwStoreDataService.accept(msg, ctx);
                    } else {
                        log.error("危废数据接收器未开启[receive.hwstore]，报文丢弃：" + msg);
                    }
                } else if (this.receiveOnline) {
                    this.onlineDataService.accept(msg, ctx);
                } else {
                    log.error("在线监测数据接收器未开启[receive.online]，报文丢弃：" + msg);
                }
            }
        } catch (Exception var4) {
            log.error("报文接收错误[" + var4.getMessage() + "],报文：" + msg, var4);
        }

    }
}