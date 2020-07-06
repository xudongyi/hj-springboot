package business.processor.service;

import business.processor.bean.DataFactorBean;
import business.processor.bean.FactorBean;
import business.processor.bean.MonitorDeviceBean;
import business.processor.bean.WarnRuleBean;
import business.receiver.bean.MonitorBean;
import business.receiver.mapper.MyBaseMapper;
import business.redis.RedisService;
import business.sms.SmsService;
import business.util.CommonsUtil;
import business.util.SqlBuilder;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("warnService")
@Slf4j
public class WarnService {
    @Autowired
    private RedisService redisService;
    @Autowired
    private SmsService smsService;

    @Value("${warn.dataerror.maxmin}")
    private boolean isCheckDataError = false;
    @Value("${warn.overproof.hour}")
    private boolean isCheckOverproofHour = false;
    @Value("${warn.overproof.day}")
    private boolean isCheckOverproofDay = false;
    @Autowired
    private MyBaseMapper myBaseMapper;
    public WarnService() {
    }

    public WarnRuleBean getOfflineWarnRule(String mn) {
        WarnRuleBean warnRule = null;
        if (StringUtils.isNotEmpty(mn)) {
            String result = this.redisService.getMapValue("warn_offline_map", mn);
            if (StringUtils.isNotEmpty(result)) {
                warnRule = (WarnRuleBean) CommonsUtil.toJsonObject(result, WarnRuleBean.class);
            } else {
                log.debug("Redis提示[获取监控点" + mn + "离线报警策略]:未取到值");
            }
        }

        return warnRule;
    }

    public List<WarnRuleBean> getOverproofWarnRule(String mn, String factorCode) {
        List<WarnRuleBean> warnRule = null;
        if (StringUtils.isNotEmpty(mn) && StringUtils.isNotEmpty(factorCode)) {
            String result = this.redisService.getMapValue("warn_overproof_map", mn + "-" + factorCode);
            if (StringUtils.isNotEmpty(result)) {
                warnRule = (List)CommonsUtil.toJsonObject(result, WarnRuleBean.class);
            } else {
                log.debug("Redis提示[获取监控点" + mn + "实时超标报警策略" + factorCode + "]:未取到值");
            }
        }

        return warnRule;
    }

    public List<WarnRuleBean> getHourOverproofWarnRule(String mn, String factorCode) {
        List<WarnRuleBean> warnRule = null;
        if (StringUtils.isNotEmpty(mn) && StringUtils.isNotEmpty(factorCode)) {
            String result = this.redisService.getMapValue("warn_overproof_hour_map", mn + "-" + factorCode);
            if (StringUtils.isNotEmpty(result)) {
                warnRule = (List)CommonsUtil.toJsonObject(result, WarnRuleBean.class);
            } else {
                log.debug("Redis提示[获取监控点" + mn + "小时超标报警策略" + factorCode + "]:未取到值");
            }
        }

        return warnRule;
    }

    public List<WarnRuleBean> getDayOverproofWarnRule(String mn, String factorCode) {
        List<WarnRuleBean> warnRule = null;
        if (StringUtils.isNotEmpty(mn) && StringUtils.isNotEmpty(factorCode)) {
            String result = this.redisService.getMapValue("warn_overproof_day_map", mn + "-" + factorCode);
            if (StringUtils.isNotEmpty(result)) {
                warnRule = (List)CommonsUtil.toJsonObject(result, WarnRuleBean.class);
            } else {
                log.debug("Redis提示[获取监控点" + mn + "日超标报警策略" + factorCode + "]:未取到值");
            }
        }

        return warnRule;
    }

    public Map<String, WarnRuleBean> getAllOverproofWarnRule() {
        Map<String, WarnRuleBean> level1WarnRule = new HashMap();
        Map<String, String> result = this.redisService.getMapAll("warn_overproof_map");
        if (result != null) {
            Iterator var3 = result.keySet().iterator();

            while(true) {
                String mnCode;
                List warnRules;
                do {
                    if (!var3.hasNext()) {
                        return level1WarnRule;
                    }

                    mnCode = (String)var3.next();
                    warnRules = (List)CommonsUtil.toJsonObject((String)result.get(mnCode), WarnRuleBean.class);
                } while(warnRules == null);

                WarnRuleBean level1WarnRuleBean = null;

                for(int i = 0; i < warnRules.size(); ++i) {
                    WarnRuleBean warnRule = (WarnRuleBean)warnRules.get(i);
                    if (warnRule.getLevel() == 1) {
                        level1WarnRuleBean = warnRule;
                        break;
                    }
                }

                if (level1WarnRuleBean != null) {
                    level1WarnRule.put(mnCode, level1WarnRuleBean);
                }
            }
        } else {
            log.debug("Redis提示[获取所有超标报警策略]:未取到值");
            return level1WarnRule;
        }
    }

