package business.processor.excute;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import business.processor.bean.DataFactorBean;
import business.processor.bean.DataPacketBean;
import business.processor.bean.FactorBean;
import business.processor.excute.airq.AirQDataExcuter;
import business.util.CommonsUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("dataParserService")
@Slf4j
public class DataParserService {
    @Autowired
    private AirQDataExcuter airQDataExcuter;

    public DataParserService() {
    }

    public void format(DataPacketBean dataPacketBean, int factorType) {
        String msg = dataPacketBean.getContent();
        String qn = "";
        int index_qn = msg.indexOf("QN=");
        if (index_qn != -1) {
            try {
                qn = msg.substring(index_qn + 3, msg.indexOf(";", index_qn));
            } catch (Exception var31) {
            }
        }

        dataPacketBean.setQn(qn);
        String pw = "";
        int index_pw = msg.indexOf("PW=");
        if (index_pw != -1) {
            try {
                pw = msg.substring(index_pw + 3, msg.indexOf(";", index_pw));
            } catch (Exception var30) {
            }
        }

        dataPacketBean.setPw(pw);
        int flag = 0;
        int index_flag = msg.indexOf("Flag=");
        if (index_flag != -1) {
            try {
                flag = Integer.valueOf(msg.substring(index_flag + 5, msg.indexOf(";", index_flag)));
            } catch (Exception var29) {
            }
        }

        dataPacketBean.setFlag(flag);
        String content = "";
        int index_content = msg.indexOf("CP=&&");
        if (index_content != -1) {
            try {
                content = msg.substring(index_content + 5, msg.lastIndexOf("&&")).toUpperCase();
            } catch (Exception var28) {
            }
        }

        dataPacketBean.setContent(content);
        Map<String, DataFactorBean> dataMap = new HashMap();
        if (StringUtils.isNotEmpty(content)) {
            String[] factorGroupsData = content.split(";");
            String[] var14 = factorGroupsData;
            int var15 = factorGroupsData.length;

            for(int var16 = 0; var16 < var15; ++var16) {
                String factorData = var14[var16];
                String[] factorItems = factorData.split(",");
                DataFactorBean dataFactorBean = new DataFactorBean();
                String factorCode = "";

                for(int i = 0; i < factorItems.length; ++i) {
                    String factorItem = factorItems[i];
                    String[] factorCodeValue = factorItem.split("=");
                    if (factorCodeValue.length == 2) {
                        String code = factorCodeValue[0].trim();
                        String value = factorCodeValue[1].trim();
                        if (i == 0) {
                            int factorCode_index = code.indexOf("-");
                            if (factorCode_index != -1) {
                                factorCode = code.substring(0, factorCode_index);
                            }

                            if (dataPacketBean.getFlag() < 4) {
                                FactorBean newFactor = this.factorService.getPollutionByOldCode(factorCode, factorType);
                                if (newFactor != null) {
                                    factorCode = newFactor.getCode();
                                }
                            }

                            dataFactorBean.setFactorCode(factorCode);
                            if (dataMap.get(factorCode) != null) {
                                dataFactorBean = dataMap.get(factorCode);
                            }

                            dataFactorBean.setDataTime(dataPacketBean.getDataTime());
                        }

                        try {
                            if (code.indexOf("-RTD") != -1) {
                                dataFactorBean.setRtd(Double.valueOf(value));
                            } else if (code.indexOf("-AVG") != -1) {
                                dataFactorBean.setAvg(Double.valueOf(value));
                            } else if (code.indexOf("-MAX") != -1) {
                                dataFactorBean.setMax(Double.valueOf(value));
                            } else if (code.indexOf("-MIN") != -1) {
                                dataFactorBean.setMin(Double.valueOf(value));
                            } else if (code.indexOf("-COU") != -1) {
                                dataFactorBean.setCou(Double.valueOf(value));
                            } else if (code.indexOf("-ZSRTD") != -1) {
                                dataFactorBean.setZsRtd(Double.valueOf(value));
                            } else if (code.indexOf("-ZSAVG") != -1) {
                                dataFactorBean.setZsAvg(Double.valueOf(value));
                            } else if (code.indexOf("-ZSMAX") != -1) {
                                dataFactorBean.setZsMax(Double.valueOf(value));
                            } else if (code.indexOf("-ZSMIN") != -1) {
                                dataFactorBean.setZsMin(Double.valueOf(value));
                            } else if (code.indexOf("-ZSCOU") != -1) {
                                dataFactorBean.setZsCou(Double.valueOf(value));
                            } else if (code.indexOf("-FLAG") != -1) {
                                dataFactorBean.setFlag(value);
                            } else if (code.indexOf("-EFLAG") != -1) {
                                dataFactorBean.setEFlag(value);
                            } else if (code.indexOf("-STATEDETAIL") != -1) {
                                dataFactorBean.setEFlag(value);
                            } else if (code.indexOf("-TIMESTAMP") == -1 && code.indexOf("-SAMPLETIME") == -1) {
                                if (code.indexOf("-TODAY") != -1) {
                                    dataFactorBean.setToday(Double.valueOf(value));
                                } else if (code.indexOf("-TOTAL") != -1) {
                                    dataFactorBean.setTotal(Double.valueOf(value));
                                } else if (code.indexOf("-SURPLUS") != -1) {
                                    dataFactorBean.setSurplus(Double.valueOf(value));
                                } else if (code.indexOf("-INFO") != -1) {
                                    dataFactorBean.setRtd(Double.valueOf(value));
                                } else if (code.indexOf("-DATA") != -1) {
                                    dataFactorBean.setData(Double.valueOf(value));
                                } else if (code.indexOf("-DAYDATA") != -1) {
                                    dataFactorBean.setDayData(Double.valueOf(value));
                                } else if (code.indexOf("-NIGHTDATA") != -1) {
                                    dataFactorBean.setNightData(Double.valueOf(value));
                                }
                            } else {
                                Date sampleTime = null;
                                if (value.length() == 14) {
                                    sampleTime = CommonsUtil.dateParse(value, "yyyyMMddHHmmss");
                                } else if (value.length() == 17) {
                                    sampleTime = CommonsUtil.dateParse(value, "yyyyMMddHHmmssSSS");
                                } else if (value.length() == 12) {
                                    sampleTime = CommonsUtil.dateParse(value, "yyyyMMddHHmm");
                                } else if (value.length() == 10) {
                                    sampleTime = CommonsUtil.dateParse(value, "yyyyMMddHH");
                                }

                                dataFactorBean.setSampleTime(sampleTime);
                            }
                        } catch (Exception var32) {
                            log.error("报文拆分转换出错，数值异常[" + var32.getMessage() + "],报文内容：" + msg);
                        }
                    }
                }

                if (dataFactorBean.getEFlag() == null) {
                    String dataFlag = dataFactorBean.getFlag();
                    if (dataFlag != null) {
                        if (dataFlag.equals("N")) {
                            dataFactorBean.setEFlag("0");
                        } else if (dataFlag.equals("F")) {
                            dataFactorBean.setEFlag("21");
                        } else if (dataFlag.equals("M")) {
                            dataFactorBean.setEFlag("22");
                        } else if (dataFlag.equals("S")) {
                            dataFactorBean.setEFlag("23");
                        } else if (dataFlag.equals("D")) {
                            dataFactorBean.setEFlag("24");
                        } else if (dataFlag.equals("C")) {
                            dataFactorBean.setEFlag("25");
                        } else if (dataFlag.equals("T")) {
                            dataFactorBean.setEFlag("5");
                        } else if (dataFlag.equals("B")) {
                            dataFactorBean.setEFlag("26");
                        }
                    }
                }

                if (dataFactorBean.getZsCou() == null && dataFactorBean.getCou() != null) {
                    dataFactorBean.setZsCou(dataFactorBean.getCou());
                }

                if (StringUtils.isNotEmpty(factorCode) && this.updateTableFieldTask.isFactorValid(factorCode, factorType)) {
                    dataMap.put(factorCode, dataFactorBean);
                }
            }
        }

        dataPacketBean.setDataMap(dataMap);
    }

