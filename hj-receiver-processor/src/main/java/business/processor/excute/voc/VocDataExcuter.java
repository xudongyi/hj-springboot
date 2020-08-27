//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package business.processor.excute.voc;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import business.ienum.FactorType;
import business.processor.bean.*;
import business.processor.excute.DataParserService;
import business.processor.service.*;
import business.processor.task.UpdateTableFieldTask;
import business.receiver.bean.MonitorBean;
import business.receiver.mapper.MyBaseMapper;
import business.util.CommonsUtil;
import business.util.MathCalcUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("vocDataExcuter")
@Transactional
public class VocDataExcuter {
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
    private CompanyScheduleService companyScheduleService;
    @Autowired
    private ReverseControlService reverseControlService;
    @Autowired
    private UpdateTableFieldTask updateTableFieldTask;
    @Autowired
    private MyBaseMapper baseDao;

    public VocDataExcuter() {
    }

    public int execute(DataPacketBean dataPacketBean) {
        if (!this.dataParserService.isExistMHDData(dataPacketBean)) {
            this.dataParserService.format(dataPacketBean, FactorType.VOCS.TYPE());
            String mn = dataPacketBean.getMn();
            MonitorBean monitor = (MonitorBean)this.monitorService.getAllMonitors().get(mn);
            if (monitor != null) {
                this.checkData(dataPacketBean, monitor);
                if (dataPacketBean.getCn().equals("2061")) {
                    this.reverseControlService.addendumHourData(dataPacketBean, monitor);
                }

                if (dataPacketBean.getCn().equals("2031")) {
                    this.reverseControlService.addendumDayData(dataPacketBean, monitor);
                    this.companyScheduleService.saveScheduleByDayData(dataPacketBean, monitor, FactorType.VOCS.TYPE());
                }
            }

            if (dataPacketBean.getCn().equals("2031")) {
                this.saveMonthYearData(dataPacketBean);
            }

            this.saveData(dataPacketBean);
        }

        return 2;
    }

    private void checkData(DataPacketBean dataPacketBean, MonitorBean monitor) {
        String monitorId = monitor.getMonitorId();
        WarnRuleBean errorWarnRule = this.warnService.getErrorWarnRule();
        WarnRuleBean abnormalwarnRule = this.warnService.getAbnormalWarnRule();
        Map<String, DataFactorBean> map = dataPacketBean.getDataMap();
        Iterator var7 = map.keySet().iterator();

        while(var7.hasNext()) {
            String factorCode = (String)var7.next();
            DataFactorBean bean = (DataFactorBean)map.get(factorCode);
            FactorBean factor = (FactorBean)this.factorService.getFactors(FactorType.VOCS.TYPE()).get(factorCode);
            if (factor != null) {
                MonitorDeviceBean device = this.monitorDeviceService.getDevice(monitorId, factorCode);
                if (device != null) {
                    boolean checkDataError = this.warnService.checkDataError(errorWarnRule, monitor, factor, bean, dataPacketBean.getCn());
                    if (checkDataError) {
                        boolean checkMHDAbnormal = this.warnService.checkMHDAbnormal(abnormalwarnRule, monitor, device, factor, bean, dataPacketBean.getCn());
                        if (checkMHDAbnormal) {
                            this.warnService.checkOverproofHourDay(dataPacketBean.getCn(), monitor, bean, factor);
                        }
                    }

                    this.warnService.checkCouError(errorWarnRule, monitor, factor, bean, dataPacketBean.getCn());
                }
            }
        }

    }

    private void saveMonthYearData(DataPacketBean dataPacketBean) {
        String mn = dataPacketBean.getMn();
        Date dataTime = dataPacketBean.getDataTime();
        Date month = CommonsUtil.dateParse(CommonsUtil.dateFormat(dataTime, "yyyyMM"), "yyyyMM");
        List<Map<String, Object>> monthData = this.baseDao.sqlQuery("SELECT * FROM BAK_VOC_MONTH WHERE STATIC_TIME='"+CommonsUtil.dateFormat(dataTime, "yyyyMM")+"' AND MN='"+mn+"'");
        if (monthData == null || monthData.size()==0) {
            this.insertMonthYearData(dataPacketBean, "BAK_VOC_MONTH", month);
        } else {
            this.updateMonthYearData(dataPacketBean, "BAK_VOC_MONTH", monthData.get(0));
        }

        Date year = CommonsUtil.dateParse(CommonsUtil.dateFormat(dataTime, "yyyy"), "yyyy");
        List<Map<String, Object>> yearData = this.baseDao.sqlQuery("SELECT * FROM BAK_VOC_YEAR WHERE STATIC_TIME='"+CommonsUtil.dateFormat(dataTime, "yyyy")+"' AND MN='"+mn+"'");
        if (yearData == null) {
            this.insertMonthYearData(dataPacketBean, "BAK_VOC_YEAR", year);
        } else {
            this.updateMonthYearData(dataPacketBean, "BAK_VOC_YEAR", yearData.get(0));
        }

    }

