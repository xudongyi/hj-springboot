package business.receiver.entity;

import lombok.Data;

@Data
public class CmdTemplate {
    private String title;
    private String content;
    private String format;
    private String note;
}
