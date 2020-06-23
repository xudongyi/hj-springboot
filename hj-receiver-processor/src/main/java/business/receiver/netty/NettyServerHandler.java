package business.receiver.netty;

import business.config.MybatisPlusConfig;
import business.receiver.entity.SysDeviceMessage;
import business.receiver.entity.SysDeviceMessageEnum;
import business.receiver.mapper.CommonMapper;
import business.receiver.mapper.SysDeviceMessageMapper;
import cn.hutool.core.date.DateUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author xudy
 * <p>
 * netty服务端处理器
 **/
@Slf4j
@Service
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    @Autowired(required=false)
    private SysDeviceMessageMapper sysDeviceMessageMapper;

    @Autowired(required=false)
    private CommonMapper commonMapper;
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
        log.warn("channelRead");
        //TODO 接收到服务端消息后对数据进行处理。
        if (msg != null) {
            String content = (String) msg;
            int index = content.indexOf("MN=");
            if(index==-1){
                log.error("数据报文格式错误[MN错误]：" + content);
            }else{
                String mn = content.substring(index + 3, content.indexOf(59, index));
                if (mn.equals("")) {
                    log.error("数据报文格式错误[MN错误]：" + content);
                }else{
                    //插入数据到数据库
                    String thisMonth = DateUtil.format(new Date(),"yyMM");
                    String tableName = "sys_device_message_" + thisMonth;
                    if(commonMapper.checkTableExistsWithSchema(tableName)==0){
                        sysDeviceMessageMapper.createSysDeviceMessageTable(tableName);
                    }
                    SysDeviceMessage deviceMessage = new SysDeviceMessage();
                    deviceMessage.setContent(content);
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