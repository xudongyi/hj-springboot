package business.receiver.mapper;


import business.receiver.entity.SysDeviceMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface  SysDeviceMessageMapper extends BaseMapper<SysDeviceMessage> {
    /**创建废水，废气的报文接收表**/
    void createSysDeviceMessageTable(String tableName);
}
