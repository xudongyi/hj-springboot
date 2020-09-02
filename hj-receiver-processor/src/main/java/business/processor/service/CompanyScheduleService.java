package business.processor.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import business.ienum.FactorType;
import business.processor.bean.*;
import business.processor.task.UpdateTableFieldTask;
import business.receiver.bean.MonitorBean;
import business.receiver.mapper.MyBaseMapper;
import business.util.CommonsUtil;
import business.util.SqlBuilder;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("companyScheduleService")
public class CompanyScheduleService {
    @Autowired
    private MyBaseMapper myBaseMapper;
    @Autowired
    private FactorService factorService;
    @Autowired
    private MonitorDeviceService monitorDeviceService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private WarnService warnService;
    @Autowired
    private UpdateTableFieldTask updateTableFieldTask;

    public CompanyScheduleService() {
    }

    public Map<String, Object> getScheduleData(Date dateTime, String cid, String mn, int type) {
        String tableName = "COM_SCHEDULE";
        if (FactorType.WATER.TYPE() == type) {
            tableName = "COM_SCHEDULE";
        } else if (FactorType.AIR.TYPE() == type) {
            tableName = "COM_SCHEDULE_AIR";
        } else if (FactorType.VOCS.TYPE() == type) {
            tableName = "COM_SCHEDULE_VOC";
        }

        String sql = "SELECT * FROM  " + tableName + " WHERE STATIC_TIME=''{0}'' AND COMPANY_ID=''{1}'' AND MN=''{2}'' ";
        List<Object> params = new ArrayList();
        params.add(DateUtil.format(dateTime, DatePattern.PURE_DATE_PATTERN));
        params.add(cid);
        params.add(mn);
        List<Map<String, Object>> list = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(sql, params));
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    public Object[] getWaterBeginScheduleData(Date today, String cid, String mn) {
        Object month_begin_data = null;
        Object year_begin_data = null;
        int day = today.getDate();
        int month = today.getMonth();
        Date yesterday = CommonsUtil.day(today, -1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(yesterday);
        calendar.set(5, 1);
        Date monthFirstday = calendar.getTime();
        calendar.set(2, 1);
        Date yearFirstday = calendar.getTime();
        if (day != 1 || month != 1) {
            String sql;
            ArrayList params;
            List yesterdayData;
            List yearBeginData;
            if (day == 1 && month != 1) {
                sql = "SELECT * FROM  COM_SCHEDULE WHERE STATIC_TIME=''{0}'' AND COMPANY_ID=''{1}'' AND MN=''{2}'' ";
                params = new ArrayList();
                params.add(DateUtil.format(yearFirstday, DatePattern.PURE_DATE_PATTERN));
                params.add(cid);
                params.add(mn);
                yesterdayData = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(sql, params));
                if (yesterdayData != null && yesterdayData.size() > 0) {
                    year_begin_data = ((Map)yesterdayData.get(0)).get("W00000_YEAR_BEGIN");
                }

                if (year_begin_data == null) {
                    sql = "SELECT * FROM  COM_SCHEDULE WHERE STATIC_TIME>=''{0}'' AND STATIC_TIME<=''{1}''  AND COMPANY_ID=''{2}'' AND MN=''{3}''  AND W00000_YEAR_BEGIN IS NOT NULL ORDER BY STATIC_TIME LIMIT 0,1";
                    params = new ArrayList();
                    params.add(DateUtil.format(yearFirstday, DatePattern.PURE_DATE_PATTERN));
                    params.add(DateUtil.format(yesterday, DatePattern.PURE_DATE_PATTERN));
                    params.add(cid);
                    params.add(mn);
                    yearBeginData = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(sql, params));
                    if (yearBeginData != null && yearBeginData.size() > 0) {
                        year_begin_data = ((Map)yearBeginData.get(0)).get("W00000_YEAR_BEGIN");
                    }
                }
            } else if (day != 1) {
                sql = "SELECT * FROM  COM_SCHEDULE WHERE STATIC_TIME=''{0}'' AND COMPANY_ID=''{1}'' AND MN=''{2}'' ";
                params = new ArrayList();
                params.add(DateUtil.format(yesterday, DatePattern.PURE_DATE_PATTERN));
                params.add(cid);
                params.add(mn);
                yesterdayData = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(sql, params));
                if (yesterdayData != null && yesterdayData.size() > 0) {
                    month_begin_data = ((Map)yesterdayData.get(0)).get("W00000_MONTH_BEGIN");
                    year_begin_data = ((Map)yesterdayData.get(0)).get("W00000_YEAR_BEGIN");
                }

                if (month_begin_data == null) {
                    sql = "SELECT * FROM  COM_SCHEDULE WHERE STATIC_TIME>=''{0}'' AND STATIC_TIME<=''{1}'' AND COMPANY_ID=''{2}'' AND MN=''{3}''   AND W00000_MONTH_BEGIN IS NOT NULL ORDER BY STATIC_TIME LIMIT 0,1";
                    params = new ArrayList();
                    params.add(DateUtil.format(monthFirstday, DatePattern.PURE_DATE_PATTERN));
                    params.add(DateUtil.format(yesterday, DatePattern.PURE_DATE_PATTERN));
                    params.add(cid);
                    params.add(mn);
                    yearBeginData = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(sql, params));
                    if (yearBeginData != null && yearBeginData.size() > 0) {
                        month_begin_data = ((Map)yearBeginData.get(0)).get("W00000_MONTH_BEGIN");
                        year_begin_data = ((Map)yearBeginData.get(0)).get("W00000_YEAR_BEGIN");
                    }
                }

