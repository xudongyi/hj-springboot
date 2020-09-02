package business.processor.task;

import business.ienum.FactorType;
import business.processor.mapper.MonitorMapper;
import business.receiver.mapper.MyBaseMapper;
import business.util.CommonsUtil;
import business.util.MathCalcUtil;
import business.util.SqlBuilder;
import cn.hutool.core.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AirQMonthTask {
    @Autowired
    private UpdateTableFieldTask updateTableFieldTask;

    @Autowired(required = true)
    private MonitorMapper monitorMapper;

    @Autowired
    private MyBaseMapper myBaseMapper;

    public AirQMonthTask() {
    }

    @Scheduled(cron = "0 0 1 1 * ?")
    @Async
    public void run() {
        int month = DateUtil.month(new Date()) + 1;
        this.saveData(1, "airq_month", "month");
        if (month == 1) {
            this.saveData(12, "airq_year", "year");
        }
        if ((month - 1) % 3 == 0) {
            this.saveData(3, "airq_quarter", "quarter");
        }

    }

    private void saveData(int month, String tableName, String column) {
        int factorType = FactorType.AIRQ.TYPE();
        Date end = CommonsUtil.dateParse(CommonsUtil.dateCurrent("yyyy-MM-dd"), "yyyy-MM-dd");
        Date begin = CommonsUtil.dateParse(CommonsUtil.month(0 - month), "yyyy-MM-dd");
        String endStr = DateUtil.format(end, "yyyy-MM-dd 00:00:00");
        String beginStr = DateUtil.format(begin, "yyyy-MM-dd 00:00:00");
        String year = CommonsUtil.dateCurrent("yyyy");
        String lastDate = CommonsUtil.dateFormat(begin, "yyyy-MM");
        if (column.equals("year")) {
            lastDate = CommonsUtil.dateFormat(begin, "yyyy");
        }
        if (column.equals("quarter")) {
            if((month - 1) / 3==1){
                lastDate = "第一季度";
            }else if((month - 1) / 3==2){
                lastDate = "第二季度";
            }else if((month - 1) / 3==3){
                lastDate = "第三季度";
            }else if((month - 1) / 3==4){
                lastDate = "第四季度";
            }
        }

        String day_sql = "select MN,LEVEL ";
        if (this.updateTableFieldTask.isFieldExist("A0502408" , factorType)) {
            day_sql = day_sql + ",A0502408_AVG";
        }

        if (this.updateTableFieldTask.isFieldExist("A21005" , factorType)) {
            day_sql = day_sql + ",A21005_AVG";
        }

        if (this.updateTableFieldTask.isFieldExist("A21026", factorType)) {
            day_sql = day_sql + ",A21026_AVG";
        }

        if (this.updateTableFieldTask.isFieldExist("A21004" , factorType)) {
            day_sql = day_sql + ",A21004_AVG";
        }

        if (this.updateTableFieldTask.isFieldExist("A3400224" , factorType)) {
            day_sql = day_sql + ",A3400224_AVG";
        }

        if (this.updateTableFieldTask.isFieldExist("A3400424" , factorType)) {
            day_sql = day_sql + ",A3400424_AVG";
        }

        day_sql = day_sql + " from airq_day where data_time >='" + beginStr + "' and data_time<'" + endStr + "'";
        String month_sql = "select * from " + tableName + "  where " + column + "='" + lastDate + "'";
        String insert_month_sql = "insert into " + tableName + "(ID," + column + ",CREATE_TIME,MN,FINE_DAYS,TOTAL_I,A21026_AVG,A21004_AVG,A34002_AVG,A34004_AVG,A21005_95,A05024_90,A21026_S,A21004_S,A34002_S,A34004_S,A21005_S,A05024_S,A21026_I,A21004_I,A34002_I,A34004_I,A21005_I,A05024_I)values(''{0}'',''{1}'',''{2}'',''{3}'',''{4}'',''{5}'',''{6}'',''{7}'',''{8}'',''{9}'',''{10}'',''{11}'',''{12}'',''{13}'',''{14}'',''{15}'',''{16}'',''{17}'',''{18}'',''{19}'',''{20}'',''{21}'',''{22}'',''{23}'')";
        if (column.equals("quarter")) {
            month_sql+=" and year="+year;
            insert_month_sql="insert into " + tableName + "(ID," + column + ",CREATE_TIME,MN,FINE_DAYS,TOTAL_I,A21026_AVG,A21004_AVG,A34002_AVG,A34004_AVG,A21005_95,A05024_90,A21026_S,A21004_S,A34002_S,A34004_S,A21005_S,A05024_S,A21026_I,A21004_I,A34002_I,A34004_I,A21005_I,A05024_I,YEAR)values(''{0}'',''{1}'',''{2}'',''{3}'',''{4}'',''{5}'',''{6}'',''{7}'',''{8}'',''{9}'',''{10}'',''{11}'',''{12}'',''{13}'',''{14}'',''{15}'',''{16}'',''{17}'',''{18}'',''{19}'',''{20}'',''{21}'',''{22}'',''{23}'',''{24}'')";
        }
        List<Object> params = new ArrayList<>();
        List<Map<String, Object>> month_data = this.myBaseMapper.sqlQuery(month_sql);
        if (month_data == null || month_data.size() == 0) {
            List<Map<String, Object>> day_data = this.myBaseMapper.sqlQuery(day_sql);
            Map<String, List<Double>> o3_data = new HashMap();
            Map<String, List<Double>> co_data = new HashMap();
            Map<String, List<Double>> so2_data = new HashMap();
            Map<String, List<Double>> no2_data = new HashMap();
            Map<String, List<Double>> pm10_data = new HashMap();
            Map<String, List<Double>> pm2_5_data = new HashMap();
            Map<String, Integer> fineDays = new HashMap();
            if (day_data != null) {
                double o3_c;
                double co;
                double so2;
                double no2;
                double pm10;
                double pm2_5;
                for (int i = 0; i < day_data.size(); ++i) {
                    Map<String, Object> data = (Map) day_data.get(i);
                    String mn = (String) data.get("MN");
                    String level = (String) data.get("LEVEL");
                    o3_c = 0.0D;
                    if (data.get("A0502408_AVG") != null) {
                        o3_c = (Double) data.get("A0502408_AVG");
                    }

                    if (o3_data.containsKey(mn)) {
                        ((List) o3_data.get(mn)).add(o3_c);
                    } else {
                        List<Double> o3_add = new ArrayList();
                        o3_add.add(o3_c);
                        o3_data.put(mn, o3_add);
                    }

                    co = 0.0D;
                    if (data.get("A21005_AVG") != null) {
                        co = (Double) data.get("A21005_AVG");
                    }

                    if (co_data.containsKey(mn)) {
                        ((List) co_data.get(mn)).add(co);
                    } else {
                        List<Double> co_add = new ArrayList();
                        co_add.add(co);
                        co_data.put(mn, co_add);
                    }

                    so2 = 0.0D;
                    if (data.get("A21026_AVG") != null) {
                        so2 = (Double) data.get("A21026_AVG");
                    }

                    if (so2_data.containsKey(mn)) {
                        ((List) so2_data.get(mn)).add(so2);
                    } else {
                        List<Double> so2_add = new ArrayList();
                        so2_add.add(so2);
                        so2_data.put(mn, so2_add);
                    }

                    no2 = 0.0D;
                    if (data.get("A21004_AVG") != null) {
                        no2 = (Double) data.get("A21004_AVG");
                    }

                    if (no2_data.containsKey(mn)) {
                        ((List) no2_data.get(mn)).add(no2);
                    } else {
                        List<Double> no2_add = new ArrayList();
                        no2_add.add(no2);
                        no2_data.put(mn, no2_add);
                    }

                    pm10 = 0.0D;
                    if (data.get("A3400224_AVG") != null) {
                        pm10 = (Double) data.get("A3400224_AVG");
                    }

                    if (pm10_data.containsKey(mn)) {
                        ((List) pm10_data.get(mn)).add(pm10);
                    } else {
                        List<Double> pm10_add = new ArrayList();
                        pm10_add.add(pm10);
                        pm10_data.put(mn, pm10_add);
                    }

                    pm2_5 = 0.0D;
                    if (data.get("A3400424_AVG") != null) {
                        pm2_5 = (Double) data.get("A3400424_AVG");
                    }

                    if (pm2_5_data.containsKey(mn)) {
                        pm2_5_data.get(mn).add(pm2_5);
                    } else {
                        List<Double> pm2_5_add = new ArrayList();
                        pm2_5_add.add(pm2_5);
                        pm2_5_data.put(mn, pm2_5_add);
                    }

                    if (level.equals("1") || level.equals("2")) {
                        if (fineDays.containsKey(mn)) {
                            fineDays.put(mn, fineDays.get(mn) + 1);
                        } else {
                            fineDays.put(mn, 1);
                        }
                    }
                }

                if (pm2_5_data.size() > 0) {
                    Iterator var59 = pm2_5_data.keySet().iterator();

                    while (var59.hasNext()) {
                        String mn = (String) var59.next();
                        double o3_s = 160.0D;
                        o3_c = MathCalcUtil.percentile((List) o3_data.get(mn), 90.0D);
                        co = CommonsUtil.numberFormat(o3_c / o3_s);
                        so2 = 60.0D;
                        no2 = MathCalcUtil.avg((List) so2_data.get(mn), 2);
                        pm10 = CommonsUtil.numberFormat(no2 / so2);
                        pm2_5 = 40.0D;
                        double no2_c = MathCalcUtil.avg((List) no2_data.get(mn), 2);
                        double no2_i = CommonsUtil.numberFormat(no2_c / pm2_5);
                        double co_s = 4.0D;
                        double co_c = MathCalcUtil.percentile((List) co_data.get(mn), 95.0D);
                        double co_i = CommonsUtil.numberFormat(co_c / co_s);
                        double pm2_5_s = 35.0D;
                        double pm2_5_c = MathCalcUtil.avg((List) pm2_5_data.get(mn), 2);
                        double pm2_5_i = CommonsUtil.numberFormat(pm2_5_c / pm2_5_s);
                        double pm10_s = 70.0D;
                        double pm10_c = MathCalcUtil.avg((List) pm10_data.get(mn), 2);
                        double pm10_i = CommonsUtil.numberFormat(pm10_c / pm10_s);
                        double total_i = CommonsUtil.numberFormat(co + pm10 + no2_i + co_i + pm2_5_i + pm10_i);
                        int finedays = 0;
                        if (fineDays.get(mn) != null) {
                            finedays = (Integer) fineDays.get(mn);
                        }

                        params.add(CommonsUtil.createUUID1());
                        params.add(lastDate);
                        params.add(DateUtil.formatDateTime(new Date()));
                        params.add(mn);
                        params.add(finedays);
                        params.add(total_i);
                        params.add(no2);
                        params.add(no2_c);
                        params.add(pm10_c);
                        params.add(pm2_5_c);
                        params.add(co_c);
                        params.add(o3_c);
                        params.add(so2);
                        params.add(pm2_5);
                        params.add(pm10_s);
                        params.add(pm2_5_s);
                        params.add(co_s);
                        params.add(o3_s);
                        params.add(pm10);
                        params.add(no2_i);
                        params.add(pm10_i);
                        params.add(pm2_5_i);
                        params.add(co_i);
                        params.add(co);
                        if (column.equals("quarter")) {
                            params.add(year);
                        }
                        this.myBaseMapper.sqlExcute(SqlBuilder.buildSql(insert_month_sql, params));
                        params = new ArrayList<>();
                    }
                }
            }
        }

    }


}