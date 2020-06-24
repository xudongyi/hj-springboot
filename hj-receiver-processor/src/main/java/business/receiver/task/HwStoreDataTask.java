package business.receiver.task;

import business.util.CommonsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class HwStoreDataTask {
    public static boolean isCreatingData = false;
    @Autowired
    private IBaseDao baseDao;

    public HwStoreDataTask() {
    }

    @Scheduled(
        cron = "0 5 0 1 * ?"
    )
    @Async
    public void run() {
        isCreatingData = true;

        try {
            Calendar cl = Calendar.getInstance();
            cl.setTime(new Date());
            cl.set(2, cl.get(2) - 1);
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
            String lastMonth = sf.format(cl.getTime());
            String thisMonth = sf.format(new Date());
            List<Object> params = new ArrayList();
            String query_sql = "select * from hh_hwstore.store_period where MONTH=?";
            String existThisMonth_sql = "select * from hh_hwstore.store_period where MONTH=? and MN=? and WASTE_NAME=? and WASTE_CODE=?";
            String sql = "insert into hh_hwstore.store_period(ID,MONTH,MN,WASTE_NAME,WASTE_CODE,IN_AMOUNT,OUT_AMOUNT,BEGIN_AMOUNT,END_AMOUNT,SELF_AMOUNT,TRANSFER_AMOUNT,DIFF_AMOUNT)values(?,?,?,?,?,?,?,?,?,?,?,?)";
            params.add(lastMonth);
            List<Map<String, Object>> list = this.baseDao.sqlQuery(query_sql, params);
            if (list != null && !list.isEmpty()) {
                Iterator var10 = list.iterator();

                while(true) {
                    Map data;
                    List existData;
                    do {
                        if (!var10.hasNext()) {
                            return;
                        }

                        data = (Map)var10.next();
                        params = new ArrayList();
                        params.add(thisMonth);
                        params.add(data.get("MN"));
                        params.add(data.get("WASTE_NAME"));
                        params.add(data.get("WASTE_CODE"));
                        existData = this.baseDao.sqlQuery(existThisMonth_sql, params);
                    } while(existData != null && !existData.isEmpty());

                    params = new ArrayList();
                    params.add(CommonsUtil.createUUID1());
                    params.add(thisMonth);
                    params.add(data.get("MN"));
                    params.add(data.get("WASTE_NAME"));
                    params.add(data.get("WASTE_CODE"));
                    params.add(0.0D);
                    params.add(0.0D);
                    params.add(data.get("END_AMOUNT"));
                    params.add(data.get("END_AMOUNT"));
                    params.add(0.0D);
                    params.add(0.0D);
                    params.add(0.0D);
                    this.baseDao.sqlExcute(sql, params);
                }
            }
        } catch (Exception var16) {
            var16.printStackTrace();
        } finally {
            isCreatingData = false;
        }

    }
}