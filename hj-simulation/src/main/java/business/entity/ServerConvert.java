package business.entity;

import lombok.Data;

@Data
public class ServerConvert {
    private String id;
    private int port;
    private String convertCondition;
    private String convertBefore;
    private String convertAfter;
}
