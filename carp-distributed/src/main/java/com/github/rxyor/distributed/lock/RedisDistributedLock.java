package com.github.rxyor.distributed.lock;

import com.github.rxyor.common.util.FileUtil;
import com.github.rxyor.redis.util.LettuceConnectionUtil;
import io.lettuce.core.RedisClient;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
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

    private static final String LUA_LOCK_SCRIPT;
    private static final String LUA_UNLOCK_SCRIPT;

    static {
        LUA_LOCK_SCRIPT = readLuaLockScript();
        LUA_UNLOCK_SCRIPT = readLuaUnlockScript();
    }

    private RedisClient redisClient;

    public RedisDistributedLock(RedisClient redisClient) {
        Objects.requireNonNull(redisClient, "redisClient can't be null");
        this.redisClient = redisClient;
    }

    @Override
    public boolean getLock(String redisKey, String taskId, Long timeout) {
        Optional.ofNullable(redisKey).filter(s -> StringUtils.isNotBlank(s))
            .orElseThrow(() -> new IllegalArgumentException("redisKey can't be blank"));
        Optional.ofNullable(redisKey).filter(s -> StringUtils.isNotBlank(s))
            .orElseThrow(() -> new IllegalArgumentException("taskId can't be blank"));
        Optional.ofNullable(timeout).filter(aLong -> timeout > 0)
            .orElseThrow(() -> new IllegalArgumentException("timeout must more bigger than 0"));

        String[] keys = new String[]{redisKey};
        String[] values = new String[]{taskId, String.valueOf(timeout)};
        return evalRedisScript(LUA_LOCK_SCRIPT, keys, values);
    }

    @Override
    public boolean releaseLock(String redisKey, String taskId) {
        Optional.ofNullable(redisKey).filter(s -> StringUtils.isNotBlank(s))
            .orElseThrow(() -> new IllegalArgumentException("redisKey can't be blank"));
        Optional.ofNullable(redisKey).filter(s -> StringUtils.isNotBlank(s))
            .orElseThrow(() -> new IllegalArgumentException("taskId can't be blank"));
        String[] keys = new String[]{redisKey};
        return evalRedisScript(LUA_UNLOCK_SCRIPT, keys, taskId);
    }

    private boolean evalRedisScript(String scrpt, String[] keys, String... values) {
        StatefulRedisConnection<String, String> conn = null;
        try {
            conn = LettuceConnectionUtil.getConnection(redisClient);
            RedisCommands<String, String> commands = conn.sync();
            return commands.<Boolean>eval(scrpt, ScriptOutputType.BOOLEAN, keys, values);
        } finally {
            LettuceConnectionUtil.releaseConnection(conn);
        }
    }

    private static String readLuaLockScript() {
        return FileUtil.readTextFromResource(RedisDistributedLock.class, "/lua/SETNX_EXPIRE.lua");
    }

    private static String readLuaUnlockScript() {
        return FileUtil.readTextFromResource(RedisDistributedLock.class, "/lua/COMPARE_DELETE.lua");
    }
}
