package business.processor.excute;

import business.processor.bean.DataPacketBean;
import business.receiver.task.UpdateReceiverTableTask;
import business.receiver.threadPool.ThreadPoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("dataParserAuto")
@Slf4j
public class DataParserAuto {
    @Autowired
    private ThreadPoolService threadPoolService;
    @Autowired
    private DataParserService dataParserService;

    public DataParserAuto() {
    }

    public void autoDistributeData(final DataPacketBean dataPacketBean) {
        this.threadPoolService.getAutoProcessPool().execute(() -> {
            DataParserAuto.this.dataParserService.distributeData(UpdateReceiverTableTask.getBakSourceSql_update_auto(), dataPacketBean, true);
        });
    }
}