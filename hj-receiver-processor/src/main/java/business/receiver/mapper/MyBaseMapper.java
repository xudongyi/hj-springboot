package business.receiver.mapper;

import java.util.List;
import java.util.Map;

public interface MyBaseMapper {
    /**
     * 使用tableName检查表是否存在
     * @param tableName
     * @return
     */
    Integer checkTableExists(String tableName);

    List<Map<String,Object>> sqlQuery(String sql,List<Object> params);

}
