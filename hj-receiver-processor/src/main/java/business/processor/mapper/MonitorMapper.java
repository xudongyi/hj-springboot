package business.processor.mapper;

import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

@Component
public interface MonitorMapper {

    @Update("sqlExcute mon_monitor set #{column}=#{status} where mn=#{mn}  ")
    void updateMonitorStatus(String column,String mn,int status);

}
