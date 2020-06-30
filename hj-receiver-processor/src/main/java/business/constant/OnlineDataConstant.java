package business.constant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnlineDataConstant {
    public static final int FACTOR_TYPE_WATER = 1;
    public static final int FACTOR_TYPE_AIR = 2;
    public static final int FACTOR_TYPE_AIRQ = 3;
    public static final int FACTOR_TYPE_RAD = 4;
    public static final int FACTOR_TYPE_SURFWATER = 5;
    public static final int FACTOR_TYPE_OIL = 6;
    public static final int FACTOR_TYPE_NOISE = 7;
    public static final int FACTOR_TYPE_INCINERATOR = 8;
    public static final int FACTOR_TYPE_VOC = 9;
    public static final int DATA_PARSER_CODE_DEFAULT = 0;
    public static final int DATA_PARSER_CODE_DOING = 1;
    public static final int DATA_PARSER_CODE_OK = 2;
    public static final int DATA_PARSER_CODE_NO = 6;
    public static final int DATA_PARSER_CODE_ERROR = 8;
    public static final int DATA_PARSER_CODE_PARTOK = 9;
    public static final String ST_AIR = "31";
    public static final String ST_WATER = "32";
    public static final String ST_AIRQ = "22";
    public static final String ST_NOISE = "23";
    public static final String ST_SURFWATER = "21";
    public static final String ST_VOC = "27";
    public static final String CN_CURRENT = "2011";
    public static final String CN_MINUTE = "2051";
    public static final String CN_HOUR = "2061";
    public static final String CN_DAY = "2031";
    public static final String CN_AIRINCINERATOR = "3020";
    public static final String CN_9011 = "9011";
    public static final String CN_9012 = "9012";
    public static final String CN_9013 = "9013";
    public static final String CN_9014 = "9014";
    public static final String CN_SAMPLE_GB = "3015";
    public static final String CN_VALVE_STATE_TZ = "3715";
    public static final String CN_VALVE_STATE_HUIHUAN = "8803";
    public static final String CN_SAMPLE_HUIHUAN = "8804";
    public static final String CN_SURPLUS_TZ = "4013";
    public static final int WARN_TYPE_OFFLINE = 1;
    public static final int WARN_TYPE_OVERPROFF = 2;
    public static final int WARN_TYPE_DEVICE = 3;
    public static final int WARN_TYPE_SURPLUS = 4;
    public static final int WARN_TYPE_TOTAL = 5;
    public static final int WARN_TYPE_FIX = 6;
    public static final int WARN_TYPE_ABNORMAL = 7;
    public static final int WARN_TYPE_ERROR = 8;
    public static final int WARN_TYPE_SURFWATER = 9;
    public static final int WARN_TYPE_NOISE = 10;
    public static final int WARN_TYPE_AIRQ = 11;
    public static final int VALUE_STATE_WARN_UP = 1;
    public static final int VALUE_STATE_WARN_LOW = -1;
    public static final int VALUE_STATE_PRE1_UP = 2;
    public static final int VALUE_STATE_PRE1_LOW = -2;
    public static final int VALUE_STATE_PRE2_UP = 3;
    public static final int VALUE_STATE_PRE2_LOW = -3;
    public static final int VALUE_STATE_PRE3_UP = 4;
    public static final int VALUE_STATE_PRE3_LOW = -4;
    public static final int VALUE_STATE_PRE4_UP = 5;
    public static final int VALUE_STATE_PRE4_LOW = -5;
    public static final int VALUE_STATE_ABNORMAL_UP = 6;
    public static final int VALUE_STATE_ABNORMAL_LOW = -6;
    public static final int VALUE_STATE_ERROR_UP = 8;
    public static final int VALUE_STATE_ERROR_LOW = -8;
    public static final int VALUE_STATE_ERROR = 7;
    public static final int VALUE_STATE_NORMAL = 9;
    public static final int MESSAGE_STATUS_0 = 0;
    public static final int MESSAGE_STATUS_1 = 1;
    public static final int MESSAGE_STATUS_2 = 2;
    public static final int MESSAGE_STATUS_3 = 3;
    public static final int VALVE_STATUS_CLOSED = 0;
    public static final int VALVE_STATUS_OPEN = 1;
    public static final String AIRQ_LEVEL_1 = "一级";
    public static final String AIRQ_LEVEL_2 = "二级";
    public static final int NOISE_TYPE_1 = 1;
    public static final int NOISE_TYPE_2 = 2;
    public static final int NOISE_TYPE_3 = 3;
    public static final int NOISE_TYPE_4 = 4;
    public static final int NOISE_TYPE_5 = 5;
    public static final int NOISE_TYPE_6 = 6;
    public static final String NOISE_CODE_LN = "LN";
    public static final String NOISE_CODE_LD = "LD";
    public static final int MONITOR_STATUS_NORMAL = 1;
    public static final int MONITOR_STATUS_MODIFY = 2;
    public static final int MONITOR_STATUS_STOP = 3;
    public static final int REVERSE_TYPE_OPNE = 1;
    public static final int REVERSE_TYPE_COLSE = 2;
    public static final int REVERSE_TYPE_SAMPLE = 3;
    public static final int REVERSE_TYPE_SETSYSTIME = 4;
    public static final int REVERSE_TYPE_GETSYSTIME = 5;
    public static final int REVERSE_TYPE_HOUR = 6;
    public static final int REVERSE_TYPE_DAY = 7;
    public static final int REVERSE_TAG_0 = 0;
    public static final int REVERSE_TAG_1 = 1;
    public static final int REVERSE_TAG_2 = 2;
    public static final int REVERSE_TAG_3 = 3;
    public static final int REVERSE_TAG_4 = 4;
    public static final int REVERSE_TAG_5 = 5;
    public static final String DEVICE_NORMA_DATA = "0";
    public static final String DEVICE_ERROR_DATA = "1";
    public static final int DEVICE_NORMA_MONITOR = 1;
    public static final int DEVICE_ERROR_MONITOR = 0;
    public static final int ISOVER_TRUE = 1;
    public static final int ISOVER_FALSE = 2;
    public static final int useFlag = 1;
    public static final int unuseFlag = 0;
    public static final int offlineStatus = 0;
    public static final int onlineStatus = 1;
    public static final List<String> SURF_WATER_LEVEL = Arrays.asList("I类", "II类", "III类", "IV类", "V类", "劣V类");
    public static final List<String> SURF_WATER_LEVEL_DESC = Arrays.asList("优", "优", "良好", "轻度污染", "中度污染", "重度污染");
    public static final List<String> NOISE_LEVEL = Arrays.asList("0类", "1类", "2类", "3类", "4a类", "4b类");
    public static final Map<String, Double> AQI_LEVEL = new HashMap<String, Double>() {
        private static final long serialVersionUID = 1L;

        {
            this.put("一级", 1.0D);
            this.put("二级", 2.0D);
            this.put("三级", 3.0D);
            this.put("四级", 4.0D);
            this.put("五级", 5.0D);
            this.put("六级", 6.0D);
            this.put("爆表", 7.0D);
        }
    };
    public static final Map<String, String> AQI_LEVEL_DESC = new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;

        {
            this.put("一级", "优");
            this.put("二级", "良");
            this.put("三级", "轻度污染");
            this.put("四级", "中度污染");
            this.put("五级", "重度污染");
            this.put("六级", "严重污染");
            this.put("爆表", "严重污染");
        }
    };

    public OnlineDataConstant() {
    }
}