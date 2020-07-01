package business.processor.service;

import business.processor.bean.DataFactorBean;
import business.processor.bean.MonitorDeviceBean;
import business.processor.bean.WarnRuleBean;
import business.processor.mapper.MonitorMapper;
import business.receiver.bean.MonitorBean;
import business.receiver.mapper.MyBaseMapper;
import business.redis.RedisService;
import business.util.CommonsUtil;
import business.util.SqlBuilder;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("monitorDeviceService")
@Slf4j
public class MonitorDeviceService {
    @Autowired
    private RedisService redisService;
    @Autowired
    private WarnService warnService;
    @Autowired
    private MyBaseMapper myBaseMapper;

    public MonitorDeviceService() {
    }

    public MonitorDeviceBean getDevice(String monitorId, String factorCode) {
        MonitorDeviceBean device = null;
        if (monitorId != null) {
            String result = this.redisService.getMapValue("monitor_device_map", monitorId);
            if (StringUtils.isNotEmpty(result)) {
                List<MonitorDeviceBean> list = (List) CommonsUtil.toJsonObject(result, MonitorDeviceBean.class);
                if (list != null) {
                    for (int i = 0; i < list.size(); ++i) {
                        if (factorCode != null && factorCode.equals((list.get(i)).getFactorCode().toUpperCase())) {
                            device = list.get(i);
                            break;
                        }
                    }
                }
            } else {
                this.log.debug("Redis提示[获取监控点" + monitorId + "设备信息" + factorCode + "]:未取到值");
            }
        }

        return device;
    }

    public DataFactorBean getCurrentData(String mn, String factorCode) {
        DataFactorBean v = null;
        if (StringUtils.isNotEmpty(mn) && StringUtils.isNotEmpty(factorCode)) {
            String result = this.redisService.getMapValue("device_current_data_map", mn + "-" + factorCode);
            if (StringUtils.isNotEmpty(result)) {
                v = (DataFactorBean) CommonsUtil.toJsonObject(result, DataFactorBean.class);
            } else {
                this.log.debug("Redis提示[获取监控点" + mn + "最新实时数据" + factorCode + "]:未取到值");
            }
        }

        return v;
    }

    public void setCurrentData(String mn, String factorCode, DataFactorBean data) {
        if (data != null && StringUtils.isNotEmpty(mn) && StringUtils.isNotEmpty(factorCode)) {
            this.redisService.setMapValue("device_current_data_map", mn + "-" + factorCode, CommonsUtil.toJsonStr(data));
        }

    }

    public DataFactorBean getMHDData(String mn, String factorCode, int cn) {
        DataFactorBean v = null;
        if (StringUtils.isNotEmpty(mn) && StringUtils.isNotEmpty(factorCode)) {
            String result = this.redisService.getMapValue("device_data_map", mn + "-" + factorCode + "-" + cn);
            if (StringUtils.isNotEmpty(result)) {
                v = (DataFactorBean) CommonsUtil.toJsonObject(result, DataFactorBean.class);
            } else {
                this.log.debug("Redis提示[获取监控点" + mn + "最新" + cn + "数据" + factorCode + "]:未取到值");
            }
        }

        return v;
    }

    public void setMHDData(String mn, String factorCode, String cn, DataFactorBean data) {
        if (data != null && StringUtils.isNotEmpty(mn) && StringUtils.isNotEmpty(factorCode)) {
            this.redisService.setMapValue("device_data_map", mn + "-" + factorCode + "-" + cn, CommonsUtil.toJsonStr(data));
        }

    }

