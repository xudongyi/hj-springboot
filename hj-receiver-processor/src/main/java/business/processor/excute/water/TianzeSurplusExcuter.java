package business.processor.excute.water;

import business.ienum.FactorType;
import business.processor.bean.DataFactorBean;
import business.processor.bean.DataPacketBean;
import business.processor.bean.WarnRuleBean;
import business.processor.excute.DataParserService;
import business.processor.service.MonitorDeviceService;
import business.processor.service.MonitorService;
import business.processor.service.WarnService;
import business.receiver.bean.MonitorBean;
import business.receiver.mapper.MyBaseMapper;
import business.util.CommonsUtil;
import business.util.SqlBuilder;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("tianzeSurplusExcuter")
@Slf4j
public class TianzeSurplusExcuter {
    @Autowired
    private MonitorService monitorService;
    @Autowired
    private MonitorDeviceService monitorDeviceService;
    @Autowired
    private WarnService warnService;
    @Autowired
    private DataParserService dataParserService;
    @Autowired
    private MyBaseMapper myBaseMapper;

    public TianzeSurplusExcuter() {
    }

    public int execute(DataPacketBean dataPacketBean) {
        this.dataParserService.format(dataPacketBean, FactorType.WATER.TYPE());
        Map<String, DataFactorBean> dataMap = dataPacketBean.getDataMap();
        String mn = dataPacketBean.getMn();
        MonitorBean monitor = (MonitorBean)this.monitorService.getAllMonitors().get(mn);
        DataFactorBean bean = (DataFactorBean)dataMap.get("W00000");
        if (bean != null) {
            if (monitor != null && bean.getSurplus() != null) {
                double balance = bean.getSurplus();
                this.saveStatistic(mn, bean.getDataTime(), balance);
                List<WarnRuleBean> surplusWarnRuleList = this.warnService.getSurplusWarnRule(mn);
                this.warnService.checkSurplus(surplusWarnRuleList, monitor, balance, bean.getDataTime());
                this.saveSurplusReids(mn, balance);
            }

            this.saveCurrentSingle(mn, bean);
        }

        return 2;
    }

    private void saveStatistic(String mn, Date dataTime, double balance) {
        String code = "W00000_SURPLUS";
        List<Object> params = new ArrayList();
        params.add(mn);
        params.add(code);
        params.add(CommonsUtil.dateFormat(dataTime, "yyyyMMdd"));
        List<Map<String, Object>> list = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql("select * from TOTAL_STATISTIC where MN=''{0}'' and CODE=''{1}'' and DATA_TIME=''{2}''", params));
        if (list == null | list.size() == 0) {
            params = new ArrayList();
            params.add(CommonsUtil.createUUID1());
            params.add(CommonsUtil.dateFormat(dataTime, "yyyyMMdd"));
            params.add(mn);
            params.add(code);
            params.add(balance);
            this.myBaseMapper.sqlExcute(SqlBuilder.buildSql("insert into TOTAL_STATISTIC(ID,DATA_TIME,MN,CODE,VALUE) values (''{0}'',''{1}'',''{2}'',''{3}'',''{4}'')", params));
        }

    }

    private void saveSurplusReids(String mn, double balance) {
        String factorCode = "W00000";
        DataFactorBean lastData = this.monitorDeviceService.getCurrentData(mn, factorCode);
        if (lastData != null) {
            lastData.setSurplus(balance);
            this.monitorDeviceService.setCurrentData(mn, factorCode, lastData);
        }

    }

    private void saveCurrentSingle(String mn, DataFactorBean bean) {
        Date dataTime = bean.getDataTime();
        Date dataTimeBegin = CommonsUtil.hour(-1);
        List<Object> params = new ArrayList();
        params.add(mn);
        params.add(DateUtil.formatDateTime(dataTimeBegin));
        params.add(DateUtil.formatDateTime(dataTime));
        List<Map<String, Object>> list = this.myBaseMapper.sqlQuery(SqlBuilder.buildSql("select * from WATER_CURRENT_TR_" + CommonsUtil.dateFormat(dataTime, "yyMM") + " where MN=''{0}'' and DATA_TIME>=''{1}'' and DATA_TIME<=''{2}'' order by DATA_TIME desc", params));
        if (list != null && list.size() > 0) {
            Map<String, Object> lastData = (Map)list.get(0);
            String lastId = (String)lastData.get("ID");
            params = new ArrayList();
            params.add(bean.getSurplus());
            params.add(lastId);
            this.myBaseMapper.sqlExcute(SqlBuilder.buildSql("update WATER_CURRENT_TR_" + CommonsUtil.dateFormat(dataTime, "yyMM") + " set W00000_SURPLUS=''{0}'' where ID=''{1}''", params));
        }

    }
}