package business.processor.bean;

import lombok.Data;

import java.util.Date;

@Data
public class DataFactorBean {
    private String factorCode;
    private Date dataTime;
    private Double rtd;
    private Integer state;
    private Double avg;
    private Integer avgState;
    private Double max;
    private Integer maxState;
    private Double min;
    private Integer minState;
    private Double cou;
    private Integer couState;
    private Double zsRtd;
    private Integer zsState;
    private Double zsAvg;
    private Integer zsAvgState;
    private Double zsMax;
    private Integer zsMaxState;
    private Double zsMin;
    private Integer zsMinState;
    private Double zsCou;
    private Integer zsCouState;
    private String flag;
    private String eFlag;
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