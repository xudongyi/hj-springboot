//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package business.processor.excute.air;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import business.ienum.FactorType;
import business.processor.bean.DataFactorBean;
import business.processor.bean.DataPacketBean;
import business.processor.excute.DataParserService;
import business.processor.task.UpdateTableFieldTask;
import business.receiver.mapper.MyBaseMapper;
import business.util.CommonsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("airIncineratorExcuter")
@Transactional
@Slf4j
public class AirIncineratorExcuter {
    @Autowired
    private DataParserService dataParserService;
    @Autowired
    private UpdateTableFieldTask updateTableFieldTask;
    @Autowired
    private MyBaseMapper baseDao;

    public AirIncineratorExcuter() {
    }

    public int execute(DataPacketBean dataPacketBean) {
        this.dataParserService.format(dataPacketBean, FactorType.ELECTRIC.TYPE());
        this.saveData(dataPacketBean);
        return 2;
    }

    private void saveData(DataPacketBean dataPacketBean) {
        StringBuilder sql_field = new StringBuilder();
        StringBuilder sql_value = new StringBuilder();
        sql_field.append("INSERT INTO " + this.dataParserService.getMHDTableName(dataPacketBean) + "(ID,DATA_TIME,CREATE_TIME,MN,STATE");
        sql_value.append(")VALUES('"+CommonsUtil.createUUID1()+"','"+CommonsUtil.dateFormat(dataPacketBean.getDataTime())+"','"
                +CommonsUtil.dateFormat(new Date())+"','"+dataPacketBean.getMn()+"',0");
        Map<String, DataFactorBean> map = dataPacketBean.getDataMap();
        Iterator var6 = map.keySet().iterator();

        while(var6.hasNext()) {
            String factorCode = (String)var6.next();
            if (this.updateTableFieldTask.isFieldExist(factorCode,  FactorType.ELECTRIC.TYPE())) {
                DataFactorBean bean = (DataFactorBean)map.get(factorCode);
                sql_field.append("," + factorCode + "_INFO");
                sql_value.append(","+bean.getRtd());
            }
        }

        sql_field.append(sql_value).append(')');
        this.baseDao.sqlExcute(sql_field.toString());
    }
}
