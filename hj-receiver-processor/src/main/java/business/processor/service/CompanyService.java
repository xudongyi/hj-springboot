package business.processor.service;

import business.processor.bean.ScheduleBean;
import business.receiver.mapper.MyBaseMapper;
import business.redis.RedisService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import business.util.CommonsUtil;
import business.util.SqlBuilder;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("companyService")
@Slf4j
public class CompanyService {
    @Autowired
    private RedisService redisService;
    @Autowired
    private MyBaseMapper myBaseMapper;

    public CompanyService() {
    }

    public ScheduleBean getCompanySchedule(String companyId, String factorCode) {
        ScheduleBean scheduleBean = null;
        if (StringUtils.isNotEmpty(companyId) && StringUtils.isNotEmpty(factorCode)) {
            String result = this.redisService.getMapValue("company_schedule_map", companyId);
            Map<String, Object> map = null;
            if (StringUtils.isNotEmpty(result)) {
                map = (Map) CommonsUtil.toJsonObject(result, (Class)null);
                if (map != null) {
                    Object schedule = ((Map)map).get(factorCode);
                    if (schedule != null) {
                        scheduleBean = (ScheduleBean)CommonsUtil.toJsonObject(schedule.toString(), ScheduleBean.class);
                    } else {
                        log.debug("Redis提示[获取企业" + companyId + "排污进度计划" + factorCode + "]:未取到值");
                    }
                }
            } else {
                log.debug("Redis提示[获取企业" + companyId + "排污进度计划]:未取到值");
            }

            if (scheduleBean == null) {
                List<Object> params = new ArrayList();
                params.add(CommonsUtil.dateCurrent("yyyy"));
                params.add(companyId);
                params.add(factorCode);
                List<Map<String, Object>> yearData = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql("select amount from company_year_permit where year=''{0}'' and company_id=''{1}'' and code=''{2}'' ", params));
                List<Map<String, Object>> monthData = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql("select amount,month from company_month_permit where year=''{0}'' and company_id=''{1}'' and code=''{2}'' ", params));
                scheduleBean = new ScheduleBean();
                if (yearData != null && yearData.size() > 0) {
                    Object year = ((Map)yearData.get(0)).get("amount");
                    if (year != null) {
                        scheduleBean.setYear((Double)year);
                    } else {
                        scheduleBean.setYear(0.0D);
                    }
                }

                if (monthData == null) {
                    scheduleBean.setMonthArray(new double[]{0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D});
                } else {
                    double[] monthArray = new double[12];
                    int i = 1;

                    while(true) {
                        if (i > 12) {
                            scheduleBean.setMonthArray(monthArray);
                            break;
                        }

                        double month = 0.0D;

                        for(int j = 0; j < monthData.size(); ++j) {
                            if (Integer.valueOf(((Map)monthData.get(j)).get("month").toString()) == i) {
                                month = (Double)((Map)monthData.get(j)).get("amount");
                            }
                        }

                        monthArray[i - 1] = month;
                        ++i;
                    }
                }

                if (map != null) {
                    ((Map)map).put(factorCode, scheduleBean);
                } else {
                    map = new HashMap();
                    ((Map)map).put(factorCode, scheduleBean);
                }

                this.redisService.setMapValue("company_schedule_map", companyId, map);
            }
        }

        return scheduleBean;
    }

    public Map<String, Object> getCompany(String cid) {
        Map<String, Object> map = new HashMap();
        if (StringUtils.isNotEmpty(cid)) {
            String result = this.redisService.getMapValue("company_map", cid);
            if (StringUtils.isNotEmpty(result)) {
                map = (Map)CommonsUtil.toJsonObject(result, (Class)null);
            }
        } else {
            this.log.debug("Redis提示[获取企业" + cid + "基础信息]:未取到值");
        }

        return (Map)map;
    }

    public void setTotalStatus(String companyId, int companyTotalStatus) {
        Map<String, Object> company = this.getCompany(companyId);
        if (company != null && company.get("totalStatus") != null) {
            int redis_companyTotalStatus = (Integer)company.get("totalStatus");
            if (redis_companyTotalStatus != companyTotalStatus) {
                boolean isEdit = false;
                if (companyTotalStatus < 5) {
                    if (companyTotalStatus < redis_companyTotalStatus) {
                        isEdit = true;
                    }
                } else {
                    isEdit = true;
                }

                if (isEdit) {
                    company.put("totalStatus", companyTotalStatus);
                    this.redisService.setMapValue("company_map", companyId, company);
                    List<Object> params = new ArrayList();
                    params.add(companyTotalStatus);
                    params.add(companyId);
                    this.myBaseMapper.sqlExcute(SqlBuilder.buildSql("update company_base set TOTAL_STATUS='{0}' where ID=''{1}''", params));
                }
            }
        } else {
            this.log.debug("Redis提示[获取企业" + companyId + "基础信息,设置缓存企业总量状态]:未取到值");
        }

    }
}
