package business.processor.excute.water;

import business.constant.OnlineDataConstant;
import business.ienum.FactorType;
import business.processor.bean.*;
import business.processor.excute.DataParserService;
import business.processor.service.*;
import business.processor.task.UpdateTableFieldTask;
import business.receiver.bean.MonitorBean;
import business.receiver.mapper.MyBaseMapper;
import business.util.CommonsUtil;
import business.util.SqlBuilder;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("waterCurrentExcuter")
@Transactional
@Slf4j
public class WaterCurrentExcuter {
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
    private CompanyService companyService;
    @Autowired
    private UpdateTableFieldTask updateTableFieldTask;
    @Autowired
    private MyBaseMapper myBaseMapper;

    public WaterCurrentExcuter() {
    }

    public int execute(DataPacketBean dataPacketBean) {
        this.dataParserService.format(dataPacketBean, FactorType.WATER.TYPE());
        String mn = dataPacketBean.getMn();
        MonitorBean monitor = (MonitorBean)this.monitorService.getAllMonitors().get(mn);
        if (monitor != null) {
            this.checkData(dataPacketBean, monitor);
            this.saveCurrent(dataPacketBean, monitor);
            this.monitorService.setMnCurrentLastUpload(mn);
        }

        this.saveCurrentSingle(dataPacketBean);
        return 2;
    }

    private void checkData(DataPacketBean dataPacket, MonitorBean monitor) {
        String mn = dataPacket.getMn();
        String monitorId = monitor.getMonitorId();
        String companyId = monitor.getCompanyId();
        WarnRuleBean abnormalWarnRule = this.warnService.getAbnormalWarnRule();
        WarnRuleBean fixWarnRule = this.warnService.getFixWarnRule();
        List<WarnRuleBean> surplusWarnRuleList = this.warnService.getSurplusWarnRule(mn);
        Map<String, DataFactorBean> dataMap = dataPacket.getDataMap();
        Iterator var10 = dataMap.keySet().iterator();

        while(var10.hasNext()) {
            String factorCode = (String)var10.next();
            DataFactorBean bean = (DataFactorBean)dataMap.get(factorCode);
            FactorBean factor = (FactorBean)this.factorService.getFactors(1).get(factorCode);
            if (factor != null) {
                MonitorDeviceBean device = this.monitorDeviceService.getDevice(monitorId, factorCode);
                if (device != null) {
                    DataFactorBean lastData = this.monitorDeviceService.getCurrentData(mn, factorCode);
                    boolean checkAbnormal = this.warnService.checkCurrentAbnormal(abnormalWarnRule, monitor, device, factor, bean);
                    this.warnService.checkOverproof(dataPacket.getSourceId(), monitor, bean, factor, checkAbnormal);
                    if (device.getWorkCycle() > 0 && lastData != null) {
                        this.checkFixData(fixWarnRule, monitor, device, factor, bean, lastData);
                    }

                    double total;
                    if (bean.getSurplus() != null) {
                        total = bean.getSurplus();
                        this.warnService.checkSurplus(surplusWarnRuleList, monitor, total, bean.getDataTime());
                        if (lastData != null) {
                            this.saveStatistic(mn, factorCode + "_SURPLUS", bean.getDataTime(), lastData.getDataTime(), total);
                        }
                    }

                    if (bean.getTotal() != null) {
                        total = bean.getTotal();
                        if (lastData != null) {
                            this.saveStatistic(mn, factorCode + "_TOTAL", bean.getDataTime(), lastData.getDataTime(), total);
                            if (factorCode.equals("W00000")) {
                                this.initialSchedule(companyId, mn, bean, lastData);
                                this.saveSchedule(monitor, bean, lastData);
                            }
                        }
                    }

                    if (bean.getEFlag() != null) {
                        this.monitorDeviceService.updateDeviceState(bean, monitor, device);
                    }
                }
            }
        }

    }

