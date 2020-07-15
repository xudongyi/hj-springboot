package business.processor.excute.water;

import business.ienum.FactorType;
import business.processor.bean.*;
import business.processor.excute.DataParserService;
import business.processor.service.*;
import business.processor.task.UpdateTableFieldTask;
import business.receiver.bean.MonitorBean;
import business.receiver.mapper.MyBaseMapper;
import business.util.CommonsUtil;
import business.util.MathCalcUtil;
import business.util.SqlBuilder;
import cn.hutool.core.date.DatePattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.*;

@Service("waterDataExcuter")
@Slf4j
public class WaterDataExcuter {
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
    @Value("${data.bak.xml.open}")
    private boolean isSaveXML = false;
    @Value("${data.bak.xml.dir}")
    private String xmlDir = null;

    public WaterDataExcuter() {
    }

    public int execute(DataPacketBean dataPacketBean) {
        if (!this.dataParserService.isExistMHDData(dataPacketBean)) {
            this.dataParserService.format(dataPacketBean, FactorType.WATER.TYPE());
            String mn = dataPacketBean.getMn();
            MonitorBean monitor = (MonitorBean)this.monitorService.getAllMonitors().get(mn);
            if (monitor != null) {
                this.checkData(dataPacketBean, monitor);
                if (dataPacketBean.getCn().equals("2061")) {
                    this.reverseControlService.addendumHourData(dataPacketBean, monitor);
                }

                if (dataPacketBean.getCn().equals("2031")) {
                    this.reverseControlService.addendumDayData(dataPacketBean, monitor);
                    this.companyScheduleService.saveScheduleByDayData(dataPacketBean, monitor, 1);
                }
            }

            if (dataPacketBean.getCn().equals("2031")) {
                this.saveMonthYearData(dataPacketBean);
            }

            this.saveData(dataPacketBean);
            if (this.isSaveXML) {
                this.saveXmlData(dataPacketBean);
            }
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
            FactorBean factor = (FactorBean)this.factorService.getFactors(1).get(factorCode);
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
        List<Object> params = new ArrayList();
        params.add(CommonsUtil.dateFormat(dataTime, "yyyyMM"));
        params.add(mn);
        Map<String, Object> monthData = this.baseDao.sqlQuery(SqlBuilder.buildSql("SELECT * FROM BAK_WATER_MONTH WHERE STATIC_TIME=''{0}'' AND MN=''{1}''", params)).get(0);
        if (monthData == null) {
            this.insertMonthYearData(dataPacketBean, "WATER_MONTH", month);
        } else {
            this.updateMonthYearData(dataPacketBean, "WATER_MONTH", monthData);
        }

        Date year = CommonsUtil.dateParse(CommonsUtil.dateFormat(dataTime, "yyyy"), "yyyy");
        params = new ArrayList();
        params.add(CommonsUtil.dateFormat(dataTime, "yyyy"));
        params.add(mn);
        Map<String, Object> yearData = this.baseDao.sqlQuery(SqlBuilder.buildSql("SELECT * FROM WATER_YEAR WHERE STATIC_TIME=''{0}'' AND MN=''{1}''", params)).get(0);
        if (yearData == null) {
            this.insertMonthYearData(dataPacketBean, "WATER_YEAR", year);
        } else {
            this.updateMonthYearData(dataPacketBean, "WATER_YEAR", yearData);
        }

    }

    private void insertMonthYearData(DataPacketBean dataPacketBean, String tableName, Date staticTime) {
        StringBuilder sql_field = new StringBuilder();
        StringBuilder sql_value = new StringBuilder();
        List<Object> params = new ArrayList();
        sql_field.append("insert into " + tableName + "(ID,DATA_TIME,CREATE_TIME,STATIC_TIME,MN,TIMES");
        sql_value.append(")VALUES(?,?,?,?,?,?");
        params.add(CommonsUtil.createUUID1());
        params.add(dataPacketBean.getDataTime());
        params.add(new Date());
        params.add(staticTime);
        params.add(dataPacketBean.getMn());
        params.add(1);
        Map<String, DataFactorBean> map = dataPacketBean.getDataMap();
        Iterator var8 = map.keySet().iterator();

        while(var8.hasNext()) {
            String factorCode = (String)var8.next();
            if (this.updateTableFieldTask.isFieldExist(factorCode, FactorType.WATER.TYPE())) {
                DataFactorBean bean = (DataFactorBean)map.get(factorCode);
                if (bean.getMinState() != null) {
                    if (bean.getMinState() == 9) {
                        sql_field.append("," + factorCode + "_MIN");
                        params.add(bean.getMin());
                        sql_value.append(",?");
                    }

                    sql_field.append("," + factorCode + "_MIN_STATE");
                    params.add(bean.getMinState());
                    sql_value.append(",?");
                }

                if (bean.getMaxState() != null) {
                    if (bean.getMaxState() == 9) {
                        sql_field.append("," + factorCode + "_MAX");
                        sql_value.append(",'"+bean.getMax()+"'");
                    }

                    sql_field.append("," + factorCode + "_MAX_STATE");
                    sql_value.append(",'"+bean.getMaxState()+"'");
                }

                if (bean.getAvgState() != null) {
                    if (bean.getAvgState() == 9) {
                        sql_field.append("," + factorCode + "_AVG");
                        sql_value.append(",'"+bean.getAvg()+"'");
                    }

                    sql_field.append("," + factorCode + "_AVG_STATE");
                    sql_value.append(",'"+bean.getAvgState()+"'");
                }

                if (bean.getCouState() != null) {
                    if (bean.getCouState() == 9) {
                        sql_field.append("," + factorCode + "_COU");
                        sql_value.append(",'"+bean.getCou()+"'");
                    }

                    sql_field.append("," + factorCode + "_COU_STATE");
                    sql_value.append(",'"+bean.getCouState()+"'");
                }
            }
        }

        sql_field.append(sql_value).append(')');
        this.baseDao.sqlExcute(SqlBuilder.buildSql(sql_field.toString(), params));
    }

    private void updateMonthYearData(DataPacketBean dataPacketBean, String tableName, Map<String, Object> data) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList();
        sql.append("update " + tableName + " set TIMES=TIMES+1,DATA_TIME=''{0}''");
        params.add(CommonsUtil.dateFormat(dataPacketBean.getDataTime(), "yyyy-MM"));
        Map<String, DataFactorBean> map = dataPacketBean.getDataMap();
        Iterator var7 = map.keySet().iterator();

        while(var7.hasNext()) {
            String factorCode = (String)var7.next();
            if (this.updateTableFieldTask.isFieldExist(factorCode, FactorType.WATER.TYPE())) {
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
                        sql.append(',').append(factorCode).append("_MIN_STATE").append("=?");
                        params.add(bean.getMinState());
                    }

                    sql.append("," + factorCode + "_MIN").append("=?");
                    params.add(cou);
                }

                if (bean.getMaxState() != null && bean.getMaxState() == 9) {
                    cou = bean.getMax();
                    oldCou = data.get(factorCode + "_MAX");
                    if (oldCou != null) {
                        if ((Double)oldCou > cou) {
                            cou = (Double)oldCou;
                        }
                    } else {
                        sql.append(',').append(factorCode).append("_MAX_STATE").append("='"+bean.getMaxState()+"'");
                    }

                    sql.append("," + factorCode + "_MAX").append("='"+cou+"'");
                }

                if (bean.getAvgState() != null && bean.getAvgState() == 9) {
                    int times = (Integer)data.get("TIMES");
                    double avg = bean.getAvg();
                    Object oldAvg = data.get(factorCode + "_AVG");
                    if (oldAvg != null) {
                        avg = MathCalcUtil.avg((Double)oldAvg, bean.getAvg(), times);
                    } else {
                        sql.append("," + factorCode + "_AVG_STATE").append("='"+bean.getAvgState()+"'");
                        params.add(bean.getAvgState());
                    }

                    sql.append("," + factorCode + "_AVG").append("='"+avg+"'");
                    params.add(avg);
                }

                if (bean.getCouState() != null && bean.getCouState() == 9) {
                    cou = bean.getCou();
                    oldCou = data.get(factorCode + "_COU");
                    if (oldCou != null) {
                        cou = CommonsUtil.numberFormat(bean.getCou() + (Double)oldCou, 4);
                    } else {
                        sql.append("," + factorCode + "_COU_STATE").append("='"+bean.getCouState()+"'");
                        params.add(bean.getCouState());
                    }

                    sql.append("," + factorCode + "_COU").append("='"+cou+"'");
                }
            }
        }

        sql.append(" WHERE ID=''{1}'' ");
        params.add(data.get("ID"));
        this.baseDao.sqlExcute(SqlBuilder.buildSql(sql.toString(), params));
    }

    private void saveData(DataPacketBean dataPacketBean) {
        String cn = dataPacketBean.getCn();
        String mn = dataPacketBean.getMn();
        StringBuilder sql_field = new StringBuilder();
        StringBuilder sql_value = new StringBuilder();
        List<Object> params = new ArrayList();
        sql_field.append("INSERT INTO ").append(this.dataParserService.getMHDTableName(dataPacketBean)).append("(ID,DATA_TIME,CREATE_TIME,MN,STATE");
        sql_value.append(")VALUES(''{0}'',''{1}'',''{2}'',''{3}'',''{4}''");
        params.add(CommonsUtil.createUUID1());
        params.add(dataPacketBean.getDataTime());
        params.add(CommonsUtil.dateFormat(new Date()));
        params.add(mn);
        params.add(0);
        Map<String, DataFactorBean> map = dataPacketBean.getDataMap();
        Iterator var8 = map.keySet().iterator();

        while(var8.hasNext()) {
            String factorCode = (String)var8.next();
            if (this.updateTableFieldTask.isFieldExist(factorCode, FactorType.WATER.TYPE())) {
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
                sql_value.append(",'"+bean.getMin()+"','"+bean.getMinState()+"','"+bean.getMax()+"'','"+bean.getMaxState()+"','"
                        +bean.getAvg()+",'"+bean.getAvgState()+",'"+bean.getCou()+",'"+bean.getCouState()+",'"+bean.getFlag()+"");
                this.monitorDeviceService.setMHDData(mn, factorCode, cn, bean);
            }
        }

        sql_field.append(sql_value).append(')');
        this.baseDao.sqlExcute(SqlBuilder.buildSql(sql_field.toString(), params));
        if (cn.equals("2061")) {
            this.monitorService.setMnHourLastUpload(mn);
        } else if (cn.equals("2031")) {
            this.monitorService.setMnDayLastUpload(mn);
        }

    }

    private void saveXmlData(DataPacketBean dataPacketBean) {
        if (this.xmlDir != null) {
            String cn = dataPacketBean.getCn();
            String mn = dataPacketBean.getMn();
            if (cn.equals("2061") || cn.equals("2031")) {
                String tableName = "";
                String fileName = "";
                if (cn.equals("2061")) {
                    tableName = "DMS_T_DATA_HOUR";
                    fileName = "HOUR_DATA_";
                } else if (cn.equals("2031")) {
                    tableName = "DMS_T_DATA_DAY";
                    fileName = "DAY_DATA_";
                }

                fileName = fileName + CommonsUtil.dateCurrent("yyyyMMddHHmmssSSS") + ".xml";
                String dataTime = CommonsUtil.dateFormat(dataPacketBean.getDataTime(), "yyyyMMddHHmmss");
                StringBuffer xml = new StringBuffer("<?xml version=\"1.0\" encoding=\"GB2312\"?>\r\n");
                xml.append("<table>\r\n");
                xml.append("\t<head table=\"" + tableName + "\" MN=\"" + mn + "\" dataTime=\"" + dataTime + "\"/>\r\n");
                xml.append("\t<datas>\r\n");
                xml.append("\t\t<data>\r\n");
                Map<String, DataFactorBean> map = dataPacketBean.getDataMap();
                Map<String, FactorBean> factors = this.factorService.getFactors(1);
                Iterator var10 = map.keySet().iterator();

                while(var10.hasNext()) {
                    String factorCode = (String)var10.next();
                    if (factors.get(factorCode) != null) {
                        DataFactorBean bean = (DataFactorBean)map.get(factorCode);
                        xml.append("\t\t\t<pollData>\r\n");
                        xml.append("\t\t\t\t<polluteCode>" + ((FactorBean)factors.get(factorCode)).getOldCode() + "</polluteCode>\r\n");
                        xml.append("\t\t\t\t<avgVal>" + bean.getAvg() + "</avgVal>\r\n");
                        xml.append("\t\t\t\t<maxVal>" + bean.getMax() + "</maxVal>\r\n");
                        xml.append("\t\t\t\t<minVal>" + bean.getMin() + "</minVal>\r\n");
                        xml.append("\t\t\t\t<couVal>" + bean.getCou() + "</couVal>\r\n");
                        xml.append("\t\t\t</pollData>\r\n");
                    }
                }

                xml.append("\t\t</data>\r\n");
                xml.append("\t</datas>\r\n");
                xml.append("</table>\r\n");
                File path = new File(this.xmlDir);
                if (!path.exists()) {
                    path.mkdirs();
                }

                File file = new File(this.xmlDir + File.separator + fileName);
                OutputStreamWriter out = null;

                try {
                    out = new OutputStreamWriter(new FileOutputStream(file), "GB2312");
                    out.write(xml.toString());
                    out.flush();
                } catch (IOException var22) {
                    log.error("转发XML格式的小时、日数据文件操作失败！\n" + var22.getMessage(), var22);
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException var21) {
                            var21.printStackTrace();
                        }
                    }

                }
            }

        }
    }
}