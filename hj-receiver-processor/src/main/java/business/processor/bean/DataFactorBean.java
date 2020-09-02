package business.processor.bean;

import lombok.Data;

import java.util.Date;

@Data
public class DataFactorBean {
    private String factorCode;
    private Date dataTime;
    private Double rtd = 0.0;
    private Integer state = 0;
    private Double avg = 0.0;
    private Integer avgState = 0;
    private Double max;
    private Integer maxState = 0;
    private Double min;
    private Integer minState = 0;
    private Double cou = 0.0;
    private Integer couState = 0;
    private Double zsRtd = 0.0;
    private Integer zsState = 0;
    private Double zsAvg = 0.0;
    private Integer zsAvgState = 0;
    private Double zsMax = 0.0;
    private Integer zsMaxState = 0;
    private Double zsMin = 0.0;
    private Integer zsMinState = 0;
    private Double zsCou = 0.0;
    private Integer zsCouState = 0;
    private String flag="N";
    private String eFlag="0";
    private Date sampleTime;
    private Double today;
    private Double total;
    private Boolean totalError;
    private Double lastCorretTotal;
    private Double surplus;
    private Double data;
    private Double dayData;
    private Double nightData;
    private Integer fixTimes;
    private Date fixBeginTime;
    private Boolean repeat;


}