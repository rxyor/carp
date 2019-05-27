package com.github.rxyor.distributed.redisson.delay.core;

import com.github.rxyor.common.core.exception.DuplicateDataException;
import com.github.rxyor.common.core.thread.CarpDiscardPolicy;
import com.github.rxyor.common.core.thread.CarpThreadFactory;
import com.github.rxyor.common.util.ThreadUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
        DelayGlobalConfig.getScanThreadNum() + DelayGlobalConfig.getProcessJobThreadNum(),
        (DelayGlobalConfig.getScanThreadNum() + DelayGlobalConfig.getProcessJobThreadNum()) * 2,
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

    private static void pushToReadyQueue() {
        List<DelayJob> delayJobList = DelayQueue.pops();
    }

    private void processJob() {
        if (CollectionUtils.isEmpty(handlerList)) {
            ThreadUtil.sleepSeconds(5 * 60L);
            return;
        }
        for (DelayJobHandler handler : handlerList) {
            String topic = Optional.ofNullable(handler).map(DelayJobHandler::getTopic).orElse(null);
            if (StringUtils.isEmpty(topic)) {
                continue;
            }
            try {
                DelayJob delayJob = ReadyQueue.pop(topic);
                handler.handleDelayJob(delayJob);
            } catch (Exception e) {
                log.error("process job fail:", e);
            }
        }
    }


}
