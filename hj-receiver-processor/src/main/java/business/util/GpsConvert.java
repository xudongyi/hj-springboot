package business.util;

import java.math.BigDecimal;

public class GpsConvert {
    public GpsConvert() {
    }

    public static double GPStoDegree(String data) {
        try {
            int point = data.indexOf(".");
            String degree = data.substring(0, point - 2);
            String scope = data.substring(point - 2);
            double result = Double.valueOf(degree) + convertNumber(Double.valueOf(scope) / 60.0D, 6);
            return result;
        } catch (Exception var6) {
            var6.printStackTrace();
            return 0.0D;
        }
    }

    public static double[] GPStoMars(double lat, double lng) {
        double a = 6378245.0D;
        double ee = 0.006693421622965943D;
        double dLat = transformLat(lng - 105.0D, lat - 35.0D);
        double dLng = transformLng(lng - 105.0D, lat - 35.0D);
        double radLat = lat / 180.0D * 3.141592653589793D;
        double magic = Math.sin(radLat);
        magic = 1.0D - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = dLat * 180.0D / (a * (1.0D - ee) / (magic * sqrtMagic) * 3.141592653589793D);
        dLng = dLng * 180.0D / (a / sqrtMagic * Math.cos(radLat) * 3.141592653589793D);
        double mgLat = lat + dLat;
        double mgLng = lng + dLng;
        return new double[]{mgLat, mgLng};
    }

    public static double[] MarsToBaidu(double gg_lat, double gg_lon) {
        double x_pi = 52.35987755982988D;
        double z = Math.sqrt(gg_lon * gg_lon + gg_lat * gg_lat) + 2.0E-5D * Math.sin(gg_lat * x_pi);
        double theta = Math.atan2(gg_lat, gg_lon) + 3.0E-6D * Math.cos(gg_lon * x_pi);
        double bd_lon = z * Math.cos(theta) + 0.0065D;
        double bd_lat = z * Math.sin(theta) + 0.006D;
        return new double[]{bd_lat, bd_lon};
    }

    public static double transformLat(double x, double y) {
        double ret = -100.0D + 2.0D * x + 3.0D * y + 0.2D * y * y + 0.1D * x * y + 0.2D * Math.sqrt(Math.abs(x));
        ret += (20.0D * Math.sin(6.0D * x * 3.141592653589793D) + 20.0D * Math.sin(2.0D * x * 3.141592653589793D)) * 2.0D / 3.0D;
        ret += (20.0D * Math.sin(y * 3.141592653589793D) + 40.0D * Math.sin(y / 3.0D * 3.141592653589793D)) * 2.0D / 3.0D;
        ret += (160.0D * Math.sin(y / 12.0D * 3.141592653589793D) + 320.0D * Math.sin(y * 3.141592653589793D / 30.0D)) * 2.0D / 3.0D;
        return ret;
    }

    public static double transformLng(double x, double y) {
        double ret = 300.0D + x + 2.0D * y + 0.1D * x * x + 0.1D * x * y + 0.1D * Math.sqrt(Math.abs(x));
        ret += (20.0D * Math.sin(6.0D * x * 3.141592653589793D) + 20.0D * Math.sin(2.0D * x * 3.141592653589793D)) * 2.0D / 3.0D;
        ret += (20.0D * Math.sin(x * 3.141592653589793D) + 40.0D * Math.sin(x / 3.0D * 3.141592653589793D)) * 2.0D / 3.0D;
        ret += (150.0D * Math.sin(x / 12.0D * 3.141592653589793D) + 300.0D * Math.sin(x / 30.0D * 3.141592653589793D)) * 2.0D / 3.0D;
        return ret;
    }

    public static double convertNumber(double number, int scale) {
        BigDecimal formatNumber = new BigDecimal(number);
        double result = formatNumber.setScale(scale, 4).doubleValue();
        return result;
    }

    public static double getDistance(double lng1, double lat1, double lng2, double lat2) {
        lng1 = ew(lng1, -180.0D, 180.0D);
        lat1 = lw(lat1, -74.0D, 74.0D);
        lng2 = ew(lng2, -180.0D, 180.0D);
        lat2 = lw(lat2, -74.0D, 74.0D);
        return 6370996.81D * Math.acos(Math.sin(oi(lat1)) * Math.sin(oi(lat2)) + Math.cos(oi(lat1)) * Math.cos(oi(lat2)) * Math.cos(oi(lng2) - oi(lng1)));
    }

    public static double ew(double a, double b, double c) {
        if (a > c) {
            a -= c - b;
        }

        if (a < b) {
            a += c - b;
        }

        return a;
    }

    public static double lw(double a, double b, double c) {
        if (a <= b) {
            a = b;
        }

        if (a > c) {
            a = c;
        }

        return a;
    }

    public static double oi(double a) {
        return 3.141592653589793D * a / 180.0D;
    }

    public static boolean isMove(double beforeLng, double beforeLat, double afterLng, double afterLat) {
        double moveLength = getDistance(beforeLng, beforeLat, afterLng, afterLat);
        return moveLength > 10.0D;
    }
}