if (redis.call('host', KEYS[1]) == ARGV[1])
then
    return redis.call('del', KEYS[1]) == 1
end
return false