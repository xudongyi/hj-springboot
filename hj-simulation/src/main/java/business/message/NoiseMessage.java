package business.message;
import business.util.DataFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class NoiseMessage implements BaseMessage {
    private List<Double> LAMinuteList = new ArrayList();
    private List<Double> LAHourList = new ArrayList();
    private List<Double> LADNList = new ArrayList();
    private List<Double> LADayList = new ArrayList();
    private List<Double> LANightList = new ArrayList();
    private List<Double> LeqMinuteList = new ArrayList();
    private List<Double> LeqHourList = new ArrayList();
    private List<Double> LeqHourDayList = new ArrayList();
    private List<Double> LeqHourNightList = new ArrayList();

    public NoiseMessage() {
    }

    public String current() {
        double LA = DataFormat.randomDouble(20, 40, 1);
        this.LAMinuteList.add(LA);
        this.LAHourList.add(LA);
        this.LADNList.add(LA);
        Date date = new Date();
        if (date.getHours() >= 6 && date.getHours() < 22) {
            this.LADayList.add(LA);
        } else {
            this.LANightList.add(LA);
        }

        StringBuffer cmd = new StringBuffer("");
        cmd.append("QN=${QN};ST=23;CN=2011;PW=123456;MN=${MN};Flag=5;CP=&&DataTime=${DataTime};");
        cmd.append("LA-Rtd=" + LA + ",LA-Flag=" + DataFormat.randomFlag());
        cmd.append("&&");
        return cmd.toString();
    }

    public String minute() {
        double L10 = DataFormat.percentile(this.LAMinuteList, 90.0D, 1);
        double L50 = DataFormat.percentile(this.LAMinuteList, 50.0D, 1);
        double L90 = DataFormat.percentile(this.LAMinuteList, 10.0D, 1);
        double LMx = DataFormat.max(this.LAMinuteList);
        double LMn = DataFormat.min(this.LAMinuteList);
        double Leq = DataFormat.leq(this.LAMinuteList);
        this.LAMinuteList.clear();
        this.LeqMinuteList.add(Leq);
        StringBuffer cmd = new StringBuffer("");
        cmd.append("QN=${QN};ST=23;CN=2051;PW=123456;MN=${MN};Flag=5;CP=&&DataTime=${DataTime};");
        cmd.append("L10-Data=" + L10 + ";L50-Data=" + L50 + ";L90-Data=" + L90 + ";LMx-Data=" + LMx + ";LMn-Data=" + LMn + ";Leq-Data=" + Leq);
        cmd.append("&&");
        return cmd.toString();
    }

    public String hour() {
        double L10 = DataFormat.percentile(this.LAHourList, 90.0D, 1);
        double L50 = DataFormat.percentile(this.LAHourList, 50.0D, 1);
        double L90 = DataFormat.percentile(this.LAHourList, 10.0D, 1);
        double LMx = DataFormat.max(this.LAHourList);
        double LMn = DataFormat.min(this.LAHourList);
        double Leq = DataFormat.leq(this.LeqMinuteList);
        this.LeqMinuteList.clear();
        this.LAHourList.clear();
        this.LeqHourList.add(Leq);
        Date date = new Date();
        if (date.getHours() >= 6 && date.getHours() < 22) {
            this.LeqHourDayList.add(Leq);
        } else {
            this.LeqHourNightList.add(Leq);
        }

        StringBuffer cmd = new StringBuffer("");
        cmd.append("QN=${QN};ST=23;CN=2061;PW=123456;MN=${MN};Flag=5;CP=&&DataTime=${DataTime};");
        cmd.append("L10-Data=" + L10 + ";L50-Data=" + L50 + ";L90-Data=" + L90 + ";LMx-Data=" + LMx + ";LMn-Data=" + LMn + ";Leq-Data=" + Leq);
        cmd.append("&&");
        return cmd.toString();
    }

    public String day() {
        double L10 = DataFormat.percentile(this.LADNList, 90.0D, 1);
        double L10Day = DataFormat.percentile(this.LADayList, 90.0D, 1);
        double L10Night = DataFormat.percentile(this.LANightList, 90.0D, 1);
        double L50 = DataFormat.percentile(this.LADNList, 50.0D, 1);
        double L50Day = DataFormat.percentile(this.LADayList, 50.0D, 1);
        double L50Night = DataFormat.percentile(this.LANightList, 50.0D, 1);
        double L90 = DataFormat.percentile(this.LADNList, 10.0D, 1);
        double L90Day = DataFormat.percentile(this.LADayList, 10.0D, 1);
        double L90Night = DataFormat.percentile(this.LANightList, 10.0D, 1);
        double LMx = DataFormat.max(this.LADNList);
        double LMxDay = DataFormat.max(this.LADayList);
        double LMxNight = DataFormat.max(this.LANightList);
        double LMn = DataFormat.min(this.LADNList);
        double LMnDay = DataFormat.min(this.LADayList);
        double LMnNight = DataFormat.min(this.LANightList);
        double ldn = DataFormat.leq(this.LeqHourList);
        double ld = DataFormat.leq(this.LeqHourDayList);
        double ln = DataFormat.leq(this.LeqHourNightList);
        this.LADNList.clear();
        this.LADayList.clear();
        this.LANightList.clear();
        this.LeqHourList.clear();
        this.LeqHourDayList.clear();
        this.LeqHourNightList.clear();
        StringBuffer cmd = new StringBuffer("");
        cmd.append("QN=${QN};ST=23;CN=2031;PW=123456;MN=${MN};Flag=5;CP=&&DataTime=${DataTime};");
        cmd.append("L10-Data=" + L10 + ";L10-DayData=" + L10Day + ";L10-NightData=" + L10Night + ";");
        cmd.append("L50-Data=" + L50 + ";L50-DayData=" + L50Day + ";L50-NightData=" + L50Night + ";");
        cmd.append("L90-Data=" + L90 + ";L90-DayData=" + L90Day + ";L90-NightData=" + L90Night + ";");
        cmd.append("LMx-Data=" + LMx + ";LMx-DayData=" + LMxDay + ";LMx-NightData=" + LMxNight + ";");
        cmd.append("LMn-Data=" + LMn + ";LMn-DayData=" + LMnDay + ";LMn-NightData=" + LMnNight + ";");
        cmd.append("Ldn-Data=" + ldn + ";Ld-DayData=" + ld + ";Ln-NightData=" + ln);
        cmd.append("&&");
        return cmd.toString();
    }
}