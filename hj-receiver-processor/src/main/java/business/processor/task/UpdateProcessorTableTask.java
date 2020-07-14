package business.processor.task;

import business.constant.TableSqlConstant;
import business.receiver.mapper.MyBaseMapper;
import business.util.CommonsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.*;

@Component
public class UpdateProcessorTableTask {
    private static Map<String, MessageFormat> allTable = new HashMap();
    private static Map<String, MessageFormat> dynamicTable = new HashMap();
    private static boolean isInitial = false;
    private static boolean isUpdateTable = true;
    @Autowired
    private MyBaseMapper myBaseMapper;

    public UpdateProcessorTableTask() {
    }

    @PostConstruct
    public void initial() {
        allTable.put("WARN_LOG", TableSqlConstant.WARN_LOG_SQL);
        allTable.put("MON_REPAIR_APPLY", TableSqlConstant.MON_REPAIR_APPLY_SQL);
        allTable.put("DEVICE_STATE", TableSqlConstant.BAK_DEVICE_STATE_SQL);
        allTable.put("RATE_DEVICE", TableSqlConstant.BAK_RATE_DEVICE_SQL);
        allTable.put("RATE_ONLINE", TableSqlConstant.BAK_RATE_ONLINE_SQL);
        allTable.put("RATE_OVERPROOF", TableSqlConstant.BAK_RATE_OVERPROOF_SQL);
        allTable.put("RATE_UPLOAD", TableSqlConstant.BAK_RATE_UPLOAD_SQL);
        allTable.put("RATE_VALID", TableSqlConstant.BAK_RATE_VALID_SQL);
        allTable.put("OVERPROOF_PERIOD", TableSqlConstant.BAK_OVERPROOF_PERIOD_SQL);
        allTable.put("WATER_HOUR", TableSqlConstant.BAK_WATER_HOUR_SQL);
        allTable.put("WATER_DAY", TableSqlConstant.BAK_WATER_DAY_SQL);
        allTable.put("WATER_MONTH", TableSqlConstant.BAK_WATER_MONTH_SQL);
        allTable.put("WATER_YEAR", TableSqlConstant.BAK_WATER_YEAR_SQL);
        allTable.put("COM_SCHEDULE", TableSqlConstant.BAK_COM_SCHEDULE_SQL);
        allTable.put("WATER_OFFLINE", TableSqlConstant.BAK_WATER_OFFLINE_SQL);
        allTable.put("WATER_CURRENT_OVERPROOF", TableSqlConstant.BAK_WATER_CURRENT_OVERPROOF_SQL);
        allTable.put("TOTAL_STATISTIC", TableSqlConstant.BAK_TOTAL_STATISTIC_SQL);
        allTable.put("AIR_HOUR", TableSqlConstant.BAK_AIR_HOUR_SQL);
        allTable.put("AIR_DAY", TableSqlConstant.BAK_AIR_DAY_SQL);
        allTable.put("AIR_MONTH", TableSqlConstant.BAK_AIR_MONTH_SQL);
        allTable.put("AIR_YEAR", TableSqlConstant.BAK_AIR_YEAR_SQL);
        allTable.put("COM_SCHEDULE_AIR", TableSqlConstant.BAK_COM_SCHEDULE_AIR_SQL);
        allTable.put("AIR_OFFLINE", TableSqlConstant.BAK_AIR_OFFLINE_SQL);
        allTable.put("AIR_CURRENT_OVERPROOF", TableSqlConstant.BAK_AIR_CURRENT_OVERPROOF_SQL);
        allTable.put("AIR_INCINERATOR_CURRENT", TableSqlConstant.BAK_AIR_INCINERATOR_CURRENT_SQL);
        allTable.put("AIRQ_HOUR", TableSqlConstant.BAK_AIRQ_HOUR_SQL);
        allTable.put("AIRQ_DAY", TableSqlConstant.BAK_AIRQ_DAY_SQL);
        allTable.put("AIRQ_MONTH", TableSqlConstant.BAK_AIRQ_MONTH_SQL);
        allTable.put("AIRQ_QUARTER", TableSqlConstant.BAK_AIRQ_QUARTER_SQL);
        allTable.put("AIRQ_YEAR", TableSqlConstant.BAK_AIRQ_YEAR_SQL);
        allTable.put("NOISE_HOUR", TableSqlConstant.BAK_NOISE_HOUR_SQL);
        allTable.put("NOISE_DAY", TableSqlConstant.BAK_NOISE_DAY_SQL);
        allTable.put("SURFWATER_HOUR", TableSqlConstant.BAK_SURFWATER_HOUR_SQL);
        allTable.put("SURFWATER_DAY", TableSqlConstant.BAK_SURFWATER_DAY_SQL);
        allTable.put("VOC_HOUR", TableSqlConstant.BAK_VOC_HOUR_SQL);
        allTable.put("VOC_DAY", TableSqlConstant.BAK_VOC_DAY_SQL);
        allTable.put("VOC_MONTH", TableSqlConstant.BAK_VOC_MONTH_SQL);
        allTable.put("VOC_YEAR", TableSqlConstant.BAK_VOC_YEAR_SQL);
        allTable.put("COM_SCHEDULE_VOC", TableSqlConstant.BAK_COM_SCHEDULE_VOC_SQL);
        allTable.put("VOC_OFFLINE", TableSqlConstant.BAK_VOC_OFFLINE_SQL);
        allTable.put("VOC_CURRENT_OVERPROOF", TableSqlConstant.BAK_VOC_CURRENT_OVERPROOF_SQL);
        Iterator var1 = allTable.keySet().iterator();

        String table;
        while (var1.hasNext()) {
            table = (String) var1.next();
            if (this.myBaseMapper.checkTableExists(table) == 0) {
                if (table.equals("WARN_LOG")) {
                    this.myBaseMapper.sqlExcute(MessageFormat.format(allTable.get(table).toPattern(), "任务受理状态:1-未受理；2-已受理；3-已生成任务"));
                } else {
                    this.myBaseMapper.sqlExcute(MessageFormat.format(allTable.get(table).toPattern(), ""));
                }
            }
        }

        dynamicTable.put("WATER_CURRENT_", TableSqlConstant.BAK_WATER_CURRENT_SQL);
        dynamicTable.put("WATER_CURRENT_TR_", TableSqlConstant.BAK_WATER_CURRENT_TR_SQL);
        dynamicTable.put("WATER_MINUTE_", TableSqlConstant.BAK_WATER_MINUTE_SQL);
        dynamicTable.put("AIR_CURRENT_TR_", TableSqlConstant.BAK_AIR_CURRENT_TR_SQL);
        dynamicTable.put("AIR_MINUTE_", TableSqlConstant.BAK_AIR_MINUTE_SQL);
        dynamicTable.put("NOISE_CURRENT_TR_", TableSqlConstant.BAK_NOISE_CURRENT_TR_SQL);
        dynamicTable.put("NOISE_MINUTE_", TableSqlConstant.BAK_NOISE_MINUTE_SQL);
        dynamicTable.put("VOC_CURRENT_TR_", TableSqlConstant.BAK_VOC_CURRENT_TR_SQL);
        dynamicTable.put("VOC_MINUTE_", TableSqlConstant.BAK_VOC_MINUTE_SQL);
        String thisMonth = CommonsUtil.dateCurrent("yyMM");
        table = CommonsUtil.month(-1).substring(2, 7).replaceAll("-", "");
        String nextMonth = CommonsUtil.month(1).substring(2, 7).replaceAll("-", "");
        this.createTable(thisMonth, table);
        this.createTable(nextMonth, thisMonth);
        isInitial = true;
    }

