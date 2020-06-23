package business.receiver.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface CommonMapper {
    /**
     * 使用information_schema检查表是否存在
     * @param tableName
     * @return
     */
    Integer checkTableExistsWithSchema(String tableName);

    /**
     * 使用show tables检查表是否存在
     * @param tableName
     * @return
     */
    Map<String, String> checkTableExistsWithShow(@Param("tableName")String tableName);
}
