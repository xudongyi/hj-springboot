package business.message;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import business.util.CommonsUtil;
import business.util.DataFormat;
public class WaterMessage implements BaseMessage {
    private double total = 0.0D;
    private double surplus = 10000.0D;
    private List<Double> codCurrentList = new ArrayList();
    private List<Double> phCurrentList = new ArrayList();
    private List<Double> nhCurrentList = new ArrayList();
    private List<Double> pCurrentList = new ArrayList();
    private List<Double> waterCurrentList = new ArrayList();
    private List<Double> codMinCouList = new ArrayList();
    private List<Double> nhMinCouList = new ArrayList();
    private List<Double> pMinCouList = new ArrayList();
    private List<Double> waterMinCouList = new ArrayList();
    private List<Double> codMinAvgList = new ArrayList();
    private List<Double> phMinAvgList = new ArrayList();
    private List<Double> nhMinAvgList = new ArrayList();
    private List<Double> pMinAvgList = new ArrayList();
    private List<Double> waterMinAvgList = new ArrayList();
    private List<Double> codMinMaxList = new ArrayList();
    private List<Double> phMinMaxList = new ArrayList();
    private List<Double> nhMinMaxList = new ArrayList();
    private List<Double> pMinMaxList = new ArrayList();
    private List<Double> waterMinMaxList = new ArrayList();
    private List<Double> codMinMinList = new ArrayList();
    private List<Double> phMinMinList = new ArrayList();
    private List<Double> nhMinMinList = new ArrayList();
    private List<Double> pMinMinList = new ArrayList();
    private List<Double> waterMinMinList = new ArrayList();
    private List<Double> codHourCouList = new ArrayList();
    private List<Double> nhHourCouList = new ArrayList();
    private List<Double> pHourCouList = new ArrayList();
    private List<Double> waterHourCouList = new ArrayList();
    private List<Double> codHourAvgList = new ArrayList();
    private List<Double> phHourAvgList = new ArrayList();
    private List<Double> nhHourAvgList = new ArrayList();
    private List<Double> pHourAvgList = new ArrayList();
    private List<Double> waterHourAvgList = new ArrayList();
    private List<Double> codHourMaxList = new ArrayList();
    private List<Double> phHourMaxList = new ArrayList();
    private List<Double> nhHourMaxList = new ArrayList();
    private List<Double> pHourMaxList = new ArrayList();
    private List<Double> waterHourMaxList = new ArrayList();
    private List<Double> codHourMinList = new ArrayList();
    private List<Double> phHourMinList = new ArrayList();
    private List<Double> nhHourMinList = new ArrayList();
    private List<Double> pHourMinList = new ArrayList();
    private List<Double> waterHourMinList = new ArrayList();

    public WaterMessage() {
    }

    public String current() {
        double cod = DataFormat.randomData(0, 100, 1);
        double ph = DataFormat.randomData(6, 7, 2);
        double nh = DataFormat.randomData(0, 35, 2);
        double p = DataFormat.randomData(0, 8, 2);
        double water = DataFormat.randomDouble(10, 50, 2);
        double total = this.randomTotal(water);
        double surplus = this.randomSurplus(water);
        this.codCurrentList.add(cod);
        this.phCurrentList.add(ph);
        this.nhCurrentList.add(nh);
        this.pCurrentList.add(p);
        this.waterCurrentList.add(water);
        StringBuffer cmd = new StringBuffer("");
        cmd.append("QN=${QN};ST=32;CN=2011;PW=123456;MN=${MN};CP=&&DataTime=${DataTime};");
        cmd.append("011-Rtd=" + cod + ",011-Flag=N,011-StateDetail=" + DataFormat.randomDeviceState() + ",011-TimeStamp=${SampleTime};");
        cmd.append("B01-Rtd=" + water + ",B01-Flag=N;B01-Total=" + total + ";");
        cmd.append("B01-Surplus=" + surplus + ";");
        cmd.append("001-Rtd=" + ph + ",001-Flag=N,001-StateDetail=" + DataFormat.randomDeviceState() + ";");
        cmd.append("060-Rtd=" + nh + ",060-Flag=" + DataFormat.randomFlag() + ",060-StateDetail=" + DataFormat.randomDeviceState() + ",060-TimeStamp=${SampleTime};");
        cmd.append("101-Rtd=" + p + ",101-Flag=" + DataFormat.randomFlag() + ",101-TimeStamp=${SampleTime}");
        cmd.append("&&");
        return cmd.toString();
    }

