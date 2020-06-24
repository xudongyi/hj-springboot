package business.processor.bean;

public class FactorBean {
    private String id;
    private String code;
    private String oldCode;
    private String name;
    private String format;
    private String unit;
    private String totalUnit;
    private double errorMax;
    private double errorMin;
    private int factorType;
    private int totalFlag;
    private int impFlag;
    private int useFlag;
    private int sameFlag;
    private int zsFlag;
    private int factorOrder;
    private String note;

    public FactorBean() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOldCode() {
        return this.oldCode;
    }

    public void setOldCode(String oldCode) {
        this.oldCode = oldCode;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getUnit() {
        return this.unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTotalUnit() {
        return this.totalUnit;
    }

    public void setTotalUnit(String totalUnit) {
        this.totalUnit = totalUnit;
    }

    public int getFactorType() {
        return this.factorType;
    }

    public void setFactorType(int factorType) {
        this.factorType = factorType;
    }

    public int getTotalFlag() {
        return this.totalFlag;
    }

    public void setTotalFlag(int totalFlag) {
        this.totalFlag = totalFlag;
    }

    public int getImpFlag() {
        return this.impFlag;
    }

    public void setImpFlag(int impFlag) {
        this.impFlag = impFlag;
    }

    public int getUseFlag() {
        return this.useFlag;
    }

    public void setUseFlag(int useFlag) {
        this.useFlag = useFlag;
    }

    public int getSameFlag() {
        return this.sameFlag;
    }

    public void setSameFlag(int sameFlag) {
        this.sameFlag = sameFlag;
    }

    public int getFactorOrder() {
        return this.factorOrder;
    }

    public void setFactorOrder(int factorOrder) {
        this.factorOrder = factorOrder;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public double getErrorMax() {
        return this.errorMax;
    }

    public void setErrorMax(double errorMax) {
        this.errorMax = errorMax;
    }

    public double getErrorMin() {
        return this.errorMin;
    }

    public void setErrorMin(double errorMin) {
        this.errorMin = errorMin;
    }

    public int getZsFlag() {
        return this.zsFlag;
    }

    public void setZsFlag(int zsFlag) {
        this.zsFlag = zsFlag;
    }
}