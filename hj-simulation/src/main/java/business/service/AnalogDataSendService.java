package business.service;

import business.cache.DataCache;
import business.mapper.MonitorMapper;
import business.receiver.entity.AnalogData;
import business.message.*;
import business.netty.client.NettyClient;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@Slf4j
public class AnalogDataSendService {
    private static Thread thread = null;
    private static boolean stop = true;
    @Autowired
    private MonitorMapper monitorMapper;
    @Value("${hj.useDb}")
    private boolean useDb = false;
    @Value("${hj.waterMN}")
    private String waterMN;
    @Value("${hj.airMN}")
    private String airMN;
    @Value("${hj.airqMN}")
    private String airqMN;
    @Value("${hj.surfwaterMN}")
    private String surfwaterMN;
    @Value("${hj.noiseMN}")
    private String noiseMN;
    @Value("${hj.vocMN}")
    private String vocMN;
    @Value("${hj.ipPort}")
    private String ipPort="127.0.0.1:6000";


    @PostConstruct
    public void initSend() {
        AnalogData data = new AnalogData();
        if (useDb) {
            List<Map<String, Object>> monitors = monitorMapper.getAllMonitor();
            List<String> waterMNList = new ArrayList<>();
            List<String> airMNList = new ArrayList<>();
            List<String> airqMNList = new ArrayList<>();
            List<String> surfwaterMNList = new ArrayList<>();
            List<String> noiseMNList = new ArrayList<>();
            List<String> vocMNList = new ArrayList<>();
            //废水，废气，VOCs，空气质量，地表水，土壤，地下水，放射源，噪声，电气
            for (Map<String, Object> m : monitors) {
                String mn = (String) m.get("mn");
                switch (m.get("type").toString()) {
                    case "0"://废水
                        waterMNList.add(mn);
                        break;
                    case "1"://废气
                        airMNList.add(mn);
                        break;
                    case "2"://VOCs
                        vocMNList.add(mn);
                        break;
                    case "3"://空气质量
                        airqMNList.add(mn);
                        break;
                    case "4"://地表水
                        surfwaterMNList.add(mn);
                        break;
                    case "5"://土壤
                        break;
                    case "6"://地下水
                        break;
                    case "7"://放射源
                        break;
                    case "8"://噪声
                        noiseMNList.add(mn);
                        break;
                    case "9"://电气
                        break;
                }
            }
            data.setWaterMN(String.join(",", waterMNList));
            data.setIsWaterSend(String.valueOf(waterMNList.size() > 0));

            data.setAirMN(String.join(",", airMNList));
            data.setIsAirSend(String.valueOf(airMNList.size() > 0));

            data.setAirqMN(String.join(",", airqMNList));
            data.setIsAirqSend(String.valueOf(airqMNList.size() > 0));

            data.setSurfwaterMN(String.join(",", surfwaterMNList));
            data.setIsSurfwaterSend(String.valueOf(surfwaterMNList.size() > 0));

            data.setNoiseMN(String.join(",", noiseMNList));
            data.setIsNoiseSend(String.valueOf(noiseMNList.size() > 0));

            data.setVocMN(String.join(",", vocMNList));
            data.setIsVocSend(String.valueOf(vocMNList.size() > 0));
        } else {
            data.setWaterMN(waterMN);
            data.setIsWaterSend(String.valueOf(waterMN.split(",").length > 0));

            data.setAirMN(airMN);
            data.setIsAirSend(String.valueOf(airMN.split(",").length > 0));

            data.setAirqMN(airqMN);
            data.setIsAirqSend(String.valueOf(airqMN.split(",").length > 0));

            data.setSurfwaterMN(surfwaterMN);
            data.setIsSurfwaterSend(String.valueOf(surfwaterMN.split(",").length > 0));

            data.setNoiseMN(noiseMN);
            data.setIsNoiseSend(String.valueOf(noiseMN.split(",").length > 0));

            data.setVocMN(vocMN);
            data.setIsVocSend(String.valueOf(vocMN.split(",").length > 0));
        }
        data.setIpPort(ipPort);
        if (AnalogDataSendService.isStop()) {
            AnalogDataSendService.send(data);
        }

    }


