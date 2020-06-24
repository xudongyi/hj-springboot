package business.receiver.task;

import business.receiver.mapper.CommonMapper;
import business.receiver.mapper.SysDeviceMessageMapper;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 更新接收程序的数据库表，sys_device_message_2006
 */
@Component
public class UpdateReceiverTableTask {
    @Autowired
    private SysDeviceMessageMapper sysDeviceMessageMapper;
    @Autowired
    private CommonMapper commonMapper;

    private static String bakSourceSql_insert_auto = "";
    private static String bakSourceSql_query_hand = "";
    private static String bakSourceSql_update_auto = "";
    private static String bakSourceSql_update_hand = "";
    private static boolean isInitial = false;
    private static int countLimit = 200;

    public UpdateReceiverTableTask() {
    }

    @Value("${process.hand.counts.persecond}")
    private void setCountLimit(String v) {
        if (StringUtils.isNotEmpty(v)) {
            try {
                countLimit = Integer.valueOf(v);
            } catch (Exception var3) {
            }
        }

    }

    @PostConstruct
    public void initial() {
        this.excuteBakSourceTable();
        if (!this.baseDao.isExistTable("BAK_REVERSE_LOG")) {
            List<Object> params = new ArrayList();
            params.add("指令类型:1-开阀;2-关阀;3-留样;4-设置现场时间;5-获取现场时间;6-小时数据补遗;7-日数据补遗;");
            params.add("状态:0-未发;1-已发;2-已接收;3-执行成功;4-执行失败;5-发送失败;");
            params.add("是否已生成取样单:1-否;2-是;");
            this.baseDao.sqlExcute(TableSqlConstant.BAK_REVERSE_LOG_SQL.toPattern(), params);
        }

        isInitial = true;
    }

    public static boolean isInitial() {
        return isInitial;
    }

    @Scheduled(
            cron = "0 0 0 1 * ?"
    )
    @Async
    public void run() {
        this.excuteBakSourceTable();
    }

    /**
     * 定时执行操作
     */
    private void excuteBakSourceTable() {
        String thisMonth = DateUtil.format(new Date(),"yyMM");
        String tableName = "sys_device_message_" + thisMonth;
        if (commonMapper.checkTableExistsWithSchema(tableName)==0) {
            sysDeviceMessageMapper.createSysDeviceMessageTable(tableName);
        }

    }

    public static String getBakSourceSql_insert_auto() {
        return bakSourceSql_insert_auto;
    }

    public static String getBakSourceSql_query_hand() {
        return bakSourceSql_query_hand;
    }

    public static String getBakSourceSql_update_auto() {
        return bakSourceSql_update_auto;
    }

    public static String getBakSourceSql_update_hand() {
        return bakSourceSql_update_hand;
    }

    public static void setBakSourceQuerySql(String yyMM) {
        synchronized(bakSourceSql_query_hand) {
            bakSourceSql_query_hand = "select * from BAK_SOURCE_" + yyMM + " WHERE ID>? and TAG = 0 order by ID asc limit 0," + countLimit + "";
            DataParserHandTask.bakSourceDataId = -1L;
        }

        synchronized(bakSourceSql_update_hand) {
            bakSourceSql_update_hand = "update BAK_SOURCE_" + yyMM + " set TAG =? where ID=? ";
        }
    }

}
