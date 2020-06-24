package business.processor.bean;

public class SurfWaterLevelBean {
    private String code;
    private String name;
    private int level;
    private double st_max;
    private double st_min;
    private Double overMultiple;
    private Integer isOver;

    public SurfWaterLevelBean() {
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getSt_max() {
        return this.st_max;
    }

    public void setSt_max(double st_max) {
        this.st_max = st_max;
    }

    public double getSt_min() {
        return this.st_min;
    }

    public void setSt_min(double st_min) {
        this.st_min = st_min;
    }

    public Double getOverMultiple() {
        return this.overMultiple;
    }

    public void setOverMultiple(Double overMultiple) {
        this.overMultiple = overMultiple;
    }

    public Integer getIsOver() {
        return this.isOver;
    }

    public void setIsOver(Integer isOver) {
        this.isOver = isOver;
    }
}
