package business.mapper;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface MonitorMapper {

    List<Map<String,Object>> getAllMonitor();
}
