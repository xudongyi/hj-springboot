package business.receiver.task;

import business.receiver.service.BlackListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BlackListTask {

    public BlackListTask() {
    }

    @Scheduled(
        cron = "0 0 0 * * ?"
    )
    @Async
    public void run() {
        log.error("每日黑名单报告：" + BlackListService.blacklistReport());
    }
}