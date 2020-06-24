package business.processor.bean;

public class AirqAQIBean {
    private String code;
    private double lowValue;
    private double highValue;
    private double liAqi;
    private double hiAqi;
    private int type;

    public AirqAQIBean() {
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getLowValue() {
        return this.lowValue;
    }

    public void setLowValue(double lowValue) {
        this.lowValue = lowValue;
    }

    public double getHighValue() {
        return this.highValue;
    }

    public void setHighValue(double highValue) {
        this.highValue = highValue;
    }

    public double getLiAqi() {
        return this.liAqi;
    }

    public void setLiAqi(double liAqi) {
        this.liAqi = liAqi;
    }

    public double getHiAqi() {
        return this.hiAqi;
    }

    public void setHiAqi(double hiAqi) {
        this.hiAqi = hiAqi;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
