package business.processor.excute.airq;

import business.constant.OnlineDataConstant;
import business.processor.bean.*;
import business.processor.excute.DataParserService;
import business.processor.service.*;
import business.processor.task.UpdateTableFieldTask;
import business.receiver.bean.MonitorBean;
import business.receiver.mapper.MyBaseMapper;
import business.util.CommonsUtil;
import business.util.SqlBuilder;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Service("airQDataExcuter")
public class AirQDataExcuter {
    @Autowired
    private MonitorService monitorService;
    @Autowired
    private MonitorDeviceService monitorDeviceService;
    @Autowired
    private FactorService factorService;
    @Autowired
    private WarnService warnService;
    @Autowired
    private DataParserService dataParserService;
    @Autowired
    private AirQualityService airQualityService;
    @Autowired
    private UpdateTableFieldTask updateTableFieldTask;
    @Autowired
    private MyBaseMapper myBaseMapper;
    public AirQDataExcuter() {
    }

    @PostConstruct
    public void initial() {
        this.airQualityService.initialAQI();
        this.airQualityService.initialLevel();
    }

    public int execute(DataPacketBean dataPacketBean) {
        String cn = dataPacketBean.getCn();
        if (!cn.equals("2061") && !cn.equals("2031")) {
            return 6;
        } else {
            if (!this.dataParserService.isExistMHDData(dataPacketBean)) {
                if (cn.equals("2061")) {
                    dataPacketBean.setContent(dataPacketBean.getContent().replaceAll("A34002-", "A3400201-").replaceAll("a34002-", "A3400201-").replaceAll("A34004-", "A3400401-").replaceAll("a34004-", "A3400401-"));
                }

                if (cn.equals("2031")) {
                    dataPacketBean.setContent(dataPacketBean.getContent().replaceAll("A34002-", "A3400224-").replaceAll("a34002-", "A3400224-").replaceAll("A34004-", "A3400424-").replaceAll("a34004-", "A3400424-"));
                }

                this.dataParserService.format(dataPacketBean, 3);
                MonitorBean monitor = this.monitorService.getAllMonitors().get(dataPacketBean.getMn());
                if (monitor != null) {
                    this.checkData(dataPacketBean, monitor);
                }

                this.saveData(dataPacketBean, monitor);
            }

            return 2;
        }
    }

    private void checkData(DataPacketBean dataPacketBean, MonitorBean monitor) {
        String cn = dataPacketBean.getCn();
        String monitorId = monitor.getMonitorId();
        WarnRuleBean abnormalwarnRule = this.warnService.getAbnormalWarnRule();
        Map<String, DataFactorBean> map = dataPacketBean.getDataMap();
        Iterator iterator = map.keySet().iterator();

        while(iterator.hasNext()) {
            String factorCode = (String)iterator.next();
            DataFactorBean bean = map.get(factorCode);
            FactorBean factor = this.factorService.getFactors(3).get(factorCode);
            if (factor != null) {
                MonitorDeviceBean device = this.monitorDeviceService.getDevice(monitorId, factorCode);
                if (device != null) {
                    this.warnService.checkMHDAbnormal(abnormalwarnRule, monitor, device, factor, bean, dataPacketBean.getCn());
                    if (cn.equals("2061") && bean.getEFlag() != null) {
                        this.monitorDeviceService.updateDeviceState(bean, monitor, device);
                    }
                }
            }
        }

    }