    public void distributeData(String bakSourceSql_update, DataPacketBean dataPacketBean, boolean showlog) {
        String st = dataPacketBean.getSt();
        String cn = dataPacketBean.getCn();
        int tag = 8;
        String msg = dataPacketBean.getContent();
        Date dataTime = null;
        int index_dataTime = msg.indexOf("DataTime=");
        if (index_dataTime != -1) {
            try {
                String dateTime_str = msg.substring(index_dataTime + 9, msg.indexOf(";", index_dataTime));
                if (dateTime_str.length() == 14) {
                    dataTime = CommonsUtil.dateParse(dateTime_str, "yyyyMMddHHmmss");
                } else if (dateTime_str.length() == 17) {
                    dataTime = CommonsUtil.dateParse(dateTime_str, "yyyyMMddHHmmssSSS");
                }
            } catch (Exception var11) {
            }
        }

        if (dataTime == null) {
            log.error("报文解析错误[DataTime错误],报文:" + msg);
        } else {
            dataPacketBean.setDataTime(dataTime);

            try {
                if (!cn.equals("3015") && !cn.equals("8804") && !cn.equals("8803") && !cn.equals("3715")) {
                    tag = 6;
                    if (st.equals("22")) {
                        if (cn.equals("2061") || cn.equals("2031")) {
                            tag = this.airQDataExcuter.execute(dataPacketBean);
                        }
                    }
                } else {
                    tag = 2;
                }
            } catch (Exception var12) {
                log.error("报文解析错误[" + var12.getMessage() + "],报文:" + msg, var12);
                tag = 9;
            }
        }

        this.bakSourceService.updateTag(bakSourceSql_update, dataPacketBean.getSourceId(), tag);
        if (showlog) {
            System.out.println("报文解析完成【" + dataPacketBean.getSourceId() + "】" + "TAG=" + tag);
        }

    }

