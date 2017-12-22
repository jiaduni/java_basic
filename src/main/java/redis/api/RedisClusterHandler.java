package redis.api;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class RedisClusterHandler implements IRedisHandler {
    private Logger logger = LoggerFactory.getLogger(RedisClusterHandler.class);

    private final static String OK = "OK";
    private String prefix;
    private JedisCluster jedisCluster;

    public boolean put(String key, Object value, int expire) {
        if (StringUtils.isBlank(key) || value == null) {
            return false;
        }
        byte[] key_b = getKey(key).getBytes();
        try {
            String result = jedisCluster.set(key_b, SerializeUtil.serialize(value));
            if (expire > 0) {
                jedisCluster.expire(key_b, expire);
            }
            if (OK.equals(result)) {
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    public <T> T get(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        try {
            byte[] result = jedisCluster.get(getKey(key).getBytes());
            if (result != null && result.length > 0) {
                return SerializeUtil.unserialize(result);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            remove(key);
        }
        return null;
    }

    public Long remove(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        try {
            return jedisCluster.del(getKey(key).getBytes());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public boolean put_str(String key, String value, int expire) {
        if (StringUtils.isBlank(key) || value == null) {
            return false;
        }
        try {
            String result = jedisCluster.set(getKey(key), value);
            if (expire > 0) {
                jedisCluster.expire(getKey(key), expire);
            }
            if (OK.equals(result)) {
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    public String get_str(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        try {
            return jedisCluster.get(getKey(key));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            remove_str(key);
        }
        return null;
    }

    public Long remove_str(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        try {
            return jedisCluster.del(getKey(key));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public Long incr(String key, long value) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        try {
            return jedisCluster.incrBy(getKey(key).getBytes(), value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public Double incrByDouble(String key, double value) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        try {
            return jedisCluster.incrByFloat(getKey(key).getBytes(), value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public long incr(String key, long value, int expire) {
        if (StringUtils.isBlank(key)) {
            return 0;
        }
        try {
            long result = jedisCluster.incrBy(getKey(key).getBytes(), value);
            if (expire > 0) {
                jedisCluster.expire(key, expire);
            }
            return result;
        } catch (Exception e) {
            logger.error(key + "\n" + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public Jedis getJedis() {
//        Map<String, JedisPool> nodeMap = jedisCluster.getClusterNodes();
//        String anyHostAndPortStr = nodeMap.keySet().iterator().next();
//        TreeMap<Long, String> slotHostMap = getSlotHostMap(anyHostAndPortStr);
//        int slot = JedisClusterCRC16.getSlot(key);
//        //获取到对应的Jedis对象
//        Map.Entry<Long, String> entry = slotHostMap.lowerEntry(Long.valueOf(slot));
//        Jedis jedis = nodeMap.get(entry.getValue()).getResource();
        return null;
    }

    /**
     * 获取slot与host对应关系
     *
     * @param anyHostAndPortStr
     * @return
     */
    private static TreeMap<Long, String> getSlotHostMap(String anyHostAndPortStr) {
        TreeMap<Long, String> tree = new TreeMap<Long, String>();
        String parts[] = anyHostAndPortStr.split(":");
        HostAndPort anyHostAndPort = new HostAndPort(parts[0], Integer.parseInt(parts[1]));
        Jedis jedis = null;
        try {
            jedis = new Jedis(anyHostAndPort.getHost(), anyHostAndPort.getPort());
            List<Object> list = jedis.clusterSlots();
            for (Object object : list) {
                List<Object> list1 = (List<Object>) object;
                List<Object> master = (List<Object>) list1.get(2);
                String hostAndPort = new String((byte[]) master.get(0)) + ":" + master.get(1);
                tree.put((Long) list1.get(0), hostAndPort);
                tree.put((Long) list1.get(1), hostAndPort);
            }
        } catch (Exception e) {
//            logger.error("getSlotHostMap error",e);
        }
        return tree;
    }

    public Long decr(String key, long value) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        try {
            return jedisCluster.decrBy(getKey(key).getBytes(), value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public Long rpush(String key, Object value) {
        if (StringUtils.isBlank(key) || value == null) {
            return null;
        }
        try {
            return jedisCluster.rpush(getKey(key).getBytes(), SerializeUtil.serialize(value));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public Long rpush_str(String key, String value) {
        if (StringUtils.isBlank(key) || value == null) {
            return null;
        }
        try {
            return jedisCluster.rpush(getKey(key), value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public Long lpush(String key, Object value) {
        if (StringUtils.isBlank(key) || value == null) {
            return null;
        }
        try {
            return jedisCluster.lpush(getKey(key).getBytes(), SerializeUtil.serialize(value));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public Long lpush_str(String key, String value) {
        if (StringUtils.isBlank(key) || value == null) {
            return null;
        }
        try {
            return jedisCluster.lpush(getKey(key), value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public <T> T lpop(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        try {
            byte[] result = jedisCluster.lpop(getKey(key).getBytes());
            if (result != null && result.length > 0) {
                return SerializeUtil.unserialize(result);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public String lpop_str(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        try {
            return jedisCluster.lpop(getKey(key));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public <T> T rpop(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        try {
            byte[] result = jedisCluster.rpop(getKey(key).getBytes());
            if (result != null && result.length > 0) {
                return SerializeUtil.unserialize(result);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public String rpop_str(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        try {
            return jedisCluster.rpop(getKey(key));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public List<byte[]> list(String key, long start, long end) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        try {
            return jedisCluster.lrange(getKey(key).getBytes(), start, end);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public List<String> list_str(String key, long start, long end) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        try {
            return jedisCluster.lrange(getKey(key), start, end);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public Long llen(String key) {
        if (StringUtils.isBlank(key)) {
            return 0L;
        }
        try {
            return jedisCluster.llen(getKey(key));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return 0L;
    }

    public Long zadd(String key, String value, double score) {
        if (StringUtils.isBlank(key) || value == null) {
            return null;
        }
        try {
            return jedisCluster.zadd(getKey(key), score, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
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
        try {
            return jedisCluster.zadd(getKey(key), scoreMembers);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public Set<String> zrange(String key, long start, long end) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        try {
            return jedisCluster.zrange(getKey(key), start, end);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public Set<String> zrangeByScore(String key, double min, double max) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        try {
            return jedisCluster.zrangeByScore(getKey(key), min, max);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
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
        try {
            return jedisCluster.zrem(getKey(key), value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
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
        try {
            return jedisCluster.zcard(getKey(key));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
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
        try {
            return jedisCluster.zremrangeByRank(getKey(key), start, end);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public Long hincrBy(String key, String field, long value) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(field)) {
            return null;
        }
        try {
            return jedisCluster.hincrBy(getKey(key), field, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public Long hincrBy(String key, String field, long value, int expire) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(field)) {
            return null;
        }
        try {
            Long result = jedisCluster.hincrBy(getKey(key), field, value);
            if (expire > 0) {
                jedisCluster.expire(getKey(key), expire);
            }
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public Long hset(String key, String field, Object value) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(field) || value == null) {
            return null;
        }
        try {
            return jedisCluster.hset(getKey(key).getBytes(), field.getBytes(), SerializeUtil.serialize(value));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public Long hset_str(String key, String field, String value) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(field) || StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return jedisCluster.hset(getKey(key), field, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public Long hdel_str(String key, String field) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(field)) {
            return null;
        }
        try {
            return jedisCluster.hdel(getKey(key), field);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    public Map<String, String> hget_str(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        try {
            return jedisCluster.hgetAll(getKey(key));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public Map<byte[], byte[]> hget(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        try {
            return jedisCluster.hgetAll(getKey(key).getBytes());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public String hget(String key, String field) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(field)) {
            return null;
        }
        try {
            return jedisCluster.hget(getKey(key), field);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public Long sadd(String key, Object... members) {
        if (StringUtils.isBlank(key) || members == null || members.length == 0) {
            return null;
        }
        byte[][] ms = new byte[members.length][];
        try {
            for (int i = 0; i < members.length; i++) {
                ms[i] = SerializeUtil.serialize(members[i]);
            }
            return jedisCluster.sadd(getKey(key).getBytes(), ms);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public Long sadd_str(String key, String... members) {
        if (StringUtils.isBlank(key) || members == null || members.length == 0) {
            return null;
        }
        try {
            return jedisCluster.sadd(getKey(key), members);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public Set<String> smembers_str(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        try {
            return jedisCluster.smembers(getKey(key));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
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

    public void setJedisCluster(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }


}
