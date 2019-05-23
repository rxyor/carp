package com.github.rxyor.common.core.thread;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-21 Tue 15:46:00
 * @since 1.0.0
 */
@Slf4j
public class CarpDiscardPolicy implements RejectedExecutionHandler {

    public CarpDiscardPolicy() {
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        log.error("the task is discarded:{}", r);
    }
}
