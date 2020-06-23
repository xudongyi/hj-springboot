package business.receiver.task;

import business.receiver.mapper.CommonMapper;
import business.receiver.mapper.SysDeviceMessageMapper;
import cn.hutool.core.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 更新接收程序的数据库表，sys_device_message_2006
 */
@Component
public class UpdateReceiverTableTask {
    @Autowired
    private SysDeviceMessageMapper sysDeviceMessageMapper;
    @Autowired
    private CommonMapper commonMapper;

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

}
