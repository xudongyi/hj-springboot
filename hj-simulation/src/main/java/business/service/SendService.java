package business.service;

import business.cache.DataCache;
import business.netty.client.NettyClient;
import business.util.CRC_16;
import business.util.DataFormat;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("sendService")
public class SendService {
    /**
     * 发送一次
     * @param id
     * @param contents
     * @param mnList
     */
    public void sendOnce(String id, String contents, String mnList) {
        NettyClient nc = DataCache.CLIENT_SOCKET_CACHE.get(id);
        if (nc != null && StringUtils.isNotEmpty(contents)) {
            String[] content_list = contents.split("\r\n");

            for (int i = 0; i < content_list.length; ++i) {
                String content = content_list[i];
                int minuteAgo = 0;
                if (content.indexOf("CN=2011;") != -1) {
                    minuteAgo = 1;
                } else if (content.indexOf("CN=2051;") != -1) {
                    minuteAgo = 10;
                } else if (content.indexOf("CN=2061;") != -1) {
                    minuteAgo = 60;
                } else if (content.indexOf("CN=2031;") != -1) {
                    minuteAgo = 1440;
                } else if (content.indexOf("CN=2811;") != -1) {
                    minuteAgo = 10;
                } else if (content.indexOf("CN=2861;") != -1) {
                    minuteAgo = 60;
                } else if (content.indexOf("CN=2831;") != -1) {
                    minuteAgo = 1440;
                }

                if (StringUtils.isNotEmpty(mnList) && content.indexOf("${MN}") != -1) {
                    String[] mns = mnList.split(",");
                    String[] var13 = mns;
                    int var12 = mns.length;

                    for (int var11 = 0; var11 < var12; ++var11) {
                        String mn = var13[var11];
                        nc.send(convertContent(content, mn, minuteAgo));
                    }
                } else {
                    nc.send(convertContent(content, null, minuteAgo));
                }
            }
        }

    }

    /**
     * 定时发送数据
     *
     * @param id
     * @param contents
     * @param mnList
     * @param interval
     */
    public void sendInterval(final String id, String contents, String mnList, final int interval) {
        if (!StringUtils.isEmpty(contents)) {
            final List<String> list = new ArrayList();
            String[] content_list = contents.split("\r\n");

            for (int i = 0; i < content_list.length; ++i) {
                String content = content_list[i];
                if (StringUtils.isNotEmpty(mnList) && content.indexOf("${MN}") != -1) {
                    String[] mns = mnList.split(",");
                    for (String mn : mns) {
                        String content_ = content.replaceAll("\\$\\{MN\\}", mn);
                        list.add(content_);
                    }
                } else {
                    list.add(content);
                }
            }

            Thread thread = new Thread(() -> {
                while (true) {
                    NettyClient nc = DataCache.CLIENT_SOCKET_CACHE.get(id);
                    if (nc == null) {
                        return;
                    }
                    Date now = new Date();
                    int second = now.getSeconds();
                    if (second == 0) {
                        int minute = now.getMinutes();
                        if (interval > 0 && interval < 60) {
                            if (minute % interval == 0) {
                                for (String contentx : list) {
                                    nc.send(SendService.convertContent(contentx, null, interval));
                                }
                            }
                        } else if (interval >= 60) {
                            int hour = now.getHours();
                            if (minute == 0 && hour % (interval / 60) == 0) {
                                for (String contentx : list) {
                                    nc.send(SendService.convertContent(contentx, null, interval));
                                }
                            }
                        }
                    }

                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }

    public static String convertContent(String content, String mn, int minuteAgo) {
        if (StringUtils.isEmpty(content)) {
            return "";
        } else {
            StringBuffer sb = new StringBuffer();

            try {
                content = content.replaceAll("\\$\\{QN\\}", DataFormat.DateFormat_QN());
                content = content.replaceAll("\\$\\{DataTime\\}", DataFormat.DateFormat_DT(minuteAgo));
                content = content.replaceAll("\\$\\{SampleTime\\}", DataFormat.DateFormat_ST());
                if (mn != null) {
                    content = content.replaceAll("\\$\\{MN\\}", mn);
                }

                Pattern p = Pattern.compile("\\$\\{N.\\d+\\(\\d+\\-\\d+\\)\\}");

                Matcher m;
                String randnum_convert;
                for (m = p.matcher(content); m.find(); m.appendReplacement(sb, randnum_convert)) {
                    String randnum = m.group();
                    int scale_index = randnum.indexOf(".");
                    String scale = randnum.substring(scale_index + 1, scale_index + 2);
                    String min = randnum.substring(randnum.indexOf("(") + 1, randnum.indexOf("-"));
                    String max = randnum.substring(randnum.indexOf("-") + 1, randnum.indexOf(")"));
                    if (Integer.valueOf(scale) == 0) {
                        randnum_convert = String.valueOf(DataFormat.randomInt(Integer.valueOf(min), Integer.valueOf(max)));
                    } else {
                        randnum_convert = String.valueOf(DataFormat.randomDouble(Integer.valueOf(min), Integer.valueOf(max), Integer.valueOf(scale)));
                    }
                }

                m.appendTail(sb);
            } catch (Exception var12) {
                var12.printStackTrace();
            }

            String result = sb.toString();
            if (StringUtils.isNotEmpty(result)) {
                if (result.endsWith("&&")) {
                    result = "##" + DataFormat.int2str(result.length(), 4) + result + CRC_16.CRC16(result) + "\r\n";
                } else {
                    result = result + "\r\n";
                }
            }

            return result;
        }
    }
}