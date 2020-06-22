package business.controller;

import business.cache.DataCache;
import business.entity.AnalogData;
import business.service.AnalogDataSendService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("sendService")
public class SendController {
    @RequestMapping("send")
    public String sendData(HttpServletRequest request, AnalogData v){
        DataCache.ANALOG_DATA_CACHE = v;
        //DataCache.saveAnalogData();
        if (AnalogDataSendService.isStop()) {
            AnalogDataSendService.send(v);
        }
        return "success";
    }

    @RequestMapping("stop")
    public void stopSendData(){
        AnalogDataSendService.stop();
    }

}
