package business.processor.task;

import business.processor.bean.WarnRuleBean;
import business.processor.service.MonitorService;
import business.processor.service.WarnService;
import business.receiver.bean.MonitorBean;
import business.receiver.mapper.MyBaseMapper;
import business.util.CommonsUtil;
import business.util.SqlBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class OfflineTask {
    @Autowired
    private MonitorService monitorService;
    @Autowired
    private WarnService warnService;
    @Autowired
    private MyBaseMapper myBaseMapper;

    public OfflineTask() {
    }

    @Scheduled(
        cron = "0 * * * * ?"
    )
    @Async
    public void run() {
        Map<String, MonitorBean> monitors = this.monitorService.getAllMonitors();
        Date now = new Date();
        List<String> onlineList = new ArrayList();
        List<String> offlineList = new ArrayList();
        Iterator var5 = monitors.keySet().iterator();

        while(true) {
            while(var5.hasNext()) {
                String mn = (String)var5.next();
                MonitorBean monitor = monitors.get(mn);
                int monitorType = monitor.getMonitorType();
                int monitorStatus = monitor.getMonitorStatus();
                Date lastUploadTime = this.monitorService.getMnCurrentLastUpload(mn);
                if (lastUploadTime == null) {
                    log.debug("Redis提示[获取监控点" + mn + "最新数据上传时间]:未取到值(不统计离线)");
                } else {
                    int monitorOnlineTime = 30;
                    if (monitorType == 3 || monitorType == 5) {
                        monitorOnlineTime = 120;
                    }

                    int onlineStatus_now ;
                    if (now.getTime() - lastUploadTime.getTime() > (long)(monitorOnlineTime * 60 * 1000)) {
                        onlineStatus_now = 0;
                        offlineList.add(mn);
                        log.info("监控点离线" + mn + "当前时间" + CommonsUtil.dateCurrent() + ",最后缓存数据时间" + CommonsUtil.dateFormat(lastUploadTime));
                    } else {
                        onlineStatus_now = 1;
                        onlineList.add(mn);
                    }

                    monitor.setOnlineStatus(onlineStatus_now);
                    this.monitorService.setMonitor(mn, monitor);
                    String table = null;
                    if (monitorType == 1) {
                        table = "WATER_OFFLINE";
                    } else if (monitorType == 2) {
                        table = "AIR_OFFLINE";
                    } else if (monitorType == 9) {
                        table = "VOC_OFFLINE";
                    }

                    ArrayList params;
                    if (table != null) {
                        List<Map<String, Object>> offlineDataList = this.myBaseMapper.sqlQuery("SELECT * FROM " + table + " WHERE MN='"+mn+"' ORDER BY START_TIME DESC");
                        boolean isAdd = false;
                        if (offlineDataList != null && offlineDataList.size()>0) {
                            Map<String, Object> offlineData  = offlineDataList.get(0);
                            String id = (String)offlineData.get("ID");
                            if (onlineStatus_now == 0) {
                                Date endTime = (Date)offlineData.get("END_TIME");
                                if (endTime != null && now.getTime() - endTime.getTime() < (long)(monitorOnlineTime * 60 * 1000)) {
                                    params = new ArrayList();
                                    params.add(now);
                                    params.add(id);
                                    this.myBaseMapper.sqlExcute("UPDATE " + table + " SET END_TIME='"+now+"' WHERE ID='+"+id+"'");
                                } else {
                                    isAdd = true;
                                }
                            }
                        } else if (onlineStatus_now == 0) {
                            isAdd = true;
                        }

                        if (isAdd) {
                            params = new ArrayList();
                            params.add(CommonsUtil.createUUID1());
                            params.add(mn);
                            params.add(lastUploadTime);
                            params.add(now);
                            this.myBaseMapper.sqlExcute(SqlBuilder.buildSql("INSERT INTO " + table + " (ID,MN,START_TIME,END_TIME)VALUES(''{0}'',''{1}'',''{2}'',''{3}'')", params));
                        }
                    }

                    if (onlineStatus_now == 0 && monitorStatus == 1) {
                        params = null;
                        WarnRuleBean warnRule;
                        if (monitorType != 3 && monitorType != 5 && monitorType != 7) {
                            warnRule = this.warnService.getOfflineWarnRule(mn);
                        } else {
                            warnRule = this.warnService.getEnvirOfflineWarnRule();
                        }

                        String warnMessage = monitor.getMonitorName() + "于" + CommonsUtil.dateCurrent() + "离线";
                        //TODO 111
                        //this.warnService.checkWarnLog(lastUploadTime, warnRule, 1, warnMessage, monitor, (String)null);
                    }
                }
            }

            if (onlineList.size() > 0) {
                this.monitorService.setOnlineStatus(onlineList, 1);
            }

            if (offlineList.size() > 0) {
                this.monitorService.setOnlineStatus(offlineList, 0);
            }

            return;
        }
    }
}