    private void saveData(DataPacketBean dataPacketBean, MonitorBean monitor) {
        String cn = dataPacketBean.getCn();
        String mn = dataPacketBean.getMn();
        StringBuilder sql_field = new StringBuilder();
        StringBuilder sql_value = new StringBuilder();
        List<Object> params = new ArrayList();
        sql_field.append("INSERT INTO " + this.dataParserService.getMHDTableName(dataPacketBean) + "(ID,DATA_TIME,CREATE_TIME,MN,STATE");
        sql_value.append(")VALUES('"+CommonsUtil.createUUID1()+"','"+DateUtil.formatDateTime(dataPacketBean.getDataTime())+"','"+DateUtil.formatDateTime(new Date())+"','"+mn+"',0");
        params.add(CommonsUtil.createUUID1());
        params.add(DateUtil.formatDateTime(dataPacketBean.getDataTime()));
        params.add(DateUtil.formatDateTime(new Date()));
        params.add(mn);
        params.add(0);
        double aqi = 0.0D;
        int monitorDeviceStatus = 1;
        Map<String, Double> iaqi = new HashMap();
        Map<String, DataFactorBean> map = dataPacketBean.getDataMap();
        Iterator var13 = map.keySet().iterator();

        double avg;
        while(var13.hasNext()) {
            String factorCode = (String)var13.next();
            if (this.updateTableFieldTask.isFieldExist(factorCode, 3)) {
                DataFactorBean bean = map.get(factorCode);
                if (bean.getAvg() != null) {
                    avg = bean.getAvg();
                    sql_field.append("," + factorCode + "_AVG");
                    sql_value.append(","+avg+"");
                    double aqi_tmp = -1.0D;
                    if (factorCode.equals("A0502401")) {
                        aqi_tmp = this.airQualityService.getAQI("A05024", 1, avg);
                    } else if (factorCode.equals("A0502408")) {
                        aqi_tmp = this.airQualityService.getAQI("A05024", 8, avg);
                    } else if (factorCode.equals("A3400401")) {
                        aqi_tmp = this.airQualityService.getAQI("A34004", 1, avg);
                    } else if (factorCode.equals("A3400424")) {
                        aqi_tmp = this.airQualityService.getAQI("A34004", 24, avg);
                    } else if (factorCode.equals("A3400201")) {
                        aqi_tmp = this.airQualityService.getAQI("A34002", 1, avg);
                    } else if (factorCode.equals("A3400224")) {
                        aqi_tmp = this.airQualityService.getAQI("A34002", 24, avg);
                    } else if (cn.equals("2061")) {
                        aqi_tmp = this.airQualityService.getAQI(factorCode, 1, avg);
                    } else {
                        aqi_tmp = this.airQualityService.getAQI(factorCode, 24, avg);
                    }

                    if (aqi_tmp >= 0.0D) {
                        sql_field.append("," + factorCode + "_IAQI");
                        sql_value.append(","+aqi_tmp);
                        iaqi.put(factorCode, aqi_tmp);
                    }

                    if (aqi_tmp > aqi) {
                        aqi = aqi_tmp;
                    }
                }

                if (bean.getEFlag() != null && !bean.getEFlag().equals("0")) {
                    monitorDeviceStatus = 0;
                }

                this.monitorDeviceService.setMHDData(mn, factorCode, cn, bean);
            }
        }

        sql_field.append(",AQI");
        sql_value.append(","+aqi+"");
        String level;
        if (aqi > 50.0D) {
            level = "";
            Iterator var22 = iaqi.keySet().iterator();

            while(var22.hasNext()) {
                String code = (String)var22.next();
                avg =  iaqi.get(code);
                if (avg == aqi) {
                    if (level.equals("")) {
                        level = code;
                    } else {
                        level = level + "," + code;
                    }
                }
            }

            sql_field.append(",FIRST_CODE");
            sql_value.append(",'"+level+"'");
        }

        level = this.airQualityService.getLevel(aqi);
        sql_field.append(",LEVEL");
        sql_value.append(","+level+"");
        sql_field.append(sql_value).append(')');
        if (cn.equals("2061")) {
            WarnRuleBean warnRule = this.warnService.getEnviAirQWarnRule();
            if (warnRule != null) {
                double warnLevel = warnRule.getMax();
                double dataLevel = OnlineDataConstant.AQI_LEVEL.get(level);
                if (dataLevel > warnLevel) {
                    if (monitor != null) {
                        String warnTime = CommonsUtil.dateFormat(dataPacketBean.getDataTime(), "yyyy-MM-dd HH");
                        String warnMessage = monitor.getMonitorName() + "小时数据于" + warnTime + "时AQI报警，当前AQI值：" + aqi + "(" + OnlineDataConstant.AQI_LEVEL_DESC.get(level) + ")。";
                        //TODO 111
                        //this.warnService.checkWarnLog(dataPacketBean.getDataTime(), warnRule, 11, warnMessage, monitor, (String)null);
                    } else {
                        log.error("AQI报警失败，未找到监控点" + mn);
                    }
                }
            }
        }
        this.myBaseMapper.sqlExcute(sql_field.toString());
        if (cn.equals("2061")) {
            this.monitorService.setDeviceStatus(mn, monitorDeviceStatus);
            this.monitorService.setMnCurrentLastUpload(mn);
            this.monitorService.setMnHourLastUpload(mn);
        } else if (cn.equals("2031")) {
            this.monitorService.setMnDayLastUpload(mn);
        }

    }
}