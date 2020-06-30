package business.receiver.threadPool;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Data
@Component
public class ThreadPoolService {
    private int reverseThreads = 20;
    private int receiveThreads = 80;
    private int autoProcessThreads = 100;
    @Value("${thread.pool.process.hand}")
    private int handProcessThreads = 100;
    private static ExecutorService THREAD_POOL_REVERSE = null;
    private static ExecutorService THREAD_POOL_RECEIVE = null;
    private static ExecutorService THREAD_POOL_PROCESS_AUTO = null;
    private static ExecutorService THREAD_POOL_PROCESS_HAND = null;

    public ThreadPoolService() {
    }

    @Value("${thread.pool.reverse}")
    private void setReversePool(String reverseThreads) {
        if (StringUtils.isNotEmpty(reverseThreads)) {
            try {
                this.reverseThreads = Integer.valueOf(reverseThreads);
            } catch (Exception var3) {
            }
        }

        THREAD_POOL_REVERSE = Executors.newFixedThreadPool(this.reverseThreads);
    }

    @Value("${thread.pool.receive}")
    private void setReceivePool(String receiveThreads) {
        if (StringUtils.isNotEmpty(receiveThreads)) {
            try {
                this.receiveThreads = Integer.valueOf(receiveThreads);
            } catch (Exception var3) {
            }
        }

        THREAD_POOL_RECEIVE = Executors.newFixedThreadPool(this.receiveThreads);
    }

    @Value("${thread.pool.process.auto}")
    private void setAutoProcessPool(String autoProcessThreads) {
        if (StringUtils.isNotEmpty(autoProcessThreads)) {
            try {
                this.autoProcessThreads = Integer.valueOf(autoProcessThreads);
            } catch (Exception var3) {
            }
        }

        THREAD_POOL_PROCESS_AUTO = Executors.newFixedThreadPool(this.autoProcessThreads);
    }

    public void setHandProcessPool() {
        THREAD_POOL_PROCESS_HAND = Executors.newFixedThreadPool(this.handProcessThreads);
    }

    public ExecutorService getReversePool() {
        return THREAD_POOL_REVERSE;
    }

    public ExecutorService getReceivePool() {
        return THREAD_POOL_RECEIVE;
    }

    public ExecutorService getAutoProcessPool() {
        return THREAD_POOL_PROCESS_AUTO;
    }

    public ExecutorService getHandProcessPool() {
        return THREAD_POOL_PROCESS_HAND;
    }
}