    public static void send(AnalogData v) {
        log.info("模拟程序数据参数：" + JSON.toJSONString(v));
        String ipPorts = v.getIpPort();
        if (StringUtils.isNotEmpty(ipPorts)) {
            stop = false;
            final String[] ipPort = ipPorts.split(",");
            final Map<String, BaseMessage> analogDataBean = createAnalogDataBean(v);
            if (analogDataBean.isEmpty()) {
                return;
            }

            thread = new Thread(() -> {
                while (!AnalogDataSendService.stop) {
                    Date now = new Date();
                    int second = now.getSeconds();
                    if (second == 0) {
                        for (int i = 0; i < ipPort.length; ++i) {
                            NettyClient nc = DataCache.getAnalogDataClient(ipPort[i]);
                            if (nc != null) {
                                int minute = now.getMinutes();
                                int hour = now.getHours();
                                Set<String> mns = analogDataBean.keySet();
                                BaseMessage message;
                                String cmd;
                                for (String mn : mns) {
                                    message = analogDataBean.get(mn);
                                    cmd = message.current();
                                    if (StringUtils.isNotEmpty(cmd)) {
                                        nc.send(SendService.convertContent(cmd, mn, 1));
                                    }
                                }
                                if (minute % 10 == 0) {
                                    mns = analogDataBean.keySet();
                                    for (String mn : mns) {
                                        message = analogDataBean.get(mn);
                                        cmd = message.minute();
                                        if (StringUtils.isNotEmpty(cmd)) {
                                            nc.send(SendService.convertContent(cmd, mn, 10));
                                        }
                                    }
                                }

                                if (minute == 0) {
                                    mns = analogDataBean.keySet();

                                    for (String mn : mns) {
                                        message = analogDataBean.get(mn);
                                        cmd = message.hour();
                                        if (StringUtils.isNotEmpty(cmd)) {
                                            nc.send(SendService.convertContent(cmd, mn, 60));
                                        }
                                    }
                                }

                                if (minute == 0 && hour == 0) {
                                    mns = analogDataBean.keySet();

                                    for (String mn : mns) {
                                        message = analogDataBean.get(mn);
                                        cmd = message.day();
                                        if (StringUtils.isNotEmpty(cmd)) {
                                            nc.send(SendService.convertContent(cmd, mn, 1440));
                                        }
                                    }
                                }
                            }
                        }
                    }

                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException var11) {
                        var11.printStackTrace();
                    }
                }

            });
            thread.start();
        }

    }

    private static Map<String, BaseMessage> createAnalogDataBean(AnalogData v) {
        Map<String, BaseMessage> result = new HashMap();
        String mn;
        int var3;
        int var4;
        String[] var5;
        if ("true".equals(v.getIsWaterSend()) && StringUtils.isNotEmpty(v.getWaterMN())) {
            var4 = (var5 = v.getWaterMN().split(",")).length;

            for (var3 = 0; var3 < var4; ++var3) {
                mn = var5[var3];
                result.put(mn, new WaterMessage());
            }
        }

        if ("true".equals(v.getIsAirSend()) && StringUtils.isNotEmpty(v.getAirMN())) {
            var4 = (var5 = v.getAirMN().split(",")).length;

            for (var3 = 0; var3 < var4; ++var3) {
                mn = var5[var3];
                result.put(mn, new AirMessage());
            }
        }

        if ("true".equals(v.getIsAirqSend()) && StringUtils.isNotEmpty(v.getAirqMN())) {
            var4 = (var5 = v.getAirqMN().split(",")).length;

            for (var3 = 0; var3 < var4; ++var3) {
                mn = var5[var3];
                result.put(mn, new AirQMessage());
            }
        }

        if ("true".equals(v.getIsSurfwaterSend()) && StringUtils.isNotEmpty(v.getSurfwaterMN())) {
            var4 = (var5 = v.getSurfwaterMN().split(",")).length;

            for (var3 = 0; var3 < var4; ++var3) {
                mn = var5[var3];
                result.put(mn, new SurfWaterMessage());
            }
        }

        if ("true".equals(v.getIsNoiseSend()) && StringUtils.isNotEmpty(v.getNoiseMN())) {
            var4 = (var5 = v.getNoiseMN().split(",")).length;

            for (var3 = 0; var3 < var4; ++var3) {
                mn = var5[var3];
                result.put(mn, new NoiseMessage());
            }
        }

        if ("true".equals(v.getIsVocSend()) && StringUtils.isNotEmpty(v.getVocMN())) {
            var4 = (var5 = v.getVocMN().split(",")).length;

            for (var3 = 0; var3 < var4; ++var3) {
                mn = var5[var3];
                result.put(mn, new VOCMessage());
            }
        }

        return result;
    }

    public static Thread getThread() {
        return thread;
    }

    public static boolean isStop() {
        return stop;
    }

    public static void stop() {
        stop = true;

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException var1) {
            var1.printStackTrace();
        }

        DataCache.clearAnalogDataClient();
    }

    public static boolean isAlive() {
        if (thread == null) {
            return false;
        } else {
            boolean isAlive = thread.isAlive();
            return isAlive;
        }
    }
}
