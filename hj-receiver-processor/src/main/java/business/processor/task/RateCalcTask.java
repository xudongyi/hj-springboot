package business.processor.task;

import business.processor.bean.FactorBean;
import business.processor.bean.WarnRuleBean;
import business.processor.service.FactorService;
import business.processor.service.MonitorService;
import business.processor.service.WarnService;
import business.receiver.bean.MonitorBean;
import business.receiver.mapper.MyBaseMapper;
import business.util.CommonsUtil;
import business.util.SqlBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class RateCalcTask {
    @Autowired
    private MonitorService monitorService;
    @Autowired
    private FactorService factorService;
    @Autowired
    private WarnService warnService;
    @Autowired
    private MyBaseMapper myBaseMapper;
    @Value("${warn.dataerror.maxmin}")
    private boolean isCheckDataError = false;

    public RateCalcTask() {
    }

    @Scheduled(
        cron = "0 30 0 * * ?"
    )
    @Async
    public void run() {
        Date endTime = CommonsUtil.dateParse(CommonsUtil.dateCurrent("yyyyMMdd"), "yyyyMMdd");
        Date startTime = CommonsUtil.day(endTime, -1);
        Map<String, MonitorBean> monitors = this.monitorService.getAllMonitors();
        if (monitors != null && monitors.size() > 0) {
            Map<String, Long> mnStopCount = this.getMonitorStopTime(monitors, startTime, endTime);
            this.calcOnlineRate(monitors, mnStopCount, startTime, endTime);
            this.calcUploadValidRate(monitors, mnStopCount, startTime, endTime);
            this.calcOverproofRate(monitors, startTime, endTime);
            this.clacDeviceRate(monitors, startTime, endTime);
        }

    }

    private void clacDeviceRate(Map<String, MonitorBean> monitors, Date yesterdayBegin, Date yesterdayEnd) {
        String deviceState_sql = "SELECT * FROM DEVICE_STATE WHERE CREATE_TIME<''{0}'' AND  END_TIME >''{1}''";
        List<Object> params = new ArrayList();
        params.add(yesterdayEnd);
        params.add(yesterdayBegin);
        List<Map<String, Object>> stateList = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(deviceState_sql, params));
        Map<String, Long[]> stateCountMap = new HashMap();
        String code;
        if (stateList != null) {
            for(int i = 0; i < stateList.size(); ++i) {
                String mn = (String)((Map)stateList.get(i)).get("MN");
                code = (String)((Map)stateList.get(i)).get("CODE");
                int state = (Integer)((Map)stateList.get(i)).get("STATE");
                Date startTime = (Date)((Map)stateList.get(i)).get("CREATE_TIME");
                Date endTime = (Date)((Map)stateList.get(i)).get("END_TIME");
                if (startTime.before(yesterdayBegin)) {
                    startTime = yesterdayBegin;
                }

                if (endTime.after(yesterdayEnd)) {
                    endTime = yesterdayEnd;
                }

                long time = endTime.getTime() - startTime.getTime();
                String key = mn + "-" + code;
                if (stateCountMap.containsKey(key)) {
                    Long[] count = (Long[])stateCountMap.get(key);
                    if (state == 0) {
                        count[0] = count[0] + time;
                    } else {
                        count[1] = count[1] + time;
                    }

                    stateCountMap.put(key, count);
                } else if (state == 0) {
                    stateCountMap.put(key, new Long[]{time, 0L});
                } else {
                    stateCountMap.put(key, new Long[]{0L, time});
                }
            }
        }

        String sql = "insert into rate_device(ID,MONITOR_ID,MONITOR_NAME,MN,MONITOR_TYPE,CODE,NAME,ERROR,NORMAL,PERCENT,DATA_TIME)values(''{0}'',''{1}'',''{2}'',''{3}'',''{4}'',''{5}'',''{6}'',''{7}'',''{8}'',''{9}'',''{10}'')";
        Iterator var25 = stateCountMap.keySet().iterator();

        while(var25.hasNext()) {
            code = (String)var25.next();
            String mn = code.split("-")[0];
            code = code.split("-")[1];
            long normalTimes = ((Long[])stateCountMap.get(code))[0];
            long errorTimes = ((Long[])stateCountMap.get(code))[1];
            double rate = 0.0D;
            int errorMin = (int)CommonsUtil.numberFormat((double)(errorTimes / 1000L / 60L), 0, 4);
            int normalMin = (int)CommonsUtil.numberFormat((double)(normalTimes / 1000L / 60L), 0, 4);
            if (errorMin + normalMin > 0) {
                rate = CommonsUtil.numberFormat((double)errorMin * 1.0D / (double)(errorMin + normalMin) * 100.0D, 2, 4);
            }

            params = new ArrayList();
            params.add(CommonsUtil.createUUID1());
            params.add(((MonitorBean)monitors.get(mn)).getMonitorId());
            params.add(((MonitorBean)monitors.get(mn)).getMonitorName());
            params.add(((MonitorBean)monitors.get(mn)).getMn());
            params.add(((MonitorBean)monitors.get(mn)).getMonitorType());
            params.add(code);
            String name = "";

            try {
                name = ((FactorBean)this.factorService.getFactors(((MonitorBean)monitors.get(mn)).getMonitorType()).get(code)).getName();
            } catch (Exception var23) {
                var23.printStackTrace();
            }

            params.add(name);
            params.add(errorMin);
            params.add(normalMin);
            params.add(rate);
            params.add(CommonsUtil.dateFormat(yesterdayBegin, "yyyy-MM-dd"));
            this.myBaseMapper.sqlExcute(SqlBuilder.buildSql(sql, params));
        }

    }

    private void calcOverproofRate(Map<String, MonitorBean> monitors, Date yesterdayBegin, Date yesterdayEnd) {
        Map<String, WarnRuleBean> allOverproof = this.warnService.getAllOverproofWarnRule();
        if (!allOverproof.isEmpty()) {
            String month = CommonsUtil.dateFormat(yesterdayBegin, "yyMM");
            String over_rate_sql = "insert into rate_overproof(ID,MONITOR_ID,MONITOR_NAME,MN,MONITOR_TYPE,CODE,NAME,COUNT,TOTAL,PERCENT,DATA_TIME)VALUES(''{0}'',''{1}'',''{2}'',''{3}'',''{4}'',''{5}''" +
                    ",''{6}'',''{7}'',''{8}'',''{9}'',''{10}'')";
            Map<String, Long> overCountMap = new HashMap();
            String water_overproof_sql = "SELECT COUNT(*)COUNTS,MN,CODE FROM WATER_CURRENT_OVERPROOF  WHERE DATA_TIME>=? AND DATA_TIME<? AND (STATUS=1 OR STATUS=-1) GROUP BY MN,CODE";
            List<Object> params = new ArrayList();
            params.add(yesterdayBegin);
            params.add(yesterdayEnd);
            List<Map<String, Object>> water_overproof_data = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(water_overproof_sql, params));
            String voc_overproof_sql;
            String mn;
            if (water_overproof_data != null) {
                for(int i = 0; i < water_overproof_data.size(); ++i) {
                    mn = (String)((Map)water_overproof_data.get(i)).get("MN");
                    voc_overproof_sql = (String)((Map)water_overproof_data.get(i)).get("CODE");
                    mn = mn + "-" + voc_overproof_sql;
                    long overproof_counts = (Long)((Map)water_overproof_data.get(i)).get("COUNTS");
                    overCountMap.put(mn, overproof_counts);
                }
            }

            String air_overproof_sql = "SELECT COUNT(*)COUNTS,MN,CODE FROM AIR_CURRENT_OVERPROOF  WHERE DATA_TIME>=''{0}'' AND DATA_TIME<''{1}'' AND (STATUS=1 OR STATUS=-1) GROUP BY MN,CODE";
            params = new ArrayList();
            params.add(yesterdayBegin);
            params.add(yesterdayEnd);
            List<Map<String, Object>> air_overproof_data = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(air_overproof_sql, params));
            String mnCode;
            if (air_overproof_data != null) {
                for(int i = 0; i < air_overproof_data.size(); ++i) {
                    mn = (String)((Map)air_overproof_data.get(i)).get("MN");
                    String code = (String)((Map)air_overproof_data.get(i)).get("CODE");
                    mnCode = mn + "-" + code;
                    long overproof_counts = (Long)((Map)air_overproof_data.get(i)).get("COUNTS");
                    overCountMap.put(mnCode, overproof_counts);
                }
            }

            voc_overproof_sql = "SELECT COUNT(*)COUNTS,MN,CODE FROM VOC_CURRENT_OVERPROOF  WHERE DATA_TIME>=''{0}'' AND DATA_TIME<''{1}'' AND (STATUS=1 OR STATUS=-1) GROUP BY MN,CODE";
            params = new ArrayList();
            params.add(yesterdayBegin);
            params.add(yesterdayEnd);
            List<Map<String, Object>> voc_overproof_data = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(voc_overproof_sql, params));
            String code;
            long overs;
            if (voc_overproof_data != null) {
                for(int i = 0; i < voc_overproof_data.size(); ++i) {
                    mnCode = (String)((Map)voc_overproof_data.get(i)).get("MN");
                    mn = (String)((Map)voc_overproof_data.get(i)).get("CODE");
                    code = mnCode + "-" + mn;
                    overs = (Long)((Map)voc_overproof_data.get(i)).get("COUNTS");
                    overCountMap.put(code, overs);
                }
            }

            Iterator var34 = allOverproof.keySet().iterator();

            while(var34.hasNext()) {
                mnCode = (String)var34.next();
                mn = mnCode.split("-")[0];
                code = mnCode.split("-")[1];
                overs = 0L;
                if (overCountMap.get(mnCode) != null) {
                    overs = (Long)overCountMap.get(mnCode);
                }

                long total = 0L;
                String current_data_sql = null;
                Map<String, FactorBean> factors = null;
                MonitorBean monitor = (MonitorBean)monitors.get(mn);
                if (monitor != null) {
                    if (1 == monitor.getMonitorType()) {
                        current_data_sql = "SELECT * FROM WATER_CURRENT_TR_" + month + " WHERE MN=? AND DATA_TIME>=''{0}''  AND DATA_TIME<''{1}''";
                        factors = this.factorService.getFactors(1);
                    } else if (2 == monitor.getMonitorType()) {
                        current_data_sql = "SELECT * FROM AIR_CURRENT_TR_" + month + " WHERE MN=? AND DATA_TIME>=''{0}''  AND DATA_TIME<''{1}''";
                        factors = this.factorService.getFactors(2);
                    } else if (9 == monitor.getMonitorType()) {
                        current_data_sql = "SELECT * FROM VOC_CURRENT_TR_" + month + " WHERE MN=? AND DATA_TIME>=''{0}''  AND DATA_TIME<''{1}''";
                        factors = this.factorService.getFactors(9);
                    }
                }

                if (current_data_sql != null) {
                    params = new ArrayList();
                    params.add(mn);
                    params.add(yesterdayBegin);
                    params.add(yesterdayEnd);
                    List<Map<String, Object>> current_data = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(current_data_sql, params));
                    if (current_data != null) {
                        for(int i = 0; i < current_data.size(); ++i) {
                            if (((Map)current_data.get(i)).get(code.toUpperCase() + "_RTD") != null) {
                                ++total;
                            }
                        }
                    }
                }

                if (total > 0L) {
                    double rate = CommonsUtil.numberFormat((double)overs * 1.0D / (double)total * 100.0D, 2, 4);
                    params = new ArrayList();
                    params.add(CommonsUtil.createUUID1());
                    params.add(monitor.getMonitorId());
                    params.add(monitor.getMonitorName());
                    params.add(mn);
                    params.add(monitor.getMonitorType());
                    params.add(code);
                    params.add((factors.get(code)).getName());
                    params.add(overs);
                    params.add(total);
                    params.add(rate);
                    params.add(CommonsUtil.dateFormat(yesterdayBegin, "yyyy-MM-dd"));
                    this.myBaseMapper.sqlExcute(SqlBuilder.buildSql(over_rate_sql, params));
                }
            }

        }
    }

    private void calcUploadValidRate(Map<String, MonitorBean> monitors, Map<String, Long> mnStopCount, Date yesterdayBegin, Date yesterdayEnd) {
        String water_hour_sql = "SELECT * FROM WATER_HOUR WHERE DATA_TIME>=''{0}'' AND DATA_TIME<''{1}''";
        List<Object> params = new ArrayList();
        params.add(yesterdayBegin);
        params.add(yesterdayEnd);
        List<Map<String, Object>> hour_data = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(water_hour_sql, params));
        String water_day_sql = "SELECT * FROM WATER_DAY WHERE DATA_TIME>=''{0}'' AND DATA_TIME<''{1}'' ";
        params = new ArrayList();
        params.add(yesterdayBegin);
        params.add(yesterdayEnd);
        List<Map<String, Object>> day_data = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(water_day_sql, params));
        String air_hour_sql = "SELECT * FROM AIR_HOUR WHERE DATA_TIME>=''{0}'' AND DATA_TIME<''{1}''";
        params = new ArrayList();
        params.add(yesterdayBegin);
        params.add(yesterdayEnd);
        List<Map<String, Object>> air_hour_data = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(air_hour_sql, params));
        hour_data.addAll(air_hour_data);
        String air_day_sql = "SELECT * FROM AIR_DAY WHERE DATA_TIME>=''{0}'' AND DATA_TIME<''{1}''";
        params = new ArrayList();
        params.add(yesterdayBegin);
        params.add(yesterdayEnd);
        List<Map<String, Object>> air_day_data = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(air_day_sql, params));
        day_data.addAll(air_day_data);
        String voc_hour_sql = "SELECT * FROM VOC_HOUR WHERE DATA_TIME>=''{0}'' AND DATA_TIME<''{1}''";
        params = new ArrayList();
        params.add(yesterdayBegin);
        params.add(yesterdayEnd);
        List<Map<String, Object>> voc_hour_data = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(voc_hour_sql, params));
        hour_data.addAll(voc_hour_data);
        String voc_day_sql = "SELECT * FROM VOC_DAY WHERE DATA_TIME>=''{0}'' AND DATA_TIME<''{1}'' ";
        params = new ArrayList();
        params.add(yesterdayBegin);
        params.add(yesterdayEnd);
        List<Map<String, Object>> voc_day_data = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(voc_day_sql, params));
        day_data.addAll(voc_day_data);
        String airq_hour_sql = "SELECT * FROM AIRQ_HOUR WHERE DATA_TIME>=? AND DATA_TIME<?";
        params = new ArrayList();
        params.add(yesterdayBegin);
        params.add(yesterdayEnd);
        List<Map<String, Object>> airq_hour_data = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(airq_hour_sql, params));
        hour_data.addAll(airq_hour_data);
        String airq_day_sql = "SELECT * FROM AIRQ_DAY WHERE DATA_TIME>=''{0}'' AND DATA_TIME<''{1}'' ";
        params = new ArrayList();
        params.add(yesterdayBegin);
        params.add(yesterdayEnd);
        List<Map<String, Object>> airq_day_data = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(airq_day_sql, params));
        day_data.addAll(airq_day_data);
        String noise_hour_sql = "SELECT * FROM NOISE_HOUR WHERE DATA_TIME>=? AND DATA_TIME<?";
        params = new ArrayList();
        params.add(yesterdayBegin);
        params.add(yesterdayEnd);
        List<Map<String, Object>> noise_hour_data = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(noise_hour_sql, params));
        hour_data.addAll(noise_hour_data);
        String noise_day_sql = "SELECT * FROM NOISE_DAY WHERE DATA_TIME>=''{0}'' AND DATA_TIME<''{1}'' ";
        params = new ArrayList();
        params.add(yesterdayBegin);
        params.add(yesterdayEnd);
        List<Map<String, Object>> noise_day_data = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(noise_day_sql, params));
        day_data.addAll(noise_day_data);
        String surfwater_hour_sql = "SELECT * FROM SURFWATER_HOUR WHERE DATA_TIME>=''{0}'' AND DATA_TIME<''{1}''";
        params = new ArrayList();
        params.add(yesterdayBegin);
        params.add(yesterdayEnd);
        List<Map<String, Object>> surfwater_hour_data = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(surfwater_hour_sql, params));
        hour_data.addAll(surfwater_hour_data);
        String surfwater_day_sql = "SELECT * FROM SURFWATER_DAY WHERE DATA_TIME>=''{0}'' AND DATA_TIME<''{1}'' ";
        params = new ArrayList();
        params.add(yesterdayBegin);
        params.add(yesterdayEnd);
        List<Map<String, Object>> surfwater_day_data = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(surfwater_day_sql, params));
        day_data.addAll(surfwater_day_data);
        Map<String, Integer> uploadCount = new HashMap();
        Map<String, Integer> validCount = new HashMap();
        int i;
        Map data;
        String mn;
        int state;
        if (hour_data != null) {
            for(i = 0; i < hour_data.size(); ++i) {
                data = (Map)hour_data.get(i);
                mn = (String)data.get("MN");
                if (uploadCount.containsKey(mn)) {
                    uploadCount.put(mn, (Integer)uploadCount.get(mn) + 1);
                } else {
                    uploadCount.put(mn, 1);
                }

                boolean isValid = true;
                Iterator var36 = data.keySet().iterator();

                label99: {
                    do {
                        String field;
                        do {
                            do {
                                if (!var36.hasNext()) {
                                    break label99;
                                }

                                field = (String)var36.next();
                            } while(!field.endsWith("_STATE"));
                        } while(data.get(field) == null);

                        state = (Integer)data.get(field);
                    } while(state != 8 && state != -8 && state != 6 && state != -6 && (!this.isCheckDataError || state != 7));

                    isValid = false;
                }

                if (isValid) {
                    if (validCount.containsKey(mn)) {
                        validCount.put(mn, (Integer)validCount.get(mn) + 1);
                    } else {
                        validCount.put(mn, 1);
                    }
                }
            }
        }

        if (day_data != null) {
            for(i = 0; i < day_data.size(); ++i) {
                data = (Map)day_data.get(i);
                mn = (String)data.get("MN");
                if (uploadCount.containsKey(mn)) {
                    uploadCount.put(mn, (Integer)uploadCount.get(mn) + 1);
                } else {
                    uploadCount.put(mn, 1);
                }
            }
        }

        String sql_upload = "INSERT INTO RATE_UPLOAD(ID,MONITOR_ID,MONITOR_NAME,MN,MONITOR_TYPE,COUNT,TOTAL,PERCENT,DATA_TIME)VALUES(''{0}'',''{1}'',''{2}'',''{3}'',''{4}'',''{5}'',''{6}'',''{7}'',''{8}'')";
        String sql_valid = "INSERT INTO RATE_VALID(ID,MONITOR_ID,MONITOR_NAME,MN,MONITOR_TYPE,COUNT,TOTAL,PERCENT,DATA_TIME)VALUES(''{0}'',''{1}'',''{2}'',''{3}'',''{4}'',''{5}'',''{6}'',''{7}'',''{8}'')";
        Iterator var49 = mnStopCount.keySet().iterator();

        while(var49.hasNext()) {
            mn = (String)var49.next();
            long stopTime = (Long)mnStopCount.get(mn);
            state = (int)CommonsUtil.numberFormat((double)(stopTime / 1000L / 60L / 60L), 0, 4);
            int uploads = 0;
            if (uploadCount.get(mn) != null) {
                uploads = (Integer)uploadCount.get(mn);
            }

            int valids = 0;
            if (validCount.get(mn) != null) {
                valids = (Integer)validCount.get(mn);
            }

            int total_upload = 25 - state;
            int total_valid = 24 - state;
            double rate_upload = 0.0D;
            double rate_valid = 0.0D;
            if (total_upload > 0) {
                rate_upload = CommonsUtil.numberFormat((double)uploads * 1.0D / (double)total_upload * 100.0D, 2, 4);
            }

            if (total_valid > 0) {
                rate_valid = CommonsUtil.numberFormat((double)valids * 1.0D / (double)total_valid * 100.0D, 2, 4);
            }

            params = new ArrayList();
            params.add(CommonsUtil.createUUID1());
            params.add(((MonitorBean)monitors.get(mn)).getMonitorId());
            params.add(((MonitorBean)monitors.get(mn)).getMonitorName());
            params.add(((MonitorBean)monitors.get(mn)).getMn());
            params.add(((MonitorBean)monitors.get(mn)).getMonitorType());
            params.add(uploads);
            params.add(total_upload);
            params.add(rate_upload);
            params.add(CommonsUtil.dateFormat(yesterdayBegin, "yyyy-MM-dd"));
            this.myBaseMapper.sqlExcute(SqlBuilder.buildSql(sql_upload, params));
            params = new ArrayList();
            params.add(CommonsUtil.createUUID1());
            params.add(((MonitorBean)monitors.get(mn)).getMonitorId());
            params.add(((MonitorBean)monitors.get(mn)).getMonitorName());
            params.add(((MonitorBean)monitors.get(mn)).getMn());
            params.add(((MonitorBean)monitors.get(mn)).getMonitorType());
            params.add(valids);
            params.add(total_valid);
            params.add(rate_valid);
            params.add(CommonsUtil.dateFormat(yesterdayBegin, "yyyy-MM-dd"));
            this.myBaseMapper.sqlExcute(SqlBuilder.buildSql(sql_valid, params));
        }

    }

    private void calcOnlineRate(Map<String, MonitorBean> monitors, Map<String, Long> mnStopCount, Date yesterdayBegin, Date yesterdayEnd) {
        String water_offlinesql = "SELECT * FROM WATER_OFFLINE WHERE START_TIME <''{0}'' AND END_TIME > ''{1}''";
        List<Object> params = new ArrayList();
        params.add(yesterdayEnd);
        params.add(yesterdayBegin);
        List<Map<String, Object>> offlineList = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(water_offlinesql, params));
        String air_offlinesql = "SELECT * FROM AIR_OFFLINE WHERE START_TIME <''{0}'' AND END_TIME > ''{1}'' ";
        params = new ArrayList();
        params.add(yesterdayEnd);
        params.add(yesterdayBegin);
        List<Map<String, Object>> air_offlineList = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(air_offlinesql, params));
        String voc_offlinesql = "SELECT * FROM VOC_OFFLINE WHERE START_TIME <''{0}'' AND END_TIME > ''{1}'' ";
        params = new ArrayList();
        params.add(yesterdayEnd);
        params.add(yesterdayBegin);
        List<Map<String, Object>> voc_offlineList = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(voc_offlinesql, params));
        offlineList.addAll(air_offlineList);
        offlineList.addAll(voc_offlineList);
        Map<String, Long> mnOfflineCount = new HashMap();
        if (offlineList != null) {
            for(int i = 0; i < offlineList.size(); ++i) {
                String mn = (String)((Map)offlineList.get(i)).get("MN");
                Date startTime = (Date)((Map)offlineList.get(i)).get("START_TIME");
                Date endTime = (Date)((Map)offlineList.get(i)).get("END_TIME");
                if (startTime.before(yesterdayBegin)) {
                    startTime = yesterdayBegin;
                }

                if (endTime.after(yesterdayEnd)) {
                    endTime = yesterdayEnd;
                }

                long time = endTime.getTime() - startTime.getTime();
                if (mnOfflineCount.containsKey(mn)) {
                    mnOfflineCount.put(mn, (Long)mnOfflineCount.get(mn) + time);
                } else {
                    mnOfflineCount.put(mn, time);
                }
            }
        }

        String sql = "INSERT INTO RATE_ONLINE(ID,MONITOR_ID,MONITOR_NAME,MN,MONITOR_TYPE,DATA_TIME,ONLINE_TIME,OFFLINE_TIME,STOP_TIME,ONLINE_RATE)VALUES(''{0}'',''{1}'',''{2}'',''{3}'',''{4}'',''{5}'',''{6}'',''{7}'',''{8}'',''{9}'')";
        Iterator var23 = mnStopCount.keySet().iterator();

        while(var23.hasNext()) {
            String mn = (String)var23.next();
            long offlineTime = 0L;
            if (mnOfflineCount.get(mn) != null) {
                offlineTime = (Long)mnOfflineCount.get(mn);
            }

            if (offlineTime > 86400000L) {
                offlineTime = 86400000L;
            }

            long stopTime = (Long)mnStopCount.get(mn);
            double rate = (double)(86400000L - offlineTime) * 1.0D / (double)(86400000L - stopTime);
            if (rate > 1.0D) {
                rate = 1.0D;
            }

            rate = CommonsUtil.numberFormat(rate * 100.0D);
            params = new ArrayList();
            params.add(CommonsUtil.createUUID1());
            params.add(((MonitorBean)monitors.get(mn)).getMonitorId());
            params.add(((MonitorBean)monitors.get(mn)).getMonitorName());
            params.add(((MonitorBean)monitors.get(mn)).getMn());
            params.add(((MonitorBean)monitors.get(mn)).getMonitorType());
            params.add(CommonsUtil.dateFormat(yesterdayBegin, "yyyy-MM-dd"));
            params.add((int)CommonsUtil.numberFormat((double)(1440L - offlineTime / 1000L / 60L), 0, 4));
            params.add((int)CommonsUtil.numberFormat((double)(offlineTime / 1000L / 60L), 0, 4));
            params.add((int)CommonsUtil.numberFormat((double)(stopTime / 1000L / 60L), 0, 4));
            params.add(rate);
            this.myBaseMapper.sqlExcute(SqlBuilder.buildSql(sql, params));
        }

    }

    private Map<String, Long> getMonitorStopTime(Map<String, MonitorBean> monitors, Date yesterdayBegin, Date yesterdayEnd) {
        Map<String, Long> result = new HashMap();
        Map<String, Long> mnStopTimeCount = new HashMap();
        String sql = "SELECT * FROM MON_REPAIR_APPLY WHERE STATUS='1' AND BEGIN_TIME<''{0}'' AND END_TIME >''{1}'' ";
        List<Object> params = new ArrayList();
        params.add(yesterdayEnd);
        params.add(yesterdayBegin);
        List<Map<String, Object>> list = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(sql, params));
        String mn;
        if (list != null) {
            for(int i = 0; i < list.size(); ++i) {
                mn = (String)((Map)list.get(i)).get("MN");
                Date startTime = (Date)((Map)list.get(i)).get("BEGIN_TIME");
                Date endTime = (Date)((Map)list.get(i)).get("END_TIME");
                if (startTime != null && endTime != null) {
                    if (startTime.before(yesterdayBegin)) {
                        startTime = yesterdayBegin;
                    }

                    if (endTime.after(yesterdayEnd)) {
                        endTime = yesterdayEnd;
                    }

                    long time = endTime.getTime() - startTime.getTime();
                    if (mnStopTimeCount.containsKey(mn)) {
                        mnStopTimeCount.put(mn, (Long)mnStopTimeCount.get(mn) + time);
                    } else {
                        mnStopTimeCount.put(mn, time);
                    }
                }
            }
        }

        if (monitors != null && monitors.size() > 0) {
            Iterator var15 = monitors.keySet().iterator();

            while(var15.hasNext()) {
                mn = (String)var15.next();
                if (this.monitorService.getMnCurrentLastUpload(mn) != null) {
                    long stopTime = 0L;
                    if (mnStopTimeCount.get(mn) != null) {
                        stopTime = (Long)mnStopTimeCount.get(mn);
                    }

                    if (stopTime < 21600000L) {
                        result.put(mn, stopTime);
                    }
                }
            }
        }

        return result;
    }
}