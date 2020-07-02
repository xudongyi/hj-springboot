package business.receiver.service;

import business.receiver.mapper.MyBaseMapper;
import business.receiver.task.HwStoreDataTask;
import business.receiver.threadPool.ThreadPoolService;
import business.util.CommonsUtil;
import business.util.SqlBuilder;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("hwStoreDataService")
@Slf4j
public class HwStoreDataService {
    @Autowired
    private BlackListService blackListService;
    @Autowired
    private ThreadPoolService threadPoolService;
    @Autowired
    private MyBaseMapper myBaseMapper;
    public HwStoreDataService() {
    }

    public void accept(final String msg, final ChannelHandlerContext ctx) {
        this.threadPoolService.getReceivePool().execute(() -> {
            try {
                HwStoreDataService.this.excute(msg, ctx);
            } catch (Exception var2) {
                HwStoreDataService.log.error("数据解析错误[危废]：" + msg, var2);
            }

        });
    }

    private void excute(String msg, ChannelHandlerContext ctx) {
        int index = msg.indexOf("MN=");
        if (index == -1) {
            log.error("数据报文格式错误[MN错误]：" + msg);
        } else {
            String key = msg.substring(index + 3, msg.indexOf(";", index));
            if (this.blackListService.isReceive(key)) {
                String content = msg.substring(msg.indexOf("#HWSTORE#") + 9, msg.indexOf("&&"));
                String[] data = content.split(";");
                Map<String, String> dataMap = new HashMap();
                String[] var8 = data;
                int var9 = data.length;

                for (int var10 = 0; var10 < var9; ++var10) {
                    String item = var8[var10];
                    String[] value = item.split("=");
                    dataMap.put(value[0], value[1]);
                }

                String ST = dataMap.get("ST");
                boolean isnew;
                if (ST.equals("SH")) {
                    if (!this.isExistStoreHistory(dataMap)) {
                        isnew = this.isNewStoreHistory(dataMap);
                        this.saveHistoryStore(dataMap);
                        if (isnew) {
                            this.saveCurrentStore(dataMap);
                            this.savePeriodStore(dataMap);
                        }
                    }
                } else if (ST.equals("SI") && !this.isExistStoreInventory(dataMap)) {
                    isnew = this.isNewStoreInventory(dataMap);
                    this.saveInventoryStore(dataMap);
                    if (isnew) {
                        this.saveCurrentStore(dataMap);
                        this.savePeriodStore(dataMap);
                    }
                }

                ctx.writeAndFlush(Unpooled.copiedBuffer("#HWSTORE#ST=" + ST + ";RESOURCE_ID=" +dataMap.get("RESOURCE_ID") + ";Flag=OK&&\r\n", CharsetUtil.UTF_8));
            }
        }
    }