    public WarnRuleBean getDeviceWarnRule(String mn, String factorCode) {
        WarnRuleBean warnRule = null;
        if (StringUtils.isNotEmpty(mn) && StringUtils.isNotEmpty(factorCode)) {
            String result = this.redisService.getMapValue("warn_device_map", mn + "-" + factorCode);
            if (StringUtils.isNotEmpty(result)) {
                warnRule = (WarnRuleBean)CommonsUtil.toJsonObject(result, WarnRuleBean.class);
            } else {
                log.debug("Redis提示[获取监控点" + mn + "设备状态" + factorCode + "报警策略]:未取到值");
            }
        }

        return warnRule;
    }

    public List<WarnRuleBean> getSurplusWarnRule(String mn) {
        List<WarnRuleBean> warnRule = null;
        if (StringUtils.isNotEmpty(mn)) {
            String result = this.redisService.getMapValue("warn_surplus_map", mn);
            if (StringUtils.isNotEmpty(result)) {
                warnRule = (List)CommonsUtil.toJsonObject(result, WarnRuleBean.class);
            } else {
                log.debug("Redis提示[获取监控点" + mn + "余额报警策略]:未取到值");
            }
        }

        return warnRule;
    }

    public List<WarnRuleBean> getTotalMonthWarnRule(String cid, int factorType, String factorCode) {
        List<WarnRuleBean> warnRule = null;
        if (StringUtils.isNotEmpty(cid) && StringUtils.isNotEmpty(factorCode)) {
            String result = this.redisService.getMapValue("warn_total_month_map", cid + "-" + factorType + "-" + factorCode);
            if (StringUtils.isNotEmpty(result)) {
                warnRule = (List)CommonsUtil.toJsonObject(result, WarnRuleBean.class);
            } else {
                log.debug("Redis提示[获取企业" + cid + "月流量" + factorCode + "报警策略]:未取到值");
            }
        }

        return warnRule;
    }

    public List<WarnRuleBean> getTotalYearWarnRule(String cid, int factorType, String factorCode) {
        List<WarnRuleBean> warnRule = null;
        if (StringUtils.isNotEmpty(cid) && StringUtils.isNotEmpty(factorCode)) {
            String result = this.redisService.getMapValue("warn_total_year_map", cid + "-" + factorType + "-" + factorCode);
            if (StringUtils.isNotEmpty(result)) {
                warnRule = (List)CommonsUtil.toJsonObject(result, WarnRuleBean.class);
            } else {
                log.debug("Redis提示[获取企业" + cid + "年流量" + factorCode + "报警策略]:未取到值");
            }
        }

        return warnRule;
    }

    public WarnRuleBean getFixWarnRule() {
        WarnRuleBean warnRule = null;
        String result = this.redisService.getStringValue("warn_fix");
        if (StringUtils.isNotEmpty(result)) {
            warnRule = (WarnRuleBean)CommonsUtil.toJsonObject(result, WarnRuleBean.class);
        } else {
            log.debug("Redis提示[获取定值报警策略]:未取到值");
        }

        return warnRule;
    }

    public WarnRuleBean getAbnormalWarnRule() {
        WarnRuleBean warnRule = null;
        String result = this.redisService.getStringValue("warn_abnormal");
        if (StringUtils.isNotEmpty(result)) {
            warnRule = (WarnRuleBean)CommonsUtil.toJsonObject(result, WarnRuleBean.class);
        } else {
            log.debug("Redis提示[获取量程报警策略]:未取到值");
        }

        return warnRule;
    }

    public WarnRuleBean getErrorWarnRule() {
        WarnRuleBean warnRule = null;
        String result = this.redisService.getStringValue("warn_error");
        if (StringUtils.isNotEmpty(result)) {
            warnRule = (WarnRuleBean)CommonsUtil.toJsonObject(result, WarnRuleBean.class);
        } else {
            log.debug("Redis提示[获取排放量异常报警策略]:未取到值");
        }

        return warnRule;
    }

    public WarnRuleBean getEnvirOfflineWarnRule() {
        WarnRuleBean warnRule = null;
        String result = this.redisService.getStringValue("warn_offline_envir");
        if (StringUtils.isNotEmpty(result)) {
            warnRule = (WarnRuleBean)CommonsUtil.toJsonObject(result, WarnRuleBean.class);
        } else {
            log.debug("Redis提示[获取环境质量离线报警策略]:未取到值");
        }

        return warnRule;
    }

