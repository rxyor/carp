package com.github.rxyor.distributed.redisson.delay.core;

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
import java.util.concurrent.atomic.AtomicBoolean;
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

    private AtomicBoolean shutDown = new AtomicBoolean(false);

    /**
     * 同步锁
     */
    private final Object LOCK = new Object();

    /**
     * 线程池
     */
    private final static ExecutorService POOL = new ThreadPoolExecutor(
        DelayGlobalConfig.getScanThreadNum(), (DelayGlobalConfig.getScanThreadNum()) * 2,
        0L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(4096), new CarpThreadFactory(), new CarpDiscardPolicy());

    /**
     * 添加任务处理器
     *
     * @param delayJobHandler 任务处理器
     * @return Boolean
     */
    public Boolean addHandler(DelayJobHandler delayJobHandler) {
        Objects.requireNonNull(delayJobHandler, "handler can't be null");
        DelayValidUtil.validateHandlerId(delayJobHandler.getId());
        DelayValidUtil.validateTopic(delayJobHandler.getTopic());

        synchronized (LOCK) {
            for (DelayJobHandler handler : handlerList) {
                if (delayJobHandler.getId().equals(handler.getId())) {
                    return false;
                }
            }
            handlerList.add(delayJobHandler);
        }
        return true;
    }

    /**
     * 移除任务处理器
     *
     * @param handlerId 处理器ID
     * @return Boolean
     */
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

    /**
     * 清空处理器
     */
    public void clearHandler() {
        synchronized (LOCK) {
            handlerList.clear();
        }
    }

    /**
     * 开始扫描和处理任务
     */
    public synchronized void startup() {
        shutDown.set(false);
        scan();
        ThreadUtil.sleepSeconds(5L);
        process();
    }

    /**
     * 关闭任务
     */
    public synchronized void shutDown() {
        shutDown.set(true);
        if (POOL != null && !POOL.isShutdown()) {
            POOL.shutdown();
        }
    }

    /**
     * 扫描出就绪的任务
     */
    private void scan() {
        POOL.submit(() -> {
            while (true && !shutDown.get()) {
                DelayQueue.pushToReady();
                ThreadUtil.sleepSeconds(1L);
            }
        });
    }

    /**
     * 处理任务
     */
    private void process() {
        POOL.submit(() -> {
            while (true && !shutDown.get()) {
                processJobFromReadyQueue();
            }
        });
    }

    /**
     * 从就绪队列中取出任务并处理
     */
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

    /**
     * 取出所有处理器的处理任务类型
     * @return String List
     */
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
