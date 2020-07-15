package business.processor.service;

import business.processor.bean.FactorBean;
import business.processor.mapper.PollutionCodeMapper;
import business.redis.RedisService;
import business.util.CommonsUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service("factorService")
@Slf4j
public class FactorService {
    @Autowired
    private RedisService redisService;

    @Autowired
    private PollutionCodeMapper pollutionCodeMapper;

    public FactorService() {
    }

    @PostConstruct
    public void initFactors(){
        List<Map<String,Object>> factors = pollutionCodeMapper.getAllCodes();
        Map<String,List<FactorBean>> redisFactor = new HashMap<>();
        for(Map<String,Object> factor : factors){
            String type = factor.get("type").toString();
            FactorBean factorBean = new FactorBean();
            factorBean.setCode(factor.get("code").toString());
            factorBean.setOldCode(factor.get("old_code").toString());
            if(StringUtils.isNotEmpty((String)factor.get("error_max")))
            factorBean.setErrorMax(Double.parseDouble(factor.get("error_max").toString()));
            if(StringUtils.isNotEmpty((String)factor.get("error_min")))
            factorBean.setErrorMin(Double.parseDouble(factor.get("error_min").toString()));
            factorBean.setFactorOrder(Integer.parseInt(factor.get("sort").toString()));
            factorBean.setFactorType(Integer.parseInt(factor.get("type").toString()));
            factorBean.setFormat(factor.get("format").toString());
            String is_important = factor.get("is_important").toString();
            factorBean.setImpFlag(is_important.equals("Y")?1:0);
            factorBean.setName(factor.get("meaning").toString());
            factorBean.setNote(factor.get("content").toString());
            String is_zs = factor.get("is_zs").toString();
            factorBean.setZsFlag(is_zs.equals("Y")?1:0);
            String is_use = factor.get("is_use").toString();
            factorBean.setUseFlag(is_use.equals("Y")?1:0);
            factorBean.setTotalUnit(factor.get("amount_unit").toString());
            String is_repeat = factor.get("is_repeat").toString();
            factorBean.setSameFlag(is_repeat.equals("Y")?1:0);
            String is_total = factor.get("is_total").toString();
            factorBean.setTotalFlag(is_total.equals("Y")?1:0);
            factorBean.setId(factor.get("id").toString());
            factorBean.setUnit(factor.get("chroma_unit").toString());
            if(redisFactor.containsKey(type)){
                redisFactor.get(type).add(factorBean);
            }else{
                List<FactorBean> factorBeans = new ArrayList<>();
                factorBeans.add(factorBean);
                redisFactor.put(type,factorBeans);
            }
        }

        for(String key : redisFactor.keySet()){
            this.redisService.setMapValue("factor_type_map",key,redisFactor.get(key));
        }
    }

    public Map<String, FactorBean> getFactors(int factorType) {
        String map = this.redisService.getMapValue("factor_type_map", String.valueOf(factorType));
        Map<String, FactorBean> result = new HashMap();
        if (map != null) {
            List<FactorBean> factors = (List) CommonsUtil.toJsonObject(map, FactorBean.class);
            if (factors != null) {
                for(int i = 0; i < factors.size(); ++i) {
                    FactorBean factor = factors.get(i);
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