    private void checkFixData(WarnRuleBean warnRule, MonitorBean monitor, MonitorDeviceBean device, FactorBean factor, DataFactorBean bean, DataFactorBean lastData) {
        Double val = bean.getRtd();
        if (val != null) {
            String factorCode = factor.getCode();
            String factorName = factor.getName() == null ? factorCode : factor.getName();
            String warnTime = CommonsUtil.dateCurrent("yyyy-MM-dd HH:mm:ss");
            Date dataTime = bean.getDataTime();
            Date fixBeginTime;
            if (lastData.getRtd() == val) {
                fixBeginTime = lastData.getFixBeginTime();
                if (fixBeginTime == null) {
                    fixBeginTime = lastData.getDataTime();
                }

                Integer fixTimes = lastData.getFixTimes();
                if (fixTimes == null) {
                    fixTimes = 0;
                }

                bean.setFixTimes(fixTimes);
                bean.setFixBeginTime(fixBeginTime);
                long minute = bean.getDataTime().getTime() - fixBeginTime.getTime();
                if (minute >= (long)(device.getWorkCycle() * 60 * 1000)) {
                    bean.setFixTimes(bean.getFixTimes() + 1);
                    bean.setFixBeginTime(dataTime);
                    if (warnRule != null && bean.getFixTimes() > warnRule.getRepeat()) {
                        String warnMessage = monitor.getMonitorName() + "[" + factorName + "]于" + warnTime + "出现定值数据，当前值为" + val + "，数据已重复" + bean.getFixTimes() + "次";
                        //TODO 111
                        //this.warnService.checkWarnLog(dataTime, warnRule, 6, warnMessage, monitor, factorCode);
                    }
                }
            } else {
                bean.setFixBeginTime(dataTime);
                bean.setFixTimes(0);
            }

            fixBeginTime = bean.getSampleTime();
            if (fixBeginTime != null) {
                if (fixBeginTime.equals(lastData.getSampleTime())) {
                    bean.setRepeat(true);
                } else {
                    bean.setRepeat(false);
                }
            } else if (lastData.getRtd() == val) {
                bean.setRepeat(true);
            } else {
                bean.setRepeat(false);
            }

        }
    }

    private void saveStatistic(String mn, String code, Date dataTime, Date lastDataTime, double val) {
        if (isNextDay(lastDataTime, dataTime)) {
            String sql = "insert into TOTAL_STATISTIC(ID,DATA_TIME,MN,CODE,VALUE) values (''{0}'',''{1}'',''{2}'',''{3}'',''{4}'')";
            List<Object> params = new ArrayList();
            params.add(CommonsUtil.createUUID1());
            params.add(DateUtil.format(dataTime, DatePattern.PURE_DATE_PATTERN));
            params.add(mn);
            params.add(code);
            params.add(val);
            this.myBaseMapper.sqlExcute(SqlBuilder.buildSql(sql, params));
        }

    }

    private void initialSchedule(String companyId, String mn, DataFactorBean bean, DataFactorBean lastData) {
        Date dataTime = bean.getDataTime();
        double total = bean.getTotal();
        double lastTotal = lastData.getTotal() == null ? 0.0D : lastData.getTotal();
        if (isNextDay(lastData.getDataTime(), dataTime)) {
            Date day = CommonsUtil.dateParse(CommonsUtil.dateFormat(dataTime, "yyyyMMdd"), "yyyyMMdd");
            Map<String, Object> data = this.companyScheduleService.getScheduleData(day, companyId, mn, 1);
            double day_begin = total;
            double month_begin = total;
            double year_begin = total;
            if (total < 0.0D || total < lastTotal) {
                day_begin = lastTotal;
                month_begin = lastTotal;
                year_begin = lastTotal;
            }

            Object[] beginData = this.companyScheduleService.getWaterBeginScheduleData(day, companyId, mn);
            if (beginData[0] != null) {
                month_begin = (Double)beginData[0];
            }

            if (beginData[1] != null) {
                year_begin = (Double)beginData[1];
            }

            StringBuilder sql;
            ArrayList params;
            if (data == null) {
                sql = new StringBuilder();
                params = new ArrayList();
                sql.append("INSERT INTO COM_SCHEDULE");
                sql.append("(ID,DATA_TIME,CREATE_TIME,STATIC_TIME,COMPANY_ID,MN,W00000_DAY_BEGIN,W00000_MONTH_BEGIN,W00000_YEAR_BEGIN)");
                sql.append("VALUES(''{0}'',''{1}'',''{2}'',''{3}'',''{4}'',''{5}'',''{6}'',''{7}'',''{8}'')");
                params.add(CommonsUtil.createUUID1());
                params.add(dataTime);
                params.add(new Date());
                params.add(DateUtil.format(day,DatePattern.PURE_DATE_PATTERN));
                params.add(companyId);
                params.add(mn);
                params.add(day_begin);
                params.add(month_begin);
                params.add(year_begin);
                this.myBaseMapper.sqlExcute(SqlBuilder.buildSql(sql.toString(), params));
            } else {
                sql = new StringBuilder();
                params = new ArrayList();
                sql.append("UPDATE COM_SCHEDULE SET DATA_TIME=''{0}'',W00000_DAY_BEGIN=''{1}'',W00000_MONTH_BEGIN=''{2}'',W00000_YEAR_BEGIN=''{3}'' WHERE ID=''{4}''");
                params.add(DateUtil.format(dataTime,DatePattern.PURE_DATE_PATTERN));
                params.add(day_begin);
                params.add(month_begin);
                params.add(year_begin);
                params.add(data.get("ID"));
                this.myBaseMapper.sqlExcute(SqlBuilder.buildSql(sql.toString(), params));
            }
        }

    }

