package business.util;

import java.text.MessageFormat;
import java.util.List;

public class SqlBuilder {

    public static  String buildSql(String sql, List<Object> params){
        return MessageFormat.format(sql,params.toArray());
    }
}
