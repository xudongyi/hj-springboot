package business.receiver.mapper;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface MyBaseMapper {
    /**
     * 使用tableName检查表是否存在
     * @param tableName
     * @return
     */
    Integer checkTableExists(String tableName);

    List<Map<String,Object>> sqlQuery(String sql);

    Integer insert(String sql);

    Integer delete(String sql);

    Integer sqlExcute(String sql);

    int isExistMHDData(String tableName,String dataTime,String mn);
}
