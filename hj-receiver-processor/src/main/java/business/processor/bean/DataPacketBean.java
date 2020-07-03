package business.processor.bean;

import lombok.Data;

import java.util.Date;
import java.util.Map;
@Data
public class DataPacketBean {

    private String sourceId;
    private String qn;
    private String st;
    private String cn;
    private String pw;
    private String mn;
    private int flag;
    private Date dataTime;
    private String content;
    private Map<String, DataFactorBean> dataMap;
}