                if (year_begin_data == null) {
                    sql = "SELECT * FROM  COM_SCHEDULE WHERE STATIC_TIME>=''{0}'' AND STATIC_TIME<=''{1}'' AND COMPANY_ID=''{2}'' AND MN=''{3}''   AND W00000_YEAR_BEGIN IS NOT NULL ORDER BY STATIC_TIME LIMIT 0,1";
                    params = new ArrayList();
                    params.add(DateUtil.format(yearFirstday, DatePattern.PURE_DATE_PATTERN));
                    params.add(DateUtil.format(yesterday, DatePattern.PURE_DATE_PATTERN));
                    params.add(cid);
                    params.add(mn);
                    yearBeginData = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(sql, params));
                    if (yearBeginData != null && yearBeginData.size() > 0) {
                        year_begin_data = ((Map)yearBeginData.get(0)).get("W00000_YEAR_BEGIN");
                    }
                }
            }
        }

        return new Object[]{month_begin_data, year_begin_data};
    }

    public Map<String, Object> getLastCouScheduleData(Date today, String cid, String mn, String factorCode, int type) {
        String tableName = "COM_SCHEDULE";
        if (FactorType.WATER.TYPE() == type) {
            tableName = "COM_SCHEDULE";
        } else if (FactorType.AIR.TYPE() == type) {
            tableName = "COM_SCHEDULE_AIR";
        } else if (FactorType.VOCS.TYPE() == type) {
            tableName = "COM_SCHEDULE_VOC";
        }

        Map<String, Object> lastData = null;
        int day = today.getDate();
        int month = today.getMonth();
        Date yesterday = CommonsUtil.day(today, -1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(yesterday);
        calendar.set(5, 1);
        Date monthFirstday = calendar.getTime();
        calendar.set(2, 1);
        Date yearFirstday = calendar.getTime();
        if (day != 1 || month != 1) {
            String sql;
            ArrayList params;
            List yesterdayData;
            List lastDataThisYear;
            if (day == 1 && month != 1) {
                sql = "SELECT * FROM  " + tableName + " WHERE STATIC_TIME=''{0}'' AND COMPANY_ID=''{1}'' AND MN=''{2}'' AND " + factorCode + "_DAY_COU IS NOT NULL ";
                params = new ArrayList();
                params.add(DateUtil.format(yesterday, DatePattern.PURE_DATE_PATTERN));
                params.add(cid);
                params.add(mn);
                yesterdayData = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(sql, params));
                if (yesterdayData != null && yesterdayData.size() > 0) {
                    lastData = (Map)yesterdayData.get(0);
                }

                if (lastData == null) {
                    sql = "SELECT * FROM  " + tableName + " WHERE STATIC_TIME>=''{0}'' AND STATIC_TIME<=''{1}'' AND COMPANY_ID=''{2}'' AND MN=''{3}'' AND " + factorCode + "_DAY_COU IS NOT NULL " + " ORDER BY STATIC_TIME DESC LIMIT 0,1";
                    params = new ArrayList();
                    params.add(DateUtil.format(yearFirstday, DatePattern.PURE_DATE_PATTERN));
                    params.add(DateUtil.format(yesterday, DatePattern.PURE_DATE_PATTERN));
                    params.add(cid);
                    params.add(mn);
                    lastDataThisYear = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(sql, params));
                    if (lastDataThisYear != null && lastDataThisYear.size() > 0) {
                        lastData = (Map)lastDataThisYear.get(0);
                    }
                }
            } else if (day != 1) {
                sql = "SELECT * FROM  " + tableName + " WHERE STATIC_TIME=''{0}'' AND COMPANY_ID=''{1}'' AND MN=''{2}'' AND " + factorCode + "_DAY_COU IS NOT NULL ";
                params = new ArrayList();
                params.add(DateUtil.format(yesterday, DatePattern.PURE_DATE_PATTERN));
                params.add(cid);
                params.add(mn);
                yesterdayData =this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(sql, params));;
                if (yesterdayData != null && yesterdayData.size() > 0) {
                    lastData = (Map)yesterdayData.get(0);
                }

                if (lastData == null) {
                    sql = "SELECT * FROM  " + tableName + " WHERE STATIC_TIME>=''{0}'' AND STATIC_TIME<=''{1}'' AND COMPANY_ID=''{2}'' AND MN=''{3}'' AND " + factorCode + "_DAY_COU IS NOT NULL " + " ORDER BY STATIC_TIME DESC LIMIT 0,1";
                    params = new ArrayList();
                    params.add(DateUtil.format(monthFirstday, DatePattern.PURE_DATE_PATTERN));
                    params.add(DateUtil.format(yesterday, DatePattern.PURE_DATE_PATTERN));
                    params.add(cid);
                    params.add(mn);
                    lastDataThisYear =this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(sql, params));;
                    if (lastDataThisYear != null && lastDataThisYear.size() > 0) {
                        lastData = (Map)lastDataThisYear.get(0);
                    }
                }

                if (lastData == null) {
                    sql = "SELECT * FROM  " + tableName + " WHERE STATIC_TIME>=''{0}'' AND STATIC_TIME<=''{1}'' AND COMPANY_ID=''{2}'' AND MN=''{3}'' AND " + factorCode + "_DAY_COU IS NOT NULL " + " ORDER BY STATIC_TIME DESC LIMIT 0,1";
                    params = new ArrayList();
                    params.add(DateUtil.format(yearFirstday, DatePattern.PURE_DATE_PATTERN));
                    params.add(DateUtil.format(yesterday, DatePattern.PURE_DATE_PATTERN));
                    params.add(cid);
                    params.add(mn);
                    lastDataThisYear =this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(sql, params));;
                    if (lastDataThisYear != null && lastDataThisYear.size() > 0) {
                        lastData = (Map)lastDataThisYear.get(0);
                    }
                }
            }
        }

        return lastData;
    }

    public void saveScheduleByDayData(DataPacketBean dataPacketBean, MonitorBean monitor, int factorType) {
        Date dataTime = dataPacketBean.getDataTime();
        Date dataDay = CommonsUtil.dateParse(CommonsUtil.dateFormat(dataTime, "yyyyMMdd"), "yyyyMMdd");
        String companyId = monitor.getCompanyId();
        String mn = monitor.getMn();
        String monitorName = monitor.getMonitorName();
        String warnTime = CommonsUtil.dateCurrent("yyyy-MM-dd HH:mm:ss");
        ScheduleBean schedule = null;
        Date yesterday = CommonsUtil.day(dataDay, -1);
        String month_today = CommonsUtil.dateFormat(dataDay, "yyyyMM");
        String month_yesterday = CommonsUtil.dateFormat(yesterday, "yyyyMM");
        String year_today = CommonsUtil.dateFormat(dataDay, "yyyy");
        String year_yesterday = CommonsUtil.dateFormat(yesterday, "yyyy");
        Map<String, Double> result = new HashMap();
        Map<String, FactorBean> factors = this.factorService.getFactors(factorType);
        Map<String, DataFactorBean> map = dataPacketBean.getDataMap();
        Iterator var19 = map.keySet().iterator();

        while(true) {
            String factorCode;
            DataFactorBean data;
            FactorBean factor;
            String factorName;
            DataFactorBean lastWaterCurrentData;
            do {
                do {
                    do {
                        do {
                            if (!var19.hasNext()) {
                                Map<String, Object> todayData = this.getScheduleData(dataDay, companyId, mn, factorType);
                                factorCode = null;
                                if (FactorType.WATER.TYPE() == factorType) {
                                    factorCode = "COM_SCHEDULE";
                                } else if (FactorType.AIR.TYPE() == factorType) {
                                    factorCode = "COM_SCHEDULE_AIR";
                                } else if (FactorType.VOCS.TYPE() == factorType) {
                                    factorCode = "COM_SCHEDULE_VOC";
                                }

                                StringBuffer sql;
                                if (todayData != null) {
                                    sql = new StringBuffer("");
                                    List<Object> params = new ArrayList();
                                    sql.append("UPDATE " + factorCode + " SET DATA_TIME=''{0}'' ");
                                    params.add(dataTime);
                                    Iterator var54 = result.keySet().iterator();

                                    while(var54.hasNext()) {
                                        String field = (String)var54.next();
                                        sql.append("," + field + "='"+result.get(field)+"' ");
                                    }

                                    sql.append(" where ID=''{1}'' ");
                                    params.add(todayData.get("ID"));
                                    this.myBaseMapper.sqlExcute(SqlBuilder.buildSql(sql.toString(), params));
                                } else {
                                    sql = new StringBuffer("");
                                    StringBuffer sql_value = new StringBuffer("");
                                    List<Object> params = new ArrayList();
                                    sql.append("INSERT INTO " + factorCode + " (ID,DATA_TIME,CREATE_TIME,STATIC_TIME,COMPANY_ID,MN");
                                    sql_value.append(") values(''{0}'',''{1}'',''{2}'',''{3}'',''{4}'',''{5}''");
                                    params.add(CommonsUtil.createUUID1());
                                    params.add(DateUtil.format(dataTime,DatePattern.PURE_DATE_PATTERN));
                                    params.add(DateUtil.formatDateTime(new Date()));
                                    params.add(CommonsUtil.dateFormat(dataTime, "yyyyMMdd"));
                                    params.add(companyId);
                                    params.add(mn);
                                    Iterator var58 = result.keySet().iterator();

                                    while(var58.hasNext()) {
                                        String field = (String)var58.next();
                                        sql.append("," + field);
                                        sql_value.append(",'"+result.get(field)+"'");
                                    }

                                    sql.append(sql_value).append(")");
                                    this.myBaseMapper.sqlExcute(SqlBuilder.buildSql(sql.toString(), params));
                                }

                                return;
                            }

                            factorCode = (String)var19.next();
                        } while(!this.updateTableFieldTask.isFieldExist(factorCode, factorType));

                        data = map.get(factorCode);
                        factor = factors.get(factorCode);
                    } while(factor == null);
                } while(factor.getTotalFlag() != 1);

                factorName = factor.getName();
                if (!"W00000".equals(factorCode)) {
                    break;
                }

                lastWaterCurrentData = this.monitorDeviceService.getCurrentData(mn, "W00000");
            } while(lastWaterCurrentData != null && lastWaterCurrentData.getTotal() != null);

            Map<String, Object> lastData = this.getLastCouScheduleData(dataDay, companyId, mn, factorCode, factorType);
            double cou_day = 0.0D;
            if (factor.getZsFlag() == 1) {
                if (data.getZsCouState() != null && data.getZsCouState() == 9) {
                    cou_day = data.getZsCou();
                }
            } else if (data.getCouState() != null && data.getCouState() == 9) {
                cou_day = data.getCou();
            }

            if (cou_day < 0.0D) {
                cou_day = 0.0D;
            }

            double cou_month = cou_day;
            double cou_year = cou_day;
            if (lastData != null) {
                Object year_data;
                if (month_today.equals(month_yesterday)) {
                    year_data = lastData.get(factorCode + "_MONTH_COU");
                    if (year_data != null) {
                        cou_month = CommonsUtil.numberFormat((Double)year_data + cou_day, 4);
                    }
                }

                if (year_today.equals(year_yesterday)) {
                    year_data = lastData.get(factorCode + "_YEAR_COU");
                    if (year_data != null) {
                        cou_year = CommonsUtil.numberFormat((Double)year_data + cou_day, 4);
                    }
                }
            }

            double process_day = 0.0D;
            double process_month = 0.0D;
            double process_year = 0.0D;
            schedule = this.companyService.getCompanySchedule(companyId, factorCode);
            if (schedule != null) {
                double dayTotal = schedule.getDay();
                double monthTotal = schedule.getMonth(dataDay);
                double yearTotal = schedule.getYear();
                if (dayTotal > 0.0D) {
                    process_day = CommonsUtil.numberFormat(cou_day / dayTotal, 4);
                }

                int companyTotalStatus = 9;
                List warnRuleList;
                int j;
                double yearTotalMax;
                WarnRuleBean yearWarnRule;
                String warnMessage;
                if (monthTotal > 0.0D) {
                    process_month = CommonsUtil.numberFormat(cou_month / monthTotal, 4);
                    warnRuleList = this.warnService.getTotalMonthWarnRule(companyId, factorType, factorCode);
                    if (warnRuleList != null) {
                        for(j = 0; j < warnRuleList.size(); ++j) {
                            yearTotalMax = ((WarnRuleBean)warnRuleList.get(j)).getMax() / 100.0D;
                            yearWarnRule = (WarnRuleBean)warnRuleList.get(j);
                            if (yearTotalMax >= 0.0D && process_month > yearTotalMax) {
                                companyTotalStatus = yearWarnRule.getLevel();
                                warnMessage = "排口[" + monitorName + "]污染因子[" + factorName + "]于" + warnTime + "月排污进度已达" + process_month * 100.0D + "%,当前设置报警阈值为" + yearTotalMax * 100.0D + "%";
                                //TODO 111
                                //this.warnService.checkWarnLog(dataTime, yearWarnRule, 5, warnMessage, monitor, factorCode);
                                break;
                            }
                        }
                    }
                }

                if (yearTotal > 0.0D) {
                    process_year = CommonsUtil.numberFormat(cou_year / yearTotal, 4);
                    warnRuleList = this.warnService.getTotalYearWarnRule(companyId, factorType, factorCode);
                    if (warnRuleList != null) {
                        for(j = 0; j < warnRuleList.size(); ++j) {
                            yearTotalMax = ((WarnRuleBean)warnRuleList.get(j)).getMax() / 100.0D;
                            yearWarnRule = (WarnRuleBean)warnRuleList.get(j);
                            if (yearTotalMax >= 0.0D && process_year > yearTotalMax) {
                                if (yearWarnRule.getLevel() < companyTotalStatus) {
                                    companyTotalStatus = yearWarnRule.getLevel();
                                }

                                warnMessage = "排口[" + monitorName + "]污染因子[" + factorName + "]于" + warnTime + "年排污进度已达" + process_year * 100.0D + "%,当前设置报警阈值为" + yearTotalMax * 100.0D + "%";
                                //TODO 111
                                //this.warnService.checkWarnLog(dataTime, yearWarnRule, 5, warnMessage, monitor, factorCode);
                                break;
                            }
                        }
                    }
                }

                this.companyService.setTotalStatus(companyId, companyTotalStatus);
            }

            result.put(factorCode + "_DAY_COU", cou_day);
            result.put(factorCode + "_DAY_PROCESS", process_day);
            result.put(factorCode + "_MONTH_COU", cou_month);
            result.put(factorCode + "_MONTH_PROCESS", process_month);
            result.put(factorCode + "_YEAR_COU", cou_year);
            result.put(factorCode + "_YEAR_PROCESS", process_year);
        }
    }
}