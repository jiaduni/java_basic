package redis.api;

import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IRedisHandler{
    
    public boolean put(String key, Object value, int expire);
    
    public <T> T get(String key);
    
    public Long remove(String key);
    
    public boolean put_str(String key, String value, int expire);
    
    public String get_str(String key);
    
    public Long remove_str(String key);
    
    public Long incr(String key, long value);
    
    public Double incrByDouble(String key, double value);
    
    public Long decr(String key, long value);
    
    public Long rpush(String key, Object value) ;
    
    public Long rpush_str(String key, String value);
    
    public Long lpush(String key, Object value);
    
    public Long lpush_str(String key, String value);
    
    public <T> T lpop(String key);
    
    public String lpop_str(String key);
    
    public <T> T rpop(String key);
    
    public String rpop_str(String key);
    
    public List<byte[]> list(String key, long start, long end);
    
    public List<String> list_str(String key, long start, long end);
    
    public Long llen(String key);
    
    public Long zadd(String key, String value, double score);
    
    public Long zadd(String key, Map<String, Double> scoreMembers);
    
    public Set<String> zrange(String key, long start, long end);
    
    public Set<String> zrangeByScore(String key, double min, double max);
    
    public Long zrem(String key, String... value);
    
    public Long zcard(String key);
    
    public Long zremrangeByRank(String key, long start, long end);
    
    public Long hincrBy(String key, String field, long value);
    
    public Long hincrBy(String key, String field, long value, int expire);
    
    public Long hset(String key, String field, Object value);
    
    public Long hset_str(String key, String field, String value);
    
    public Long hdel_str(String key, String field);
    
    public Map<String, String> hget_str(String key);
    
    public Map<byte[],byte[]> hget(String key);
    
    public String hget(String key, String field);
    
    public Long sadd(String key, Object... members);
    
    public Long sadd_str(String key, String... members);
    
    public Set<String> smembers_str(String key);

    public long incr(String key, long value, int expire);

    public Jedis getJedis();
    
}
