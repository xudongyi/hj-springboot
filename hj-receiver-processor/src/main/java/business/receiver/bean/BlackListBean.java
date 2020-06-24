package business.receiver.bean;

import lombok.Data;

import java.util.Date;
@Data
public class BlackListBean {
    private long counts = 0L;
    private Date beginTime = new Date();

}