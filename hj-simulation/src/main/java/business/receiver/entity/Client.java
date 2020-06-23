package business.receiver.entity;

import lombok.Data;

@Data
public class Client {
    private String id;
    private String text;
    private String title;
    private String ip;
    private int port;
    private int interval;
    private String mnList;
    private String content;
}
