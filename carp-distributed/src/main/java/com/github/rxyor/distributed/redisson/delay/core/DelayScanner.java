package com.github.rxyor.distributed.redisson.delay.core;

import com.github.rxyor.common.core.exception.DuplicateDataException;
import com.github.rxyor.common.core.thread.CarpDiscardPolicy;
import com.github.rxyor.common.core.thread.CarpThreadFactory;
import com.github.rxyor.common.util.ThreadUtil;
import com.github.rxyor.redis.redisson.util.RedissonUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-27 Mon 09:50:00
 * @since 1.0.0
 */
@Slf4j
public class DelayScanner {

    private List<DelayJobHandler> handlerList = new ArrayList<>(8);

    /**
     * 同步锁
     */
    private final Object LOCK = new Object();

    private final static ExecutorService POOL = new ThreadPoolExecutor(
        DelayGlobalConfig.getScanThreadNum(), (DelayGlobalConfig.getScanThreadNum()) * 2,
        0L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(4096), new CarpThreadFactory(), new CarpDiscardPolicy());


    public Boolean addHandler(DelayJobHandler delayJobHandler) {
        Objects.requireNonNull(delayJobHandler, "handler can't be null");
        DelayValidUtil.validateHandlerId(delayJobHandler.getId());
        DelayValidUtil.validateTopic(delayJobHandler.getTopic());

        synchronized (LOCK) {
            for (DelayJobHandler handler : handlerList) {
                if (delayJobHandler.getId().equals(handler.getId())) {
                    throw new DuplicateDataException("handler has existed");
                }
            }
            handlerList.add(delayJobHandler);
        }
        return true;
    }

    public Boolean removeHandler(String handlerId) {
        if (StringUtils.isEmpty(handlerId)) {
            return false;
        }
        synchronized (LOCK) {
            Integer index = null;
            for (int i = 0; i < handlerList.size(); i++) {
                if (handlerId.equals(handlerList.get(i).getId())) {
                    index = i;
                    break;
                }
            }
            if (index != null) {
                handlerList.remove(index);
                return true;
            }
        }
        return false;
    }

    public void clearHandler() {
        synchronized (LOCK) {
            handlerList.clear();
        }
    }

    public void startup() {
        scan();
        ThreadUtil.sleepSeconds(5L);
        process();
    }

    private void scan() {
        POOL.submit((Runnable) () -> {
            while (true) {
                DelayQueue.pushToReady();
                ThreadUtil.sleepSeconds(1L);
            }
        });
    }

    private void process() {
        POOL.submit((Runnable) () -> {
            while (true) {
                processJobFromReadyQueue();
            }
        });
    }

    private void processJobFromReadyQueue() {
        if (CollectionUtils.isEmpty(handlerList)) {
            ThreadUtil.sleepSeconds(5 * 60L);
            return;
        }
        List<String> allTopics = computeAllTopic();
        int emptyTopicCount = 0;
        for (String topic : allTopics) {
            DelayJob delayJob = DelayBucket.popReady(topic);
            if (delayJob == null) {
                emptyTopicCount++;
                continue;
            }
            for (DelayJobHandler handler : handlerList) {
                if (topic.equals(handler.getTopic())) {
                    POOL.submit(() -> {
                        handler.consume(delayJob);
                    });
                }
            }
        }
        //所有Topic的Ready Queue都没发现任务时，挂起一会儿
        if (emptyTopicCount == allTopics.size()) {
            ThreadUtil.sleepSeconds(30L);
        }
    }

    private List<String> computeAllTopic() {
        List<String> topics = new ArrayList<>(16);
        for (DelayJobHandler handler : handlerList) {
            if (handler == null) {
                continue;
            }
            if (StringUtils.isNotEmpty(handler.getTopic())) {
                topics.add(handler.getTopic());
            }
        }
        return topics;
    }

    public static void main(String[] args) {

        RedissonUtil.configFromYaml(DelayQueue.class, "/redis.yml");
        DelayScanner delayScanner = new DelayScanner();
        delayScanner.addHandler(new LogDelayJobHandler("Girl"));
        delayScanner.startup();

        int i = 0;
        while (true) {
            Map<String, Object> map = new HashMap<>();
            map.put("age", 19);
            map.put("name", "陈悠");
            DelayBucket.offer("Girl", (long) i++, 3, 10L, map);
            ThreadUtil.sleepSeconds(1L);
        }
    }

}
