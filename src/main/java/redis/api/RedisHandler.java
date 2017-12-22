package redis.api;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class RedisHandler implements IRedisHandler {

    private Logger logger = LoggerFactory.getLogger(RedisHandler.class);

    private final static String OK = "OK";
    private String prefix;
    private JedisPool jedisPool;

    public boolean put(String key, Object value, int expire) {
        if (StringUtils.isBlank(key) || value == null) {
            return false;
        }
        byte[] key_b = getKey(key).getBytes();
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String result = jedis.set(key_b, SerializeUtil.serialize(value));
            if (expire > 0) {
                jedis.expire(key_b, expire);
            }
            if (OK.equals(result)) {
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return false;
    }

    public <T> T get(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            byte[] result = jedis.get(getKey(key).getBytes());
            if (result != null && result.length > 0) {
                return SerializeUtil.unserialize(result);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            remove(key);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public void closeJedisPool(Jedis jedis) {
        if (jedis != null) {
            if (jedis.isConnected()) {
                try {
                    jedis.close();
                    System.out.println("退出" + jedis.toString() + ":" + jedis.quit());
                    jedis.disconnect();
                } catch (Exception e) {
                    System.out.println("退出失败");
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public Long remove(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.del(getKey(key).getBytes());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public boolean put_str(String key, String value, int expire) {
        if (StringUtils.isBlank(key) || value == null) {
            return false;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String result = jedis.set(getKey(key), value);
            if (expire > 0) {
                jedis.expire(getKey(key), expire);
            }
            if (OK.equals(result)) {
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return false;
    }

    public String get_str(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.get(getKey(key));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            remove_str(key);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public Long remove_str(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.del(getKey(key));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public Long incr(String key, long value) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.incrBy(getKey(key).getBytes(), value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public Double incrByDouble(String key, double value) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.incrByFloat(getKey(key).getBytes(), value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public Long decr(String key, long value) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.decrBy(getKey(key).getBytes(), value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public Long rpush(String key, Object value) {
        if (StringUtils.isBlank(key) || value == null) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.rpush(getKey(key).getBytes(), SerializeUtil.serialize(value));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public Long rpush_str(String key, String value) {
        if (StringUtils.isBlank(key) || value == null) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.rpush(getKey(key), value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public Long lpush(String key, Object value) {
        if (StringUtils.isBlank(key) || value == null) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.lpush(getKey(key).getBytes(), SerializeUtil.serialize(value));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public Long lpush_str(String key, String value) {
        if (StringUtils.isBlank(key) || value == null) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.lpush(getKey(key), value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public <T> T lpop(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            byte[] result = jedis.lpop(getKey(key).getBytes());
            if (result != null && result.length > 0) {
                return SerializeUtil.unserialize(result);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public String lpop_str(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.lpop(getKey(key));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public <T> T rpop(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            byte[] result = jedis.rpop(getKey(key).getBytes());
            if (result != null && result.length > 0) {
                return SerializeUtil.unserialize(result);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public String rpop_str(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.rpop(getKey(key));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public List<byte[]> list(String key, long start, long end) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.lrange(getKey(key).getBytes(), start, end);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public List<String> list_str(String key, long start, long end) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.lrange(getKey(key), start, end);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public Long llen(String key) {
        if (StringUtils.isBlank(key)) {
            return 0L;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.llen(getKey(key));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return 0L;
    }

    public Long zadd(String key, String value, double score) {
        if (StringUtils.isBlank(key) || value == null) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zadd(getKey(key), score, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public Long zadd(String key, Map<String, Double> scoreMembers) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        if (scoreMembers == null || scoreMembers.size() == 0) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zadd(getKey(key), scoreMembers);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public long incr(String key, long value, int expire) {
        if (StringUtils.isBlank(key)) {
            return 0;
        }
        key = getKey(key);
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            long result = jedis.incrBy(key, value);
            if (expire > 0) {
                jedis.expire(key, expire);
            }
            return result;
        } catch (Exception e) {
            logger.error(key + "\n" + e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return 0;
    }

    @Override
    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    public Set<String> zrange(String key, long start, long end) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zrange(getKey(key), start, end);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public Set<String> zrangeByScore(String key, double min, double max) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zrangeByScore(getKey(key), min, max);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public Long zrem(String key, String... value) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        if (value == null || value.length == 0) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zrem(getKey(key), value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    /**
     * 获取长度
     */
    public Long zcard(String key) {
        if (StringUtils.isBlank(key)) {
            return 0L;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zcard(getKey(key));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return 0L;
    }

    /**
     * 清除
     */
    public Long zremrangeByRank(String key, long start, long end) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zremrangeByRank(getKey(key), start, end);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public Long hincrBy(String key, String field, long value) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(field)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hincrBy(getKey(key), field, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public Long hincrBy(String key, String field, long value, int expire) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(field)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Long result = jedis.hincrBy(getKey(key), field, value);
            if (expire > 0) {
                jedis.expire(getKey(key), expire);
            }
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public Long hset(String key, String field, Object value) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(field) || value == null) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hset(getKey(key).getBytes(), field.getBytes(), SerializeUtil.serialize(value));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public Long hset_str(String key, String field, String value) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(field) || StringUtils.isBlank(value)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hset(getKey(key), field, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public Long hdel_str(String key, String field) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(field)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hdel(getKey(key), field);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public Map<String, String> hget_str(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hgetAll(getKey(key));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public Map<byte[], byte[]> hget(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hgetAll(getKey(key).getBytes());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public String hget(String key, String field) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(field)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hget(getKey(key), field);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public Long sadd(String key, Object... members) {
        if (StringUtils.isBlank(key) || members == null || members.length == 0) {
            return null;
        }
        Jedis jedis = null;
        byte[][] ms = new byte[members.length][];
        try {
            for (int i = 0; i < members.length; i++) {
                ms[i] = SerializeUtil.serialize(members[i]);
            }
            jedis = jedisPool.getResource();
            return jedis.sadd(getKey(key).getBytes(), ms);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public Long sadd_str(String key, String... members) {
        if (StringUtils.isBlank(key) || members == null || members.length == 0) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.sadd(getKey(key), members);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    public Set<String> smembers_str(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.smembers(getKey(key));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    private String getKey(String key) {
        if (StringUtils.isBlank(prefix)) {
            return key;
        }
        return prefix + key;
    }

    public void setPrefix(String prefix) {
        if (StringUtils.isNotBlank(prefix)) {
            this.prefix = prefix.trim();
        }
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
}
