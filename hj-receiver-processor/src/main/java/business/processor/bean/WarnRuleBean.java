package business.processor.bean;

public class WarnRuleBean {
    private int interval;
    private int level;
    private double max;
    private double min;
    private int repeat;
    private int isSend;
    private int isColsed;
    private int isSample;
    private int isUse;

    public WarnRuleBean() {
    }

    public int getInterval() {
        return this.interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getMax() {
        return this.max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return this.min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public int getRepeat() {
        return this.repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public int getIsSend() {
        return this.isSend;
    }

    public void setIsSend(int isSend) {
        this.isSend = isSend;
    }

    public int getIsColsed() {
        return this.isColsed;
    }

    public void setIsColsed(int isColsed) {
        this.isColsed = isColsed;
    }

    public int getIsSample() {
        return this.isSample;
    }

    public void setIsSample(int isSample) {
        this.isSample = isSample;
    }

    public int getIsUse() {
        return this.isUse;
    }

    public void setIsUse(int isUse) {
        this.isUse = isUse;
    }
}