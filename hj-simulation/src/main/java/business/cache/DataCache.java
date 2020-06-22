package business.cache;

import business.entity.*;
import business.netty.client.NettyClient;
import business.netty.server.NettyServer;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 数据缓存，用于发送报文
 */
@Component
@Slf4j
public class DataCache {
    public static Map<String, Client> CLIENT_CACHE = new LinkedHashMap();
    public static Map<Integer, Server> SERVER_CACHE = new LinkedHashMap();
    public static Map<Integer, List<ServerReply>> SERVER_REPLY_CACHE = new ConcurrentHashMap();
    public static Map<Integer, List<ServerConvert>> SERVER_CONVERT_CACHE = new ConcurrentHashMap();
    public static Map<String, NettyClient> CLIENT_SOCKET_CACHE = new LinkedHashMap();
    public static Map<Integer, NettyServer> SERVER_SOCKET_CACHE = new LinkedHashMap();
    public static Map<String, List<String>> TRANSFER_CLIENT_SEND = new ConcurrentHashMap();
    public static Map<String, List<String>> TRANSFER_CLIENT_REV = new ConcurrentHashMap();
    public static Map<String, List<String>> TRANSFER_SERVER_REV = new ConcurrentHashMap();
    private static Map<String, NettyClient> FORWARD_CLIENT = new ConcurrentHashMap();
    public static Map<String, Long> WHITE_LIST_CACHE = new LinkedHashMap();
    public static Map<String, Integer> BLACK_LIST_CACHE = new ConcurrentHashMap();
    public static Map<String, BlackList> BLACK_LIST_TMP = new ConcurrentHashMap();
    public static List<CmdTemplate> CMD_TEMPLATE_CACHE = new ArrayList();
    public static AnalogData ANALOG_DATA_CACHE = new AnalogData();
    private static Map<String, NettyClient> ANALOG_DATA_CLIENT = new ConcurrentHashMap();
    public static ExecutorService THREAD_POOL_CACHE = Executors.newFixedThreadPool(10);
    public static Map<String, ChannelHandlerContext> REVERSE_CTX_CACHE = new ConcurrentHashMap();
    public static Map<String, List<ReverseBean>> REVERSE_CMD_CACHE = new ConcurrentHashMap();

    /**
     * 初始化缓存
     */
    @PostConstruct
    public void initial() {
        //TODO 需要从配置文件中读取对应的数据或者从数据库读取
        log.warn("初始化模拟程序数据！");
    }

    /**
     * 根据ip端口获取对应的Netty客户端实例
     * @param ipPort
     * @return
     */
    public static NettyClient getAnalogDataClient(String ipPort) {
        if (ANALOG_DATA_CLIENT.containsKey(ipPort)) {
            return ANALOG_DATA_CLIENT.get(ipPort);
        } else {
            if (StringUtils.isNotEmpty(ipPort)) {
                String[] ip_port = ipPort.split(":");
                if (ip_port.length == 2) {
                    NettyClient nc = null;
                    try {
                        nc = new NettyClient(ip_port[0], Integer.valueOf(ip_port[1]), "ANALOG-DATA");
                        ANALOG_DATA_CLIENT.put(ipPort, nc);
                        return nc;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }
    }

    /**
     * 清除全部客户端
     */
    public static void clearAnalogDataClient() {
        if (!ANALOG_DATA_CLIENT.isEmpty()) {
            Iterator var1 = ANALOG_DATA_CLIENT.keySet().iterator();

            while(var1.hasNext()) {
                String key = (String)var1.next();
                ANALOG_DATA_CLIENT.get(key).closed();
            }
        }

        ANALOG_DATA_CLIENT.clear();
    }
}
