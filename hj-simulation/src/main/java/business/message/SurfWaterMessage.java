package business.message;
import java.util.ArrayList;
import java.util.List;
import business.util.DataFormat;
public class SurfWaterMessage implements BaseMessage {
    private List<Double> W01001List = new ArrayList();
    private List<Double> W01010List = new ArrayList();
    private List<Double> W01009List = new ArrayList();
    private List<Double> W21003List = new ArrayList();
    private List<Double> W01019List = new ArrayList();
    private List<Double> W01018List = new ArrayList();
    private List<Double> W02003List = new ArrayList();

    public SurfWaterMessage() {
    }

    public String hour() {
        double W01001 = DataFormat.randomData(6, 9, 2);
        double W01010 = DataFormat.randomData(0, 30, 1);
        double W01009 = DataFormat.randomData(6, 8, 1);
        double W21003 = DataFormat.randomData(0, 1, 2) / 2.0D;
        double W01019 = DataFormat.randomData(0, 4, 1);
        double W01018 = DataFormat.randomData(0, 15, 1);
        double W02003 = DataFormat.randomData(0, 200, 0);
        this.W01001List.add(W01001);
        this.W01010List.add(W01010);
        this.W01009List.add(W01009);
        this.W21003List.add(W21003);
        this.W01019List.add(W01019);
        this.W01018List.add(W01018);
        this.W02003List.add(W02003);
        StringBuffer cmd = new StringBuffer("");
        cmd.append("QN=${QN};ST=21;CN=2061;PW=123456;MN=${MN};CP=&&DataTime=${DataTime};");
        cmd.append("W01001-Avg=" + W01001 + ",W01001-Flag=" + DataFormat.randomFlag() + ";");
        cmd.append("W01010-Avg=" + W01010 + ";");
        cmd.append("W01009-Avg=" + W01009 + ";");
        cmd.append("W21003-Avg=" + W21003 + ";");
        cmd.append("W01019-Avg=" + W01019 + ";");
        cmd.append("W01018-Avg=" + W01018 + ";");
        cmd.append("W02003-Avg=" + W02003);
        cmd.append("&&");
        return cmd.toString();
    }

    public String day() {
        double W01001 = DataFormat.avg(this.W01001List, 2);
        double W01010 = DataFormat.avg(this.W01010List, 1);
        double W01009 = DataFormat.avg(this.W01009List, 3);
        double W21003 = DataFormat.avg(this.W21003List, 3);
        double W01019 = DataFormat.avg(this.W01019List, 3);
        double W01018 = DataFormat.avg(this.W01018List, 3);
        double W02003 = DataFormat.avg(this.W02003List, 0);
        this.W01001List.clear();
        this.W01010List.clear();
        this.W01009List.clear();
        this.W21003List.clear();
        this.W01019List.clear();
        this.W01018List.clear();
        this.W02003List.clear();
        StringBuffer cmd = new StringBuffer("");
        cmd.append("QN=${QN};ST=21;CN=2031;PW=123456;MN=${MN};CP=&&DataTime=${DataTime};");
        cmd.append("W01001-Avg=" + W01001 + ";");
        cmd.append("W01010-Avg=" + W01010 + ";");
        cmd.append("W01009-Avg=" + W01009 + ";");
        cmd.append("W21003-Avg=" + W21003 + ";");
        cmd.append("W01019-Avg=" + W01019 + ";");
        cmd.append("W01018-Avg=" + W01018 + ";");
        cmd.append("W02003-Avg=" + W02003);
        cmd.append("&&");
        return cmd.toString();
    }

    public String current() {
        return "";
    }

    public String minute() {
        return "";
    }
}