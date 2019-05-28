package com.github.rxyor.distributed.redisson.delay.core;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-24 Fri 11:05:00
 * @since 1.0.0
 */
public class DelayGlobalConfig {

    /**
     * 待处理job
     */
    private static final String WAIT_BUCKET_NAME = "delay_wait_bucket";

    /**
     * 就绪job
     */
    private static final String READY_QUEUE_NAME = "delay_ready_queue";

    /**
     * blocking queue
     */
    private static final String BLOCKING_QUEUE_NAME = "delay_blocking_queue";

    /**
     * 执行失败的任务
     */
    private static final String FAIL_JOB_KEY = "fail_delay_job";

    /**
     * 任务池(所有任务未处理任务详情)
     */
    private static final String DELAY_JOB_POOL = "delay_job_pool";

    @Getter
    @Setter
    private static String appName = "app_name";

    /**
     * 任务通数量
     */
    @Getter
    private static Integer buckets = 4;

    /**
     * 扫描线程数
     */
    @Getter
    private static int scanThreadNum = 4;

    private static final String SPLITTER = ":";

    /**
     * 计算job所属bucket(redis key)
     * @param delayJobId
     * @return String
     */
    public static String computeBucketKey(Long delayJobId) {
        DelayValidUtil.validateJobId(delayJobId);
        Long bucketId = Math.floorMod(delayJobId, buckets);
        return appName + SPLITTER + WAIT_BUCKET_NAME + SPLITTER + bucketId;
    }

    /**
     * 根据数组索引返回 bucket key
     *
     * @param index 数组下标
     * @return bucket key
     */
    public static String gainBucketKeyByIndex(Integer index) {
        if (index == null || index < 0 || index >= buckets) {
            throw new IllegalArgumentException("index must between 0 and " + (buckets - 1));
        }
        return appName + SPLITTER + WAIT_BUCKET_NAME + SPLITTER + index;
    }

    /**
     *获取就绪队列redis key
     * @return
     */
    public static String gainReadyQueueKey() {
        return appName + SPLITTER + READY_QUEUE_NAME;
    }

    /**
     * 获取阻塞队列redis key
     * @return
     */
    public static String gainBlockingQueueKey(String topic) {
        String prefix = appName + SPLITTER + BLOCKING_QUEUE_NAME;
        return StringUtils.isBlank(topic) ? prefix : prefix + SPLITTER + topic;
    }

    /**
     * 获取任务池redis key
     * @return
     */
    public static String gainDelayJobPoolKey() {
        return appName + SPLITTER + DELAY_JOB_POOL;
    }

    /**
     * 失败任务 redis key
     */
    public static String gainFailDelayJobKey() {
        return appName + SPLITTER + FAIL_JOB_KEY;
    }

}
