package business.processor.service;

import business.processor.mapper.MonitorMapper;
import business.receiver.bean.MonitorBean;
import business.redis.RedisService;
import business.util.CommonsUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service("monitorService")
@Slf4j
public class MonitorService{
    @Autowired
    private RedisService redisService;
    @Autowired
    private MonitorMapper monitorMapper;

    public MonitorService() {
    }


    @PostConstruct
    public void initMonitor(){
        List<Map<String,Object>> monitors = monitorMapper.getAllMonitor();
        for(Map<String,Object> map : monitors){
            MonitorBean monitorBean = new MonitorBean();
            String mn = map.get("mn").toString();
            monitorBean.setMn(mn);
            setMonitor(mn,monitorBean);
        }
    }

    public Map<String, MonitorBean> getAllMonitors() {
        Map<String, String> map = this.redisService.getMapAll("mn_monitor_map");
        Map<String, MonitorBean> result = new HashMap();
        if (map != null) {
            Iterator var3 = map.keySet().iterator();

            while(var3.hasNext()) {
                String mn = (String)var3.next();
                MonitorBean monitor = (MonitorBean) CommonsUtil.toJsonObject(map.get(mn), MonitorBean.class);
                result.put(mn, monitor);
            }
        } else {
            log.debug("Redis提示[获取所有监控点]:未取到值");
        }

        return result;
    }

    public Date getMnCurrentLastUpload(String mn) {
        Date result = null;
        String time = this.redisService.getMapValue("mn_current_last_upload", mn);
        if (StringUtils.isNotEmpty(time)) {
            result = CommonsUtil.dateParse(time, "yyyy-MM-dd HH:mm:ss.SSS");
        } else {
            log.debug("Redis提示[获取MN" + mn + "最后数据（实时）上传时间]:未取到值");
        }

        return result;
    }

    public void setMnCurrentLastUpload(String mn) {
        String time = CommonsUtil.dateFormat(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
        this.redisService.setMapValue("mn_current_last_upload", mn, time);
    }

    public Date getMnHourLastUpload(String mn) {
        Date result = null;
        String time = this.redisService.getMapValue("mn_hour_last_upload", mn);
        if (StringUtils.isNotEmpty(time)) {
            result = CommonsUtil.dateParse(time, "yyyy-MM-dd HH:mm:ss.SSS");
        } else {
            log.debug("Redis提示[获取MN最后数据（小时）上传时间]:未取到值");
        }

        return result;
    }

    public void setMnHourLastUpload(String mn) {
        String time = CommonsUtil.dateFormat(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
        this.redisService.setMapValue("mn_hour_last_upload", mn, time);
    }

    public Date getMnDayLastUpload(String mn) {
        Date result = null;
        String time = this.redisService.getMapValue("mn_day_last_upload", mn);
        if (StringUtils.isNotEmpty(time)) {
            result = CommonsUtil.dateParse(time, "yyyy-MM-dd HH:mm:ss.SSS");
        } else {
            log.debug("Redis提示[获取MN最后数据（日）上传时间]:未取到值");
        }

        return result;
    }

    public void setMnDayLastUpload(String mn) {
        String time = CommonsUtil.dateFormat(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
        this.redisService.setMapValue("mn_day_last_upload", mn, time);
    }

    public void setMonitor(String mn, MonitorBean monitor) {
        this.redisService.setMapValue("mn_monitor_map", mn, monitor);
    }

    public void setOnlineStatus(List<String> mnList, int onlineStatus) {
        List<Object[]> params = new ArrayList();

        for(int i = 0; i < mnList.size(); ++i) {
            params.add(new Object[]{onlineStatus, mnList.get(i)});
            this.monitorMapper.updateMonitorStatus("online_status",mnList.get(i),onlineStatus);
        }
    }

    public void setDataStatus(String mn, int dataStatus) {
        MonitorBean monitor = this.getAllMonitors().get(mn);
        if (monitor != null && dataStatus != monitor.getDataStatus()) {
            List<Object> params = new ArrayList();
            params.add(dataStatus);
            params.add(mn);
            this.monitorMapper.updateMonitorStatus("data_status",mn,dataStatus);
            monitor.setDataStatus(dataStatus);
            this.redisService.setMapValue("mn_monitor_map", mn, monitor);
        }

    }

    public void setDeviceStatus(String mn, int deviceStatus) {
        MonitorBean monitor = this.getAllMonitors().get(mn);
        if (monitor != null && deviceStatus != monitor.getDeviceStatus()) {
            List<Object> params = new ArrayList();
            params.add(deviceStatus);
            params.add(mn);
            this.monitorMapper.updateMonitorStatus("device_status",mn,deviceStatus);
            monitor.setDeviceStatus(deviceStatus);
            this.redisService.setMapValue("mn_monitor_map", mn, monitor);
        }

    }
}