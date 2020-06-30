package business.constant;

import java.text.MessageFormat;

/**
 * @program: hj-springboot
 * @description: sql常量类
 * @author: xudy
 * @create: 2020-06-20 10:35
 **/
public class TableSqlConstant {
    public static final MessageFormat BAK_SOURCE_SQL = new MessageFormat("create table sys_device_message_{0}(ID bigint primary key not null auto_increment,TAG int,RECTIME datetime,CONTEXT varchar(2048),MN char(24),UNIQUE(ID),INDEX(TAG))");
    public static final MessageFormat BAK_REVERSE_LOG_SQL = new MessageFormat("create table REVERSE_LOG(QN char(17) primary key not null,MN varchar(24) not null,CREATE_TIME datetime,UPDATE_TIME datetime,TYPE int comment ?,NOTE varchar(200),CMD varchar(1024) not null,TAG int not null comment ?,RESULTS varchar(200),OPERATOR_ID varchar(50),OPERATOR_NAME varchar(20),TASK_STATUS int default 1 comment ?,INDEX(MN),INDEX(CREATE_TIME),INDEX(TAG))");
    public static final String BAK_REVERSE_LOG_SQL_1 = "指令类型:1-开阀;2-关阀;3-留样;4-设置现场时间;5-获取现场时间;6-小时数据补遗;7-日数据补遗;";
    public static final String BAK_REVERSE_LOG_SQL_2 = "状态:0-未发;1-已发;2-已接收;3-执行成功;4-执行失败;5-发送失败;";
    public static final String BAK_REVERSE_LOG_SQL_3 = "是否已生成取样单:1-否;2-是;";
    public static final MessageFormat BAK_WATER_CURRENT_SQL = new MessageFormat("create table WATER_CURRENT_{0}(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,MN varchar(24),CODE varchar(14),VALUE double,STATE int,SAMPLE_TIME datetime,FLAG varchar(3),INDEX(MN),INDEX(CODE),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_WATER_CURRENT_TR_SQL = new MessageFormat("create table WATER_CURRENT_TR_{0}(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,MN varchar(24),STATE int,W00000_TODAY double,W00000_TODAY_STATE int,W00000_TOTAL double,W00000_TOTAL_STATE int,W00000_SURPLUS double,W00000_SURPLUS_STATE int,W00000_RTD double,W00000_STATE int,W00000_FLAG varchar(2),W00000_SAMPLETIME datetime,INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_WATER_MINUTE_SQL = new MessageFormat("create table WATER_MINUTE_{0}(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,MN varchar(24),STATE int,W00000_MIN double,W00000_MIN_STATE int,W00000_MAX double,W00000_MAX_STATE int,W00000_AVG double,W00000_AVG_STATE int,W00000_COU double,W00000_COU_STATE int,W00000_FLAG varchar(2),INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_AIR_CURRENT_TR_SQL = new MessageFormat("create table AIR_CURRENT_TR_{0}(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,MN varchar(24),STATE int,A00000_RTD double,A00000_STATE int,A00000_ZSRTD double,A00000_ZSSTATE int,A00000_FLAG varchar(2),INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_AIR_MINUTE_SQL = new MessageFormat("create table AIR_MINUTE_{0}(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,MN varchar(24),STATE int,A00000_MIN double,A00000_MIN_STATE int,A00000_ZSMIN double,A00000_ZSMIN_STATE int,A00000_MAX double,A00000_MAX_STATE int,A00000_ZSMAX double,A00000_ZSMAX_STATE int,A00000_AVG double,A00000_AVG_STATE int,A00000_ZSAVG double,A00000_ZSAVG_STATE int,A00000_COU double,A00000_COU_STATE int,A00000_ZSCOU double,A00000_ZSCOU_STATE int,A00000_FLAG varchar(2),INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat WARN_LOG_SQL = new MessageFormat("create table WARN_LOG(ID char(32) primary key not null,TYPE int,COMPANY_ID char(32),MN varchar(24),WARN_TYPE int,CODE varchar(20),WARN_LEVEL int,MESSAGE_STATUS int,CONTENT varchar(1024),WARN_TIME datetime,TASK_STATUS int default 1 COMMENT ?,INDEX(MN),INDEX(WARN_TIME))");
    public static final String WARN_LOG_SQL_1 = "任务受理状态:1-未受理；2-已受理；3-已生成任务";
    public static final MessageFormat MON_REPAIR_APPLY_SQL = new MessageFormat("create table mon_repair_apply(ID char(32) primary key not null,MONITOR_ID char(32),MN varchar(24),INSTRUCTION varchar(1024),BEGIN_TIME datetime not null,END_TIME datetime not null,OPER_USER varchar(32),EXAM_USER varchar(32),EXAM_RES varchar(100),STATUS int)");
    public static final MessageFormat BAK_DEVICE_STATE_SQL = new MessageFormat("create table DEVICE_STATE(ID char(32) primary key not null,MN varchar(24),DATA_TIME datetime,CREATE_TIME datetime,END_TIME datetime,SAMPLE_TIME datetime,CODE varchar(10),STATE int,INDEX(MN))");
    public static final MessageFormat BAK_RATE_DEVICE_SQL = new MessageFormat("create table rate_device(ID char(32) primary key not null,MONITOR_ID char(32),MONITOR_NAME varchar(100),MN varchar(24),MONITOR_TYPE int,CODE varchar(20),NAME varchar(50),ERROR int,NORMAL int,PERCENT double,DATA_TIME varchar(10),INDEX(MN),INDEX(MONITOR_ID))");
    public static final MessageFormat BAK_RATE_ONLINE_SQL = new MessageFormat("create table rate_online(ID char(32) primary key not null,MONITOR_ID char(32),MONITOR_NAME varchar(100),MN varchar(24),MONITOR_TYPE int,DATA_TIME varchar(10),ONLINE_TIME int,OFFLINE_TIME int,STOP_TIME int,ONLINE_RATE double,INDEX(MN),INDEX(DATA_TIME),INDEX(MONITOR_ID))");
    public static final MessageFormat BAK_RATE_OVERPROOF_SQL = new MessageFormat("create table rate_overproof(ID char(32) primary key not null,MONITOR_ID char(32),MONITOR_NAME varchar(100),MN varchar(24),MONITOR_TYPE int,CODE varchar(20),NAME varchar(50),COUNT int,TOTAL int,PERCENT double,DATA_TIME varchar(10),INDEX(MN),INDEX(DATA_TIME),INDEX(MONITOR_ID))");
    public static final MessageFormat BAK_RATE_UPLOAD_SQL = new MessageFormat("create table rate_upload(ID char(32) primary key not null,MONITOR_ID char(32),MONITOR_NAME varchar(100),MN varchar(24),MONITOR_TYPE int,COUNT int,TOTAL int,PERCENT double,DATA_TIME varchar(10),INDEX(MN),INDEX(DATA_TIME),INDEX(MONITOR_ID))");
    public static final MessageFormat BAK_RATE_VALID_SQL = new MessageFormat("create table rate_valid(ID char(32) primary key not null,MONITOR_ID char(32),MONITOR_NAME varchar(100),MN varchar(24),MONITOR_TYPE int,COUNT int,TOTAL int,PERCENT double,DATA_TIME varchar(10),INDEX(MN),INDEX(DATA_TIME),INDEX(MONITOR_ID))");
    public static final MessageFormat BAK_OVERPROOF_PERIOD_SQL = new MessageFormat("create table overproof_period(ID char(32) primary key not null,MONITOR_TYPE int,MN varchar(24),FACTOR_CODE varchar(20),BEGIN_TIME datetime,END_TIME datetime,OVER_TIME double,MAX_VALUE double,ST_VALUE double,MULTIPLE_VALUE double,INDEX(MN),INDEX(FACTOR_CODE),INDEX(END_TIME))");
    public static final MessageFormat BAK_WATER_HOUR_SQL = new MessageFormat("create table WATER_HOUR(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,MN varchar(24),STATE int,W00000_MIN double,W00000_MIN_STATE int,W00000_MAX double,W00000_MAX_STATE int,W00000_AVG double,W00000_AVG_STATE int,W00000_COU double,W00000_COU_STATE int,W00000_FLAG varchar(2),INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_WATER_DAY_SQL = new MessageFormat("create table WATER_DAY(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,MN varchar(24),STATE int,W00000_MIN double,W00000_MIN_STATE int,W00000_MAX double,W00000_MAX_STATE int,W00000_AVG double,W00000_AVG_STATE int,W00000_COU double,W00000_COU_STATE int,W00000_FLAG varchar(2),INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_WATER_MONTH_SQL = new MessageFormat("create table WATER_MONTH(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,STATIC_TIME datetime,MN varchar(24),TIMES int,W00000_MIN double,W00000_MIN_STATE int,W00000_MAX double,W00000_MAX_STATE int,W00000_AVG double,W00000_AVG_STATE int,W00000_COU double,W00000_COU_STATE int,INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_WATER_YEAR_SQL = new MessageFormat("create table WATER_YEAR(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,STATIC_TIME datetime,MN varchar(24),TIMES int,W00000_MIN double,W00000_MIN_STATE int,W00000_MAX double,W00000_MAX_STATE int,W00000_AVG double,W00000_AVG_STATE int,W00000_COU double,W00000_COU_STATE int,INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_COM_SCHEDULE_SQL = new MessageFormat("create table COM_SCHEDULE(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,STATIC_TIME datetime,COMPANY_ID char(32),MN varchar(24),W00000_DAY_BEGIN double,W00000_MONTH_BEGIN double,W00000_YEAR_BEGIN double,W00000_DAY_COU double,W00000_MONTH_COU double,W00000_YEAR_COU double,W00000_DAY_PROCESS double,W00000_MONTH_PROCESS double,W00000_YEAR_PROCESS double,INDEX(STATIC_TIME),INDEX(MN))");
    public static final MessageFormat BAK_WATER_OFFLINE_SQL = new MessageFormat("create table WATER_OFFLINE(ID char(32) primary key not null,MN varchar(24),START_TIME datetime,END_TIME datetime,INDEX(MN))");
    public static final MessageFormat BAK_WATER_CURRENT_OVERPROOF_SQL = new MessageFormat("create table WATER_CURRENT_OVERPROOF(ID char(32) primary key not null,SOURCE_ID varchar(20),MN varchar(24),CODE varchar(20),VALUE double,STANDARD_VALUE double,STATUS int,DATA_TIME datetime,SAMPLE_TIME datetime,INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_TOTAL_STATISTIC_SQL = new MessageFormat("create table TOTAL_STATISTIC(ID char(32) primary key not null,DATA_TIME datetime,MN varchar(24),CODE varchar(24),VALUE double,INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_AIR_HOUR_SQL = new MessageFormat("create table AIR_HOUR(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,MN varchar(24),STATE int,A00000_MIN double,A00000_MIN_STATE int,A00000_ZSMIN double,A00000_ZSMIN_STATE int,A00000_MAX double,A00000_MAX_STATE int,A00000_ZSMAX double,A00000_ZSMAX_STATE int,A00000_AVG double,A00000_AVG_STATE int,A00000_ZSAVG double,A00000_ZSAVG_STATE int,A00000_COU double,A00000_COU_STATE int,A00000_ZSCOU double,A00000_ZSCOU_STATE int,A00000_FLAG varchar(2),INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_AIR_DAY_SQL = new MessageFormat("create table AIR_DAY(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,MN varchar(24),STATE int,A00000_MIN double,A00000_MIN_STATE int,A00000_ZSMIN double,A00000_ZSMIN_STATE int,A00000_MAX double,A00000_MAX_STATE int,A00000_ZSMAX double,A00000_ZSMAX_STATE int,A00000_AVG double,A00000_AVG_STATE int,A00000_ZSAVG double,A00000_ZSAVG_STATE int,A00000_COU double,A00000_COU_STATE int,A00000_ZSCOU double,A00000_ZSCOU_STATE int,A00000_FLAG varchar(2),INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_AIR_MONTH_SQL = new MessageFormat("create table AIR_MONTH(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,STATIC_TIME datetime,TIMES int,MN varchar(24),A00000_MIN double,A00000_MIN_STATE int,A00000_ZSMIN double,A00000_ZSMIN_STATE int,A00000_MAX double,A00000_MAX_STATE int,A00000_ZSMAX double,A00000_ZSMAX_STATE int,A00000_AVG double,A00000_AVG_STATE int,A00000_ZSAVG double,A00000_ZSAVG_STATE int,A00000_COU double,A00000_COU_STATE int,A00000_ZSCOU double,A00000_ZSCOU_STATE int,INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_AIR_YEAR_SQL = new MessageFormat("create table AIR_YEAR(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,STATIC_TIME datetime,TIMES int,MN varchar(24),A00000_MIN double,A00000_MIN_STATE int,A00000_ZSMIN double,A00000_ZSMIN_STATE int,A00000_MAX double,A00000_MAX_STATE int,A00000_ZSMAX double,A00000_ZSMAX_STATE int,A00000_AVG double,A00000_AVG_STATE int,A00000_ZSAVG double,A00000_ZSAVG_STATE int,A00000_COU double,A00000_COU_STATE int,A00000_ZSCOU double,A00000_ZSCOU_STATE int,INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_COM_SCHEDULE_AIR_SQL = new MessageFormat("create table COM_SCHEDULE_AIR(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,STATIC_TIME datetime,COMPANY_ID char(32),MN varchar(24),A00000_DAY_COU double,A00000_MONTH_COU double,A00000_YEAR_COU double,A00000_DAY_PROCESS double,A00000_MONTH_PROCESS double,A00000_YEAR_PROCESS double,INDEX(STATIC_TIME),INDEX(MN))");
    public static final MessageFormat BAK_AIR_OFFLINE_SQL = new MessageFormat("create table AIR_OFFLINE(ID char(32) primary key not null,MN varchar(24),START_TIME datetime,END_TIME datetime,INDEX(MN))");
    public static final MessageFormat BAK_AIR_CURRENT_OVERPROOF_SQL = new MessageFormat("create table AIR_CURRENT_OVERPROOF(ID char(32) primary key not null,SOURCE_ID varchar(20),MN varchar(24),CODE varchar(20),VALUE double,STANDARD_VALUE double,STATUS int,DATA_TIME datetime,INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_AIRQ_HOUR_SQL = new MessageFormat("create table airq_hour(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,MN varchar(24),STATE int,LEVEL varchar(20),FIRST_CODE varchar(50),AQI double,INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_AIRQ_DAY_SQL = new MessageFormat("create table airq_day(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,MN varchar(24),STATE int,LEVEL varchar(20),FIRST_CODE varchar(50),AQI double,INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_AIRQ_MONTH_SQL = new MessageFormat("create table airq_month(ID char(32) primary key not null,MONTH char(6),CREATE_TIME datetime,MN varchar(24),FINE_DAYS int,TOTAL_I double,A21026_AVG double,A21004_AVG double,A34002_AVG double,A34004_AVG double,A21005_95 double,A05024_90 double,A21026_S double,A21004_S double,A34002_S double,A34004_S double,A21005_S double,A05024_S double,A21026_I double,A21004_I double,A34002_I double,A34004_I double,A21005_I double,A05024_I double,INDEX(MN),INDEX(MONTH))");
    public static final MessageFormat BAK_NOISE_CURRENT_TR_SQL = new MessageFormat("create table NOISE_CURRENT_TR_{0}(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,MN varchar(24),STATE int,INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_NOISE_MINUTE_SQL = new MessageFormat("create table NOISE_MINUTE_{0}(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,MN varchar(24),STATE int,INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_NOISE_HOUR_SQL = new MessageFormat("create table NOISE_HOUR(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,MN varchar(24),STATE int,INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_NOISE_DAY_SQL = new MessageFormat("create table NOISE_DAY(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,MN varchar(24),STATE int,INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_SURFWATER_HOUR_SQL = new MessageFormat("create table surfwater_hour(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,MN varchar(24),STATE int,FIRST_CODE varchar(50),LEVEL int,LEVEL_ST int,OVER_PERCENT double,INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_SURFWATER_DAY_SQL = new MessageFormat("create table surfwater_day(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,MN varchar(24),STATE int,FIRST_CODE varchar(50),LEVEL int,LEVEL_ST int,OVER_PERCENT double,INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_AIR_INCINERATOR_CURRENT_SQL = new MessageFormat("create table AIR_INCINERATOR_CURRENT(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,MN varchar(24),STATE int,INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_VOC_CURRENT_TR_SQL = new MessageFormat("create table VOC_CURRENT_TR_{0}(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,MN varchar(24),STATE int,INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_VOC_MINUTE_SQL = new MessageFormat("create table VOC_MINUTE_{0}(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,MN varchar(24),STATE int,INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_VOC_HOUR_SQL = new MessageFormat("create table VOC_HOUR(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,MN varchar(24),STATE int,INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_VOC_DAY_SQL = new MessageFormat("create table VOC_DAY(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,MN varchar(24),STATE int,INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_VOC_MONTH_SQL = new MessageFormat("create table VOC_MONTH(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,STATIC_TIME datetime,TIMES int,MN varchar(24),INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_VOC_YEAR_SQL = new MessageFormat("create table VOC_YEAR(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,STATIC_TIME datetime,TIMES int,MN varchar(24),INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_COM_SCHEDULE_VOC_SQL = new MessageFormat("create table COM_SCHEDULE_VOC(ID char(32) primary key not null,DATA_TIME datetime,CREATE_TIME datetime,STATIC_TIME datetime,COMPANY_ID char(32),MN varchar(24),INDEX(STATIC_TIME),INDEX(MN))");
    public static final MessageFormat BAK_VOC_OFFLINE_SQL = new MessageFormat("create table VOC_OFFLINE(ID char(32) primary key not null,MN varchar(24),START_TIME datetime,END_TIME datetime,INDEX(MN))");
    public static final MessageFormat BAK_VOC_CURRENT_OVERPROOF_SQL = new MessageFormat("create table VOC_CURRENT_OVERPROOF(ID char(32) primary key not null,SOURCE_ID varchar(20),MN varchar(24),CODE varchar(20),VALUE double,STANDARD_VALUE double,STATUS int,DATA_TIME datetime,INDEX(MN),INDEX(DATA_TIME))");
    public static final MessageFormat BAK_WATER_CURRENT_TR_ALTERSQL = new MessageFormat("alter table WATER_CURRENT_TR_{0}  add column  {1}_RTD double,add column  {2}_FLAG varchar(2),add column  {3}_STATE int,add column  {4}_SAMPLETIME datetime");
    public static final MessageFormat BAK_WATER_MHD_ALTERSQL = new MessageFormat("alter table WATER_{0} add column {1}_MIN double,add column {2}_MIN_STATE int,add column {3}_MAX double,add column {4}_MAX_STATE int,add column {5}_AVG double,add column {6}_AVG_STATE int,add column {7}_COU double,add column {8}_COU_STATE int,add column {9}_FLAG varchar(2)");
    public static final MessageFormat BAK_WATER_MY_ALTERSQL = new MessageFormat("alter table WATER_{0} add column {1}_MIN double,add column {2}_MIN_STATE int,add column {3}_MAX double,add column {4}_MAX_STATE int,add column {5}_AVG double,add column {6}_AVG_STATE int,add column {7}_COU double,add column {8}_COU_STATE int");
    public static final MessageFormat BAK_COM_SCHEDULE_ALTERSQL = new MessageFormat("alter table COM_SCHEDULE add column {0}_DAY_COU double,add column {1}_MONTH_COU double,add column {2}_YEAR_COU double,add column {3}_DAY_PROCESS double,add column {4}_MONTH_PROCESS double,add column {5}_YEAR_PROCESS double");
    public static final MessageFormat BAK_AIR_CURRENT_TR_ALTERSQL = new MessageFormat("alter table AIR_CURRENT_TR_{0}  add column  {1}_RTD double,add column  {2}_FLAG varchar(2),add column  {3}_STATE int, add column  {4}_ZSRTD double,add column  {5}_ZSSTATE int");
    public static final MessageFormat BAK_AIR_MHD_ALTERSQL = new MessageFormat("alter table AIR_{0} add column {1}_MIN double,add column {2}_MIN_STATE int,add column {3}_MAX double,add column {4}_MAX_STATE int,add column {5}_AVG double,add column {6}_AVG_STATE int,add column {7}_COU double,add column {8}_COU_STATE int,add column {9}_FLAG varchar(2),add column {10}_ZSMIN double,add column {11}_ZSMIN_STATE int,add column {12}_ZSMAX double,add column {13}_ZSMAX_STATE int,add column {14}_ZSAVG double,add column {15}_ZSAVG_STATE int,add column {16}_ZSCOU double,add column {17}_ZSCOU_STATE int");
    public static final MessageFormat BAK_AIR_MY_ALTERSQL = new MessageFormat("alter table AIR_{0} add column {1}_MIN double,add column {2}_MIN_STATE int,add column {3}_MAX double,add column {4}_MAX_STATE int,add column {5}_AVG double,add column {6}_AVG_STATE int,add column {7}_COU double,add column {8}_COU_STATE int,add column {9}_ZSMIN double,add column {10}_ZSMIN_STATE int,add column {11}_ZSMAX double,add column {12}_ZSMAX_STATE int,add column {13}_ZSAVG double,add column {14}_ZSAVG_STATE int,add column {15}_ZSCOU double,add column {16}_ZSCOU_STATE int");
    public static final MessageFormat BAK_COM_SCHEDULE_AIR_ALTERSQL = new MessageFormat("alter table COM_SCHEDULE_AIR add column {0}_DAY_COU double,add column {1}_MONTH_COU double,add column {2}_YEAR_COU double,add column {3}_DAY_PROCESS double,add column {4}_MONTH_PROCESS double,add column {5}_YEAR_PROCESS double");
    public static final MessageFormat BAK_AIRQ_HOUR_ALTERSQL = new MessageFormat("alter table AIRQ_HOUR add column {0}_AVG double,add column  {1}_IAQI double");
    public static final MessageFormat BAK_AIRQ_DAY_ALTERSQL = new MessageFormat("alter table AIRQ_DAY add column {0}_AVG double,add column  {1}_IAQI double");
    public static final MessageFormat BAK_SURFWATER_HOUR_ALTERSQL = new MessageFormat("alter table surfwater_hour add column {0}_AVG double,add column  {1}_ST varchar(24),add column {2}_OVER_MULTIPLE double,add column {3}_ISOVER int,add column {4}_LEVEL int");
    public static final MessageFormat BAK_SURFWATER_DAY_ALTERSQL = new MessageFormat("alter table surfwater_day add column {0}_AVG double,add column  {1}_ST varchar(24),add column {2}_OVER_MULTIPLE double,add column {3}_ISOVER int,add column {4}_LEVEL int");
    public static final MessageFormat BAK_NOISE_CURRENT_TR_ALTERSQL = new MessageFormat("alter table noise_current_tr_{0} add column {1}_RTD double ");
    public static final MessageFormat BAK_NOISE_MINUTE_ALTERSQL = new MessageFormat("alter table noise_minute_{0} add column {1}_DATA double ");
    public static final MessageFormat BAK_NOISE_HOUR_ALTERSQL = new MessageFormat("alter table noise_hour add column {0}_DATA double ");
    public static final MessageFormat BAK_NOISE_DAY_ALTERSQL = new MessageFormat("alter table noise_day add column  {0}_DATA double,add  column  {1}_DAYDATA double,add column  {2}_NIGHTDATA double");
    public static final MessageFormat BAK_NOISE_DAY_LDN_ALTERSQL = new MessageFormat("alter table noise_day add column  {0}_DATA double,add  column  {1}_DAYDATA double,add column  {2}_NIGHTDATA double,add column {3}_ISOVER int COMMENT '{4}'");
    public static final String BAK_NOISE_DAY_LDN_ALTERSQL_1 = "1-达标；2-不达标";
    public static final MessageFormat BAK_AIR_INCINERATOR_CURRENT_ALTERSQL = new MessageFormat("alter table air_incinerator_current add column {0}_INFO double");
    public static final MessageFormat BAK_VOC_CURRENT_TR_ALTERSQL = new MessageFormat("alter table VOC_CURRENT_TR_{0}  add column  {1}_RTD double,add column  {2}_FLAG varchar(2),add column  {3}_STATE int, add column  {4}_ZSRTD double,add column  {5}_ZSSTATE int");
    public static final MessageFormat BAK_VOC_MHD_ALTERSQL = new MessageFormat("alter table VOC_{0} add column {1}_MIN double,add column {2}_MIN_STATE int,add column {3}_MAX double,add column {4}_MAX_STATE int,add column {5}_AVG double,add column {6}_AVG_STATE int,add column {7}_COU double,add column {8}_COU_STATE int,add column {9}_FLAG varchar(2),add column {10}_ZSMIN double,add column {11}_ZSMIN_STATE int,add column {12}_ZSMAX double,add column {13}_ZSMAX_STATE int,add column {14}_ZSAVG double,add column {15}_ZSAVG_STATE int,add column {16}_ZSCOU double,add column {17}_ZSCOU_STATE int");
    public static final MessageFormat BAK_VOC_MY_ALTERSQL = new MessageFormat("alter table VOC_{0} add column {1}_MIN double,add column {2}_MIN_STATE int,add column {3}_MAX double,add column {4}_MAX_STATE int,add column {5}_AVG double,add column {6}_AVG_STATE int,add column {7}_COU double,add column {8}_COU_STATE int,add column {9}_ZSMIN double,add column {10}_ZSMIN_STATE int,add column {11}_ZSMAX double,add column {12}_ZSMAX_STATE int,add column {13}_ZSAVG double,add column {14}_ZSAVG_STATE int,add column {15}_ZSCOU double,add column {16}_ZSCOU_STATE int");
    public static final MessageFormat BAK_COM_SCHEDULE_VOC_ALTERSQL = new MessageFormat("alter table COM_SCHEDULE_VOC add column {0}_DAY_COU double,add column {1}_MONTH_COU double,add column {2}_YEAR_COU double,add column {3}_DAY_PROCESS double,add column {4}_MONTH_PROCESS double,add column {5}_YEAR_PROCESS double");

}
