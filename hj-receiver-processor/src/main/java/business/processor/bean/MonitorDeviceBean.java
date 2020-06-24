package business.processor.bean;

public class MonitorDeviceBean {
    private String deviceId;
    private String monitorId;
    private String factorCode;
    private String deviceName;
    private double normalMax;
    private double normalMin;
    private int workCycle;
    private int deviceStatus;

    public MonitorDeviceBean() {
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMonitorId() {
        return this.monitorId;
    }

    public void setMonitorId(String monitorId) {
        this.monitorId = monitorId;
    }

    public String getFactorCode() {
        return this.factorCode;
    }

    public void setFactorCode(String factorCode) {
        this.factorCode = factorCode;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public double getNormalMax() {
        return this.normalMax;
    }

    public void setNormalMax(double normalMax) {
        this.normalMax = normalMax;
    }

    public double getNormalMin() {
        return this.normalMin;
    }

    public void setNormalMin(double normalMin) {
        this.normalMin = normalMin;
    }

    public int getDeviceStatus() {
        return this.deviceStatus;
    }

    public void setDeviceStatus(int deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public int getWorkCycle() {
        return this.workCycle;
    }

    public void setWorkCycle(int workCycle) {
        this.workCycle = workCycle;
    }
}