    private void waitPeriodCreating() {
        for (int i = 0; HwStoreDataTask.isCreatingData && i <= 30; ++i) {
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException var3) {
                var3.printStackTrace();
            }
        }

    }

    private void saveCurrentStore(Map<String, String> data) {
        List<Object> params = new ArrayList();
        params.add(data.get("MN"));
        params.add(data.get("WASTE_CODE"));
        params.add(data.get("WASTE_NAME"));
        List<Map<String, Object>> list = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql("select * from hh_hwstore.store_current where MN=? and WASTE_CODE=? and WASTE_NAME=? ", params));
        if (list != null && list.size() > 0) {
            String id = (String) list.get(0).get("ID");
            this.updateCurrentStore(data, id);
        } else {
            this.insertCurrentStore(data);
        }

    }

    private void insertCurrentStore(Map<String, String> data) {
        List<Object> params = new ArrayList();
        String sql = "insert into hh_hwstore.store_current(ID,MN,UPDATE_TIME,WASTE_NAME,WASTE_CODE,AMOUNT)values(?,?,?,?,?,?)";
        params.add(CommonsUtil.createUUID1());
        params.add(data.get("MN"));
        params.add(CommonsUtil.dateParse((String) data.get("DataTime"), "yyyyMMddHHmmss"));
        params.add(data.get("WASTE_NAME"));
        params.add(data.get("WASTE_CODE"));
        if (data.get("TOTAL_AMOUNT") != null) {
            params.add(Double.valueOf((String) data.get("TOTAL_AMOUNT")));
        } else if (data.get("NEW_AMOUNT") != null) {
            params.add(Double.valueOf((String) data.get("NEW_AMOUNT")));
        }

        this.myBaseMapper.sqlExcute(SqlBuilder.buildSql(sql, params));
    }

    private void updateCurrentStore(Map<String, String> data, String id) {
        List<Object> params = new ArrayList();
        String sql = "sqlExcute hh_hwstore.store_current set AMOUNT=?,UPDATE_TIME=? where ID=?";
        if (data.get("TOTAL_AMOUNT") != null) {
            params.add(Double.valueOf(data.get("TOTAL_AMOUNT")));
        } else if (data.get("NEW_AMOUNT") != null) {
            params.add(Double.valueOf( data.get("NEW_AMOUNT")));
        }

        params.add(CommonsUtil.dateParse(data.get("DataTime"), "yyyyMMddHHmmss"));
        params.add(id);
        this.myBaseMapper.sqlExcute(SqlBuilder.buildSql(sql, params));
    }

    private void saveHistoryStore(Map<String, String> data) {
        List<Object> params = new ArrayList();
        String sql = "insert into hh_hwstore.store_history(ID,MN,WASTE_NAME,WASTE_CODE,BATCH_NO,TYPE,DETAIL_TYPE,AMOUNT,INPUT_AMOUNT,DIFF_AMOUNT,TOTAL_AMOUNT,CONTAINER_COUNTS,STORE_AREA,DATA_TIME,OPERATOR,CONTAINER_TYPE,CONTAINER_SIZE,CONTAINER_MATERIAL,DISPOSAL_NAME,DISPOSAL_ADDR,DISPOSAL_CITY,LECENCE_NO,DISPOSAL_TYPE,WASTE_SOURCE,WORKSHOP,TRANSFER_MANAGER,STORE_MANAGER)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        params.add(CommonsUtil.createUUID1());
        params.add(data.get("MN"));
        params.add(data.get("WASTE_NAME"));
        params.add(data.get("WASTE_CODE"));
        params.add(data.get("BATCH_NO"));
        params.add(Integer.valueOf(data.get("TYPE")));
        params.add(Integer.valueOf(data.get("DETAIL_TYPE")));
        params.add(Double.valueOf(data.get("AMOUNT")));
        params.add(Double.valueOf(data.get("INPUT_AMOUNT")));
        params.add(Double.valueOf(data.get("DIFF_AMOUNT")));
        params.add(Double.valueOf(data.get("TOTAL_AMOUNT")));
        params.add(Integer.valueOf(data.get("CONTAINER_COUNTS")));
        params.add(data.get("STORE_AREA"));
        params.add(CommonsUtil.dateParse((String) data.get("DataTime"), "yyyyMMddHHmmss"));
        params.add(data.get("OPERATOR"));
        params.add(data.get("CONTAINER_TYPE"));
        params.add(data.get("CONTAINER_SIZE"));
        params.add(data.get("CONTAINER_MATERIAL"));
        params.add(data.get("DISPOSAL_NAME"));
        params.add(data.get("DISPOSAL_ADDR"));
        params.add(data.get("DISPOSAL_CITY"));
        params.add(data.get("LECENCE_NO"));
        params.add(data.get("DISPOSAL_TYPE"));
        String wasteSource = "";
        String workshop = "";
        new ArrayList();
        String waste_source_sql = "";
        ArrayList params_waste_source;
        if (Integer.valueOf(data.get("TYPE")) == 1) {
            waste_source_sql = " select distinct WASTE_SOURCE,WORKSHOP  from hh_hwstore.com_product_waste_view a  left join hh_hwstore.waste_view b on a.WASTE_ID=b.ID  left join hh_hwstore.com_storage_view c on c.COMPANY_ID=a.COMPANY_ID  left join hh_hwstore.storage_mn d on d.STORAGE_ID=c.ID  where d.MN=? and b.NAME=? and b.CODE=?  group by WASTE_SOURCE,WORKSHOP";
            params_waste_source = new ArrayList();
            params_waste_source.add(data.get("MN"));
            params_waste_source.add(data.get("WASTE_NAME"));
            params_waste_source.add(data.get("WASTE_CODE"));
        } else {
            waste_source_sql = "select WASTE_SOURCE,WORKSHOP from hh_hwstore.store_history where MN=? and BATCH_NO=? and TYPE=1 ";
            params_waste_source = new ArrayList();
            params_waste_source.add(data.get("MN"));
            params_waste_source.add(data.get("BATCH_NO"));
        }

        List<Map<String, Object>> waste_source_list = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(waste_source_sql, params_waste_source));
        if (waste_source_list != null && waste_source_list.size() > 0) {
            for (int i = 0; i < waste_source_list.size(); ++i) {
                String data_waste_source = (String) ((Map) waste_source_list.get(i)).get("WASTE_SOURCE");
                if (StringUtils.isNotEmpty(data_waste_source)) {
                    wasteSource = wasteSource + data_waste_source + "/";
                }

                String data_workshop = (String) waste_source_list.get(i).get("WORKSHOP");
                if (StringUtils.isNotEmpty(data_workshop)) {
                    workshop = workshop + data_workshop + "/";
                }
            }
        }

        if (wasteSource.endsWith("/")) {
            wasteSource = wasteSource.substring(0, wasteSource.length() - 1);
        }

        if (workshop.endsWith("/")) {
            workshop = workshop.substring(0, workshop.length() - 1);
        }

        params.add(wasteSource);
        params.add(workshop);
        params.add(data.get("TRANSFER_MANAGER"));
        params.add(data.get("STORE_MANAGER"));
        this.myBaseMapper.sqlExcute(SqlBuilder.buildSql(sql, params));
    }

    private void saveInventoryStore(Map<String, String> data) {
        List<Object> params = new ArrayList();
        String sql = "insert into hh_hwstore.store_inventory(ID,MN,WASTE_NAME,WASTE_CODE,OLD_AMOUNT,NEW_AMOUNT,DIFF_AMOUNT,TYPE,DATA_TIME,OPERATOR)values(?,?,?,?,?,?,?,?,?,?)";
        params.add(CommonsUtil.createUUID1());
        params.add(data.get("MN"));
        params.add(data.get("WASTE_NAME"));
        params.add(data.get("WASTE_CODE"));
        params.add(Double.valueOf( data.get("OLD_AMOUNT")));
        params.add(Double.valueOf(data.get("NEW_AMOUNT")));
        params.add(Double.valueOf(data.get("DIFF_AMOUNT")));
        params.add(Integer.valueOf(data.get("TYPE")));
        params.add(CommonsUtil.dateParse(data.get("DataTime"), "yyyyMMddHHmmss"));
        params.add(data.get("OPERATOR"));
        this.myBaseMapper.sqlExcute(SqlBuilder.buildSql(sql, params));
    }

    private void savePeriodStore(Map<String, String> data) {
        this.waitPeriodCreating();
        List<Object> params = new ArrayList();
        params.add(data.get("MN"));
        params.add(data.get("WASTE_CODE"));
        params.add(data.get("WASTE_NAME"));
        params.add(((String) data.get("DataTime")).substring(0, 6));
        List<Map<String, Object>> list = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql("select * from hh_hwstore.store_period where MN=? and WASTE_CODE=? and WASTE_NAME=? and MONTH=?", params));
        if (list != null && list.size() > 0) {
            this.updatePeriodStore(data, (Map) list.get(0));
        } else {
            this.insertPeriodStore(data);
        }

    }

    private void insertPeriodStore(Map<String, String> data) {
        List<Object> params = new ArrayList();
        String sql = "insert into hh_hwstore.store_period(ID,MONTH,MN,WASTE_NAME,WASTE_CODE,IN_AMOUNT,OUT_AMOUNT,BEGIN_AMOUNT,END_AMOUNT,SELF_AMOUNT,TRANSFER_AMOUNT,DIFF_AMOUNT)values(?,?,?,?,?,?,?,?,?,?,?,?)";
        params.add(CommonsUtil.createUUID1());
        params.add((data.get("DataTime")).substring(0, 6));
        params.add(data.get("MN"));
        params.add(data.get("WASTE_NAME"));
        params.add(data.get("WASTE_CODE"));
        Double IN_AMOUNT = null;
        Double OUT_AMOUNT = null;
        Double BEGIN_AMOUNT = null;
        Double END_AMOUNT = null;
        Double SELF_AMOUNT = null;
        Double TRANSFER_AMOUNT = null;
        Double DIFF_AMOUNT = null;
        String ST = data.get("ST");
        if (ST.equals("SH")) {
            if (data.get("TYPE") != null && data.get("AMOUNT") != null) {
                if ((data.get("TYPE")).equals("1")) {
                    IN_AMOUNT = Double.valueOf(data.get("AMOUNT"));
                } else {
                    OUT_AMOUNT = Double.valueOf(data.get("AMOUNT"));
                }
            }

            if (data.get("DETAIL_TYPE") != null && data.get("AMOUNT") != null) {
                if (!(data.get("DETAIL_TYPE")).equals("4") && !(data.get("DETAIL_TYPE")).equals("5")) {
                    if ((data.get("DETAIL_TYPE")).equals("6") || (data.get("DETAIL_TYPE")).equals("7") || (data.get("DETAIL_TYPE")).equals("8")) {
                        TRANSFER_AMOUNT = Double.valueOf(data.get("AMOUNT"));
                    }
                } else {
                    SELF_AMOUNT = Double.valueOf(data.get("AMOUNT"));
                }
            }

            if (data.get("TOTAL_AMOUNT") != null) {
                if (IN_AMOUNT != null) {
                    BEGIN_AMOUNT = CommonsUtil.numberFormat(Double.valueOf(data.get("TOTAL_AMOUNT")) - IN_AMOUNT);
                    END_AMOUNT = Double.valueOf(data.get("TOTAL_AMOUNT"));
                } else if (OUT_AMOUNT != null) {
                    BEGIN_AMOUNT = CommonsUtil.numberFormat(Double.valueOf(data.get("TOTAL_AMOUNT")) + OUT_AMOUNT);
                    END_AMOUNT = Double.valueOf(data.get("TOTAL_AMOUNT"));
                }
            }
        } else if (ST.equals("SI")) {
            if (data.get("OLD_AMOUNT") != null) {
                BEGIN_AMOUNT = Double.valueOf(data.get("OLD_AMOUNT"));
            }

            if (data.get("NEW_AMOUNT") != null) {
                END_AMOUNT = Double.valueOf(data.get("NEW_AMOUNT"));
            }

            if (data.get("DIFF_AMOUNT") != null) {
                DIFF_AMOUNT = Double.valueOf(data.get("DIFF_AMOUNT"));
            }
        }

        params.add(IN_AMOUNT);
        params.add(OUT_AMOUNT);
        params.add(BEGIN_AMOUNT);
        params.add(END_AMOUNT);
        params.add(SELF_AMOUNT);
        params.add(TRANSFER_AMOUNT);
        params.add(DIFF_AMOUNT);
        this.myBaseMapper.sqlExcute(SqlBuilder.buildSql(sql, params));
    }

    private void updatePeriodStore(Map<String, String> data, Map<String, Object> existData) {
        List<Object> params = new ArrayList();
        StringBuffer sql = new StringBuffer("sqlExcute hh_hwstore.store_period set ");
        Double IN_AMOUNT = null;
        Double OUT_AMOUNT = null;
        Double END_AMOUNT = null;
        Double SELF_AMOUNT = null;
        Double TRANSFER_AMOUNT = null;
        Double DIFF_AMOUNT = null;
        String ST = data.get("ST");
        if (ST.equals("SH")) {
            if (data.get("TYPE") != null && data.get("AMOUNT") != null) {
                if ((data.get("TYPE")).equals("1")) {
                    IN_AMOUNT = Double.valueOf(data.get("AMOUNT"));
                    if (existData.get("IN_AMOUNT") != null) {
                        IN_AMOUNT = CommonsUtil.numberFormat((Double) existData.get("IN_AMOUNT") + IN_AMOUNT);
                    }
                } else {
                    OUT_AMOUNT = Double.valueOf(data.get("AMOUNT"));
                    if (existData.get("OUT_AMOUNT") != null) {
                        OUT_AMOUNT = CommonsUtil.numberFormat((Double) existData.get("OUT_AMOUNT") + OUT_AMOUNT);
                    }
                }
            }

            if (data.get("DETAIL_TYPE") != null && data.get("AMOUNT") != null) {
                if (!(data.get("DETAIL_TYPE")).equals("4") && !(data.get("DETAIL_TYPE")).equals("5")) {
                    if ((data.get("DETAIL_TYPE")).equals("6") || (data.get("DETAIL_TYPE")).equals("7") || (data.get("DETAIL_TYPE")).equals("8")) {
                        TRANSFER_AMOUNT = Double.valueOf(data.get("AMOUNT"));
                        if (existData.get("TRANSFER_AMOUNT") != null) {
                            TRANSFER_AMOUNT = CommonsUtil.numberFormat((Double) existData.get("TRANSFER_AMOUNT") + TRANSFER_AMOUNT);
                        }
                    }
                } else {
                    SELF_AMOUNT = Double.valueOf(data.get("AMOUNT"));
                    if (existData.get("SELF_AMOUNT") != null) {
                        SELF_AMOUNT = CommonsUtil.numberFormat((Double) existData.get("SELF_AMOUNT") + SELF_AMOUNT);
                    }
                }
            }

            if (data.get("TOTAL_AMOUNT") != null) {
                END_AMOUNT = Double.valueOf(data.get("TOTAL_AMOUNT"));
            }
        } else if (ST.equals("SI")) {
            if (data.get("NEW_AMOUNT") != null) {
                END_AMOUNT = Double.valueOf(data.get("NEW_AMOUNT"));
            }

            if (data.get("DIFF_AMOUNT") != null) {
                DIFF_AMOUNT = Double.valueOf(data.get("DIFF_AMOUNT"));
                if (existData.get("DIFF_AMOUNT") != null) {
                    DIFF_AMOUNT = CommonsUtil.numberFormat((Double) existData.get("DIFF_AMOUNT") + DIFF_AMOUNT);
                }
            }
        }

        if (IN_AMOUNT != null) {
            sql.append("IN_AMOUNT=?,");
            params.add(IN_AMOUNT);
        }

        if (OUT_AMOUNT != null) {
            sql.append("OUT_AMOUNT=?,");
            params.add(OUT_AMOUNT);
        }

        if (END_AMOUNT != null) {
            sql.append("END_AMOUNT=?,");
            params.add(END_AMOUNT);
        }

        if (SELF_AMOUNT != null) {
            sql.append("SELF_AMOUNT=?,");
            params.add(SELF_AMOUNT);
        }

        if (TRANSFER_AMOUNT != null) {
            sql.append("TRANSFER_AMOUNT=?,");
            params.add(TRANSFER_AMOUNT);
        }

        if (DIFF_AMOUNT != null) {
            sql.append("DIFF_AMOUNT=?,");
            params.add(DIFF_AMOUNT);
        }

        if (sql.toString().endsWith(",")) {
            sql.deleteCharAt(sql.length() - 1);
        }

        sql.append(" where ID=?");
        params.add(existData.get("ID"));
        this.myBaseMapper.sqlExcute(SqlBuilder.buildSql(sql.toString(), params));
    }

    private boolean isExistStoreHistory(Map<String, String> data) {
        String sql = "select * from hh_hwstore.store_history where MN=? and BATCH_NO=? and TYPE=?";
        List<Object> params = new ArrayList();
        params.add(data.get("MN"));
        params.add(data.get("BATCH_NO"));
        params.add(Integer.valueOf((String) data.get("TYPE")));
        List<Map<String, Object>> list = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(sql, params));
        return list != null && !list.isEmpty();
    }

    private boolean isNewStoreHistory(Map<String, String> data) {
        String sql = "select * from hh_hwstore.store_history where MN=? and DATA_TIME>=?";
        List<Object> params = new ArrayList();
        params.add(data.get("MN"));
        params.add(CommonsUtil.dateParse((String) data.get("DataTime"), "yyyyMMddHHmmss"));
        List<Map<String, Object>> list = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(sql, params));
        return list == null || list.isEmpty();
    }

    private boolean isExistStoreInventory(Map<String, String> data) {
        String sql = "select * from hh_hwstore.store_inventory where MN=? and DATA_TIME=?";
        List<Object> params = new ArrayList();
        params.add(data.get("MN"));
        params.add(CommonsUtil.dateParse((String) data.get("DataTime"), "yyyyMMddHHmmss"));
        List<Map<String, Object>> list = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(sql, params));
        return list != null && !list.isEmpty();
    }

    private boolean isNewStoreInventory(Map<String, String> data) {
        String sql = "select * from hh_hwstore.store_history where MN=? and DATA_TIME>=?";
        List<Object> params = new ArrayList();
        params.add(data.get("MN"));
        params.add(CommonsUtil.dateParse((String) data.get("DataTime"), "yyyyMMddHHmmss"));
        List<Map<String, Object>> list = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql(sql, params));
        return list == null || list.isEmpty();
    }
}