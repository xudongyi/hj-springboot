package business.receiver.entity;


public enum SysDeviceMessageEnum {
    IS_RECIEVE("0","已接收"),
    IS_JOINQEUE("1","已加入队列"),
    IS_PARSE("2","已解析"),
    IS_FAIL("7","解析失败");
    private String flag;
    private String meaning;

    SysDeviceMessageEnum(String flag,String meaning){
        this.flag = flag;
        this.meaning = meaning;
    }


    public String code() {
        return flag;
    }

    public String mean() {
        return meaning;
    }
}
