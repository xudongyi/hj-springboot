package business.processor.task;

import business.constant.TableSqlConstant;
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
    private IBaseDao baseDao;
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
                } catch (InterruptedException var9) {
                    var9.printStackTrace();
                }
            }

            String thisMonth = CommonsUtil.dateCurrent("yyMM");
            String waterTable = "bak_water_current_tr_" + thisMonth;
            String airTable = "bak_air_current_tr_" + thisMonth;
            String airqTable = "bak_airq_hour";
            String noiseTable = "bak_noise_current_tr_" + thisMonth;
            String surfWaterTable = "bak_surfwater_hour";
            String incineratorTable = "bak_air_incinerator_current";
            String vocTable = "bak_voc_current_tr_" + thisMonth;
            UpdateTableFieldTask.this.initialField(waterTable, 1);
            UpdateTableFieldTask.this.initialField(airTable, 2);
            UpdateTableFieldTask.this.initialField(airqTable, 3);
            UpdateTableFieldTask.this.initialField(noiseTable, 7);
            UpdateTableFieldTask.this.initialField(surfWaterTable, 5);
            UpdateTableFieldTask.this.initialField(incineratorTable, 8);
            UpdateTableFieldTask.this.initialField(vocTable, 9);
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
        List<Map<String, Object>> list = this.baseDao.sqlQuery("DESC " + tableName, (List) null);
        if (list != null) {
            for (int i = 0; i < list.size(); ++i) {
                String column = (String) list.get(i).get("Field");
                if (column.indexOf("_") != -1) {
                    String field = column.substring(0, column.indexOf("_")).toUpperCase();
                    if (this.factorService.getFactors(factorType).containsKey(field)) {
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
            if (factorType == 1) {
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_WATER_CURRENT_TR_ALTERSQL.format(new Object[]{thisMonth, factorCode, factorCode, factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_WATER_MHD_ALTERSQL.format(new Object[]{"MINUTE_" + thisMonth, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_WATER_MHD_ALTERSQL.format(new Object[]{"HOUR", factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_WATER_MHD_ALTERSQL.format(new Object[]{"DAY", factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_WATER_MY_ALTERSQL.format(new Object[]{"MONTH", factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_WATER_MY_ALTERSQL.format(new Object[]{"YEAR", factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_COM_SCHEDULE_ALTERSQL.format(new Object[]{factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_WATER_CURRENT_TR_ALTERSQL.format(new Object[]{nextMonth, factorCode, factorCode, factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_WATER_MHD_ALTERSQL.format(new Object[]{"MINUTE_" + nextMonth, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
            } else if (factorType == 2) {
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_AIR_CURRENT_TR_ALTERSQL.format(new Object[]{thisMonth, factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_AIR_MHD_ALTERSQL.format(new Object[]{"MINUTE_" + thisMonth, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_AIR_MHD_ALTERSQL.format(new Object[]{"HOUR", factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_AIR_MHD_ALTERSQL.format(new Object[]{"DAY", factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_AIR_MY_ALTERSQL.format(new Object[]{"MONTH", factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_AIR_MY_ALTERSQL.format(new Object[]{"YEAR", factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_COM_SCHEDULE_AIR_ALTERSQL.format(new Object[]{factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_AIR_CURRENT_TR_ALTERSQL.format(new Object[]{nextMonth, factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_AIR_MHD_ALTERSQL.format(new Object[]{"MINUTE_" + nextMonth, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
            } else if (factorType == 3) {
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_AIRQ_HOUR_ALTERSQL.format(new Object[]{factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_AIRQ_DAY_ALTERSQL.format(new Object[]{factorCode, factorCode}), (List) null);
            } else if (factorType == 5) {
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_SURFWATER_HOUR_ALTERSQL.format(new Object[]{factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_SURFWATER_DAY_ALTERSQL.format(new Object[]{factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
            } else if (factorType == 7) {
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_NOISE_CURRENT_TR_ALTERSQL.format(new Object[]{thisMonth, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_NOISE_MINUTE_ALTERSQL.format(new Object[]{thisMonth, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_NOISE_HOUR_ALTERSQL.format(new Object[]{factorCode}), (List) null);
                if (!factorCode.equals("LD") && !factorCode.equals("LN")) {
                    UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_NOISE_DAY_ALTERSQL.format(new Object[]{factorCode, factorCode, factorCode}), (List) null);
                } else {
                    List<Object> params = new ArrayList();
                    params.add("1-达标；2-不达标");
                    UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_NOISE_DAY_LDN_ALTERSQL.format(new Object[]{factorCode, factorCode, factorCode, factorCode}), params);
                }

                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_NOISE_CURRENT_TR_ALTERSQL.format(new Object[]{nextMonth, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_NOISE_MINUTE_ALTERSQL.format(new Object[]{nextMonth, factorCode}), (List) null);
            } else if (factorType == 8) {
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_AIR_INCINERATOR_CURRENT_ALTERSQL.format(new Object[]{factorCode}), (List) null);
            } else if (factorType == 9) {
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_VOC_CURRENT_TR_ALTERSQL.format(new Object[]{thisMonth, factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_VOC_MHD_ALTERSQL.format(new Object[]{"MINUTE_" + thisMonth, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_VOC_MHD_ALTERSQL.format(new Object[]{"HOUR", factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_VOC_MHD_ALTERSQL.format(new Object[]{"DAY", factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_VOC_MY_ALTERSQL.format(new Object[]{"MONTH", factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_VOC_MY_ALTERSQL.format(new Object[]{"YEAR", factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_COM_SCHEDULE_VOC_ALTERSQL.format(new Object[]{factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_VOC_CURRENT_TR_ALTERSQL.format(new Object[]{nextMonth, factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
                UpdateTableFieldTask.this.baseDao.sqlExcute(TableSqlConstant.BAK_VOC_MHD_ALTERSQL.format(new Object[]{"MINUTE_" + nextMonth, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode, factorCode}), (List) null);
            }

            String key = factorCode + "-" + factorType;
            UpdateTableFieldTask.waitingFieldMap.remove(key);
            UpdateTableFieldTask.pollutionFieldMap.put(key, factorCode);
        });
        thread.start();
    }
}