package business.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MathCalcUtil {
    public MathCalcUtil() {
    }

    public static double avg(double avg1, double avg2, int times) {
        return CommonsUtil.numberFormat((avg1 * (double)times + avg2) / (double)(times + 1), 4);
    }

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

    public static double max(List<Double> data) {
        if (data != null && data.size() != 0) {
            double max = (Double)data.get(0);

            for(int i = 0; i < data.size(); ++i) {
                if ((Double)data.get(i) > max) {
                    max = (Double)data.get(i);
                }
            }

            return max;
        } else {
            return 0.0D;
        }
    }

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

    public static double cou(List<Double> data, int scale) {
        if (data != null && data.size() != 0) {
            double cou = 0.0D;

            for(int i = 0; i < data.size(); ++i) {
                cou += (Double)data.get(i);
            }

            cou = CommonsUtil.numberFormat(cou, 4);
            return cou;
        } else {
            return 0.0D;
        }
    }

    public static double percentile(List<Double> list, double percent) {
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

                    result = CommonsUtil.numberFormat((Double)list.get(index));
                } else {
                    --index;
                    if (index < 0) {
                        index = 0;
                    }

                    result = CommonsUtil.numberFormat(((Double)list.get(index) + (Double)list.get(index + 1)) / 2.0D);
                }
            } else {
                index = (int)CommonsUtil.numberFormat(i, 0);
                if (index > list.size()) {
                    index = list.size();
                }

                --index;
                if (index < 0) {
                    index = 0;
                }

                result = CommonsUtil.numberFormat((Double)list.get(index));
            }
        }

        return result;
    }
}