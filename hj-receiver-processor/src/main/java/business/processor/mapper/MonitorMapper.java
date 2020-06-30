package business.processor.mapper;

import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface MonitorMapper {

    @Update("update site_monitor_point set ${column}=#{status} where mn=#{mn}  ")
    void updateMonitorStatus(String column,String mn,int status);

    List<Map<String,Object>> getAllMonitor();

    List<Map<String,Object>> getAirQMonitor();
}
