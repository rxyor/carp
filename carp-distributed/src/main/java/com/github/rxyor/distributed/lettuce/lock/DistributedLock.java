package com.github.rxyor.distributed.lettuce.lock;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-14 Tue 18:34:00
 * @since 1.0.0
 */
public interface DistributedLock {

    /**
     * 获取锁
     *
     * @param redisKey
     * @param timeout 超时时间(秒)
     * @return 是否成功获取锁
     */
    boolean getLock(String redisKey, Long timeout);

    /**
     * 释放锁
     *
     * @param redisKey
     * @return 是否能够释放锁
     */
    boolean releaseLock(String redisKey);

    /**
     * 释放锁
     *
     * @return 是否能够释放锁
     */
    boolean releaseLock();

}
