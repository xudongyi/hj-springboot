package business.receiver.service;

import business.processor.bean.DataPacketBean;
import business.receiver.threadPool.ThreadPoolService;
import business.redis.RedisService;
import business.sms.SmsService;
import business.util.CRC_16;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("onlineDataService")
@Slf4j
public class OnlineDataService {
    @Autowired
    private IBaseDao baseDao;
    @Autowired
    private ReverseService reverseService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private BlackListService blackListService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private ThreadPoolService threadPoolService;
    @Autowired
    private DataParserAuto dataParserAuto;
    @Value("${receive.online.checkCRC}")
    private boolean checkCRC = true;
    public static int VALVE_STATUS_CLOSED = 0;
    public static int VALVE_STATUS_OPEN = 1;
    public static Map<String, Date> VALVE_STATE_CHANGE_TIME_CACHE = new HashMap();

    public OnlineDataService() {
    }

    public void accept(final String content, final ChannelHandlerContext ctx) {
        this.threadPoolService.getReceivePool().execute(() -> {
            int index = content.indexOf("MN=");
            if (index == -1) {
                log.error("数据报文格式错误[MN错误]：" + content);
            } else {
                String mn = "";

                try {
                    mn = content.substring(index + 3, content.indexOf(59, index));
                } catch (Exception var12) {
                }

                if (mn.equals("")) {
                    log.error("数据报文格式错误[MN错误]：" + content);
                } else {
                    String st = "";
                    int st_index = content.indexOf("ST=");
                    if (st_index != -1) {
                        try {
                            st = content.substring(st_index + 3, content.indexOf(";", st_index));
                        } catch (Exception var11) {
                        }
                    }

                    String cn = "";
                    int cn_index = content.indexOf("CN=");
                    if (cn_index != -1) {
                        try {
                            cn = content.substring(cn_index + 3, content.indexOf(";", cn_index));
                        } catch (Exception var10) {
                        }
                    }

                    if (!content.startsWith("##BeatHeart;") && (st.equals("") || cn.equals(""))) {
                        log.error("数据报文格式错误[ST/CN错误]:" + content);
                    } else if (OnlineDataService.this.blackListService.isReceive(mn)) {
                        OnlineDataService.this.reverseService.setChennel(mn, ctx);
                        OnlineDataService.this.reverseService.sendLeaveCmdAfterSocketAccept(mn, ctx);
                        if (cn.equals("9011")) {
                            OnlineDataService.this.receive9011(mn, content);
                        } else if (cn.equals("9012")) {
                            OnlineDataService.this.receive9012(mn, content);
                        } else if (cn.equals("3015")) {
                            OnlineDataService.this.receive3015(mn, content);
                        } else if (!cn.equals("3715")) {
                            if (cn.equals("8803")) {
                                OnlineDataService.this.receive8803(mn, content);
                            } else if (cn.equals("8804")) {
                                OnlineDataService.this.receive8804(mn, content);
                            }
                        }

                        if (content.indexOf("##BeatHeart;") == -1 && !cn.equals("9011") && !cn.equals("9012") && !cn.equals("9013") && !cn.equals("9014")) {
                            long sourceId = OnlineDataService.this.receiveData(mn, content);
                            if (OnlineDataService.this.checkData(content)) {
                                DataPacketBean dataPacketBean = new DataPacketBean();
                                dataPacketBean.setSourceId(sourceId);
                                dataPacketBean.setSt(st);
                                dataPacketBean.setCn(cn);
                                dataPacketBean.setMn(mn);
                                dataPacketBean.setContent(content);
                                OnlineDataService.this.dataParserAuto.autoDistributeData(dataPacketBean);
                            }
                        }

                    }
                }
            }
        });
    }

    public boolean checkData(String content) {
        if (!this.checkCRC) {
            return true;
        } else if (content.length() < 10) {
            log.error("数据报文格式错误[长度不符]：" + content);
            return false;
        } else {
            boolean var2 = true;

            try {
                int length = Integer.parseInt(content.substring(2, 6));
                if (content.length() != length + 4 + 6) {
                    log.error("数据报文格式错误[长度不符]：" + content);
                    return false;
                }
            } catch (Exception var5) {
                log.error("数据报文格式错误[长度不符]：" + content);
                return false;
            }

            String toCheckStr = content.substring(6, content.length() - 4);
            String crc16 = CRC_16.CRC16(toCheckStr);
            String crc16_orignal = content.substring(content.length() - 4);
            if (crc16.equals(crc16_orignal.toUpperCase())) {
                return true;
            } else {
                log.error("数据报文格式错误[CRC校验失败]：" + content);
                return false;
            }
        }
    }

