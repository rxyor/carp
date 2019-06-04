package com.github.rxyor.distributed.redisson.delay.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-06-04 Tue 15:55:00
 * @since 1.0.0
 */
@Data
public class DelayConfig {

    /**
     * 默认redis key salt 防止key冲突
     */
    private static final String SALT_KEY = "JXU2MEEw";

    /**
     * 分割符
     */
    private static final String SPLITTER = ":";

    /**
     * 默认等待队列
     */
    public static final String DEFAULT_WAIT_QUEUE_KEY = "wait_queue";

    /**
     * 默认就绪队列
     */
    public static final String DEFAULT_READY_QUEUE_KEY = "ready_queue";

    /**
     * 默认就绪 topic key 前缀
     */
    public static final String DEFAULT_READY_TOPIC_KEY_PREFIX = "ready_topic";

    /**
     * 默认任务详情池
     */
    public static final String DEFAULT_JOB_DETAILS_KEY = "job_details";

    /**
     * 默认失败任务
     */
    public static final String DEFAULT_FAIL_JOBS_KEY = "fail_jobs";

    /**
     * 默认应用名
     */
    public static final String DEFAULT_APP_NAME = "app_name";

    public static final Integer DEFAULT_SCAN_THREADS = 6;

    private String waitQueueKey;

    private String readyQueueKey;

    private String readyTopicKeyPrefix;

    private String jobDetailsKey;

    private String failJobsKey;

    private String appName;

    private Integer scanThreads;

    public void setScanThreads(Integer scanThreads) {
        if (scanThreads == null || scanThreads < DEFAULT_SCAN_THREADS) {
            throw new IllegalArgumentException("scanThreads must >=" + DEFAULT_SCAN_THREADS);
        }
        this.scanThreads = scanThreads;
    }

    public Integer buildScanThreads() {
        return scanThreads == null || scanThreads < DEFAULT_SCAN_THREADS ? DEFAULT_SCAN_THREADS : scanThreads;
    }

    public String buildWaitQueueKey() {
        return buildKeyPrefix() + (StringUtils.isBlank(waitQueueKey) ? DEFAULT_WAIT_QUEUE_KEY : waitQueueKey);
    }

    public String buildReadyQueueKey() {
        return buildKeyPrefix() + (StringUtils.isBlank(readyQueueKey) ? DEFAULT_READY_QUEUE_KEY : readyQueueKey);
    }

    public String buildReadyTopicKey(String topic) {
        if (StringUtils.isBlank(topic)) {
            throw new IllegalArgumentException("topic can't be blank");
        }
        return buildKeyPrefix() + (StringUtils.isBlank(readyTopicKeyPrefix) ? DEFAULT_READY_TOPIC_KEY_PREFIX
            : readyTopicKeyPrefix) + SPLITTER + topic;
    }

    public String buildJobDetailsKey() {
        return buildKeyPrefix() + (StringUtils.isBlank(jobDetailsKey) ? DEFAULT_JOB_DETAILS_KEY : jobDetailsKey);
    }

    public String buildFailJobsKey(String jobId) {
        if (StringUtils.isBlank(jobId)) {
            throw new IllegalArgumentException("jobId can't be blank");
        }
        return buildKeyPrefix() + (StringUtils.isBlank(failJobsKey) ? DEFAULT_FAIL_JOBS_KEY : failJobsKey) + SPLITTER
            + jobId;
    }


    private String buildKeyPrefix() {
        return validGetAppName() + SPLITTER + SALT_KEY + SPLITTER;
    }

    private String validGetAppName() {
        return StringUtils.isBlank(this.appName) ? DEFAULT_APP_NAME : this.appName;
    }

}
