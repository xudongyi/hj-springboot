package business.processor.task;

import business.processor.bean.DataPacketBean;
import business.processor.excute.DataParserService;
import business.receiver.mapper.MyBaseMapper;
import business.receiver.mapper.SysDeviceMessageMapper;
import business.receiver.service.OnlineDataService;
import business.receiver.task.UpdateReceiverTableTask;
import business.receiver.threadPool.ThreadPoolService;
import business.util.CommonsUtil;
import business.util.SqlBuilder;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@Slf4j
@Transactional
public class DataParserHandTask {
    public static String bakSourceDataId = "";
    @Autowired
    private MyBaseMapper myBaseMapper;
    @Autowired
    private ThreadPoolService threadPoolService;
    @Autowired
    private DataParserService dataParserService;
    @Autowired
    private SysDeviceMessageMapper sysDeviceMessageMapper;
    @Autowired
    private OnlineDataService onlineDataService;
    @Value("${process.hand.open}")
    private boolean isOpen = false;

    public DataParserHandTask() {
    }

    @Scheduled(
        cron = "0/1 * * * * ?"
    )
    @Async
    public void run() {
        if (this.isOpen) {
            if (UpdateTableFieldTask.isInitial()) {
                if (this.threadPoolService.getHandProcessPool() == null) {
                    this.threadPoolService.setHandProcessPool();
                } else {
                    if (!this.threadPoolService.getHandProcessPool().isTerminated()) {
                        return;
                    }

                    this.threadPoolService.setHandProcessPool();
                }

                List<Object> params = new ArrayList();
                params.add(bakSourceDataId);
                List<Map<String, Object>> list = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(UpdateReceiverTableTask.getBakSourceSql_query_hand(), params));
                if (list != null) {
                    List<DataPacketBean> toExcute = new ArrayList();
                    Map<String, String> errorExcute = new HashMap();

                    for(int i = 0; i < list.size(); ++i) {
                        Map<String, Object> map = (Map)list.get(i);
                        String content = (String)map.get("content");
                        String id = (String)map.get("id");
                        bakSourceDataId = id;
                        boolean isValid = true;
                        if (!this.onlineDataService.checkData(content)) {
                            isValid = false;
                            log.error("数据报文格式错误[长度/CRC]：id " + id);
                        }

                        String mn = null;
                        String st = null;
                        String cn = null;

                        try {
                            int index = content.indexOf("MN=");
                            mn = content.substring(index + 3, content.indexOf(59, index));
                            int st_index = content.indexOf("ST=");
                            st = content.substring(st_index + 3, content.indexOf(";", st_index));
                            int cn_index = content.indexOf("CN=");
                            cn = content.substring(cn_index + 3, content.indexOf(";", cn_index));
                        } catch (Exception var17) {
                            isValid = false;
                            log.error("数据报文格式错误[MN/ST/CN错误]：id " + id);
                        }

                        if (isValid) {
                            DataPacketBean dataPacketBean = new DataPacketBean();
                            dataPacketBean.setSourceId(id);
                            dataPacketBean.setSt(st);
                            dataPacketBean.setCn(cn);
                            dataPacketBean.setMn(mn);
                            dataPacketBean.setContent(content);
                            toExcute.add(dataPacketBean);
                        } else {
                            errorExcute.put(id, id);
                        }
                    }

                    if (!toExcute.isEmpty()) {
                        log.info(CommonsUtil.dateCurrent() + "开始解析:" + ((Map)list.get(0)).get("ID") + "—" + ((Map)list.get(list.size() - 1)).get("ID") + "，共" + list.size() + "条记录");
                    }

                    Iterator var18 = toExcute.iterator();

                    while(var18.hasNext()) {
                        final DataPacketBean v = (DataPacketBean)var18.next();
                        this.threadPoolService.getHandProcessPool().execute(new Runnable() {
                            public void run() {
                                DataParserHandTask.this.dataParserService.distributeData(UpdateReceiverTableTask.getBakSourceSql_update_hand(), v, false);
                            }
                        });
                    }

                    var18 = errorExcute.keySet().iterator();

                    while(var18.hasNext()) {
                        final String id = (String)var18.next();
                        this.threadPoolService.getHandProcessPool().execute(new Runnable() {
                            public void run() {
                                DataParserHandTask.this.sysDeviceMessageMapper.updateTag("sys_device_message_" + DateUtil.format(new Date(), "yyMM"), id, 8);
                            }
                        });
                    }

                    this.threadPoolService.getHandProcessPool().shutdown();
                }

            }
        }
    }
}