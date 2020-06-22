package business.service;

import business.util.DataFormat;

/**
 * 反控的service类
 */
public class ReverseService {
    public static String excute(String msg) {
        String result = "";
        String qn = "";
        int index = msg.indexOf("QN=");
        if (index != -1) {
            qn = msg.substring(index + 3, index + 20);
        }

        String mn = "";
        index = msg.indexOf("MN=");
        String valve;
        if (index != -1) {
            valve = msg.substring(index);
            index = valve.indexOf(";");
            mn = valve.substring(3, index);
        }

        if (!mn.equals("") && !qn.equals("")) {
            if (msg.indexOf("CN=8804") != -1) {
                result = "QN=" + qn + ";ST=32;CN=8804;PW=123456;MN=" + mn + ";Flag=0;CP=&&DateTime=" + DataFormat.DateFormat_DT(0) + ";Bottle=1&&";
            } else if (msg.indexOf("CN=8802") != -1) {
                index = msg.indexOf("ExeValve=");
                if (index != -1) {
                    valve = msg.substring(index + 9, msg.indexOf("&&"));
                    if (valve.equals("1")) {
                        result = "QN=" + qn + ";ST=32;CN=8803;PW=123456;MN=" + mn + ";Flag=0;CP=&&ValveStatus=1&&";
                    } else if (valve.equals("0")) {
                        result = "QN=" + qn + ";ST=32;CN=8803;PW=123456;MN=" + mn + ";Flag=0;CP=&&ValveStatus=2&&";
                    }
                }
            }
        }

        return result + "\r\n";
    }
}