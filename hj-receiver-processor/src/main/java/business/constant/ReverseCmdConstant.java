package business.constant;

import java.text.MessageFormat;

public class ReverseCmdConstant {
    public static final MessageFormat CMD_GET_DATA = new MessageFormat("QN={0};ST=32;CN={1};PW=123456;MN={2};Flag=1;CP=&&BeginTime={3},EndTime={4}&&");
    public static final MessageFormat CMD_SAMPLE_HH = new MessageFormat("QN={0};ST=32;CN=8804;PW=123456;MN={1};Flag=1;CP=&&&&");
    public static final MessageFormat CLOSE_VALVE = new MessageFormat("QN={0};ST=32;CN=8802;PW=123456;MN={1};Flag=1;CP=&&ExeValve=0&&");
    public static final MessageFormat OPEN_VALVE = new MessageFormat("QN={0};ST=32;CN=8802;PW=123456;MN={1};Flag=1;CP=&&ExeValve=1&&");
    public static final MessageFormat SET_OVERTIME = new MessageFormat("QN={0};ST=32;CN=1000;PW=123456;MN={1};Flag=3;CP=&&overTime={2},reCount={3}&&");
    public static final MessageFormat GET_SYSTEMTIME = new MessageFormat("QN={0};ST=32;CN=1011;PW=123456;MN={1};Flag=3;CP=&&&&");
    public static final MessageFormat SET_SYSTEMTIME = new MessageFormat("QN={0};ST=32;CN=1012;PW=123456;MN={1};Flag=3;CP=&&SystemTime={2}&&");
    public static final MessageFormat GET_RTD_INTERVAL = new MessageFormat("QN={0};ST=32;CN=1061;PW=123456;MN={1};Flag=3;CP=&&&&");
    public static final MessageFormat SET_RTD_INTERVAL = new MessageFormat("QN={0};ST=32;CN=1062;PW=123456;MN={1};Flag=3;CP=&&RtdInterval={2}&&");
    public static final MessageFormat GET_MIN_INTERVAL = new MessageFormat("QN={0};ST=32;CN=1063;PW=123456;MN={1};Flag=3;CP=&&&&");
    public static final MessageFormat SET_MIN_INTERVAL = new MessageFormat("QN={0};ST=32;CN=1064;PW=123456;MN={1};Flag=3;CP=&&MinInterval={2}&&");
    public static final MessageFormat GET_RTD_DATA = new MessageFormat("QN={0};ST=32;CN=2011;PW=123456;MN={1};Flag=3;CP=&&&&");
    public static final MessageFormat STOP_RTD_DATA = new MessageFormat("QN={0};ST=32;CN=2012;PW=123456;MN={1};Flag=3;CP=&&&&");
    public static final MessageFormat GET_DEVICE_STATE = new MessageFormat("QN={0};ST=32;CN=2021;PW=123456;MN={1};Flag=3;CP=&&&&");
    public static final MessageFormat STOP_DEVICE_STATE = new MessageFormat("QN={0};ST=32;CN=2022;PW=123456;MN={1};Flag=3;CP=&&&&");
    public static final MessageFormat CKECK_ZERO = new MessageFormat("QN={0};ST=32;CN=3011;PW=123456;MN={1};Flag=3;CP=&&&&");
    public static final MessageFormat GET_WATER = new MessageFormat("QN={0};ST=32;CN=3012;PW=123456;MN={1};Flag=3;CP=&&&&");
    public static final MessageFormat SET_CTIME_INTERVAL = new MessageFormat("QN={0};ST=32;CN=3016;PW=123456;MN={1};Flag=3;CP=&&CstartTime={2};CTime={3}&&");
    public static final MessageFormat CMD_SAMPLE_GB = new MessageFormat("QN={0};ST=32;CN=3015;PW=123456;MN={1};Flag=3;CP=&&&&");
    public static final MessageFormat GET_CTIME_INTERVAL = new MessageFormat("QN={0};ST=32;CN=3017;PW=123456;MN={1};Flag=3;CP=&&&&");
    public static final MessageFormat OPEN_VALVE_TZ = new MessageFormat("QN={0};ST=32;CN=3380;PW=123456;MN={1};Flag=3;CP=&&&&");
    public static final MessageFormat CLOSE_VALVE_TZ = new MessageFormat("QN={0};ST=32;CN=3381;PW=123456;MN={1};Flag=3;CP=&&&&");
    public static final MessageFormat SET_PRICE = new MessageFormat("QN={0};ST=32;CN=3390;PW=123456;MN={1};Flag=3;CP=&&price={2}&&");
    public static final MessageFormat GET_PRICE = new MessageFormat("QN={0};ST=32;CN=3391;PW=123456;MN={1};Flag=3;CP=&&&&");

    public ReverseCmdConstant() {
    }
}