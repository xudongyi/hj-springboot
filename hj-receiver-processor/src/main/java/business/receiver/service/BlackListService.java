package business.receiver.service;

import business.receiver.bean.BlackListBean;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service("blackListService")
@Slf4j
public class BlackListService {
    private static Map<String, String> blackList = new HashMap();
    private static Map<String, String> whiteList = new HashMap();
    private static Map<String, BlackListBean> dataTmpList = new HashMap();
    @Value("${blacklist.max.perhour}")
    private long maxCounts;
    @Value("${blacklist.key}")
    private String blacklistConfig;
    @Value("${whitelist.key}")
    private String whitelistConfig;

    public BlackListService() {
    }

    public boolean isReceive(String key) {
        if (blackList.containsKey(key) && !whiteList.containsKey(key)) {
            return false;
        } else {
            if (!whiteList.containsKey(key) && this.maxCounts > 0L) {
                BlackListBean dataTmp = dataTmpList.get(key);
                if (dataTmp != null) {
                    dataTmp.setCounts(dataTmp.getCounts() + 1L);
                    long time = (new Date()).getTime() - dataTmp.getBeginTime().getTime();
                    if (dataTmp.getCounts() >= this.maxCounts) {
                        dataTmp.setBeginTime(new Date());
                        dataTmp.setCounts(0L);
                        if (time < 3600000L) {
                            blackList.put(key, key);
                            log.error("黑名单新增：" + key);
                        }
                    }
                } else {
                    dataTmp = new BlackListBean();
                    dataTmpList.put(key, dataTmp);
                }
            }

            return true;
        }
    }

    @PostConstruct
    public void initial() {
        String[] whitelists;
        String[] var2;
        int var3;
        int var4;
        String v;
        if (StringUtils.isNotEmpty(this.blacklistConfig)) {
            whitelists = this.blacklistConfig.split(",");
            var2 = whitelists;
            var3 = whitelists.length;

            for(var4 = 0; var4 < var3; ++var4) {
                v = var2[var4];
                blackList.put(v, v);
            }
        }

        if (StringUtils.isNotEmpty(this.whitelistConfig)) {
            whitelists = this.whitelistConfig.split(",");
            var2 = whitelists;
            var3 = whitelists.length;

            for(var4 = 0; var4 < var3; ++var4) {
                v = var2[var4];
                whiteList.put(v, v);
            }
        }

    }

    public static String blacklistReport() {
        String report = "";

        String key;
        for(Iterator var1 = blackList.keySet().iterator(); var1.hasNext(); report = report + key + ",") {
            key = (String)var1.next();
        }

        return report;
    }
}
