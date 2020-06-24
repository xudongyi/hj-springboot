package business.receiver.bean;

import lombok.Data;

import java.util.Date;

@Data
public class ReverseBean {
    private String qn;
    private String mn;
    private String content;
    private Date createTime;
}