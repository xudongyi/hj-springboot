package business.redis;

import business.util.CommonsUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service("redisService")
@Slf4j
public class RedisService {
    private static JedisPool pool = null;
    @Value("${redis.host}")
    private String redis_ip;
    @Value("${redis.port}")
    private int redis_port;
    @Value("${redis.password}")
    private String redis_password;
    private int redis_maxTotal = 500;
    private int redis_maxIdle = 50;
    private long redis_maxWaitMillis = 100000L;
    private boolean redis_testOnBorrow = true;

    public RedisService() {
    }

    @PostConstruct
    public void initial() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(this.redis_maxTotal);
        config.setMaxIdle(this.redis_maxIdle);
        config.setMaxWaitMillis(this.redis_maxWaitMillis);
        config.setTestOnBorrow(this.redis_testOnBorrow);
        if (StringUtils.isEmpty(this.redis_password)) {
            pool = new JedisPool(config, this.redis_ip, this.redis_port, 3000);
        } else {
            pool = new JedisPool(config, this.redis_ip, this.redis_port, 3000, this.redis_password);
        }

    }

    private JedisPool getPool() {
        return pool;
    }

    public Map<String, String> getMapAll(String field) {
        Jedis jedis = null;
        Object result = new HashMap();

        try {
            jedis = this.getPool().getResource();
            result = jedis.hgetAll(field);
        } catch (Exception var8) {
            log.error("Redis错误:" + var8.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }

        }

        return (Map)result;
    }

    public String getMapValue(String field, String key) {
        Jedis jedis = null;
        String result = "";

        try {
            jedis = this.getPool().getResource();
            result = jedis.hget(field, key);
        } catch (Exception var9) {
            log.error("Redis错误:" + var9.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }

        }

        return result;
    }

    public String getStringValue(String field) {
        Jedis jedis = null;
        String result = "";

        try {
            jedis = this.getPool().getResource();
            result = jedis.get(field);
        } catch (Exception var8) {
            log.error("Redis错误:" + var8.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }

        }

        return result;
    }

    public void setMapValue(String field, String key, Object val) {
        Jedis jedis = null;

        try {
            jedis = this.getPool().getResource();
            String value = "";
            if (val instanceof String) {
                value = (String)val;
            } else {
                value = CommonsUtil.toJsonStr(val);
            }

            jedis.hset(field, key, value);
        } catch (Exception var9) {
            log.error("Redis错误:" + var9.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }

        }

    }
}