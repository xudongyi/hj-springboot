package business.entity;

import lombok.Data;

@Data
public class AnalogData {
    private String waterMN;
    private String isWaterSend;
    private String airMN;
    private String isAirSend;
    private String airqMN;
    private String isAirqSend;
    private String surfwaterMN;
    private String isSurfwaterSend;
    private String noiseMN;
    private String isNoiseSend;
    private String vocMN;
    private String isVocSend;
    private String ipPort;
}