    public String minute() {
        double waterCou = CommonsUtil.numberFormat(DataFormat.cou(this.waterCurrentList, 2) * 60.0D / 1000.0D, 2);
        double waterMax = DataFormat.max(this.waterCurrentList);
        double waterMin = DataFormat.min(this.waterCurrentList);
        double waterAvg = DataFormat.avg(this.waterCurrentList, 2);
        this.waterMinCouList.add(waterCou);
        this.waterMinMaxList.add(waterMax);
        this.waterMinMinList.add(waterMin);
        this.waterMinAvgList.add(waterAvg);
        this.waterCurrentList.clear();
        double codCou = CommonsUtil.numberFormat(DataFormat.cou(this.codCurrentList, 2) * waterCou / 1000.0D, 2);
        double codMax = DataFormat.max(this.codCurrentList);
        double codMin = DataFormat.min(this.codCurrentList);
        double codAvg = DataFormat.avg(this.codCurrentList, 2);
        this.codMinCouList.add(codCou);
        this.codMinMaxList.add(codMax);
        this.codMinMinList.add(codMin);
        this.codMinAvgList.add(codAvg);
        this.codCurrentList.clear();
        double phMax = DataFormat.max(this.phCurrentList);
        double phMin = DataFormat.min(this.phCurrentList);
        double phAvg = DataFormat.avg(this.phCurrentList, 2);
        this.phMinMaxList.add(phMax);
        this.phMinMinList.add(phMin);
        this.phMinAvgList.add(phAvg);
        this.phCurrentList.clear();
        double nhCou = CommonsUtil.numberFormat(DataFormat.cou(this.nhCurrentList, 2) * waterCou / 1000.0D, 2);
        double nhMax = DataFormat.max(this.nhCurrentList);
        double nhMin = DataFormat.min(this.nhCurrentList);
        double nhAvg = DataFormat.avg(this.nhCurrentList, 2);
        this.nhMinCouList.add(nhCou);
        this.nhMinMaxList.add(nhMax);
        this.nhMinMinList.add(nhMin);
        this.nhMinAvgList.add(nhAvg);
        this.nhCurrentList.clear();
        double pCou = CommonsUtil.numberFormat(DataFormat.cou(this.pCurrentList, 3) * waterCou / 1000.0D, 3);
        double pMax = DataFormat.max(this.pCurrentList);
        double pMin = DataFormat.min(this.pCurrentList);
        double pAvg = DataFormat.avg(this.pCurrentList, 3);
        this.pMinCouList.add(pCou);
        this.pMinMaxList.add(pMax);
        this.pMinMinList.add(pMin);
        this.pMinAvgList.add(pAvg);
        this.pCurrentList.clear();
        StringBuffer cmd = new StringBuffer("");
        cmd.append("QN=${QN};ST=32;CN=2051;PW=123456;MN=${MN};CP=&&DataTime=${DataTime};");
        cmd.append("011-Cou=" + codCou + ",011-Min=" + codMin + ",011-Max=" + codMax + ",011-Avg=" + codAvg + ",011-Flag=N;");
        cmd.append("B01-Cou=" + waterCou + ",B01-Min=" + waterMin + ",B01-Max=" + waterMax + ",B01-Avg=" + waterAvg + ",B01-Flag=N;");
        cmd.append("001-Min=" + phMin + ",001-Max=" + phMax + ",001-Avg=" + phAvg + ",001-Flag=N;");
        cmd.append("060-Cou=" + nhCou + ",060-Min=" + nhMin + ",060-Max=" + nhMax + ",060-Avg=" + nhAvg + ",060-Flag=N;");
        cmd.append("101-Cou=" + pCou + ",101-Min=" + pMin + ",101-Max=" + pMax + ",101-Avg=" + pAvg + ",101-Flag=N");
        cmd.append("&&");
        return cmd.toString();
    }