    private void insertMonthYearData(DataPacketBean dataPacketBean, String tableName, Date staticTime) {
        StringBuilder sql_field = new StringBuilder();
        StringBuilder sql_value = new StringBuilder();
        sql_field.append("insert into " + tableName + "(ID,DATA_TIME,CREATE_TIME,STATIC_TIME,MN,TIMES");
        sql_value.append(")VALUES(?,?,?,?,?,?");
        sql_value.append(")VALUES('"+CommonsUtil.createUUID1()+"','"+dataPacketBean.getDataTime()+"','"
                +CommonsUtil.dateFormat(new Date())+"','"+CommonsUtil.dateFormat(staticTime)+"','"+dataPacketBean.getMn()+"',1");
        Map<String, DataFactorBean> map = dataPacketBean.getDataMap();
        Iterator var8 = map.keySet().iterator();

        while(var8.hasNext()) {
            String factorCode = (String)var8.next();
            if (this.updateTableFieldTask.isFieldExist(factorCode, FactorType.VOCS.TYPE())) {
                DataFactorBean bean = (DataFactorBean)map.get(factorCode);
                if (bean.getMinState() != null) {
                    if (bean.getMinState() == 9) {
                        sql_field.append("," + factorCode + "_MIN");
                        sql_value.append(","+bean.getMin());
                    }

                    sql_field.append("," + factorCode + "_MIN_STATE");
                    sql_value.append(","+bean.getMinState());
                }

                if (bean.getMaxState() != null) {
                    if (bean.getMaxState() == 9) {
                        sql_field.append("," + factorCode + "_MAX");
                        sql_value.append(","+bean.getMax());
                    }

                    sql_field.append("," + factorCode + "_MAX_STATE");
                    sql_value.append(","+bean.getMaxState());
                }

                if (bean.getAvgState() != null) {
                    if (bean.getAvgState() == 9) {
                        sql_field.append("," + factorCode + "_AVG");
                        sql_value.append(","+bean.getAvg());
                    }

                    sql_field.append("," + factorCode + "_AVG_STATE");
                    sql_value.append(","+bean.getAvgState());
                }

                if (bean.getCouState() != null) {
                    if (bean.getCouState() == 9) {
                        sql_field.append("," + factorCode + "_COU");
                        sql_value.append(","+bean.getCou());
                    }

                    sql_field.append("," + factorCode + "_COU_STATE");
                    sql_value.append(","+bean.getCouState());
                }

                if (bean.getZsMinState() != null) {
                    if (bean.getZsMinState() == 9) {
                        sql_field.append("," + factorCode + "_ZSMIN");
                        sql_value.append(","+bean.getZsMin());
                    }

                    sql_field.append("," + factorCode + "_ZSMIN_STATE");
                    sql_value.append(","+bean.getZsMinState());
                }

                if (bean.getZsMaxState() != null) {
                    if (bean.getZsMaxState() == 9) {
                        sql_field.append("," + factorCode + "_ZSMAX");
                        sql_value.append(","+bean.getZsMax());
                    }

                    sql_field.append("," + factorCode + "_ZSMAX_STATE");
                    sql_value.append(","+bean.getZsMaxState());
                }

                if (bean.getZsAvgState() != null) {
                    if (bean.getZsAvgState() == 9) {
                        sql_field.append("," + factorCode + "_ZSAVG");
                        sql_value.append(","+bean.getZsAvg());
                    }

                    sql_field.append("," + factorCode + "_ZSAVG_STATE");
                    sql_value.append(","+bean.getZsAvgState());
                }

                if (bean.getZsCouState() != null) {
                    if (bean.getZsCouState() == 9) {
                        sql_field.append("," + factorCode + "_ZSCOU");
                        sql_value.append(","+bean.getZsCou());
                    }

                    sql_field.append("," + factorCode + "_ZSCOU_STATE");
                    sql_value.append(","+bean.getZsCouState());
                }
            }
        }

        sql_field.append(sql_value).append(')');
        this.baseDao.sqlExcute(sql_field.toString());
    }

