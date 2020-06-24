package business.receiver.bean;

import lombok.Data;

@Data
public class MonitorBean {
    private String monitorId;
    private String monitorName;
    private String companyId;
    private int monitorType;
    private String mn;
    private int monitorStatus;
    private int currentRate;
    private int minitRate;
    private int onlineStatus;
    private int deviceStatus;
    private int dataStatus;
    private int valveStatus;
    private int noiseType;
    private int surfWaterType;

}