package com.github.rxyor.distributed.lock;

import com.github.rxyor.common.util.FileUtil;
import com.github.rxyor.common.util.RandomUtil;
import com.github.rxyor.redis.util.LettuceConnectionUtil;
import io.lettuce.core.RedisClient;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 *<p>
 *Redis 分布式锁
 *</p>
 *
 * @author liuyang
 * @date 2019-05-14 Tue 18:51:00
 * @since 1.0.0
 */
public class RedisDistributedLock implements DistributedLock {

    /**
     * taskId本地线程缓存
     */
    private static final ThreadLocal<String> LOCAL_TASK_IDS = new ThreadLocal<>();
    /**
     * redis key本地线程缓存
     */
    private static final ThreadLocal<String> LOCAL_KEYS = new ThreadLocal<>();
    /**
     * 默认锁失效时间
     */
    private static final long DEFAULT_TIMEOUT = 5L;
    /**
     * 加锁LUA脚本
     */
    private static final String LUA_LOCK_SCRIPT;
    /**
     * 释放锁LUA脚本
     */
    private static final String LUA_UNLOCK_SCRIPT;

    static {
        LUA_LOCK_SCRIPT = readLuaLockScript();
        LUA_UNLOCK_SCRIPT = readLuaUnlockScript();
    }

    /**
     * redis 客户端
     */
    private RedisClient redisClient;

    /**
     * redis key前缀
     */
    @Getter
    @Setter
    private String keyPrefix;

    public RedisDistributedLock(RedisClient redisClient) {
        Objects.requireNonNull(redisClient, "redisClient can't be null");
        this.redisClient = redisClient;
    }

    /**
     * 获取锁
     *
     * @param redisKey
     * @param timeout 超时时间(秒)
     * @return 获取锁结果
     */
    @Override
    public boolean getLock(String redisKey, Long timeout) {
        long expire = (timeout == null || timeout <= 0L ? DEFAULT_TIMEOUT : timeout);
        String redisKeyWithPrefix = this.gainRedisKey(redisKey);
        LOCAL_KEYS.set(redisKeyWithPrefix);
        String taskId = this.generateTaskId();
        LOCAL_TASK_IDS.set(taskId);

        return evalRedisScript(LUA_LOCK_SCRIPT, new String[]{redisKeyWithPrefix},
            new String[]{taskId, String.valueOf(expire)});
    }

    /**
     * 释放锁
     *
     * @param redisKey
     * @return 释放结果
     */
    @Override
    public boolean releaseLock(String redisKey) {
        String redisKeyWithPrefix = this.gainRedisKey(redisKey);
        boolean success = evalRedisScript(LUA_UNLOCK_SCRIPT, new String[]{redisKeyWithPrefix},
            LOCAL_TASK_IDS.get());
        if (success) {
            this.clean();
        }
        return success;
    }


    @Override
    public boolean releaseLock() {
        String redisKeyWithPrefix = this.gainRedisKey(null);
        boolean success = evalRedisScript(LUA_UNLOCK_SCRIPT, new String[]{redisKeyWithPrefix},
            LOCAL_TASK_IDS.get());
        if (success) {
            this.clean();
        }
        return success;
    }

    /**
     * 执行lua脚本
     *
     * @param script 脚本
     * @param keys redis key
     * @param values redis参数
     * @return 执行结果
     */
    private boolean evalRedisScript(String script, String[] keys, String... values) {
        StatefulRedisConnection<String, String> conn = null;
        try {
            conn = LettuceConnectionUtil.getConnection(redisClient);
            RedisCommands<String, String> commands = conn.sync();
            return commands.<Boolean>eval(script, ScriptOutputType.BOOLEAN, keys, values);
        } finally {
            LettuceConnectionUtil.releaseConnection(conn);
        }
    }

    /**
     * 拼装或从缓存中取redis key
     *
     * @param key 原始redis key
     * @return String
     */
    private String gainRedisKey(String key) {
        if (StringUtils.isNotEmpty(key)) {
            String redisKeyPrefix = Optional.ofNullable(keyPrefix).orElse("");
            return redisKeyPrefix + key;
        }
        return Optional.ofNullable(LOCAL_KEYS.get()).filter(s -> StringUtils.isNotEmpty(s))
            .orElseThrow(() -> new IllegalArgumentException("redis key can't be empty"));
    }

    /**
     * 生成一个唯一的TaskId(UUID)
     * @return UUID
     */
    private String generateTaskId() {
        return RandomUtil.createUuid();
    }

    /**
     * 清空ThreadLocal缓存
     */
    private void clean() {
        LOCAL_KEYS.remove();
        LOCAL_TASK_IDS.remove();
    }

    /**
     * 读取加锁LUA脚本
     * @return String
     */
    private static String readLuaLockScript() {
        return FileUtil.readTextFromResource(RedisDistributedLock.class, "/lua/SETNX_EXPIRE.lua");
    }

    /**
     * 读取释放锁LUA脚本
     * @return String
     */
    private static String readLuaUnlockScript() {
        return FileUtil.readTextFromResource(RedisDistributedLock.class, "/lua/COMPARE_DELETE.lua");
    }
}