    public String getMHDTableName(DataPacketBean dataPacketBean) {
        String st = dataPacketBean.getSt();
        String cn = dataPacketBean.getCn();
        Date dataTime = dataPacketBean.getDataTime();
        String month = CommonsUtil.dateFormat(dataTime, "yyMM");
        String tableName = null;
        if (st.equals("32")) {
            if (cn.equals("2051")) {
                tableName = "BAK_WATER_MINUTE_" + month;
            } else if (cn.equals("2061")) {
                tableName = "BAK_WATER_HOUR";
            } else if (cn.equals("2031")) {
                tableName = "BAK_WATER_DAY";
            }
        } else if (st.equals("31")) {
            if (cn.equals("2051")) {
                tableName = "BAK_AIR_MINUTE_" + month;
            } else if (cn.equals("2061")) {
                tableName = "BAK_AIR_HOUR";
            } else if (cn.equals("2031")) {
                tableName = "BAK_AIR_DAY";
            } else if (cn.equals("3020")) {
                tableName = "BAK_AIR_INCINERATOR_CURRENT";
            }
        } else if (st.equals("22")) {
            if (cn.equals("2061")) {
                tableName = "BAK_AIRQ_HOUR";
            } else if (cn.equals("2031")) {
                tableName = "BAK_AIRQ_DAY";
            }
        } else if (st.equals("23")) {
            if (cn.equals("2011")) {
                tableName = "BAK_NOISE_CURRENT_TR_" + month;
            } else if (cn.equals("2051")) {
                tableName = "BAK_NOISE_MINUTE_" + month;
            } else if (cn.equals("2061")) {
                tableName = "BAK_NOISE_HOUR";
            } else if (cn.equals("2031")) {
                tableName = "BAK_NOISE_DAY";
            }
        } else if (st.equals("21")) {
            if (cn.equals("2061")) {
                tableName = "BAK_SURFWATER_HOUR";
            } else if (cn.equals("2031")) {
                tableName = "BAK_SURFWATER_DAY";
            }
        }

        if (st.equals("27")) {
            if (cn.equals("2051")) {
                tableName = "BAK_VOC_MINUTE_" + month;
            } else if (cn.equals("2061")) {
                tableName = "BAK_VOC_HOUR";
            } else if (cn.equals("2031")) {
                tableName = "BAK_VOC_DAY";
            }
        }

        return tableName;
    }

    public boolean isExistMHDData(DataPacketBean dataPacketBean) {
        String sql = "SELECT COUNT(1) AS COUNTNUM FROM " + this.getMHDTableName(dataPacketBean) + " WHERE DATA_TIME=? AND MN=?";
        List<Object> params = new ArrayList();
        params.add(dataPacketBean.getDataTime());
        params.add(dataPacketBean.getMn());
        List<Map<String, Object>> list = this.baseDao.sqlQuery(sql, params);
        if (list != null && list.size() > 0) {
            long i = (Long)list.get(0).get("COUNTNUM");
            if (i > 0L) {
                return true;
            }
        }

        return false;
    }
}