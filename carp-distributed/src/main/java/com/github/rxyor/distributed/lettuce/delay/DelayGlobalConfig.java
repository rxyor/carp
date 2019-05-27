package com.github.rxyor.distributed.lettuce.delay;

import com.github.rxyor.common.core.exception.DuplicateDataException;
import com.github.rxyor.redis.lettuce.config.RedisConnectionProperties;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-22 Wed 17:26:00
 * @since 1.0.0
 */
public class DelayGlobalConfig {

    private static final String READY_QUEUE_NAME = "delay_ready_queue";

    private static final String WAIT_QUEUE_NAME = "delay_wait_queue";

    private static final Object LOCK = new Object();

    @Getter
    @Setter
    private static String appName = "";

    @Getter
    @Setter
    private static RedisConnectionProperties redisConnectionProperties;

    @Getter
    private static List<DelayJobHandler> delayJobHandlerList = new ArrayList<>(8);

    @Getter
    private static int scanThreadNum = 1;

    @Getter
    private static int processJobThreadNum = 2;

    /**
     * 获取就绪队列 Redis Key
     * @return
     */
    public static String getReadyQueueTopic() {
        return StringUtils.isEmpty(appName) ? READY_QUEUE_NAME : appName + ":" + READY_QUEUE_NAME;
    }

    /**
     * 获取等待队列 Redis Key
     * @return
     */
    public static String getWaitQueueTopic() {
        return StringUtils.isEmpty(appName) ? WAIT_QUEUE_NAME : appName + ":" + WAIT_QUEUE_NAME;
    }

    public static String rebuildWaitQueueTopic(String topic) {
        if (StringUtils.isBlank(topic)) {
            throw new IllegalArgumentException("topic can't be blank");
        }
        return getWaitQueueTopic() + ":" + topic;
    }

    public static Boolean addHandler(DelayJobHandler delayJobHandler) {
        if (delayJobHandler == null || StringUtils.isEmpty(delayJobHandler.getId())) {
            throw new IllegalArgumentException("handler's id can't not be empty");
        }
        synchronized (LOCK) {
            for (DelayJobHandler handler : delayJobHandlerList) {
                if (delayJobHandler.getId().equals(handler.getId())) {
                    throw new DuplicateDataException("handler has existed");
                }
            }
            delayJobHandlerList.add(delayJobHandler);
        }
        return true;
    }

    public static Boolean removeHandler(String handlerId) {
        if (StringUtils.isEmpty(handlerId)) {
            return false;
        }
        synchronized (LOCK) {
            Integer delIndex = null;
            for (int i = 0; i < delayJobHandlerList.size(); i++) {
                if (handlerId.equals(delayJobHandlerList.get(i).getId())) {
                    delIndex = i;
                    break;
                }
            }
            if (delIndex != null) {
                delayJobHandlerList.remove(delIndex);
                return true;
            }
        }
        return false;
    }

    public static void clearHandler() {
        synchronized (LOCK) {
            delayJobHandlerList.clear();
        }
    }

    public static List<String> gainAllTopics() {
        List<String> allTopics = new ArrayList<>(16);
        for (DelayJobHandler handler : delayJobHandlerList) {
            if (CollectionUtils.isNotEmpty(handler.getProcessibleTopics())) {
                allTopics.addAll(handler.getProcessibleTopics());
            }
        }
        return allTopics;
    }

    public synchronized static void setScanThreadNum(Integer scanThreadNum) {
        if (scanThreadNum == null || scanThreadNum <= 0) {
            throw new IllegalArgumentException("scanThreadNum must >0");
        }
        DelayGlobalConfig.scanThreadNum = scanThreadNum;
    }

    public synchronized static void setProcessJobThreadNum(Integer processJobThreadNum) {
        if (processJobThreadNum == null || processJobThreadNum <= 0) {
            throw new IllegalArgumentException("processJobThreadNum must >0");
        }
        DelayGlobalConfig.processJobThreadNum = processJobThreadNum;
    }
}
