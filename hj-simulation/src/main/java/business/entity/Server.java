package business.entity;

import lombok.Data;

@Data
public class Server {
    private int port;
    private String text;
    private String title;
    private String forward;
}
