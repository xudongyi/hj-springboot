package business.receiver.task;

import business.receiver.mapper.MyBaseMapper;
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
    private MyBaseMapper commonMapper;

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
        setBakSourceQuerySql(thisMonth);
        String tableName = "sys_device_message_" + thisMonth;
        if (commonMapper.checkTableExists(tableName)==0) {
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
            bakSourceSql_query_hand = "select * from sys_device_message_" + yyMM + " WHERE ID>''{0}'' and TAG = 0 order by ID asc limit 0," + countLimit + "";
        }

        synchronized(bakSourceSql_update_hand) {
            bakSourceSql_update_hand = "update sys_device_message_" + yyMM + " set TAG ={0} where ID=''{1}'' ";
        }
    }

}
