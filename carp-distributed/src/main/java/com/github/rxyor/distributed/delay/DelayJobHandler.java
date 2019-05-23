package com.github.rxyor.distributed.delay;

import java.util.List;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-21 Tue 13:56:00
 * @since 1.0.0
 */
public interface DelayJobHandler {

    /**
     * handle DelayJob
     * @param delayJob
     */
    void handleDelayJob(DelayJob delayJob);

    /**
     * handler id
     */
    String getId();

    /**
     * topics
     */
    List<String> getProcessibleTopics();
}
