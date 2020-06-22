package business.entity;

import lombok.Data;

import java.util.Date;

@Data
public class BlackList {
    private long counts = 0L;
    private Date beginTime = new Date();
}