    public WarnRuleBean getEnvirDeviceWarnRule() {
        WarnRuleBean warnRule = null;
        String result = this.redisService.getStringValue("warn_device_envir");
        if (StringUtils.isNotEmpty(result)) {
            warnRule = (WarnRuleBean)CommonsUtil.toJsonObject(result, WarnRuleBean.class);
        } else {
            log.debug("Redis提示[获取环境质量设备报警策略]:未取到值");
        }

        return warnRule;
    }

    public WarnRuleBean getEnviSurfwaterWarnRule() {
        WarnRuleBean warnRule = null;
        String result = this.redisService.getStringValue("warn_surfwater");
        if (StringUtils.isNotEmpty(result)) {
            warnRule = (WarnRuleBean)CommonsUtil.toJsonObject(result, WarnRuleBean.class);
        } else {
            log.debug("Redis提示[获取环境质量地表水超标报警策略]:未取到值");
        }

        return warnRule;
    }

    public WarnRuleBean getEnviNoiseWarnRule() {
        WarnRuleBean warnRule = null;
        String result = this.redisService.getStringValue("warn_noise");
        if (StringUtils.isNotEmpty(result)) {
            warnRule = (WarnRuleBean)CommonsUtil.toJsonObject(result, WarnRuleBean.class);
        } else {
            log.debug("Redis提示[获取环境质量噪声超标报警策略]:未取到值");
        }

        return warnRule;
    }

    public WarnRuleBean getEnviAirQWarnRule() {
        WarnRuleBean warnRule = null;
        String result = this.redisService.getStringValue("warn_airq");
        if (StringUtils.isNotEmpty(result)) {
            warnRule = (WarnRuleBean)CommonsUtil.toJsonObject(result, WarnRuleBean.class);
        } else {
            log.debug("Redis提示[获取空气质量AQI超标报警策略]:未取到值");
        }

        return warnRule;
    }

    public String getEnvirWarnContact(int warnType, String monitorId) {
        String contact = null;
        String result = this.redisService.getMapValue("warn_contact_envir", String.valueOf(warnType));
        if (StringUtils.isNotEmpty(result)) {
            Map<String, Object> map = (Map)CommonsUtil.toJsonObject(result, (Class)null);
            contact = (String)map.get(monitorId);
        } else {
            log.debug("Redis提示[获取环境质量监控点" + monitorId + "报警类型" + warnType + "报警联系人]:未取到值");
        }

        return contact;
    }

    public String getWarnContact(int warnType, String cid, String factorCode, int factorType) {
        String contact = null;
        String result = this.redisService.getMapValue("warn_contact", String.valueOf(warnType));
        if (StringUtils.isNotEmpty(result)) {
            Map<String, Object> map = (Map)CommonsUtil.toJsonObject(result, (Class)null);
            if (warnType == 2) {
                contact = (String)map.get(cid + "-" + factorType + "-" + factorCode);
            } else {
                contact = (String)map.get(cid);
            }
        } else {
            log.debug("Redis提示[获取企业" + cid + "报警类型" + warnType + "因子" + factorCode + "报警联系人]:未取到值");
        }

        return contact;
    }

    public void checkWarnlog(Date dataTime, WarnRuleBean warnRule, int warnType, String warnMessage, MonitorBean monitor, String factorCode) {
        if (warnRule != null) {
            String cid = monitor.getCompanyId();
            String mn = monitor.getMn();
            int interval = warnRule.getInterval();
            int level = warnRule.getLevel();
            StringBuffer sql = new StringBuffer("");
            List<Object> parmas = new ArrayList();
            sql.append("select WARN_TIME from warn_log where WARN_TYPE=? and WARN_TIME>? ");
            parmas.add(warnType);
            Date date = CommonsUtil.hour(-interval);
            parmas.add(date);
            if (warnType != 1 && warnType != 4 && warnType != 9 && warnType != 10 && warnType != 11) {
                if (warnType == 5) {
                    sql.append(" and COMPANY_ID=? and CODE=?");
                    parmas.add(cid);
                    parmas.add(factorCode);
                } else {
                    sql.append(" and MN=? and CODE=? ");
                    parmas.add(mn);
                    parmas.add(factorCode);
                }
            } else {
                sql.append(" and MN=? ");
                parmas.add(mn);
            }

            if (warnType == 2 || warnType == 4 || warnType == 5) {
                sql.append(" and WARN_LEVEL<=? ");
                parmas.add(level);
            }

            sql.append(" order by WARN_TIME desc ");
            //TODO 111
          /*  List<Map<String, Object>> result = this.baseDao.sqlQuery(sql.toString(), parmas);
            if (result == null || result.isEmpty()) {
                this.addWarnlog(dataTime, warnType, warnRule, monitor, monitor.getMonitorType(), factorCode, warnMessage);
            }*/

        }
    }