    public static boolean isInitial() {
        return isInitial;
    }

    @Scheduled(
            cron = "0 0 0 1 * ?"
    )
    @Async
    public void createNextMonthTable() {
        isUpdateTable = false;
        String thisMonth = CommonsUtil.dateCurrent("yyMM");
        String nextMonth = CommonsUtil.month(1).substring(2, 7).replaceAll("-", "");
        this.createTable(nextMonth, thisMonth);
        isUpdateTable = true;
    }

    private void createTable(String thisMonth, String lastMonth) {
        Iterator var3 = dynamicTable.keySet().iterator();

        while (var3.hasNext()) {
            String key = (String) var3.next();
            String thisMonthTable = key + thisMonth;
            String lastMonthTable = key + lastMonth;
            if (this.myBaseMapper.checkTableExists(thisMonthTable) == 0) {
                if (this.myBaseMapper.checkTableExists(lastMonthTable)>0) {
                    this.myBaseMapper.sqlExcute("create table " + thisMonthTable + " like " + lastMonthTable);
                } else {
                    this.myBaseMapper.sqlExcute(MessageFormat.format(dynamicTable.get(key).toPattern(), thisMonth));
                }
            }
        }

    }

    public static boolean isUpdateTable() {
        return isUpdateTable;
    }
}