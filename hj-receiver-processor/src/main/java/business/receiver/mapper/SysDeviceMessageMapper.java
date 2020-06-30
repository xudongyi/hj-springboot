package business.receiver.mapper;


import business.receiver.entity.SysDeviceMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public interface  SysDeviceMessageMapper extends BaseMapper<SysDeviceMessage> {
    /**创建废水，废气的报文接收表**/
    void createSysDeviceMessageTable(String tableName);

    /**更新tag状态**/
    int updateTag(String tableName,long sourceId, int tag);

}