    private void updateMonthYearData(DataPacketBean dataPacketBean, String tableName, Map<String, Object> data) {
        StringBuilder sql = new StringBuilder();
        sql.append("update " + tableName + " set TIMES=TIMES+1,DATA_TIME='"+CommonsUtil.dateFormat(dataPacketBean.getDataTime())+"'");
        Map<String, DataFactorBean> map = dataPacketBean.getDataMap();
        Iterator var7 = map.keySet().iterator();

        while(var7.hasNext()) {
            String factorCode = (String)var7.next();
            if (this.updateTableFieldTask.isFieldExist(factorCode, 9)) {
                DataFactorBean bean = (DataFactorBean)map.get(factorCode);
                double cou;
                Object oldCou;
                if (bean.getMinState() != null && bean.getMinState() == 9) {
                    cou = bean.getMin();
                    oldCou = data.get(factorCode + "_MIN");
                    if (oldCou != null) {
                        if ((Double)oldCou < cou) {
                            cou = (Double)oldCou;
                        }
                    } else {
                        sql.append(',').append(factorCode).append("_MIN_STATE").append("="+bean.getMinState());
                    }

                    sql.append("," + factorCode + "_MIN").append("="+cou);
                }

                if (bean.getMaxState() != null && bean.getMaxState() == 9) {
                    cou = bean.getMax();
                    oldCou = data.get(factorCode + "_MAX");
                    if (oldCou != null) {
                        if ((Double)oldCou > cou) {
                            cou = (Double)oldCou;
                        }
                    } else {
                        sql.append(',').append(factorCode).append("_MAX_STATE").append("="+bean.getMaxState());
                    }

                    sql.append("," + factorCode + "_MAX").append("="+cou);
                }

                double avg;
                Object oldAvg;
                int times;
                if (bean.getAvgState() != null && bean.getAvgState() == 9) {
                    times = (Integer)data.get("TIMES");
                    avg = bean.getAvg();
                    oldAvg = data.get(factorCode + "_AVG");
                    if (oldAvg != null) {
                        avg = MathCalcUtil.avg((Double)oldAvg, bean.getAvg(), times);
                    } else {
                        sql.append("," + factorCode + "_AVG_STATE").append("="+bean.getAvgState());
                    }

                    sql.append("," + factorCode + "_AVG").append("="+avg);
                }

                if (bean.getCouState() != null && bean.getCouState() == 9) {
                    cou = bean.getCou();
                    oldCou = data.get(factorCode + "_COU");
                    if (oldCou != null) {
                        cou = CommonsUtil.numberFormat(bean.getCou() + (Double)oldCou, 4);
                    } else {
                        sql.append("," + factorCode + "_COU_STATE").append("="+bean.getCouState());
                    }

                    sql.append("," + factorCode + "_COU").append("="+cou);
                }

                if (bean.getZsMinState() != null && bean.getZsMinState() == 9) {
                    cou = bean.getZsMin();
                    oldCou = data.get(factorCode + "_ZSMIN");
                    if (oldCou != null) {
                        if ((Double)oldCou < cou) {
                            cou = (Double)oldCou;
                        }
                    } else {
                        sql.append(',').append(factorCode).append("_ZSMIN_STATE").append("="+bean.getZsMinState());
                    }

                    sql.append("," + factorCode + "_ZSMIN").append("="+cou);
                }

                if (bean.getZsMaxState() != null && bean.getZsMaxState() == 9) {
                    cou = bean.getZsMax();
                    oldCou = data.get(factorCode + "_ZSMAX");
                    if (oldCou != null) {
                        if ((Double)oldCou > cou) {
                            cou = (Double)oldCou;
                        }
                    } else {
                        sql.append(',').append(factorCode).append("_ZSMAX_STATE").append("="+bean.getZsMaxState());
                    }

                    sql.append("," + factorCode + "_ZSMAX").append("="+cou);
                }

                if (bean.getZsAvgState() != null && bean.getZsAvgState() == 9) {
                    times = (Integer)data.get("TIMES");
                    avg = bean.getZsAvg();
                    oldAvg = data.get(factorCode + "_ZSAVG");
                    if (oldAvg != null) {
                        avg = MathCalcUtil.avg(bean.getZsAvg(), (Double)oldAvg, times);
                    } else {
                        sql.append("," + factorCode + "_ZSAVG_STATE").append("="+bean.getZsAvgState());
                    }

                    sql.append("," + factorCode + "_ZSAVG").append("="+avg);
                }

                if (bean.getZsCouState() != null && bean.getZsCouState() == 9) {
                    cou = bean.getZsCou();
                    oldCou = data.get(factorCode + "_ZSCOU");
                    if (oldCou != null) {
                        cou = CommonsUtil.numberFormat(bean.getZsCou() + (Double)oldCou, 4);
                    } else {
                        sql.append("," + factorCode + "_ZSCOU_STATE").append("="+bean.getZsCouState());
                    }

                    sql.append("," + factorCode + "_ZSCOU").append("="+cou);
                }
            }
        }

        sql.append(" WHERE ID='"+data.get("ID")+"' ");
        this.baseDao.sqlExcute(sql.toString());
    }