    public String hour() {
        double waterCou = DataFormat.cou(this.waterMinCouList, 2);
        double waterMax = DataFormat.max(this.waterMinMaxList);
        double waterMin = DataFormat.min(this.waterMinMinList);
        double waterAvg = DataFormat.avg(this.waterMinAvgList, 2);
        this.waterHourCouList.add(waterCou);
        this.waterHourMaxList.add(waterMax);
        this.waterHourMinList.add(waterMin);
        this.waterHourAvgList.add(waterAvg);
        this.waterMinCouList.clear();
        this.waterMinMaxList.clear();
        this.waterMinMinList.clear();
        this.waterMinAvgList.clear();
        double codCou = DataFormat.cou(this.codMinCouList, 2);
        double codMax = DataFormat.max(this.codMinMaxList);
        double codMin = DataFormat.min(this.codMinMinList);
        double codAvg = DataFormat.avg(this.codMinAvgList, 2);
        this.codHourCouList.add(codCou);
        this.codHourMaxList.add(codMax);
        this.codHourMinList.add(codMin);
        this.codHourAvgList.add(codAvg);
        this.codMinCouList.clear();
        this.codMinMaxList.clear();
        this.codMinMinList.clear();
        this.codMinAvgList.clear();
        double phMax = DataFormat.max(this.phMinMaxList);
        double phMin = DataFormat.min(this.phMinMinList);
        double phAvg = DataFormat.avg(this.phMinAvgList, 2);
        this.phHourMaxList.add(phMax);
        this.phHourMinList.add(phMin);
        this.phHourAvgList.add(phAvg);
        this.phMinMaxList.clear();
        this.phMinMinList.clear();
        this.phMinAvgList.clear();
        double nhCou = DataFormat.cou(this.nhMinCouList, 2);
        double nhMax = DataFormat.max(this.nhMinMaxList);
        double nhMin = DataFormat.min(this.nhMinMinList);
        double nhAvg = DataFormat.avg(this.nhMinAvgList, 2);
        this.nhHourCouList.add(nhCou);
        this.nhHourMaxList.add(nhMax);
        this.nhHourMinList.add(nhMin);
        this.nhHourAvgList.add(nhAvg);
        this.nhMinCouList.clear();
        this.nhMinMaxList.clear();
        this.nhMinMinList.clear();
        this.nhMinAvgList.clear();
        double pCou = DataFormat.cou(this.pMinCouList, 3);
        double pMax = DataFormat.max(this.pMinMaxList);
        double pMin = DataFormat.min(this.pMinMinList);
        double pAvg = DataFormat.avg(this.pMinAvgList, 3);
        this.pHourCouList.add(pCou);
        this.pHourMaxList.add(pMax);
        this.pHourMinList.add(pMin);
        this.pHourAvgList.add(pAvg);
        this.pMinCouList.clear();
        this.pMinMaxList.clear();
        this.pMinMinList.clear();
        this.pMinAvgList.clear();
        StringBuffer cmd = new StringBuffer("");
        cmd.append("QN=${QN};ST=32;CN=2061;PW=123456;MN=${MN};CP=&&DataTime=${DataTime};");
        cmd.append("011-Cou=" + codCou + ",011-Min=" + codMin + ",011-Max=" + codMax + ",011-Avg=" + codAvg + ",011-Flag=N;");
        cmd.append("B01-Cou=" + waterCou + ",B01-Min=" + waterMin + ",B01-Max=" + waterMax + ",B01-Avg=" + waterAvg + ",B01-Flag=N;");
        cmd.append("001-Min=" + phMin + ",001-Max=" + phMax + ",001-Avg=" + phAvg + ",001-Flag=N;");
        cmd.append("060-Cou=" + nhCou + ",060-Min=" + nhMin + ",060-Max=" + nhMax + ",060-Avg=" + nhAvg + ",060-Flag=N;");
        cmd.append("101-Cou=" + pCou + ",101-Min=" + pMin + ",101-Max=" + pMax + ",101-Avg=" + pAvg + ",101-Flag=N");
        cmd.append("&&");
        return cmd.toString();
    }

