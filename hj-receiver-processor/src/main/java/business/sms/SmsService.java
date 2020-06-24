package business.sms;

import cn.hutool.http.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("smsService")
public class SmsService {
    @Value("${sms.url}")
    private String smsUrl;

    public SmsService() {
    }

    public int sendMessage(String tels, String message) {
        Map param = new HashMap();
        param.put("mobiles", tels);
        param.put("message", message);
        if (!this.smsUrl.endsWith("/")) {
            this.smsUrl = this.smsUrl + "/";
        }

        HttpUtil.post(this.smsUrl + "api/send.do", param);
        return 0;
    }
}