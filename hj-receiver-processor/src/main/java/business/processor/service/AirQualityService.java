//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package business.processor.service;

import business.processor.bean.AirqAQIBean;
import business.processor.mapper.AirQualityMapper;
import business.receiver.mapper.SysDeviceMessageMapper;
import business.redis.RedisService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import business.util.CommonsUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("airQualityService")
@Slf4j
public class AirQualityService {
    @Autowired
    private RedisService redisService;
    @Autowired
    private SysDeviceMessageMapper sysDeviceMessageMapper;
    @Autowired
    private AirQualityMapper airQualityMapper;


    public AirQualityService() {
    }

    public double getAQI(String factorCode, int type, double avg) {
        double aqi = -1.0D;
        String result = this.redisService.getMapValue("airq_aqi_map", factorCode + "-" + type);
        if (StringUtils.isNotEmpty(result)) {
            List<AirqAQIBean> list = (List) CommonsUtil.toJsonObject(result, AirqAQIBean.class);
            if (list != null) {
                for(int i = 0; i < list.size(); ++i) {
                    if (list.get(i).getHighValue() >= avg && (list.get(i)).getLowValue() <= avg) {
                        aqi = this.calcuteAQI((list.get(i)).getHiAqi(), (list.get(i)).getLiAqi(), (list.get(i)).getHighValue(), (list.get(i)).getLowValue(), avg);
                        break;
                    }
                }

                AirqAQIBean maxbean =list.get(list.size() - 1);
                if (aqi == -1.0D && avg > maxbean.getHighValue()) {
                    aqi = this.calcuteAQI(maxbean.getHiAqi(), maxbean.getLiAqi(), maxbean.getHighValue(), maxbean.getLowValue(), avg);
                }
            }
        } else {
            this.log.debug("Redis提示[获取空气质量分指数计算标准" + factorCode + "-" + type + "]:未取到值");
        }

        return aqi;
    }

    public void initialAQI() {
        Map<String, List<AirqAQIBean>> result = new HashMap();
        List<Map<String, Object>> data = airQualityMapper.getAIRQ_AQI();
        if (data != null) {
            for(int i = 0; i < data.size(); ++i) {
                try {
                    AirqAQIBean v = new AirqAQIBean();
                    Map<String, Object> map = data.get(i);
                    v.setCode((String)map.get("factor_code"));
                    if (map.get("li_aqi") != null) {
                        v.setLiAqi((Double)map.get("li_aqi"));
                    }

                    if (map.get("hi_aqi") != null) {
                        v.setHiAqi((Double)map.get("hi_aqi"));
                    }

                    if (map.get("l_value") != null) {
                        v.setLowValue((Double)map.get("l_value"));
                    }

                    if (map.get("h_value") != null) {
                        v.setHighValue((Double)map.get("h_value"));
                    }

                    if (map.get("type") != null) {
                        v.setType((Integer)map.get("type"));
                    }

                    String key = v.getCode().toUpperCase() + "-" + v.getType();
                    if (result.containsKey(key)) {
                        ((List)result.get(key)).add(v);
                    } else {
                        List<AirqAQIBean> list = new ArrayList();
                        list.add(v);
                        result.put(key, list);
                    }
                } catch (Exception var8) {
                    this.log.info("初始化空气质量分指数计算标准出错:" + var8.getMessage());
                }
            }
        }

        if (!result.isEmpty()) {
            Iterator var9 = result.keySet().iterator();

            while(var9.hasNext()) {
                String key = (String)var9.next();
                this.redisService.setMapValue("airq_aqi_map", key, result.get(key));
            }
        } else {
            this.log.info("初始化空气质量分指数计算标准失败:未取到值");
        }

    }

    public String getLevel(double aqi) {
        aqi = CommonsUtil.numberFormat(aqi, 0);
        String result = "7";
        Map<String, String> map = this.redisService.getMapAll("airq_level_map");
        if (map != null && !map.isEmpty()) {
            Iterator var5 = map.keySet().iterator();

            while(var5.hasNext()) {
                String level = (String)var5.next();

                try {
                    Map<String, Object> data = (Map)CommonsUtil.toJsonObject((String)map.get(level), (Class)null);
                    if (data != null) {
                        double aqi_h = 0.0D;
                        if (data.get("aqi_h") != null) {
                            aqi_h = Double.valueOf(String.valueOf(data.get("aqi_h")));
                        }

                        double aqi_l = 0.0D;
                        if (data.get("aqi_l") != null) {
                            aqi_l = Double.valueOf(String.valueOf(data.get("aqi_l")));
                        }

                        if (aqi_h >= aqi && aqi_l <= aqi) {
                            result = data.get("level")+"";
                            break;
                        }
                    }
                } catch (Exception var12) {
                    this.log.debug("Redis错误[获取空气质量等级]:" + var12.getMessage());
                }
            }
        } else {
            this.log.debug("Redis提示[获取空气质量等级]:未取到值");
            result = "";
        }

        return result;
    }

    public void initialLevel() {
        List<Map<String, Object>> data = airQualityMapper.getAIRQ_LEVEL();
        if (data != null && data.size() > 0) {
            for(int i = 0; i < data.size(); ++i) {
                try {
                    this.redisService.setMapValue("airq_level_map",data.get(i).get("level")+"", data.get(i));
                } catch (Exception var4) {
                    this.log.debug("Redis提示[初始化空气质量等级]出错:" + var4.getMessage());
                }
            }
        } else {
            this.log.debug("Redis提示[初始化空气质量等级]:未取到值");
        }

    }

    private double calcuteAQI(double HiAQI, double LiAQI, double HValue, double LValue, double value) {
        double IAQI = 0.0D;
        IAQI = (HiAQI - LiAQI) / (HValue - LValue) * (value - LValue) + LiAQI;
        IAQI = CommonsUtil.numberFormat(IAQI, 0);
        return IAQI;
    }
}