    private void saveData(DataPacketBean dataPacketBean) {
        String cn = dataPacketBean.getCn();
        String mn = dataPacketBean.getMn();
        StringBuilder sql_field = new StringBuilder();
        StringBuilder sql_value = new StringBuilder();
        sql_field.append("INSERT INTO ").append(this.dataParserService.getMHDTableName(dataPacketBean)).append("(ID,DATA_TIME,CREATE_TIME,MN,STATE");
        sql_value.append(") VALUES('"+CommonsUtil.createUUID1()+"','"+CommonsUtil.dateFormat(dataPacketBean.getDataTime())+"','"+CommonsUtil.dateFormat(new Date())+"','"+mn+"',0");
        Map<String, DataFactorBean> map = dataPacketBean.getDataMap();
        Iterator var8 = map.keySet().iterator();

        while(var8.hasNext()) {
            String factorCode = (String)var8.next();
            if (this.updateTableFieldTask.isFieldExist(factorCode, FactorType.VOCS.TYPE())) {
                DataFactorBean bean = (DataFactorBean)map.get(factorCode);
                sql_field.append("," + factorCode + "_MIN");
                sql_field.append("," + factorCode + "_MIN_STATE");
                sql_field.append("," + factorCode + "_MAX");
                sql_field.append("," + factorCode + "_MAX_STATE");
                sql_field.append("," + factorCode + "_AVG");
                sql_field.append("," + factorCode + "_AVG_STATE");
                sql_field.append("," + factorCode + "_COU");
                sql_field.append("," + factorCode + "_COU_STATE");
                sql_field.append("," + factorCode + "_FLAG");
                sql_value.append(","+bean.getMin());
                sql_value.append(","+bean.getMinState());
                sql_value.append(","+bean.getMax());
                sql_value.append(","+bean.getMaxState());
                sql_value.append(","+bean.getAvg());
                sql_value.append(","+bean.getAvgState());
                sql_value.append(","+bean.getCou());
                sql_value.append(","+bean.getCouState());
                sql_value.append(",'"+bean.getFlag()+"'");
                sql_field.append("," + factorCode + "_ZSMIN");
                sql_field.append("," + factorCode + "_ZSMIN_STATE");
                sql_field.append("," + factorCode + "_ZSMAX");
                sql_field.append("," + factorCode + "_ZSMAX_STATE");
                sql_field.append("," + factorCode + "_ZSAVG");
                sql_field.append("," + factorCode + "_ZSAVG_STATE");
                sql_field.append("," + factorCode + "_ZSCOU");
                sql_field.append("," + factorCode + "_ZSCOU_STATE");
                sql_value.append(","+bean.getZsMin());
                sql_value.append(","+bean.getZsMinState());
                sql_value.append(","+bean.getZsMax());
                sql_value.append(","+bean.getZsMaxState());
                sql_value.append(","+bean.getZsAvg());
                sql_value.append(","+bean.getZsAvgState());
                sql_value.append(","+bean.getZsCou());
                sql_value.append(","+bean.getZsCouState());
                this.monitorDeviceService.setMHDData(mn, factorCode, cn, bean);
            }
        }

        sql_field.append(sql_value).append(')');
        this.baseDao.sqlExcute(sql_field.toString());
        if (cn.equals("2061")) {
            this.monitorService.setMnHourLastUpload(mn);
        } else if (cn.equals("2031")) {
            this.monitorService.setMnDayLastUpload(mn);
        }

    }
}
