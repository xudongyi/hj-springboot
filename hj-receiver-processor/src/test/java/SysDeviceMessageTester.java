import business.HjReceiverProcessorApplication;
import business.receiver.entity.SysDeviceMessage;
import business.receiver.entity.SysDeviceMessageEnum;
import business.receiver.mapper.SysDeviceMessageMapper;
import com.xy.format.hbt212.core.T212Mapper;
import com.xy.format.hbt212.exception.T212FormatException;
import com.xy.format.hbt212.model.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = HjReceiverProcessorApplication.class)
@Slf4j
public class SysDeviceMessageTester {
    @Autowired
    private SysDeviceMessageMapper sysDeviceMessageMapper;

    @Test
    public void insertData() {
       log.info("insertData---");
        SysDeviceMessage sysDeviceMessage = new SysDeviceMessage();
        String content =  "##0136ST=32;CN=2011;PW=123456;MN=LD130133000015;CP=" +
                "&&DataTime=20160824003817;B01-Rtd=36.91;011-Rtd=231.0,011-Flag=N;060-Rtd=1.803,060-Flag=N&&" +
                "4980\r\n";
        sysDeviceMessage.setContent(content);
        T212Mapper mapper = new T212Mapper()
                .enableDefaultParserFeatures()
                .enableDefaultVerifyFeatures();
        try {
            Data data = mapper.readData(content);
            sysDeviceMessage.setMn(data.getMn());
            SysDeviceMessageEnum IS_RECIEVE =  SysDeviceMessageEnum.IS_RECIEVE;
            sysDeviceMessage.setFlag(IS_RECIEVE.code());
            Assert.assertEquals(1,sysDeviceMessageMapper.insert(sysDeviceMessage));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (T212FormatException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkTable() {
        log.info("checkTable---");
        sysDeviceMessageMapper.createSysDeviceMessageTable("sys_device_message_2017");
    }
}
