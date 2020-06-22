package business.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class CommonsUtil {
    public CommonsUtil() {
    }

    public static String createUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }

    public static final boolean isWin() {
        Properties prop = System.getProperties();
        String os = prop.getProperty("os.name");
        return os.startsWith("win") || os.startsWith("Win");
    }

    public static String getProjectName() {
        String path = CommonsUtil.class.getResource("/").getPath();
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        if (path.lastIndexOf("/") != -1) {
            path = path.substring(0, path.lastIndexOf("/"));
        }

        if (path.lastIndexOf("/") != -1) {
            path = path.substring(0, path.lastIndexOf("/"));
        }

        return path.substring(path.lastIndexOf("/") + 1, path.length());
    }

    public static double numberFormat(double number) {
        BigDecimal formatNumber = new BigDecimal(number);
        double result = formatNumber.setScale(2, 4).doubleValue();
        return result;
    }

    public static double numberFormat(double number, int scale) {
        BigDecimal formatNumber = new BigDecimal(number);
        double result = formatNumber.setScale(scale, 4).doubleValue();
        return result;
    }

    public static double numberFormat(double number, int scale, int roundingMode) {
        BigDecimal formatNumber = new BigDecimal(number);
        double result = formatNumber.setScale(scale, roundingMode).doubleValue();
        return result;
    }

    public static final boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }

    public static boolean isNumeric(char c) {
        return Character.isDigit(c);
    }

    public static String toJsonStr(Object o) {
        String result = "";

        try {
            if (o instanceof Collection) {
                result = JSONArray.toJSONString(o);
            } else if (o instanceof Map) {
                result = JSONArray.toJSONString(o);
            } else if (o instanceof Object[]) {
                result = JSONArray.toJSONString(o);
            } else {
                result = JSONObject.toJSONString(o);
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return result;
    }

    public static Object toJsonObject(String str, Class<?> entity) {
        Object result = null;

        try {
            if (str.indexOf("[") != -1) {
                if (entity != null) {
                    result = JSONArray.parseArray(str, entity);
                } else {
                    result = JSONArray.parse(str);
                }
            } else if (entity != null) {
                result = JSONObject.parseObject(str, entity);
            } else {
                result = JSONObject.parse(str);
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return result;
    }
}