    public void setDeviceStatus(String mn, String factorCode, String deviceStatus) {
        if (StringUtils.isNotEmpty(mn) && StringUtils.isNotEmpty(factorCode) && StringUtils.isNotEmpty(deviceStatus)) {
            this.redisService.setMapValue("device_status_map", mn + "-" + factorCode, deviceStatus);
            String monitorRedis = this.redisService.getMapValue("mn_monitor_map", mn);
            if (StringUtils.isNotEmpty(monitorRedis)) {
                MonitorBean monitor = (MonitorBean) CommonsUtil.toJsonObject(monitorRedis, MonitorBean.class);
                String monitorId = monitor.getMonitorId();
                String result = this.redisService.getMapValue("monitor_device_map", monitorId);
                if (StringUtils.isNotEmpty(result)) {
                    List<MonitorDeviceBean> list = (List) CommonsUtil.toJsonObject(result, MonitorDeviceBean.class);
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); ++i) {
                            MonitorDeviceBean device = (MonitorDeviceBean) list.get(i);
                            if (factorCode != null && factorCode.equals(device.getFactorCode().toUpperCase())) {
                                device.setDeviceStatus(Integer.valueOf(deviceStatus));
                                List<Object> params = new ArrayList();
                                params.add(Integer.valueOf(deviceStatus));
                                params.add(device.getDeviceId());
                                this.myBaseMapper.sqlExcute(SqlBuilder.buildSql("update mon_device set device_status={0} where device_id={1}", params));
                                break;
                            }
                        }

                        this.redisService.setMapValue("monitor_device_map", monitorId, list);
                    }
                } else {
                    this.log.debug("Redis提示[获取监控点" + mn + "下设备信息]:未取到值");
                }
            } else {
                this.log.debug("Redis提示[获取监控点基础信息" + mn + "]:未取到值");
            }
        }

    }

    public Map<String, String> getDeviceStatusDic() {
        Map<String, String> map = new HashMap();
        map.put("0","故障");
        map.put("1","维修");
        map.put("2","停运");
        return map;
    }

    public void updateDeviceState(DataFactorBean bean, MonitorBean monitor, MonitorDeviceBean device) {
        Date now = new Date();
        String mn = monitor.getMn();
        int monitorType = monitor.getMonitorType();
        String thisState = bean.getEFlag();
        String factorCode = bean.getFactorCode();
        Date dataTime = bean.getDataTime();
        String warnTime = CommonsUtil.dateCurrent("yyyy-MM-dd HH:mm:ss");
        String sql = "SELECT * FROM DEVICE_STATE WHERE MN=''{0}'' AND CODE=''{1}'' ORDER BY CREATE_TIME DESC";
        List<Object> params = new ArrayList();
        params.add(mn);
        params.add(factorCode);
        List<Map<String, Object>> lastDeviceStateData = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(sql, params));
        boolean isAdd = false;
        Map warnRule;
        if (lastDeviceStateData != null && lastDeviceStateData.size() > 0) {
            warnRule = lastDeviceStateData.get(0);
            params = new ArrayList();
            params.add(now);
            params.add(dataTime);
            params.add(warnRule.get("ID"));
            sql = "UPDATE DEVICE_STATE SET END_TIME=''{0}'',DATA_TIME=''{1}'' WHERE ID={2}";
            this.myBaseMapper.sqlExcute(SqlBuilder.buildSql(sql, params));
            int lastState = (Integer) warnRule.get("STATE");
            if (lastState != Integer.valueOf(thisState)) {
                isAdd = true;
            }
        } else {
            isAdd = true;
        }

        if (isAdd) {
            params = new ArrayList();
            params.add(CommonsUtil.createUUID1());
            params.add(mn);
            params.add(factorCode);
            params.add(dataTime);
            params.add(now);
            params.add(now);
            params.add(bean.getSampleTime());
            params.add(Integer.valueOf(thisState));
            sql = "INSERT INTO DEVICE_STATE(ID,MN,CODE,DATA_TIME,CREATE_TIME,END_TIME,SAMPLE_TIME,STATE) VALUES({0},''{1}'',''{2}'',''{3}'',''{4}'',''{5}'',''{6}'',''{7}'')";
            this.myBaseMapper.sqlExcute(SqlBuilder.buildSql(sql,params));
        }

        if (!thisState.equals("0")) {
            WarnRuleBean warnRuleBean;
            if (monitorType != 3 && monitorType != 5 && monitorType != 7) {
                warnRuleBean = this.warnService.getDeviceWarnRule(mn, factorCode);
            } else {
                warnRuleBean = this.warnService.getEnvirDeviceWarnRule();
            }

            String warnMessage = monitor.getMonitorName() + "监测设备[" + device.getDeviceName() + "]于" + warnTime + "出现异常：" + this.getDeviceStatusDic().get(thisState) + "。";
            this.warnService.checkWarnlog(bean.getDataTime(), warnRuleBean, 3, warnMessage, monitor, factorCode);
        }

        this.setDeviceStatus(mn, factorCode, thisState);
    }
}