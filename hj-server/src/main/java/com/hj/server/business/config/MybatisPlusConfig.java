package com.hj.server.business.config;

import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.core.parser.ISqlParserFilter;
import com.baomidou.mybatisplus.core.parser.SqlParserHelper;
import com.baomidou.mybatisplus.extension.parsers.DynamicTableNameParser;
import com.baomidou.mybatisplus.extension.parsers.ITableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

/**
 * @author xudy
 * @since 2020-06-20
 */
@Configuration
@MapperScan("com.hj.server.business.mapper")
public class MybatisPlusConfig {

    /**
     * user动态表存放对象
     */
    public static ThreadLocal<String> tableName =new ThreadLocal<>();

    @Bean
    PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        ArrayList<ISqlParser> sqlParsersList = new ArrayList<>();

        //动态表名解析器(报文接收表)
        Map<String, ITableNameHandler> tableNameHandlerMap = new HashMap<>();
        tableNameHandlerMap.put("sys_device_message", new ITableNameHandler() {
            @Override
            public String dynamicTableName(MetaObject metaObject, String sql, String tableName) {
                return MybatisPlusConfig.tableName.get();
            }
        });
        tableNameHandlerMap.put("water_current", new ITableNameHandler() {
            @Override
            public String dynamicTableName(MetaObject metaObject, String sql, String tableName) {
                return MybatisPlusConfig.tableName.get();
            }
        });

        DynamicTableNameParser dynamicTableNameParser = new DynamicTableNameParser();
        dynamicTableNameParser.setTableNameHandlerMap(tableNameHandlerMap);
        sqlParsersList.add(dynamicTableNameParser);

        paginationInterceptor.setSqlParserList(sqlParsersList);

        /**
         * 过滤掉（即不引入）哪些方法的sql解析功能（即不进行动态表名替换）
         */
        paginationInterceptor.setSqlParserFilter(new ISqlParserFilter() {
            @Override
            public boolean doFilter(MetaObject metaObject) {
                MappedStatement ms = SqlParserHelper.getMappedStatement(metaObject);
                if("com.dsf.mp.dynamicTableNameParser.dao.UserMapper.selectById".equals(ms.getId())){
                    return true;
                }
                return false;
            }
        });

        return paginationInterceptor;
    }
}