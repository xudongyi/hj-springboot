package business.processor.task;

import business.constant.TableSqlConstant;
import business.ienum.FactorType;
import business.processor.service.FactorService;
import business.receiver.mapper.MyBaseMapper;
import business.receiver.task.UpdateReceiverTableTask;
import business.util.CommonsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service("updateTableFieldTask")
public class UpdateTableFieldTask {

    @Autowired
    private MyBaseMapper myBaseMapper;
    @Autowired
    private FactorService factorService;
    private static boolean isInitial = false;
    private static Map<String, String> pollutionFieldMap = new ConcurrentHashMap();
    private static Map<String, String> waitingFieldMap = new ConcurrentHashMap();

    public UpdateTableFieldTask() {
    }

    @PostConstruct
    public void initialField() {
        Thread thread = new Thread(() -> {
            while (!UpdateProcessorTableTask.isInitial() || !UpdateReceiverTableTask.isInitial()) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            String thisMonth = CommonsUtil.dateCurrent("yyMM");
            String waterTable = "water_current_tr_" + thisMonth;
            String airTable = "air_current_tr_" + thisMonth;
            String airqTable = "airq_hour";
            String noiseTable = "noise_current_tr_" + thisMonth;
            String surfWaterTable = "surfwater_hour";
            String incineratorTable = "air_incinerator_current";
            String vocTable = "voc_current_tr_" + thisMonth;
            UpdateTableFieldTask.this.initialField(waterTable, FactorType.WATER.TYPE());
            UpdateTableFieldTask.this.initialField(airTable, FactorType.AIR.TYPE());
            UpdateTableFieldTask.this.initialField(airqTable, FactorType.AIRQ.TYPE());
            UpdateTableFieldTask.this.initialField(noiseTable, FactorType.NOISE.TYPE());
            UpdateTableFieldTask.this.initialField(surfWaterTable, FactorType.SURFWATER.TYPE());
            UpdateTableFieldTask.this.initialField(incineratorTable, FactorType.ELECTRIC.TYPE());
            UpdateTableFieldTask.this.initialField(vocTable, FactorType.VOCS.TYPE());
            UpdateTableFieldTask.isInitial = true;
        });
        thread.start();
    }

    public static boolean isInitial() {
        return isInitial;
    }