    private void saveSchedule(MonitorBean monitor, DataFactorBean bean, DataFactorBean lastData) {
        String companyId = monitor.getCompanyId();
        String mn = monitor.getMn();
        String monitorName = monitor.getMonitorName();
        String factorCode = bean.getFactorCode();
        String warnTime = CommonsUtil.dateCurrent("yyyy-MM-dd HH:mm:ss");
        Date dataTime = bean.getDataTime();
        double total = bean.getTotal();
        Date lastDataTime = lastData.getDataTime();
        double lastTotal = lastData.getTotal() == null ? total : lastData.getTotal();
        boolean lastTotalError = lastData.getTotalError() == null ? false : lastData.getTotalError();
        double lastCorretTotal = lastData.getLastCorretTotal() == null ? lastTotal : lastData.getLastCorretTotal();
        if (total > 0.0D && isNextHour(lastDataTime, dataTime)) {
            Date day = CommonsUtil.dateParse(CommonsUtil.dateFormat(dataTime, "yyyyMMdd"), "yyyyMMdd");
            if (isNextDay(lastDataTime, dataTime)) {
                day = CommonsUtil.dateParse(CommonsUtil.dateFormat(lastDataTime, "yyyyMMdd"), "yyyyMMdd");
            }

            Map<String, Object> data = this.companyScheduleService.getScheduleData(day, companyId, mn, 1);
            if (data != null) {
                List<Object> list = new ArrayList();
                double dayBeginTotal = total;
                if (data.get("W00000_DAY_BEGIN") != null) {
                    dayBeginTotal = (Double)data.get("W00000_DAY_BEGIN");
                }

                double monthBeginTotal = total;
                if (data.get("W00000_MONTH_BEGIN") != null) {
                    monthBeginTotal = (Double)data.get("W00000_MONTH_BEGIN");
                }

                double yearBeginTotal = total;
                if (data.get("W00000_YEAR_BEGIN") != null) {
                    yearBeginTotal = (Double)data.get("W00000_YEAR_BEGIN");
                }

                StringBuilder sql = new StringBuilder();
                sql.append("update COM_SCHEDULE set DATA_TIME=?");
                list.add(dataTime);
                if (total < lastTotal) {
                    bean.setTotalError(true);
                    bean.setLastCorretTotal(lastCorretTotal);
                    return;
                }

                bean.setTotalError(true);
                bean.setLastCorretTotal(total);
                if (lastTotalError && total < lastCorretTotal && !isNextDay(lastDataTime, dataTime)) {
                    dayBeginTotal = CommonsUtil.numberFormat(0.0D - lastCorretTotal - dayBeginTotal, 4);
                    monthBeginTotal = CommonsUtil.numberFormat(0.0D - lastCorretTotal - monthBeginTotal, 4);
                    yearBeginTotal = CommonsUtil.numberFormat(0.0D - lastCorretTotal - yearBeginTotal, 4);
                    List<Object> params_updateBegin = new ArrayList();
                    params_updateBegin.add(dayBeginTotal);
                    params_updateBegin.add(monthBeginTotal);
                    params_updateBegin.add(yearBeginTotal);
                    params_updateBegin.add(DateUtil.format(day,DatePattern.PURE_DATE_PATTERN));
                    params_updateBegin.add(companyId);
                    params_updateBegin.add(mn);
                    this.myBaseMapper.sqlExcute(SqlBuilder.buildSql("UPDATE COM_SCHEDULE SET W00000_DAY_BEGIN=''{0}'',W00000_MONTH_BEGIN=''{1}'',W00000_YEAR_BEGIN=''{2}''  WHERE STATIC_TIME=''{3}'' AND COMPANY_ID=''{4}'' AND MN=''{5}'' ", params_updateBegin));
                }

                double dv = CommonsUtil.numberFormat(total - dayBeginTotal, 4);
                if (dv >= 0.0D) {
                    sql.append(",W00000_DAY_COU='"+dv+"'");
                }

                double mv = CommonsUtil.numberFormat(total - monthBeginTotal, 4);
                if (mv >= 0.0D) {
                    sql.append(",W00000_MONTH_COU='"+mv+"'");
                }

                double yv = CommonsUtil.numberFormat(total - yearBeginTotal, 4);
                if (yv >= 0.0D) {
                    sql.append(",W00000_YEAR_COU='"+yv+"'");
                    list.add(yv);
                }

                ScheduleBean schedule = this.companyService.getCompanySchedule(companyId, factorCode);
                double dayAllow = 0.0D;
                if (schedule != null) {
                    dayAllow = schedule.getDay();
                }

                if (dayAllow > 0.0D && dv >= 0.0D) {
                    sql.append(",W00000_DAY_PROCESS='"+CommonsUtil.numberFormat(dv / dayAllow, 4)+"'");
                }

                int companyTotalStatus = 9;
                double monthAllow = 0.0D;
                if (schedule != null) {
                    monthAllow = schedule.getMonth(bean.getDataTime());
                }

                double month_process;
                if (monthAllow > 0.0D && mv >= 0.0D) {
                    sql.append(",W00000_MONTH_PROCESS='"+CommonsUtil.numberFormat(mv / monthAllow, 4)+"'");
                    month_process = CommonsUtil.numberFormat(mv / monthAllow, 4);
                    List<WarnRuleBean> warnRuleList = this.warnService.getTotalMonthWarnRule(companyId, 1, factorCode);
                    if (warnRuleList != null) {
                        for(int j = 0; j < warnRuleList.size(); ++j) {
                            double monthTotalMax = ((WarnRuleBean)warnRuleList.get(j)).getMax() / 100.0D;
                            WarnRuleBean monthWarnRule = (WarnRuleBean)warnRuleList.get(j);
                            if (monthTotalMax >= 0.0D && month_process > monthTotalMax) {
                                companyTotalStatus = monthWarnRule.getLevel();
                                String warnMessage = "排口[" + monitorName + "]废水累计排放量于" + warnTime + "月排污进度已达" + CommonsUtil.numberFormat(month_process * 100.0D) + "%,当前设置报警阈值为" + CommonsUtil.numberFormat(monthTotalMax * 100.0D) + "%";
                                //TODO 111
                                //this.warnService.checkWarnLog(bean.getDataTime(), monthWarnRule, 5, warnMessage, monitor, factorCode);
                                break;
                            }
                        }
                    }
                }

                month_process = 0.0D;
                if (schedule != null) {
                    month_process = schedule.getYear();
                }

                if (month_process > 0.0D && yv >= 0.0D) {
                    sql.append(",W00000_YEAR_PROCESS='"+CommonsUtil.numberFormat(yv / month_process, 4)+"'");
                    double year_process = CommonsUtil.numberFormat(yv / month_process, 4);
                    List<WarnRuleBean> warnRuleList = this.warnService.getTotalYearWarnRule(companyId, 1, factorCode);
                    if (warnRuleList != null) {
                        for(int j = 0; j < warnRuleList.size(); ++j) {
                            double yearTotalMax = ((WarnRuleBean)warnRuleList.get(j)).getMax() / 100.0D;
                            WarnRuleBean yearWarnRule = (WarnRuleBean)warnRuleList.get(j);
                            if (yearTotalMax >= 0.0D && year_process > yearTotalMax) {
                                if (yearWarnRule.getLevel() < companyTotalStatus) {
                                    companyTotalStatus = yearWarnRule.getLevel();
                                }

                                String warnMessage = "排口[" + monitor.getMonitorName() + "]废水累计排放量于" + warnTime + "年排污进度已达" + CommonsUtil.numberFormat(year_process * 100.0D) + "%,当前设置报警阈值为" + CommonsUtil.numberFormat(yearTotalMax * 100.0D) + "%";
                                //TODO 111
                                //this.warnService.checkWarnLog(bean.getDataTime(), yearWarnRule, 5, warnMessage, monitor, factorCode);
                                break;
                            }
                        }
                    }
                }

                sql.append(" WHERE ID=''{6}'' ");
                list.add(data.get("ID"));
                this.myBaseMapper.sqlExcute(SqlBuilder.buildSql(sql.toString(), list));
                this.companyService.setTotalStatus(companyId, companyTotalStatus);
            }
        }

    }