    private long receiveData(String key, String content) {
        List<Object> params = new ArrayList();
        params.add(1);
        params.add(new Date());
        params.add(content);
        params.add(key);
        return this.baseDao.sqlInsertAutoIncrementID(UpdateReceiverTableTask.getBakSourceSql_insert_auto(), params);
    }

    private void receive9011(String key, String content) {
        log.info("收到设备" + key + "的反控指令接收确认回执:" + content);
        int index = content.indexOf("QN=");
        String qn = content.substring(index + 3, index + 20);
        this.reverseService.updateReverselog(qn, key, 2, "");
    }

    private void receive9012(String key, String content) {
        log.info("收到设备" + key + "的反控指令接收执行结果回执:" + content);
        int index = content.indexOf("QN=");
        String qn = content.substring(index + 3, index + 20);
        index = content.indexOf("ExeRtn=");
        if (index != -1) {
            content = content.substring(index);
            String result = content.substring(7, content.indexOf("&&"));
            if (result.equals("1")) {
                this.reverseService.updateReverselog(qn, key, 3, "");
            } else {
                this.reverseService.updateReverselog(qn, key, 4, "");
            }
        }

    }

    private void receive3015(String key, String content) {
        int index = content.indexOf("QN=");
        String qn = content.substring(index + 3, index + 20);
        index = content.indexOf("VaseNo=");
        if (index != -1) {
            content = content.substring(index);
            String result = content.substring(7, content.indexOf("&&"));
            this.reverseService.updateReverselog(qn, key, 2, "当前瓶号:" + result);
        }

    }

    private void receive8803(String key, String content) {
        int index = content.indexOf("QN=");
        String qn = content.substring(index + 3, index + 20);
        index = content.indexOf("ValveStatus=");
        if (index != -1) {
            content = content.substring(index);
            String result = content.substring(12, content.indexOf("&&"));
            if (result.equals("1")) {
                this.reverseService.updateReverselog(qn, key, 3, "当前阀门状态：已关闭");
                this.updateMonitorValveStatus(key, VALVE_STATUS_CLOSED);
            } else if (result.equals("2")) {
                this.reverseService.updateReverselog(qn, key, 3, "当前阀门状态：已打开");
                this.updateMonitorValveStatus(key, VALVE_STATUS_OPEN);
            }
        }

    }

    private void receive8804(String key, String content) {
        int index = content.indexOf("QN=");
        String qn = content.substring(index + 3, index + 20);
        index = content.indexOf("Bottle=");
        if (index != -1) {
            content = content.substring(index);
            String result = content.substring(7, content.indexOf("&&"));
            this.reverseService.updateReverselog(qn, key, 3, "当前瓶号：" + result);
        }

    }

    private void updateMonitorValveStatus(String mn, int valveStatus) {
        Date now = new Date();
        String redisMonitor = this.redisService.getMapValue("mn_monitor_map", mn);
        if (redisMonitor != null) {
            MonitorBean monitor = (MonitorBean) CommonsUtil.toJsonObject(redisMonitor, MonitorBean.class);
            if (monitor != null) {
                if (monitor.getValveStatus() != valveStatus) {
                    monitor.setValveStatus(valveStatus);
                    this.redisService.setMapValue("mn_monitor_map", mn, monitor);
                    List<Object> params = new ArrayList();
                    params.add(valveStatus);
                    params.add(mn);
                    this.baseDao.sqlExcute("update mon_monitor set valve_status=? where mn=? ", params);
                    if (valveStatus == VALVE_STATUS_OPEN && VALVE_STATE_CHANGE_TIME_CACHE.containsKey(mn)) {
                        Date lastChangeTime = (Date) VALVE_STATE_CHANGE_TIME_CACHE.get(mn);
                        if (now.getTime() - lastChangeTime.getTime() > 180000L) {
                            this.sendValveMessage(monitor, now);
                        }
                    }

                    VALVE_STATE_CHANGE_TIME_CACHE.put(mn, now);
                } else if (!VALVE_STATE_CHANGE_TIME_CACHE.containsKey(mn)) {
                    VALVE_STATE_CHANGE_TIME_CACHE.put(mn, now);
                }
            }
        } else {
            log.debug("Redis提示[获取监控点" + mn + "]:未取到值");
        }

    }

    private void sendValveMessage(MonitorBean monitor, Date date) {
        String contact = null;
        String result = this.redisService.getMapValue("warn_contact", "4");
        if (StringUtils.isNotEmpty(result)) {
            Map<String, Object> map = (Map) CommonsUtil.toJsonObject(result, (Class) null);
            if (map != null) {
                contact = (String) map.get(monitor.getCompanyId());
            }
        } else {
            log.debug("Redis提示[获取余额报警联系人]:未取到值");
        }

        if (contact != null) {
            String message = monitor.getMonitorName() + "," + CommonsUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss") + ",阀门已打开";
            this.smsService.sendMessage(contact, message);
        }

    }
}