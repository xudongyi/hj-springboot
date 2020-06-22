package business.service;

import business.cache.DataCache;
import business.entity.ServerConvert;
import business.util.CRC_16;
import business.util.DataFormat;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.util.List;

public class ConvertMessageService {

    public static String convert(int port, String message) {
        if (StringUtils.isNotEmpty(message)) {
            List<ServerConvert> convertList = DataCache.SERVER_CONVERT_CACHE.get(port);
            if (convertList != null && convertList.size() > 0) {
                for (ServerConvert v : convertList) {
                    String condition = v.getConvertCondition();
                    String convertBefore = v.getConvertBefore();
                    String convertAfter = v.getConvertAfter();
                    if (message.indexOf(condition) != -1) {
                        message = message.replaceAll(convertBefore, convertAfter);
                    }
                }
            }

            if (message.startsWith("##") && (message.endsWith("&&")
                    || message.lastIndexOf("&&") == message.length() - 4)) {
                message = message.substring(2, message.lastIndexOf("&&"));
                message = "##" + DataFormat.int2str(message.length(), 4) + message + CRC_16.CRC16(message);
            }
        }

        return message;
    }
}