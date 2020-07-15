package business.processor.service;

import business.constant.ReverseCmdConstant;
import business.processor.bean.BakReverseLog;
import business.processor.bean.DataPacketBean;
import business.receiver.bean.MonitorBean;
import business.receiver.mapper.MyBaseMapper;
import business.receiver.service.ReverseService;
import business.util.CommonsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("reverseControlService")
@Slf4j
public class ReverseControlService {
    @Autowired
    private MonitorService monitorService;
    @Autowired
    private MyBaseMapper myBaseMapper;
    @Autowired
    private ReverseService reverseService;
    public static String lock = "lock";

    public ReverseControlService() {
    }

    public void addendumHourData(DataPacketBean packet, MonitorBean monitor) {
        if (monitor.getMonitorStatus() == 1) {
            Date lastTime = this.monitorService.getMnHourLastUpload(packet.getMn());
            if (lastTime != null) {
                Date dataTime = packet.getDataTime();
                long h = dataTime.getTime() - lastTime.getTime();
                if (h > 3600000L) {
                    Date beginTime = CommonsUtil.hour(lastTime, 1);
                    this.sendReverseGetDataRequest(packet, beginTime, dataTime);
                }

            }
        }
    }

    public void addendumDayData(DataPacketBean packet, MonitorBean monitor) {
        if (monitor.getMonitorStatus() == 1) {
            Date lastTime = this.monitorService.getMnDayLastUpload(packet.getMn());
            if (lastTime != null) {
                Date dataTime = packet.getDataTime();
                long h = dataTime.getTime() - lastTime.getTime();
                if (h > 86400000L) {
                    Date beginTtime = CommonsUtil.day(lastTime, 1);
                    this.sendReverseGetDataRequest(packet, beginTtime, dataTime);
                }

            }
        }
    }

    private void sendReverseGetDataRequest(DataPacketBean packet, Date begin, Date end) {
        String qn = "";
        synchronized(lock) {
            try {
                Thread.sleep(1L);
            } catch (InterruptedException var9) {
                var9.printStackTrace();
            }

            qn = CommonsUtil.dateCurrent("yyyyMMddHHmmssSSS");
        }

        String beginTime = CommonsUtil.dateFormat(begin, "yyyyMMddHHmmss");
        String endTime = CommonsUtil.dateFormat(end, "yyyyMMddHHmmss");
        String cmd = ReverseCmdConstant.CMD_GET_DATA.format(new Object[]{qn, packet.getCn(), packet.getMn(), beginTime, endTime});
        BakReverseLog v = new BakReverseLog();
        v.setQn(qn);
        v.setMn(packet.getMn());
        if ("2061".equals(packet.getCn())) {
            v.setType(6);
            v.setNote("平台自动补遗（小时数据）" + beginTime + "~" + endTime);
        } else if ("2031".equals(packet.getCn())) {
            v.setType(7);
            v.setNote("平台自动补遗（日数据）" + beginTime + "~" + endTime);
        }

        v.setCmd(cmd);
        v.setTag(0);
        v.setOperatorID("system");
        v.setOperatorName("admin");
        v.setCreateTime(new Date());
        //TODO 实体类插入
        //this.baseDao.insert(v);
        this.reverseService.receiveCmdAndSendNow(qn, packet.getMn(), cmd);
    }

    public void sendReverseCloseValve(String mn, String message) {
        String qn = "";
        synchronized(lock) {
            try {
                Thread.sleep(1L);
            } catch (InterruptedException var7) {
                var7.printStackTrace();
            }

            qn = CommonsUtil.dateCurrent("yyyyMMddHHmmssSSS");
        }

        String cmd = ReverseCmdConstant.CLOSE_VALVE.format(new Object[]{qn, mn});
        BakReverseLog v = new BakReverseLog();
        v.setCmd(cmd);
        v.setCreateTime(new Date());
        v.setMn(mn);
        v.setNote("平台自动关阀，" + message);
        v.setOperatorID("system");
        v.setOperatorName("admin");
        v.setQn(qn);
        v.setTag(0);
        v.setType(2);
        //TODO 实体类插入
        //this.baseDao.insert(v);
        this.reverseService.receiveCmdAndSendNow(qn, mn, cmd);
    }

    public void sendReverseSample(String mn, String message) {
        String qn = "";
        synchronized(lock) {
            try {
                Thread.sleep(1L);
            } catch (InterruptedException var7) {
                var7.printStackTrace();
            }

            qn = CommonsUtil.dateCurrent("yyyyMMddHHmmssSSS");
        }

        String cmd = ReverseCmdConstant.CMD_SAMPLE_HH.format(new Object[]{qn, mn});
        BakReverseLog v = new BakReverseLog();
        v.setCmd(cmd);
        v.setCreateTime(new Date());
        v.setMn(mn);
        v.setNote("平台自动超标留样，" + message);
        v.setOperatorID("system");
        v.setOperatorName("admin");
        v.setQn(qn);
        v.setTag(0);
        v.setType(3);
        //TODO 实体类插入
        //this.baseDao.insert(v);
        this.reverseService.receiveCmdAndSendNow(qn, mn, cmd);
    }
}