package business.service;

import business.cache.DataCache;
import business.receiver.entity.AnalogData;
import business.message.*;
import business.netty.client.NettyClient;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.util.*;

public class AnalogDataSendService {
    private static Thread thread = null;
    private static boolean stop = true;

    public static void send(AnalogData v) {
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
