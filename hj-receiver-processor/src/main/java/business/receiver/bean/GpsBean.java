package business.receiver.bean;

import lombok.Data;

import java.util.Date;
@Data
public class GpsBean {
    private String id;
    private String no;
    private double lng;
    private double lat;
    private double speed;
    private double high;
    private Date begin_time;
    private Date end_time;
    private double direction;
    private double distance;
    private long rec_times;

}