package business.receiver.task;

import business.receiver.service.ReverseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReverseTask {
    @Autowired
    private ReverseService reverseService;

    public ReverseTask() {
    }

    @Scheduled(
        cron = "0 30 * * * ?"
    )
    @Async
    public void run() {
        this.reverseService.removeCmd();
    }
}
