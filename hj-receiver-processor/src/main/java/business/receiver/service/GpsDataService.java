package business.receiver.service;

import business.receiver.bean.GpsBean;
import business.receiver.threadPool.ThreadPoolService;
import business.util.CommonsUtil;
import business.util.GpsConvert;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("gpsDataService")
@Slf4j
public class GpsDataService {
    @Autowired
    private IBaseDao baseDao;
    @Autowired
    private BlackListService blackListService;
    @Autowired
    private ThreadPoolService threadPoolService;
    private static Map<String, GpsBean> gps_tmp = new HashMap();

    public GpsDataService() {
    }

    public void accept(final String msg) {
        this.threadPoolService.getReceivePool().execute(new Runnable() {
            public void run() {
                if (GpsDataService.this.checkData(msg)) {
                    GpsDataService.this.excute(msg);
                }

            }
        });
    }

    private boolean checkData(String content) {
        boolean available = false;
        if (StringUtils.isNotEmpty(content)) {
            String[] data = content.split(",");
            if (data.length == 14) {
                String status = data[3];
                if (StringUtils.isNotEmpty(status) && status.equals("A")) {
                    if (this.validate(content)) {
                        available = true;
                    } else {
                        log.error("GPS报文校验错误：" + content);
                    }
                } else {
                    log.error("GPS定位无效：" + content);
                }
            } else {
                log.error("GPS报文格式错误：" + content);
            }
        }

        return available;
    }

    private boolean validate(String content) {
        if (StringUtils.isNotEmpty(content)) {
            content = content.toUpperCase();
            String data = content.substring(content.indexOf(",$") + 1, content.indexOf("*") + 1);
            String check = content.substring(content.indexOf("*") + 1);
            if (StringUtils.isNotEmpty(data) && StringUtils.isNotEmpty(check)) {
                char[] a = data.toCharArray();
                int result = a[1];

                for(int i = 2; i < a.length; ++i) {
                    if (a[i] != '*') {
                        result ^= a[i];
                    }
                }

                if (Integer.toHexString(result).toUpperCase().equals(check.toUpperCase())) {
                    return true;
                }
            }
        }

        return false;
    }

    private void excute(String content) {
        String[] data = content.split(",");
        String key = data[0].substring(1);
        if (this.blackListService.isReceive(key)) {
            Date dataTime = this.convertTime(data[10] + data[2]);
            double lat = GpsConvert.GPStoDegree(data[4]);
            if (data[5].equals("S")) {
                lat = -lat;
            }

            double lng = GpsConvert.GPStoDegree(data[6]);
            if (data[7].equals("W")) {
                lng = -lng;
            }

            double[] marsPoints = GpsConvert.GPStoMars(lat, lng);
            double[] baiduPoints = GpsConvert.MarsToBaidu(marsPoints[0], marsPoints[1]);
            double speed = 0.0D;

            try {
                speed = Double.valueOf(data[8]);
                speed *= 1.85D;
                BigDecimal bg = new BigDecimal(speed);
                speed = bg.setScale(2, 4).doubleValue();
            } catch (Exception var26) {
                var26.printStackTrace();
            }

            double direction = 0.0D;

            try {
                direction = Double.valueOf(data[9]);
            } catch (Exception var25) {
                var25.printStackTrace();
            }

            double baidu_lng = GpsConvert.convertNumber(baiduPoints[1], 6);
            double baidu_lat = GpsConvert.convertNumber(baiduPoints[0], 6);
            String sql = null;
            List<Object> params = null;
            GpsBean gpsBean = (GpsBean)gps_tmp.get(key);
            if (gpsBean != null && !GpsConvert.isMove(gpsBean.getLng(), gpsBean.getLat(), baidu_lng, baidu_lat)) {
                gpsBean.setRec_times(gpsBean.getRec_times() + 1L);
                sql = "update hh_gps.gps_data set END_TIME=?,REC_TIMES=? where ID=? ";
                params = new ArrayList();
                params.add(dataTime);
                params.add(gpsBean.getRec_times());
                params.add(gpsBean.getId());
            } else {
                double distance = 0.0D;
                if (gpsBean != null) {
                    distance = GpsConvert.convertNumber(GpsConvert.getDistance(gpsBean.getLng(), gpsBean.getLat(), baidu_lng, baidu_lat), 2);
                }

                gpsBean = new GpsBean();
                String id = CommonsUtil.createUUID1();
                gpsBean.setId(id);
                gpsBean.setLat(baidu_lat);
                gpsBean.setLng(baidu_lng);
                gpsBean.setRec_times(1L);
                gps_tmp.put(key, gpsBean);
                sql = "insert into hh_gps.gps_data(ID,NO,LNG,LAT,SPEED,DIRECTION,BEGIN_TIME,END_TIME,REC_TIMES,DISTANCE)values(?,?,?,?,?,?,?,?,?,?)";
                params = new ArrayList();
                params.add(id);
                params.add(key);
                params.add(baidu_lng);
                params.add(baidu_lat);
                params.add(speed);
                params.add(direction);
                params.add(dataTime);
                params.add(dataTime);
                params.add(1);
                params.add(distance);
            }

            this.baseDao.sqlExcute(sql, params);
        }
    }

    private Date convertTime(String date) {
        SimpleDateFormat sf = new SimpleDateFormat("ddMMyyHHmmss.SSS");
        sf.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date date_ = sf.parse(date);
            return date_;
        } catch (ParseException var4) {
            var4.printStackTrace();
            return null;
        }
    }
}