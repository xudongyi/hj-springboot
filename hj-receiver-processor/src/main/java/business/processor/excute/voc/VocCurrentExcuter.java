package business.processor.excute.voc;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import business.ienum.FactorType;
import business.processor.bean.*;
import business.processor.excute.DataParserService;
import business.processor.service.FactorService;
import business.processor.service.MonitorDeviceService;
import business.processor.service.MonitorService;
import business.processor.service.WarnService;
import business.processor.task.UpdateTableFieldTask;
import business.receiver.bean.MonitorBean;
import business.receiver.mapper.MyBaseMapper;
import business.util.CommonsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("VocCurrentExcuter")
@Transactional
@Slf4j
public class VocCurrentExcuter {
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
    private UpdateTableFieldTask updateTableFieldTask;
    @Autowired
    private MyBaseMapper baseDao;

    public VocCurrentExcuter() {
    }

    public int execute(DataPacketBean dataPacketBean) {
        this.dataParserService.format(dataPacketBean, FactorType.VOCS.TYPE());
        String mn = dataPacketBean.getMn();
        MonitorBean monitor = (MonitorBean)this.monitorService.getAllMonitors().get(mn);
        if (monitor != null) {
            this.checkData(dataPacketBean, monitor);
            this.updateRedisData(dataPacketBean);
            this.monitorService.setMnCurrentLastUpload(mn);
        }

        this.saveCurrentSingle(dataPacketBean);
        return 2;
    }

    private void checkData(DataPacketBean dataPacket, MonitorBean monitor) {
        String monitorId = monitor.getMonitorId();
        WarnRuleBean abnormalWarnRule = this.warnService.getAbnormalWarnRule();
        Map<String, DataFactorBean> dataMap = dataPacket.getDataMap();
        Iterator var6 = dataMap.keySet().iterator();

        while(var6.hasNext()) {
            String factorCode = (String)var6.next();
            DataFactorBean bean = (DataFactorBean)dataMap.get(factorCode);
            FactorBean factor = (FactorBean)this.factorService.getFactors(9).get(factorCode);
            if (factor != null) {
                MonitorDeviceBean device = this.monitorDeviceService.getDevice(monitorId, factorCode);
                if (device != null) {
                    boolean checkAbnormal = this.warnService.checkCurrentAbnormal(abnormalWarnRule, monitor, device, factor, bean);
                    this.warnService.checkOverproof(dataPacket.getSourceId(), monitor, bean, factor, checkAbnormal);
                    if (bean.getEFlag() != null) {
                        this.monitorDeviceService.updateDeviceState(bean, monitor, device);
                    }
                }
            }
        }

    }

    private void updateRedisData(DataPacketBean dataPacket) {
        Map<String, DataFactorBean> dataMap = dataPacket.getDataMap();
        Iterator var3 = dataMap.keySet().iterator();

        while(var3.hasNext()) {
            String factorCode = (String)var3.next();
            DataFactorBean bean = (DataFactorBean)dataMap.get(factorCode);
            this.monitorDeviceService.setCurrentData(dataPacket.getMn(), bean.getFactorCode(), bean);
        }

    }

    private void saveCurrentSingle(DataPacketBean dataPacketBean) {
        Map<String, FactorBean> factors = this.factorService.getFactors(9);
        Date dataTime = dataPacketBean.getDataTime();
        String mn = dataPacketBean.getMn();
        StringBuffer sql_field = new StringBuffer();
        StringBuffer sql_value = new StringBuffer();
        sql_field.append("INSERT INTO VOC_CURRENT_TR_" + CommonsUtil.dateFormat(dataTime, "yyMM") + "(ID,DATA_TIME,CREATE_TIME,MN,STATE");
        sql_value.append(") VALUES('"+CommonsUtil.createUUID1()+"','"+CommonsUtil.dateFormat(dataTime)+"','"+CommonsUtil.dateFormat(new Date())+"','"+mn+"',0");
        int monitorDataStatus = 9;
        int monitorDeviceStatus = 1;
        Map<String, DataFactorBean> map = dataPacketBean.getDataMap();
        Iterator var11 = map.keySet().iterator();

        while(var11.hasNext()) {
            String factorCode = (String)var11.next();
            if (this.updateTableFieldTask.isFieldExist(factorCode, FactorType.VOCS.TYPE())) {
                DataFactorBean bean = (DataFactorBean)map.get(factorCode);
                if (bean.getRtd() != null) {
                    sql_field.append("," + factorCode + "_RTD");
                    sql_value.append(","+bean.getRtd());
                }

                if (bean.getZsRtd() != null) {
                    sql_field.append("," + factorCode + "_ZSRTD");
                    sql_value.append(","+bean.getZsRtd());
                }

                if (bean.getFlag() != null) {
                    sql_field.append("," + factorCode + "_FLAG");
                    sql_value.append(",'"+bean.getFlag()+"'");
                }

                if (bean.getState() != null) {
                    sql_field.append("," + factorCode + "_STATE");
                    sql_value.append(","+bean.getState());
                }

                if (bean.getZsState() != null) {
                    sql_field.append("," + factorCode + "_ZSSTATE");
                    sql_value.append(","+bean.getZsState());
                }

                if (factors.get(factorCode) != null) {
                    int dataState = 9;
                    if (((FactorBean)factors.get(factorCode)).getZsFlag() == 1) {
                        if (bean.getZsState() != null) {
                            dataState = bean.getZsState();
                        }
                    } else if (bean.getState() != null) {
                        dataState = bean.getState();
                    }

                    if (dataState != 9) {
                        if (monitorDataStatus == 9) {
                            monitorDataStatus = dataState;
                        } else if (Math.abs(dataState) <= 5) {
                            if (Math.abs(dataState) < Math.abs(monitorDataStatus)) {
                                monitorDataStatus = dataState;
                            }
                        } else if (Math.abs(monitorDataStatus) > 5 && Math.abs(dataState) > Math.abs(monitorDataStatus)) {
                            monitorDataStatus = dataState;
                        }
                    }
                }

                if (bean.getEFlag() != null && !bean.getEFlag().equals("0")) {
                    monitorDeviceStatus = 0;
                }
            }
        }

        sql_field.append(sql_value).append(')');
        this.baseDao.sqlExcute(sql_field.toString());
        this.monitorService.setDataStatus(mn, monitorDataStatus);
        this.monitorService.setDeviceStatus(mn, monitorDeviceStatus);
    }
}
