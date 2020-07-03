package business.message;
import business.util.DataFormat;

import java.util.ArrayList;
import java.util.List;
public class AirQMessage implements BaseMessage {
    private List<Double> o38List = new ArrayList();
    private List<Double> o31List = new ArrayList();
    private List<Double> noList = new ArrayList();
    private List<Double> noxList = new ArrayList();

    public AirQMessage() {
    }

    public String hour() {
        double o38 = DataFormat.randomData(0, 200, 2);
        double o31 = DataFormat.randomData(0, 160, 2);
        double pm1001 = DataFormat.randomData(0, 150, 2);
        double pm1024 = DataFormat.randomData(0, 150, 2);
        double pm2524 = DataFormat.randomData(0, 75, 2);
        double pm2501 = DataFormat.randomData(0, 75, 2);
        double so2 = DataFormat.randomData(0, 500, 2);
        double no = DataFormat.randomData(0, 100, 2);
        double no2 = DataFormat.randomData(0, 200, 2);
        double nox = DataFormat.randomData(0, 100, 2);
        double co = DataFormat.randomData(0, 10, 2);
        double a01001 = DataFormat.randomData(15, 20, 1);
        double a01002 = DataFormat.randomData(70, 90, 1);
        double a01006 = DataFormat.randomData(0, 7, 1);
        double a01007 = DataFormat.randomData(90, 120, 0);
        this.o38List.add(o38);
        this.o31List.add(o31);
        this.noList.add(no);
        this.noxList.add(nox);
        StringBuffer cmd = new StringBuffer("");
        cmd.append("QN=${QN};ST=22;CN=2061;PW=123456;MN=${MN};CP=&&DataTime=${DataTime};");
        cmd.append("A0502408-Avg=" + o38 + ";");
        cmd.append("A0502401-Avg=" + o31 + ";");
        cmd.append("A3400201-Avg=" + pm1001 + ",A3400201-Flag=" + DataFormat.randomFlag() + ";");
        cmd.append("A3400224-Avg=" + pm1024 + ";");
        cmd.append("A3400424-Avg=" + pm2524 + ";");
        cmd.append("A3400401-Avg=" + pm2501 + ";");
        cmd.append("A21026-Avg=" + so2 + ",A21026-Flag=" + DataFormat.randomFlag() + ",A21026-EFlag=" + DataFormat.randomDeviceState() + ";");
        cmd.append("A21003-Avg=" + no + ",A21003-EFlag=" + DataFormat.randomDeviceState() + ";");
        cmd.append("A21004-Avg=" + no2 + ";");
        cmd.append("A21002-Avg=" + nox + ";");
        cmd.append("A21005-Avg=" + co + ";");
        cmd.append("A01001-Avg=" + a01001 + ";");
        cmd.append("A01002-Avg=" + a01002 + ";");
        cmd.append("A01006-Avg=" + a01006 + ";");
        cmd.append("A01007-Avg=" + a01007);
        cmd.append("&&");
        return cmd.toString();
    }

    public String day() {
        double o38 = DataFormat.avg(this.o38List, 2);
        double o31 = DataFormat.avg(this.o31List, 2);
        double pm1024 = DataFormat.randomData(0, 150, 2);
        double pm2524 = DataFormat.randomData(0, 75, 2);
        double so2 = DataFormat.randomData(0, 150, 2);
        double no = DataFormat.avg(this.noList, 2);
        double no2 = DataFormat.randomData(0, 80, 2);
        double nox = DataFormat.avg(this.noxList, 2);
        double co = DataFormat.randomData(0, 4, 2);
        double a01001 = DataFormat.randomData(15, 20, 1);
        double a01002 = DataFormat.randomData(70, 90, 1);
        double a01007 = DataFormat.randomData(0, 7, 1);
        double a01008 = DataFormat.randomData(90, 120, 0);
        this.o38List.clear();
        this.o31List.clear();
        this.noList.clear();
        this.noxList.clear();
        StringBuffer cmd = new StringBuffer("");
        cmd.append("QN=${QN};ST=22;CN=2031;PW=123456;MN=${MN};CP=&&DataTime=${DataTime};");
        cmd.append("A0502408-Avg=" + o38 + ";");
        cmd.append("A0502401-Avg=" + o31 + ";");
        cmd.append("A3400224-Avg=" + pm1024 + ";");
        cmd.append("A3400424-Avg=" + pm2524 + ";");
        cmd.append("A21026-Avg=" + so2 + ";");
        cmd.append("A21003-Avg=" + no + ";");
        cmd.append("A21004-Avg=" + no2 + ";");
        cmd.append("A21002-Avg=" + nox + ";");
        cmd.append("A21005-Avg=" + co + ";");
        cmd.append("A01001-Avg=" + a01001 + ";");
        cmd.append("A01002-Avg=" + a01002 + ";");
        cmd.append("A01007-Avg=" + a01007 + ";");
        cmd.append("A01008-Avg=" + a01008);
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