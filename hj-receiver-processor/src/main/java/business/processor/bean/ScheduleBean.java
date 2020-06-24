package business.processor.bean;

public class ScheduleBean {
    private double day = 0.0D;
    private double[] monthArray;
    private double year = 0.0D;

    public ScheduleBean() {
    }

    public double getDay() {
        return this.day;
    }

    public void setDay(double day) {
        this.day = day;
    }

    public double[] getMonthArray() {
        return this.monthArray;
    }

    public double getMonth(Date d) {
        String s = CommonsUtil.dateFormat(d, "MM");
        int m = Integer.parseInt(s);
        return this.monthArray[m - 1];
    }

    public void setMonthArray(double[] monthArray) {
        this.monthArray = monthArray;
    }

    public double getYear() {
        return this.year;
    }

    public void setYear(double year) {
        this.year = year;
    }
}