    private void saveCurrent(DataPacketBean dataPacketBean, MonitorBean monitor) {
        String mn = monitor.getMn();
        Date dataTime = dataPacketBean.getDataTime();
        Map<String, DataFactorBean> map = dataPacketBean.getDataMap();
        Iterator var6 = map.keySet().iterator();

        while(var6.hasNext()) {
            String factorCode = (String)var6.next();
            DataFactorBean bean = map.get(factorCode);
            this.monitorDeviceService.setCurrentData(mn, factorCode, bean);
            if (bean.getRepeat() != null && !bean.getRepeat() && bean.getRtd() != null) {
                MonitorDeviceBean device = this.monitorDeviceService.getDevice(monitor.getMonitorId(), factorCode);
                if (device != null && device.getWorkCycle() > 0 && bean.getRtd() != null) {
                    StringBuffer sql_field = new StringBuffer();
                    StringBuffer sql_value = new StringBuffer();
                    List<Object> params = new ArrayList();
                    sql_field.append("INSERT INTO WATER_CURRENT_" + CommonsUtil.dateFormat(dataTime, "yyMM") + "(");
                    sql_value.append(") VALUES(");
                    sql_field.append("ID");
                    sql_value.append("'"+CommonsUtil.createUUID1()+"'");
                    sql_field.append(",DATA_TIME");
                    sql_value.append(",'"+CommonsUtil.dateFormat(dataTime,DatePattern.NORM_DATETIME_PATTERN)+"'");
                    sql_field.append(",CREATE_TIME");
                    sql_value.append(",'"+CommonsUtil.dateFormat(new Date(),DatePattern.NORM_DATETIME_PATTERN)+"'");
                    sql_field.append(",MN");
                    sql_value.append(",'"+mn+"'");
                    sql_field.append(",CODE");
                    sql_value.append(",'"+factorCode+"'");
                    params.add(factorCode);
                    sql_field.append(",VALUE");
                    sql_value.append(",'"+bean.getRtd()+"'");
                    sql_field.append(",STATE");
                    sql_value.append(",'"+bean.getState()+"'");
                    params.add(bean.getState());
                    if (bean.getSampleTime() != null) {
                        sql_field.append(",SAMPLE_TIME");
                        sql_value.append(",'"+CommonsUtil.dateFormat(bean.getSampleTime(),DatePattern.NORM_DATETIME_PATTERN)+"'");
                    }

                    if (bean.getFlag() != null) {
                        sql_field.append(",FLAG");
                        sql_value.append(",'"+bean.getFlag()+"'");
                    }

                    sql_field.append(sql_value);
                    sql_field.append(')');
                    this.myBaseMapper.sqlExcute(sql_field.toString());
                }
            }
        }

    }

