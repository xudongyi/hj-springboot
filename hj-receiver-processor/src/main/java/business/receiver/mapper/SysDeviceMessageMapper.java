package business.receiver.mapper;


import business.receiver.entity.SysDeviceMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface  SysDeviceMessageMapper extends BaseMapper<SysDeviceMessage> {

    void createSysDeviceMessageTable(String tableName);
}
