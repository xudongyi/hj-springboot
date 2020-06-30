package business.processor.service;

import business.processor.bean.FactorBean;
import business.redis.RedisService;
import business.util.CommonsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service("factorService")
@Slf4j
public class FactorService {
    @Autowired
    private RedisService redisService;

    public FactorService() {
    }

    public Map<String, FactorBean> getFactors(int factorType) {
        String map = this.redisService.getMapValue("factor_type_map", String.valueOf(factorType));
        Map<String, FactorBean> result = new HashMap();
        if (map != null) {
            List<FactorBean> factors = (List) CommonsUtil.toJsonObject(map, FactorBean.class);
            if (factors != null) {
                for(int i = 0; i < factors.size(); ++i) {
                    FactorBean factor = (FactorBean)factors.get(i);
                    factor.setCode(factor.getCode().toUpperCase());
                    factor.setOldCode(factor.getOldCode().toUpperCase());
                    result.put(factor.getCode(), factor);
                }
            }
        } else {
            log.debug("Redis提示[获取某类所有污染因子]:未取到值");
        }

        return result;
    }

    public FactorBean getPollutionByOldCode(String oldFactorCode, int factorType) {
        FactorBean result = null;
        Map factors = this.getFactors(factorType);

        try {
            Iterator var5 = factors.keySet().iterator();

            while(var5.hasNext()) {
                String factorCode = (String)var5.next();
                if (oldFactorCode.equals(((FactorBean)factors.get(factorCode)).getOldCode().toUpperCase())) {
                    result = (FactorBean)factors.get(factorCode);
                    result.setCode(result.getCode().toUpperCase());
                    result.setOldCode(result.getOldCode().toUpperCase());
                    break;
                }
            }
        } catch (Exception var7) {
        }

        return result;
    }
}