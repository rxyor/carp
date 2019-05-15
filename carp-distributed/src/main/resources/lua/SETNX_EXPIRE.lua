if (redis.call('setnx', KEYS[1], ARGV[1]) == 1)
then
    redis.call('expire', KEYS[1], ARGV[2])
    return true
else
    return false
end