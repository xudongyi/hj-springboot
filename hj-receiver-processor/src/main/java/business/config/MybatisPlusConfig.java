package business.config;

import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.core.parser.ISqlParserFilter;
import com.baomidou.mybatisplus.core.parser.SqlParserHelper;
import com.baomidou.mybatisplus.extension.parsers.DynamicTableNameParser;
import com.baomidou.mybatisplus.extension.parsers.ITableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 单数据源配置（jeecg.datasource.open = false时生效）
 *
 * @Author zhoujf
 */
@Configuration
@MapperScan(value = {"business.receiver.mapper","business.processor.mapper"})
public class MybatisPlusConfig {


    /**
     * user动态表存放对象
     */
    public static ThreadLocal<String> tableName = new ThreadLocal<>();

    @Bean
    PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        ArrayList<ISqlParser> sqlParsersList = new ArrayList<>();
        //动态表名解析器(报文接收表)
        Map<String, ITableNameHandler> tableNameHandlerMap = new HashMap<>();
        tableNameHandlerMap.put("sys_device_message", (metaObject, sql, tableName) -> MybatisPlusConfig.tableName.get());
        tableNameHandlerMap.put("water_current", (metaObject, sql, tableName) -> MybatisPlusConfig.tableName.get());
        DynamicTableNameParser dynamicTableNameParser = new DynamicTableNameParser();
        dynamicTableNameParser.setTableNameHandlerMap(tableNameHandlerMap);
        sqlParsersList.add(dynamicTableNameParser);
        paginationInterceptor.setSqlParserList(sqlParsersList);

        /**
         * 过滤掉（即不引入）哪些方法的sql解析功能（即不进行动态表名替换）
         */
        paginationInterceptor.setSqlParserFilter((metaObject) -> {
            MappedStatement ms = SqlParserHelper.getMappedStatement(metaObject);
            if ("com.dsf.mp.dynamicTableNameParser.dao.UserMapper.selectById".equals(ms.getId())) {
                return true;
            }
            return false;
        });

        return paginationInterceptor;
    }
}