    public void addWarnlog(Date dataTime, int warnType, WarnRuleBean warnRule, MonitorBean monitor, int factorType, String factorCode, String message) {
        int message_status = 0;
        String contact = null;
        Date date = new Date();
        String mn = monitor.getMn();
        String companyId = monitor.getCompanyId();
        String monitorId = monitor.getMonitorId();
        int monitorType = monitor.getMonitorType();
        long time = date.getTime() - dataTime.getTime();
        if (time > 0L && time < 86400000L) {
            if (warnRule.getIsSend() == 1) {
                if (monitorType != 3 && monitorType != 5 && monitorType != 7) {
                    contact = this.getWarnContact(warnType, companyId, factorCode, factorType);
                } else {
                    contact = this.getEnvirWarnContact(warnType, monitorId);
                }

                if (StringUtils.isNotEmpty(contact)) {
                    try {
                        message_status = this.smsService.sendMessage(contact, message);
                    } catch (Exception var19) {
                        message_status = 2;
                        log.error("短信发送失败", var19);
                    }
                } else {
                    message_status = 3;
                }
            }

            if (warnRule.getIsColsed() == 1 && (warnType == 2 || warnType == 4 || warnType == 5) && monitor.getValveStatus() == 1) {
                if (contact == null) {
                    contact = this.getWarnContact(warnType, companyId, factorCode, factorType);
                }

                this.smsService.sendMessage(contact, monitor.getMonitorName() + "于" + CommonsUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss") + "关阀,原因为：" + message);
                //TODO 111
                //this.reverseControlService.sendReverseCloseValve(mn, message);
            }

            if (warnRule.getIsSample() == 1 && warnType == 2) {
                //TODO 111
                ///this.reverseControlService.sendReverseSample(mn, message);
            }
        }

        String sql = "INSERT INTO sys_warn_log(ID,TYPE,MN,COMPANY_ID,WARN_TYPE,CODE,WARN_LEVEL,MESSAGE_STATUS,CONTENT,WARN_TIME)values(?,?,?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList();
        params.add(CommonsUtil.createUUID1());
        params.add(monitor.getMonitorType());
        params.add(mn);
        params.add(companyId);
        params.add(warnType);
        params.add(factorCode);
        params.add(warnRule.getLevel());
        params.add(message_status);
        params.add(message);
        params.add(date);
        //TODO 111
        //this.baseDao.sqlExcute(sql, params);
    }

    public void addWaterOverproof(long source_id, String mn, Date dataTime, DataFactorBean currentData, double standard_value) {
        String factorCode = currentData.getFactorCode();
        String sql = "insert into water_current_overproof(ID,SOURCE_ID,MN,CODE,VALUE,STANDARD_VALUE,STATUS,DATA_TIME,SAMPLE_TIME)values(''{0}'',''{1}'',''{2}'',''{3}'',''{4}'',''{5}'',''{6}'',''{7}'',''{8}'')";
        List<Object> params = new ArrayList();
        params.add(CommonsUtil.createUUID1());
        params.add(source_id);
        params.add(mn);
        params.add(factorCode);
        params.add(currentData.getRtd());
        params.add(standard_value);
        params.add(currentData.getState());
        params.add(dataTime);
        params.add(currentData.getSampleTime());
        this.myBaseMapper.sqlExcute(SqlBuilder.buildSql(sql, params));
    }

    public void addAirOverproof(int tableType, long source_id, String mn, Date dataTime, DataFactorBean currentData, double standard_value, int zsFlag) {
        String factorCode = currentData.getFactorCode();
        String tableName = "";
        if (tableType == 2) {
            tableName = "air_current_overproof";
        } else if (tableType == 9) {
            tableName = "voc_current_overproof";
        }

        String sql = "insert into " + tableName + "(ID,SOURCE_ID,MN,CODE,STANDARD_VALUE,VALUE,STATUS,DATA_TIME)values(''{0}'',''{1}'',''{2}'',''{3}'',''{4}'',''{5}'',''{6}'',''{7}'')";
        List<Object> params = new ArrayList();
        params.add(CommonsUtil.createUUID1());
        params.add(source_id);
        params.add(mn);
        params.add(factorCode);
        params.add(standard_value);
        if (zsFlag == 1) {
            params.add(currentData.getZsRtd());
            params.add(currentData.getZsState());
        } else {
            params.add(currentData.getRtd());
            params.add(currentData.getState());
        }

        params.add(dataTime);
        this.myBaseMapper.sqlExcute(SqlBuilder.buildSql(sql, params));
    }

    public boolean checkCurrentAbnormal(WarnRuleBean warnRule, MonitorBean monitor, MonitorDeviceBean device, FactorBean factor, DataFactorBean bean) {
        int result = 9;
        Double val = null;
        String warn_value = "";
        if (factor.getZsFlag() == 1) {
            val = bean.getZsRtd();
            warn_value = "折算值";
        } else {
            val = bean.getRtd();
            warn_value = "实测值";
        }

        if (val == null) {
            return true;
        } else {
            String factorCode = bean.getFactorCode();
            String factorName = factor.getName() == null ? factorCode : factor.getName();
            String monitorName = monitor.getMonitorName();
            String warnTime = CommonsUtil.dateFormat(bean.getDataTime(), "yyyy-MM-dd HH:mm:ss");
            String warnMessage = "";
            if (device.getNormalMax() != 0.0D && val > device.getNormalMax()) {
                result = 6;
                warnMessage = monitorName + "[" + factorName + "]" + warn_value + val + "于" + warnTime + "超出量程上限(" + device.getNormalMax() + ")。";
            } else if (val < device.getNormalMin()) {
                result = -6;
                warnMessage = monitorName + "[" + factorName + "]" + warn_value + val + "于" + warnTime + "低于量程下限(" + device.getNormalMin() + ")。";
            }

            if (factor.getZsFlag() == 1) {
                bean.setZsState(Integer.valueOf(result));
            } else {
                bean.setState(Integer.valueOf(result));
            }

            if (result == 9) {
                return true;
            } else {
                this.checkWarnlog(bean.getDataTime(), warnRule, 7, warnMessage, monitor, factorCode);
                return false;
            }
        }
    }

    public boolean checkOverproof(long sourceId, MonitorBean monitor, DataFactorBean bean, FactorBean factor, boolean isAbnormal) {
        int result = 9;
        Double val = null;
        String warn_value = "";
        if (factor.getZsFlag() == 1) {
            val = bean.getZsRtd();
            warn_value = "实时折算值";
        } else {
            val = bean.getRtd();
            warn_value = "实时值";
        }

        if (val == null) {
            return true;
        } else {
            String factorCode = bean.getFactorCode();
            String factorName = factor.getName() == null ? factorCode : factor.getName();
            int factorType = factor.getFactorType();
            String mn = monitor.getMn();
            String monitorName = monitor.getMonitorName();
            String warnTime = CommonsUtil.dateFormat(bean.getDataTime(), "yyyy-MM-dd HH:mm:ss");
            Date dataTime = bean.getDataTime();
            List<WarnRuleBean> warnRuleList = this.getOverproofWarnRule(mn, factorCode);
            WarnRuleBean warnRuleSt = null;
            double warnSt = 0.0D;
            if (warnRuleList != null) {
                for(int j = 0; j < warnRuleList.size(); ++j) {
                    double overMax = ((WarnRuleBean)warnRuleList.get(j)).getMax();
                    double overMin = ((WarnRuleBean)warnRuleList.get(j)).getMin();
                    WarnRuleBean overWarnRule = (WarnRuleBean)warnRuleList.get(j);
                    if (overMax != 0.0D && val > overMax) {
                        result = overWarnRule.getLevel();
                        warnRuleSt = overWarnRule;
                        warnSt = overMax;
                        break;
                    }

                    if (val < overMin) {
                        result = -overWarnRule.getLevel();
                        warnRuleSt = overWarnRule;
                        warnSt = overMin;
                        break;
                    }
                }
            }

            if (isAbnormal) {
                if (factor.getZsFlag() == 1) {
                    bean.setZsState(result);
                } else {
                    bean.setState(result);
                }
            }

            if (result == 9) {
                return true;
            } else {
                if (factorType == 1) {
                    this.addWaterOverproof(sourceId, mn, dataTime, bean, warnSt);
                } else if (factorType == 2) {
                    this.addAirOverproof(factorType, sourceId, mn, dataTime, bean, warnSt, factor.getZsFlag());
                } else if (factorType == 9) {
                    this.addAirOverproof(factorType, sourceId, mn, dataTime, bean, warnSt, factor.getZsFlag());
                }
                //TODO 111
                //this.bakOverproofPeriodService.excute(monitor, bean, factor, val, warnSt);
                if (isAbnormal) {
                    String message_level = warnRuleSt.getLevel() == 1 ? "超标" : "预警";
                    String over_level = result > 0 ? "上限" : "下限";
                    String warnMessage = monitorName + "[" + factorName + "]" + warn_value + val + "于" + warnTime + "超出" + message_level + over_level + "(" + warnSt + ")。";
                    this.checkWarnlog(bean.getDataTime(), warnRuleSt, 2, warnMessage, monitor, factorCode);
                }

                return false;
            }
        }
    }

    public boolean checkOverproofHourDay(String cn, MonitorBean monitor, DataFactorBean bean, FactorBean factor) {
        if (cn.equals("2061")) {
            if (!this.isCheckOverproofHour) {
                return true;
            }
        } else {
            if (!cn.equals("2031")) {
                return true;
            }

            if (!this.isCheckOverproofDay) {
                return true;
            }
        }

        int result = 9;
        Double val = null;
        String warn_value = "";
        if (factor.getZsFlag() == 1) {
            val = bean.getZsAvg();
            if (cn.equals("2061")) {
                warn_value = "小时折算均值";
            } else if (cn.equals("2031")) {
                warn_value = "日折算均值";
            }
        } else {
            val = bean.getAvg();
            if (cn.equals("2061")) {
                warn_value = "小时均值";
            } else if (cn.equals("2031")) {
                warn_value = "日均值";
            }
        }

        if (val == null) {
            return true;
        } else {
            String factorCode = bean.getFactorCode();
            String factorName = factor.getName() == null ? factorCode : factor.getName();
            String mn = monitor.getMn();
            String monitorName = monitor.getMonitorName();
            String warnTime = CommonsUtil.dateFormat(bean.getDataTime(), "yyyy-MM-dd HH:mm:ss");
            List<WarnRuleBean> warnRuleList = null;
            if (cn.equals("2061")) {
                warnRuleList = this.getHourOverproofWarnRule(mn, factorCode);
            } else if (cn.equals("2031")) {
                warnRuleList = this.getDayOverproofWarnRule(mn, factorCode);
            }

            WarnRuleBean warnRuleSt = null;
            double warnSt = 0.0D;
            if (warnRuleList != null) {
                for(int j = 0; j < warnRuleList.size(); ++j) {
                    double overMax = ((WarnRuleBean)warnRuleList.get(j)).getMax();
                    double overMin = ((WarnRuleBean)warnRuleList.get(j)).getMin();
                    WarnRuleBean overWarnRule = (WarnRuleBean)warnRuleList.get(j);
                    if (overMax != 0.0D && val > overMax) {
                        result = overWarnRule.getLevel();
                        warnRuleSt = overWarnRule;
                        warnSt = overMax;
                        break;
                    }

                    if (val < overMin) {
                        result = -overWarnRule.getLevel();
                        warnRuleSt = overWarnRule;
                        warnSt = overMin;
                        break;
                    }
                }
            }

            if (factor.getZsFlag() == 1) {
                bean.setZsAvgState(result);
            } else {
                bean.setAvgState(result);
            }

            if (result == 9) {
                return true;
            } else {
                String message_level = warnRuleSt.getLevel() == 1 ? "超标" : "预警";
                String over_level = result > 0 ? "上限" : "下限";
                String warnMessage = monitorName + "[" + factorName + "]" + warn_value + val + "于" + warnTime + "超出" + message_level + over_level + "(" + warnSt + ")。";
                this.addWarnlog(bean.getDataTime(), 2, warnRuleSt, monitor, monitor.getMonitorType(), factorCode, warnMessage);
                return false;
            }
        }
    }

    public void checkSurplus(List<WarnRuleBean> warnRuleList, MonitorBean monitor, double balance, Date dataTime) {
        if (warnRuleList != null) {
            String monitorName = monitor.getMonitorName();
            String warnTime = CommonsUtil.dateCurrent("yyyy-MM-dd HH:mm:ss");

            for(int j = 0; j < warnRuleList.size(); ++j) {
                double supplusMin = ((WarnRuleBean)warnRuleList.get(j)).getMin();
                WarnRuleBean supplusWarnRule = (WarnRuleBean)warnRuleList.get(j);
                if (supplusMin >= 0.0D && balance < supplusMin) {
                    String warnMessage = monitorName + "于" + warnTime + "余额不足" + supplusMin + "元，当前余额" + balance + "元。";
                    this.checkWarnlog(dataTime, supplusWarnRule, 4, warnMessage, monitor, (String)null);
                    break;
                }
            }
        }

    }

    public boolean checkMHDAbnormal(WarnRuleBean warnRule, MonitorBean monitor, MonitorDeviceBean device, FactorBean factor, DataFactorBean bean, String cn) {
        String factorCode = bean.getFactorCode();
        String factorName = factor.getName() == null ? factorCode : factor.getName();
        boolean isWarn = false;
        String warnMessage = "";
        if (factor.getZsFlag() == 1) {
            if (bean.getZsAvg() != null) {
                bean.setZsAvgState(9);
                if (device.getNormalMax() != 0.0D && bean.getZsAvg() > device.getNormalMax()) {
                    bean.setZsAvgState(6);
                    isWarn = true;
                    warnMessage = warnMessage + "折算平均值（" + bean.getZsAvg() + "）超过量程上限（" + device.getNormalMax() + "），";
                } else if (bean.getZsAvg() < device.getNormalMin()) {
                    bean.setZsAvgState(-6);
                    isWarn = true;
                    warnMessage = warnMessage + "折算平均值（" + bean.getZsAvg() + "）低于量程下限（" + device.getNormalMin() + "），";
                }
            }

            if (bean.getZsMax() != null) {
                bean.setZsMaxState(9);
                if (device.getNormalMax() != 0.0D && bean.getZsMax() > device.getNormalMax()) {
                    bean.setZsMaxState(6);
                    isWarn = true;
                    warnMessage = warnMessage + "折算最大值（" + bean.getZsMax() + "）超过量程上限（" + device.getNormalMax() + "），";
                }
            }

            if (bean.getZsMin() != null) {
                bean.setZsMinState(9);
                if (bean.getZsMin() < device.getNormalMin()) {
                    bean.setZsMinState(-6);
                    isWarn = true;
                    warnMessage = warnMessage + "折算最小值（" + bean.getZsMin() + "）低于量程下限（" + device.getNormalMin() + "），";
                }
            }
        } else {
            if (bean.getAvg() != null) {
                bean.setAvgState(9);
                if (device.getNormalMax() != 0.0D && bean.getAvg() > device.getNormalMax()) {
                    bean.setAvgState(6);
                    isWarn = true;
                    warnMessage = warnMessage + "平均值（" + bean.getAvg() + "）超过量程上限（" + device.getNormalMax() + "），";
                } else if (bean.getAvg() < device.getNormalMin()) {
                    bean.setAvgState(-6);
                    isWarn = true;
                    warnMessage = warnMessage + "平均值（" + bean.getAvg() + "）低于量程下限（" + device.getNormalMin() + "），";
                }
            }

            if (bean.getMax() != null) {
                bean.setMaxState(9);
                if (device.getNormalMax() != 0.0D && bean.getMax() > device.getNormalMax()) {
                    bean.setMaxState(6);
                    isWarn = true;
                    warnMessage = warnMessage + "最大值（" + bean.getMax() + "）超过量程上限（" + device.getNormalMax() + "），";
                }
            }

            if (bean.getMin() != null) {
                bean.setMinState(9);
                if (bean.getMin() < device.getNormalMin()) {
                    bean.setMinState(-6);
                    isWarn = true;
                    warnMessage = warnMessage + "最小值（" + bean.getMin() + "）低于量程下限（" + device.getNormalMin() + "），";
                }
            }
        }

        if (!cn.equals("2051") && isWarn) {
            String warnTime = "";
            if (cn.equals("2061")) {
                warnMessage = "小时数据" + warnMessage;
                warnTime = CommonsUtil.dateFormat(bean.getDataTime(), "yyyy-MM-dd HH") + "时";
            } else if (cn.equals("2031")) {
                warnMessage = "日数据" + warnMessage;
                warnTime = CommonsUtil.dateFormat(bean.getDataTime(), "yyyy-MM-dd");
            }

            if (warnMessage.endsWith("，")) {
                warnMessage = warnMessage.substring(0, warnMessage.length() - 1);
            }

            warnMessage = warnMessage + "。";
            warnMessage = monitor.getMonitorName() + "[" + factorName + "]于" + warnTime + warnMessage;
            this.checkWarnlog(bean.getDataTime(), warnRule, 7, warnMessage, monitor, factorCode);
        }

        return !isWarn;
    }

    public boolean checkDataError(WarnRuleBean warnRule, MonitorBean monitor, FactorBean factor, DataFactorBean bean, String cn) {
        String factorCode = bean.getFactorCode();
        String factorName = factor.getName() == null ? factorCode : factor.getName();
        boolean isWarn = false;
        String warnMessage = "";
        if (this.isCheckDataError) {
            if (factor.getZsFlag() == 1) {
                if (bean.getZsMin() != null && bean.getZsAvg() != null && bean.getZsMax() != null) {
                    bean.setZsMinState(9);
                    bean.setZsAvgState(9);
                    bean.setZsMaxState(9);
                    if (bean.getZsMin() > bean.getZsAvg()) {
                        bean.setZsMinState(7);
                        bean.setZsAvgState(7);
                        isWarn = true;
                        warnMessage = warnMessage + "折算最小值（" + bean.getZsMin() + "）大于折算平均值（" + bean.getZsAvg() + "），";
                    }

                    if (bean.getZsAvg() > bean.getZsMax()) {
                        bean.setZsAvgState(7);
                        bean.setZsMaxState(7);
                        isWarn = true;
                        warnMessage = warnMessage + "折算平均值（" + bean.getZsAvg() + "）大于折算最大值（" + bean.getZsMax() + "），";
                    }
                }
            } else if (bean.getMin() != null && bean.getAvg() != null && bean.getMax() != null) {
                bean.setMinState(9);
                bean.setAvgState(9);
                bean.setMaxState(9);
                if (bean.getMin() > bean.getAvg()) {
                    bean.setMinState(7);
                    bean.setAvgState(7);
                    isWarn = true;
                    warnMessage = warnMessage + "最小值（" + bean.getMin() + "）大于平均值（" + bean.getAvg() + "），";
                }

                if (bean.getAvg() > bean.getMax()) {
                    bean.setAvgState(7);
                    bean.setMaxState(7);
                    isWarn = true;
                    warnMessage = warnMessage + "平均值（" + bean.getAvg() + "）大于最大值（" + bean.getMax() + "），";
                }
            }
        }

        if (!cn.equals("2051") && isWarn) {
            String warnTime = "";
            if (cn.equals("2061")) {
                warnMessage = "小时数据" + warnMessage;
                warnTime = CommonsUtil.dateFormat(bean.getDataTime(), "yyyy-MM-dd HH") + "时";
            } else if (cn.equals("2031")) {
                warnMessage = "日数据" + warnMessage;
                warnTime = CommonsUtil.dateFormat(bean.getDataTime(), "yyyy-MM-dd");
            }

            if (warnMessage.endsWith("，")) {
                warnMessage = warnMessage.substring(0, warnMessage.length() - 1);
            }

            warnMessage = warnMessage + "。";
            warnMessage = monitor.getMonitorName() + "[" + factorName + "]于" + warnTime + warnMessage;
            this.checkWarnlog(bean.getDataTime(), warnRule, 8, warnMessage, monitor, factorCode);
        }

        return !isWarn;
    }

    public boolean checkCouError(WarnRuleBean warnRule, MonitorBean monitor, FactorBean factor, DataFactorBean bean, String cn) {
        String factorCode = bean.getFactorCode();
        String factorName = factor.getName() == null ? factorCode : factor.getName();
        boolean isWarn = false;
        String warnMessage = "";
        double couMax = factor.getErrorMax();
        double couMin = factor.getErrorMin();
        if (factor.getZsFlag() == 1) {
            if (bean.getZsCou() != null) {
                bean.setZsCouState(9);
                if (couMax > 0.0D && bean.getZsCou() > couMax) {
                    bean.setZsCouState(8);
                    isWarn = true;
                    warnMessage = "折算COU值（" + bean.getZsCou() + "）超出排放量异常上限（" + couMax + "），";
                } else if (couMin >= 0.0D && bean.getZsCou() < couMin) {
                    bean.setZsCouState(-8);
                    isWarn = true;
                    warnMessage = "折算COU值（" + bean.getZsCou() + "）低于排放量异常下限（" + couMin + "），";
                }
            }
        } else if (bean.getCou() != null) {
            bean.setCouState(9);
            if (couMax > 0.0D && bean.getCou() > couMax) {
                bean.setCouState(8);
                isWarn = true;
                warnMessage = "COU值（" + bean.getCou() + "）超出排放量异常上限（" + couMax + "），";
            } else if (couMin >= 0.0D && bean.getCou() < couMin) {
                bean.setCouState(-8);
                isWarn = true;
                warnMessage = "COU值（" + bean.getCou() + "）低于排放量异常下限（" + couMin + "），";
            }
        }

        if (!cn.equals("2051") && isWarn) {
            String warnTime = "";
            if (cn.equals("2061")) {
                warnMessage = "小时数据" + warnMessage;
                warnTime = CommonsUtil.dateFormat(bean.getDataTime(), "yyyy-MM-dd HH") + "时";
            } else if (cn.equals("2031")) {
                warnMessage = "日数据" + warnMessage;
                warnTime = CommonsUtil.dateFormat(bean.getDataTime(), "yyyy-MM-dd");
            }

            if (warnMessage.endsWith("，")) {
                warnMessage = warnMessage.substring(0, warnMessage.length() - 1);
            }

            warnMessage = warnMessage + "。";
            warnMessage = monitor.getMonitorName() + "[" + factorName + "]于" + warnTime + warnMessage;
            this.checkWarnlog(bean.getDataTime(), warnRule, 8, warnMessage, monitor, factorCode);
        }

        return !isWarn;
    }
}
