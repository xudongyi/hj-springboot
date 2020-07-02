package business.receiver.entity;

import com.xy.format.hbt212.CodeMean;

public enum SysDeviceMessageEnum implements CodeMean {
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


    @Override
    public String code() {
        return flag;
    }

    @Override
    public String mean() {
        return meaning;
    }
}
