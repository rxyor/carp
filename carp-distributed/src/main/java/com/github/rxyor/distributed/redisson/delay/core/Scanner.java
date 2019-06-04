package com.github.rxyor.distributed.redisson.delay.core;

import com.github.rxyor.common.core.thread.CarpDiscardPolicy;
import com.github.rxyor.common.core.thread.CarpThreadFactory;
import com.github.rxyor.common.util.FileUtil;
import com.github.rxyor.common.util.ThreadUtil;
import com.github.rxyor.distributed.redisson.delay.config.DelayConfig;
import com.github.rxyor.distributed.redisson.delay.handler.JobHandler;
import com.github.rxyor.distributed.redisson.delay.handler.LogJobHandler;
import com.github.rxyor.redis.redisson.factory.CarpRedissonFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-06-04 Tue 19:41:00
 * @since 1.0.0
 */
public class Scanner {


    private List<JobHandler> handlerList = new ArrayList<>(8);

    @Getter
    private DelayClientProxy delayClientProxy;

    private AtomicBoolean shutDown = new AtomicBoolean(false);

    /**
     * 同步锁
     */
    private final Object LOCK = new Object();

    /**
     * 线程池
     */
    private final ExecutorService pool;

    public Scanner(DelayClientProxy delayClientProxy) {
        Objects.requireNonNull(delayClientProxy, "delayClientProxy can't be null");
        this.delayClientProxy = delayClientProxy;
        int scanThreads = Optional.ofNullable(delayClientProxy.getDelayConfig()).map(DelayConfig::getScanThreads)
            .orElse(DelayConfig.DEFAULT_SCAN_THREADS);
        this.pool = new ThreadPoolExecutor(
            scanThreads, scanThreads * 2,
            0L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(4096), new CarpThreadFactory(), new CarpDiscardPolicy());
    }

    public void setDelayClientProxy(DelayClientProxy delayClientProxy) {
        Objects.requireNonNull(delayClientProxy, "delayClientProxy can't be null");
        this.delayClientProxy = delayClientProxy;
    }

    /**
     * 添加任务处理器
     *
     * @param delayJobHandler 任务处理器
     * @return Boolean
     */
    public Boolean addHandler(JobHandler delayJobHandler) {
        Objects.requireNonNull(delayJobHandler, "handler can't be null");
        DelayValidUtil.validateHandlerId(delayJobHandler.getId());
        DelayValidUtil.validateTopic(delayJobHandler.getTopic());

        synchronized (LOCK) {
            for (JobHandler handler : handlerList) {
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
        pool.submit(() -> {
            scan();
            ThreadUtil.sleepSeconds(5L);
            process();
        });

    }

    /**
     * 关闭任务
     */
    public synchronized void shutDown() {
        shutDown.set(true);
        if (pool != null && !pool.isShutdown()) {
            pool.shutdown();
        }
    }

    /**
     * 扫描出就绪的任务
     */
    private void scan() {
        pool.submit(() -> {
            while (true && !shutDown.get()) {
                delayClientProxy.popsNowAndPushToReady();
                ThreadUtil.sleepSeconds(1L);
            }
        });
    }

    /**
     * 处理任务
     */
    private void process() {
        pool.submit(() -> {
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
            ThreadUtil.sleepSeconds(10 * 60L);
            return;
        }
        List<String> allTopics = computeAllTopic();
        int emptyTopicCount = 0;
        for (String topic : allTopics) {
            DelayJob delayJob = delayClientProxy.popReadyJob(topic);
            if (delayJob == null) {
                emptyTopicCount++;
                continue;
            }
            for (JobHandler handler : handlerList) {
                if (topic.equals(handler.getTopic())) {
                    pool.submit(() -> {
                        handler.consume(delayJob);
                    });
                }
            }
        }
        //所有Topic的Ready Queue都没发现任务时，挂起一会儿
        if (emptyTopicCount == allTopics.size()) {
            ThreadUtil.sleepSeconds(1L);
        }
    }

    /**
     * 取出所有处理器的处理任务类型
     * @return String List
     */
    private List<String> computeAllTopic() {
        List<String> topics = new ArrayList<>(16);
        for (JobHandler handler : handlerList) {
            if (handler == null) {
                continue;
            }
            if (StringUtils.isNotEmpty(handler.getTopic())) {
                topics.add(handler.getTopic());
            }
        }
        return topics;
    }

    private DelayClientProxy requireNonNullDelayProxy() {
        Objects.requireNonNull(delayClientProxy, "delayClientProxy can't be null");
        return this.delayClientProxy;
    }

    public static void main(String[] args) {

        CarpRedissonFactory factory = CarpRedissonFactory.builder()
            .yaml(FileUtil.findRealPathByClasspath(Scanner.class, "/redis.yml")).build();
        RedissonClient redissonClient = factory.createRedissonClient();
        DelayClientProxy proxy = new DelayClientProxy(redissonClient, new DelayConfig());
        Scanner delayScanner = new Scanner(proxy);
        delayScanner.addHandler(new LogJobHandler("Girl", proxy));
        delayScanner.startup();

        int i = 0;
        while (true) {
            Map<String, Object> map = new HashMap<>();
            map.put("age", 19);
            map.put("name", "陈悠");
            proxy.offer("Girl", (long) i++, 3, 10L, map);
            ThreadUtil.sleepSeconds(1L);
        }
    }

}