    public String day() {
        double waterCou = DataFormat.cou(this.waterHourCouList, 2);
        double waterMax = DataFormat.max(this.waterHourMaxList);
        double waterMin = DataFormat.min(this.waterHourMinList);
        double waterAvg = DataFormat.avg(this.waterHourAvgList, 2);
        this.waterHourCouList.clear();
        this.waterHourMaxList.clear();
        this.waterHourMinList.clear();
        this.waterHourAvgList.clear();
        double codCou = DataFormat.cou(this.codHourCouList, 2);
        double codMax = DataFormat.max(this.codHourMaxList);
        double codMin = DataFormat.min(this.codHourMinList);
        double codAvg = DataFormat.avg(this.codHourAvgList, 2);
        this.codHourCouList.clear();
        this.codHourMaxList.clear();
        this.codHourMinList.clear();
        this.codHourAvgList.clear();
        double phMax = DataFormat.max(this.phHourMaxList);
        double phMin = DataFormat.min(this.phHourMinList);
        double phAvg = DataFormat.avg(this.phHourAvgList, 2);
        this.phHourMaxList.clear();
        this.phHourMinList.clear();
        this.phHourAvgList.clear();
        double nhCou = DataFormat.cou(this.nhHourCouList, 2);
        double nhMax = DataFormat.max(this.nhHourMaxList);
        double nhMin = DataFormat.min(this.nhHourMinList);
        double nhAvg = DataFormat.avg(this.nhHourAvgList, 2);
        this.nhHourCouList.clear();
        this.nhHourMaxList.clear();
        this.nhHourMinList.clear();
        this.nhHourAvgList.clear();
        double pCou = DataFormat.cou(this.pHourCouList, 3);
        double pMax = DataFormat.max(this.pHourMaxList);
        double pMin = DataFormat.min(this.pHourMinList);
        double pAvg = DataFormat.avg(this.pHourAvgList, 3);
        this.pHourCouList.clear();
        this.pHourMaxList.clear();
        this.pHourMinList.clear();
        this.pHourAvgList.clear();
        StringBuffer cmd = new StringBuffer("");
        cmd.append("QN=${QN};ST=32;CN=2031;PW=123456;MN=${MN};CP=&&DataTime=${DataTime};");
        cmd.append("011-Cou=" + codCou + ",011-Min=" + codMin + ",011-Max=" + codMax + ",011-Avg=" + codAvg + ",011-Flag=N;");
        cmd.append("B01-Cou=" + waterCou + ",B01-Min=" + waterMin + ",B01-Max=" + waterMax + ",B01-Avg=" + waterAvg + ",B01-Flag=N;");
        cmd.append("001-Min=" + phMin + ",001-Max=" + phMax + ",001-Avg=" + phAvg + ",001-Flag=N;");
        cmd.append("060-Cou=" + nhCou + ",060-Min=" + nhMin + ",060-Max=" + nhMax + ",060-Avg=" + nhAvg + ",060-Flag=N;");
        cmd.append("101-Cou=" + pCou + ",101-Min=" + pMin + ",101-Max=" + pMax + ",101-Avg=" + pAvg + ",101-Flag=N");
        cmd.append("&&");
        return cmd.toString();
    }

    private double randomTotal(double waterRtd) {
        if (this.total == 0.0D) {
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.set(2017, 5, 23, 0, 0, 0);
            long time = date.getTime() - calendar.getTimeInMillis();
            this.total = (double)(time / 1000L / 60L);
        }

        this.total = CommonsUtil.numberFormat(this.total + waterRtd * 60.0D / 1000.0D, 2);
        return this.total;
    }

    private double randomSurplus(double waterRtd) {
        if (this.surplus < 0.0D) {
            this.surplus += 10000.0D;
        }

        this.surplus = CommonsUtil.numberFormat(this.surplus - waterRtd * 60.0D / 1000.0D, 2);
        return this.surplus;
    }
}