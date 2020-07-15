package business.ienum;

public enum FactorType {
    WATER(0, "废水"),
    AIR(1, "废气"),
    VOCS(2, "VOCs"),
    AIRQ(3, "空气质量"),
    SURFWATER(4, "地表水"),
    SOIL(5, "土壤"),
    GROUNDWATER(6, "地下水"),
    RADIOACT(7, "放射源"),
    NOISE(8, "噪声"),
    ELECTRIC(9, "电气");
    // 成员变量
    private String name;
    private int type;

    // 构造方法
    FactorType(int type, String name) {
        this.type = type;
        this.name = name;

    }

    //覆盖方法
    @Override
    public String toString() {
        return this.type + "_" + this.name;
    }

    public int TYPE() {
        return this.type;
    }
}