    public boolean isFactorValid(String factorCode, int factorType) {
        if (this.factorService.getFactors(factorType).containsKey(factorCode)) {
            String key = factorCode + "-" + factorType;
            if (pollutionFieldMap.containsKey(key)) {
                return true;
            } else {
                if (UpdateProcessorTableTask.isUpdateTable() && !waitingFieldMap.containsKey(key)) {
                    waitingFieldMap.put(key, factorCode);
                    this.updateField(factorCode, factorType);
                }

                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isFieldExist(String factorCode, int factorType) {
        return pollutionFieldMap.containsKey(factorCode + "-" + factorType);
    }

    private void initialField(String tableName, int factorType) {
        List<Map<String, Object>> list = this.myBaseMapper.sqlQuery("DESC " + tableName);
        if (list != null) {
            for (int i = 0; i < list.size(); ++i) {
                String column = (String) list.get(i).get("Field");
                if (column.indexOf("_") != -1) {
                    String field = column.substring(0, column.indexOf("_")).toUpperCase();
                    if (this.factorService.getFactors(factorType).containsKey(field)) {
                        //只存储系统中启用的污染因子
                        pollutionFieldMap.put(field.toUpperCase() + "-" + factorType, field);
                    }
                }
            }
        }

    }

    private void updateField(final String factorCode, final int factorType) {
        Thread thread = new Thread(() -> {
            String thisMonth = CommonsUtil.dateCurrent("yyMM");
            String nextMonth = CommonsUtil.month(1).substring(2, 7).replaceAll("-", "");
            if (factorType == FactorType.WATER.TYPE()) {
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_WATER_CURRENT_TR_ALTERSQL.format(new Object[]{thisMonth, factorCode, factorCode, factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_WATER_MHD_ALTERSQL.format(new Object[]{"MINUTE_" + thisMonth, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_WATER_MHD_ALTERSQL.format(new Object[]{"HOUR", factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_WATER_MHD_ALTERSQL.format(new Object[]{"DAY", factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_WATER_MY_ALTERSQL.format(new Object[]{"MONTH", factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_WATER_MY_ALTERSQL.format(new Object[]{"YEAR", factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_COM_SCHEDULE_ALTERSQL.format(new Object[]{factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_WATER_CURRENT_TR_ALTERSQL.format(new Object[]{nextMonth, factorCode, factorCode, factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_WATER_MHD_ALTERSQL.format(new Object[]{"MINUTE_" + nextMonth, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}));
            } else if (factorType == FactorType.AIR.TYPE()) {
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_AIR_CURRENT_TR_ALTERSQL.format(new Object[]{thisMonth, factorCode, factorCode, factorCode, factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_AIR_MHD_ALTERSQL.format(new Object[]{"MINUTE_" + thisMonth, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_AIR_MHD_ALTERSQL.format(new Object[]{"HOUR", factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_AIR_MHD_ALTERSQL.format(new Object[]{"DAY", factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_AIR_MY_ALTERSQL.format(new Object[]{"MONTH", factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_AIR_MY_ALTERSQL.format(new Object[]{"YEAR", factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_COM_SCHEDULE_AIR_ALTERSQL.format(new Object[]{factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_AIR_CURRENT_TR_ALTERSQL.format(new Object[]{nextMonth, factorCode, factorCode, factorCode, factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_AIR_MHD_ALTERSQL.format(new Object[]{"MINUTE_" + nextMonth, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}));
            } else if (factorType == FactorType.AIRQ.TYPE()) {
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_AIRQ_HOUR_ALTERSQL.format(new Object[]{factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_AIRQ_DAY_ALTERSQL.format(new Object[]{factorCode, factorCode}));
            } else if (factorType == FactorType.SURFWATER.TYPE()) {
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_SURFWATER_HOUR_ALTERSQL.format(new Object[]{factorCode, factorCode, factorCode, factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_SURFWATER_DAY_ALTERSQL.format(new Object[]{factorCode, factorCode, factorCode, factorCode, factorCode}));
            } else if (factorType == FactorType.NOISE.TYPE()) {
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_NOISE_CURRENT_TR_ALTERSQL.format(new Object[]{thisMonth, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_NOISE_MINUTE_ALTERSQL.format(new Object[]{thisMonth, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_NOISE_HOUR_ALTERSQL.format(new Object[]{factorCode}));
                if (!factorCode.equals("LD") && !factorCode.equals("LN")) {
                    UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_NOISE_DAY_ALTERSQL.format(new Object[]{factorCode, factorCode, factorCode}));
                } else {
                    UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_NOISE_DAY_LDN_ALTERSQL.format(new Object[]{factorCode, factorCode, factorCode, factorCode,"1-达标；2-不达标"}));
                }

                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_NOISE_CURRENT_TR_ALTERSQL.format(new Object[]{nextMonth, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_NOISE_MINUTE_ALTERSQL.format(new Object[]{nextMonth, factorCode}));
            } else if (factorType == FactorType.ELECTRIC.TYPE()) {
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_AIR_INCINERATOR_CURRENT_ALTERSQL.format(new Object[]{factorCode}));
            } else if (factorType == FactorType.VOCS.TYPE()) {
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_VOC_CURRENT_TR_ALTERSQL.format(new Object[]{thisMonth, factorCode, factorCode, factorCode, factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_VOC_MHD_ALTERSQL.format(new Object[]{"MINUTE_" + thisMonth, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_VOC_MHD_ALTERSQL.format(new Object[]{"HOUR", factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_VOC_MHD_ALTERSQL.format(new Object[]{"DAY", factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_VOC_MY_ALTERSQL.format(new Object[]{"MONTH", factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_VOC_MY_ALTERSQL.format(new Object[]{"YEAR", factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_COM_SCHEDULE_VOC_ALTERSQL.format(new Object[]{factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_VOC_CURRENT_TR_ALTERSQL.format(new Object[]{nextMonth, factorCode, factorCode, factorCode, factorCode, factorCode}));
                UpdateTableFieldTask.this.myBaseMapper.sqlExcute(TableSqlConstant.BAK_VOC_MHD_ALTERSQL.format(new Object[]{"MINUTE_" + nextMonth, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}));
            }

            String key = factorCode + "-" + factorType;
            UpdateTableFieldTask.waitingFieldMap.remove(key);
            UpdateTableFieldTask.pollutionFieldMap.put(key, factorCode);
        });
        thread.start();
    }
}