    private void saveCurrentSingle(DataPacketBean dataPacketBean) {
        Date dataTime = dataPacketBean.getDataTime();
        String mn = dataPacketBean.getMn();
        StringBuffer sql_field = new StringBuffer();
        StringBuffer sql_value = new StringBuffer();
        sql_field.append("INSERT INTO WATER_CURRENT_TR_" + CommonsUtil.dateFormat(dataTime, "yyMM") + "(ID,DATA_TIME,CREATE_TIME,MN,STATE");
        sql_value.append(") VALUES('"+CommonsUtil.createUUID1()+"','"+CommonsUtil.dateFormat(dataTime)+"','"+CommonsUtil.dateFormat(new Date())+"','"+mn+"',0");
        int monitorDataStatus = 9;
        int monitorDeviceStatus = 1;
        Map<String, DataFactorBean> map = dataPacketBean.getDataMap();
        Iterator var10 = map.keySet().iterator();

        while(var10.hasNext()) {
            String factorCode = (String)var10.next();
            if (this.updateTableFieldTask.isFieldExist(factorCode, FactorType.WATER.TYPE())) {
                DataFactorBean bean = (DataFactorBean)map.get(factorCode);
                if (bean.getRtd() != null) {
                    sql_field.append("," + factorCode + "_RTD");
                    sql_value.append(",'"+bean.getRtd()+"'");
                }

                if (bean.getSampleTime() != null) {
                    sql_field.append("," + factorCode + "_SAMPLETIME");
                    sql_value.append(",'"+CommonsUtil.dateFormat(bean.getSampleTime(),DatePattern.NORM_DATETIME_PATTERN)+"'");
                }

                if (bean.getFlag() != null) {
                    sql_field.append("," + factorCode + "_FLAG");
                    sql_value.append(",'"+bean.getFlag()+"'");
                }

                if (bean.getState() != null) {
                    sql_field.append("," + factorCode + "_STATE");
                    sql_value.append(",'"+bean.getState()+"'");
                    if (bean.getState() != 9) {
                        if (monitorDataStatus == 9) {
                            monitorDataStatus = bean.getState();
                        } else if (Math.abs(bean.getState()) <= 5) {
                            if (Math.abs(bean.getState()) < Math.abs(monitorDataStatus)) {
                                monitorDataStatus = bean.getState();
                            }
                        } else if (Math.abs(monitorDataStatus) > 5 && Math.abs(bean.getState()) > Math.abs(monitorDataStatus)) {
                            monitorDataStatus = bean.getState();
                        }
                    }
                }

                if (bean.getEFlag() != null && !bean.getEFlag().equals("0")) {
                    monitorDeviceStatus = 0;
                }

                if (factorCode.equals("W00000")) {
                    if (bean.getTotal() != null) {
                        sql_field.append("," + factorCode + "_TOTAL");
                        sql_value.append(",'"+bean.getTotal()+"'");
                    }

                    if (bean.getToday() != null) {
                        sql_field.append("," + factorCode + "_TODAY");
                        sql_value.append(",'"+bean.getTotal()+"'");
                    }

                    if (bean.getSurplus() != null) {
                        sql_field.append("," + factorCode + "_SURPLUS");
                        sql_value.append(",'"+bean.getSurplus()+"'");
                    }
                }
            }
        }

        sql_field.append(sql_value).append(')');
        this.myBaseMapper.sqlExcute(sql_field.toString());
        this.monitorService.setDataStatus(mn, monitorDataStatus);
        this.monitorService.setDeviceStatus(mn, monitorDeviceStatus);
    }

    public static boolean isNextDay(Date before, Date after) {
        Calendar clBefore = Calendar.getInstance();
        clBefore.setTime(before);
        Calendar clAfter = Calendar.getInstance();
        clAfter.setTime(after);
        return clBefore.compareTo(clAfter) < 0 && clBefore.get(5) != clAfter.get(5);
    }

    public static boolean isNextHour(Date before, Date after) {
        Calendar clBefore = Calendar.getInstance();
        clBefore.setTime(before);
        Calendar clAfter = Calendar.getInstance();
        clAfter.setTime(after);
        return clBefore.compareTo(clAfter) < 0 && clBefore.get(11) != clAfter.get(11);
    }
}