package business.processor.mapper;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
@Component
public interface MonitorDeviceMapper {
    List<Map<String,Object>> getAllMonitorDevice();
}
