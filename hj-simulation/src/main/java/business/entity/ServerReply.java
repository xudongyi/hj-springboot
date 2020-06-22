package business.entity;

import lombok.Data;

@Data
public class ServerReply {
    private String id;
    private int port;
    private String rule;
    private String reply;
}
