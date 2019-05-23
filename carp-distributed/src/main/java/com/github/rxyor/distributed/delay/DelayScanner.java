package com.github.rxyor.distributed.delay;

import com.alibaba.fastjson.JSON;
import com.github.rxyor.common.core.thread.CarpDiscardPolicy;
import com.github.rxyor.common.core.thread.CarpThreadFactory;
import com.github.rxyor.common.util.ThreadUtil;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-21 Tue 17:29:00
 * @since 1.0.0
 */
@Slf4j
public class DelayScanner {


    private static ExecutorService pool = new ThreadPoolExecutor(
        DelayGlobalConfig.getScanThreadNum() + DelayGlobalConfig.getProcessJobThreadNum(),
        (DelayGlobalConfig.getScanThreadNum() + DelayGlobalConfig.getProcessJobThreadNum()) * 2, 0L, TimeUnit.SECONDS,
        new LinkedBlockingDeque<>(4096), new CarpThreadFactory(), new CarpDiscardPolicy());

    public static void startup() {
        doScan();
        doProcess();
    }

    private static void doScan() {
        for (int i = 0; i < DelayGlobalConfig.getScanThreadNum(); i++) {
            pool.submit((Runnable) () -> {
                while (true) {
                    scanAndPush();
                }
            });
        }
    }

    private static void doProcess() {
        for (int i = 0; i < DelayGlobalConfig.getProcessJobThreadNum(); i++) {
            pool.submit((Runnable) () -> {
                while (true) {
                    processJob();
                }
            });
        }
    }

    private static void scanAndPush() {
        for (String topic : DelayGlobalConfig.gainAllTopics()) {
            List<String> jobs = RedisClientWrapper.zrangeByScoreAndZrem(DelayGlobalConfig.rebuildWaitQueueTopic(topic));
            ThreadUtil.sleepSeconds(1L);
            RedisClientWrapper.lpush(DelayGlobalConfig.getReadyQueueTopic(), jobs.toArray(new String[jobs.size()]));
        }
    }

    private static void processJob() {
        String json = RedisClientWrapper.lpop(DelayGlobalConfig.getReadyQueueTopic());
        DelayJob delayJob = JSON.parseObject(json, DelayJob.class);
        if (delayJob == null) {
            ThreadUtil.sleepSeconds(3L);
            return;
        }
        for (DelayJobHandler handler : DelayGlobalConfig.getDelayJobHandlerList()) {
            List<String> processibleTopics = handler.getProcessibleTopics();
            if (processibleTopics != null && processibleTopics.contains(delayJob.getTopic())) {
                handler.handleDelayJob(delayJob);
            }
        }
    }
}
