package business.processor.bean;

import business.util.CommonsUtil;
import lombok.Data;

import java.util.Date;
@Data
public class ScheduleBean {
    private double day = 0.0D;
    private double[] monthArray;
    private double year = 0.0D;


    public double getMonth(Date d) {
        String s = CommonsUtil.dateFormat(d, "MM");
        int m = Integer.parseInt(s);
        return this.monthArray[m - 1];
    }
}