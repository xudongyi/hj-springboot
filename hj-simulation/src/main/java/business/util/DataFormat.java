package business.util;

import cn.hutool.core.date.DateUtil;

import java.util.*;

public class DataFormat {
    /**
     * QN 获取
     * @return
     */
    public static String DateFormat_QN() {
        return DateUtil.format(new Date(),"yyyyMMddHHmmssSSS");
    }

    /**
     * DT 获取
     * @param minuteAgo
     * @return
     */
    public static String DateFormat_DT(int minuteAgo) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(new Date());
        cl.set(12, cl.get(12) - minuteAgo);
        return DateUtil.format(cl.getTime(),"yyyyMMddHHmm00");

    }

    /**
     * ST 获取
     * @return
     */
    public static String DateFormat_ST() {
        return DateUtil.format(new Date(),"yyyyMMddHH0000");
    }

    /**
     * 在最小值和最大值之间找到一个整数
     * @param min
     * @param max
     * @return
     */
    public static int randomInt(int min, int max) {
        Random random = new Random();
        int s = random.nextInt(max - min + 1) + min;
        return s;
    }
    /**
     * 在最小值和最大值之间找到双精度小数
     * @param min
     * @param max
     * @param scale 小数位
     * @return
     */
    public static double randomDouble(int min, int max, int scale) {
        return CommonsUtil.numberFormat(Math.random() * (double)(max - min) + (double)min, scale);
    }

    /**
     * int转string（可以根据length的长度前面自动补充0）
     * @param n
     * @param length
     * @return
     */
    public static String int2str(int n, int length) {
        String str = "";

        for(int i = 0; i < length; ++i) {
            str = str + "0";
        }

        String str_m = String.valueOf(n);
        str_m = str.subSequence(0, length - str_m.length()) + str_m;
        return str_m;
    }

    public static int randomDeviceState() {
        int result = 0;
        if (Math.random() > 0.999999D) {
            result = randomInt(1, 15);
        }

        return result;
    }

    public static String randomFlag() {
        if (Math.random() <= 0.999999D) {
            return "N";
        } else {
            double t = Math.random();
            if (t <= 0.1D) {
                return "F";
            } else if (t > 0.1D & t <= 0.2D) {
                return "M";
            } else if (t > 0.2D & t <= 0.3D) {
                return "S";
            } else if (t > 0.3D & t <= 0.7D) {
                return "D";
            } else if (t > 0.7D & t <= 0.8D) {
                return "C";
            } else if (t > 0.8D & t <= 0.9D) {
                return "T";
            } else {
                return t > 0.9D & t <= 1.0D ? "B" : "N";
            }
        }
    }

    public static double randomData(int overMin, int overMax, int scale) {
        double result = 0.0D;
        if (Math.random() <= 0.995D) {
            result = randomDouble(overMin, overMax, scale);
        } else if (Math.random() < 0.001D) {
            if (Math.random() > 0.5D) {
                result = -1.0D;
            } else if (Math.random() < 0.3D) {
                result = 9999999.0D;
            } else {
                result = -9999999.0D;
            }
        } else if (Math.random() > 0.5D) {
            result = randomDouble(0, overMin, scale);
        } else {
            result = randomDouble(overMax, overMax * 2, scale);
        }

        return result;
    }

    /**
     * 获取一组数据的平均值
     * @param data
     * @param scale
     * @return
     */
    public static double avg(List<Double> data, int scale) {
        if (data != null && data.size() != 0) {
            double count = 0.0D;

            for(int i = 0; i < data.size(); ++i) {
                count += (Double)data.get(i);
            }

            double avg = 0.0D;
            if (count > 0.0D) {
                avg = CommonsUtil.numberFormat(count / (double)data.size(), scale);
            }

            return avg;
        } else {
            return 0.0D;
        }
    }

    /**
     * 获取一组数据的最大值
     * @param data
     * @return
     */
    public static double max(List<Double> data) {
        if (data != null && data.size() != 0) {
            double max = data.get(0);

            for(int i = 0; i < data.size(); ++i) {
                if (data.get(i) > max) {
                    max = data.get(i);
                }
            }

            return max;
        } else {
            return 0.0D;
        }
    }

    /**
     * 获取一组数据的最小值
     * @param data
     * @return
     */
    public static double min(List<Double> data) {
        if (data != null && data.size() != 0) {
            double min = (Double)data.get(0);

            for(int i = 0; i < data.size(); ++i) {
                if ((Double)data.get(i) < min) {
                    min = (Double)data.get(i);
                }
            }

            return min;
        } else {
            return 0.0D;
        }
    }

    /**
     * 获取一组数据的总量
     * @param data
     * @param scale
     * @return
     */
    public static double cou(List<Double> data, int scale) {
        if (data != null && data.size() != 0) {
            double cou = 0.0D;

            for(int i = 0; i < data.size(); ++i) {
                cou += (Double)data.get(i);
            }

            cou = CommonsUtil.numberFormat(cou, scale);
            return cou;
        } else {
            return 0.0D;
        }
    }

    public static double percentile(List<Double> list, double percent, int scale) {
        double result = 0.0D;
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<Double>() {
                public int compare(Double o1, Double o2) {
                    return o1.compareTo(o2);
                }
            });
            double i = (double)list.size() * percent / 100.0D;
            int index;
            if (i % 10.0D == 0.0D) {
                index = (int)i;
                if (index > list.size()) {
                    index = list.size();
                }

                if (index == list.size()) {
                    --index;
                    if (index < 0) {
                        index = 0;
                    }

                    result = CommonsUtil.numberFormat(list.get(index), scale);
                } else {
                    --index;
                    if (index < 0) {
                        index = 0;
                    }

                    result = CommonsUtil.numberFormat((list.get(index) + list.get(index + 1)) / 2.0D, scale);
                }
            } else {
                index = (int)CommonsUtil.numberFormat(i, 0, 0);
                if (index > list.size()) {
                    index = list.size();
                }

                --index;
                if (index < 0) {
                    index = 0;
                }

                result = CommonsUtil.numberFormat((Double)list.get(index), scale);
            }
        }

        return result;
    }

    public static double leq(List<Double> list) {
        double leq = 0.0D;
        if (list != null && list.size() > 0) {
            double sum = 0.0D;

            for(int i = 0; i < list.size(); ++i) {
                sum += Math.pow(10.0D, 0.1D * (Double)list.get(i));
            }

            leq = 10.0D * Math.log10(sum / (double)list.size());
        }

        return CommonsUtil.numberFormat(leq, 1);
    }
}