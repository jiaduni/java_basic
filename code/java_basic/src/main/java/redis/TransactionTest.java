package redis;

import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;

import java.util.List;

/**
 * describe :
 * Created by jiadu on 2017/11/5 0005.
 */
public class TransactionTest {
    static JedisShardInfo jedisShardInfo = null;
    static Jedis jedis = null;


    public static void init() {
        if (jedis == null) {
            jedisShardInfo = new JedisShardInfo("47.92.66.13", 6379);
            jedisShardInfo.setPassword("123456");
            jedis = jedisShardInfo.createResource();
        }
    }

    /**
     * 回滚测试
     */
    @Test
    public void testTransaction() {
        init();
        //---exec 执行事务队列内命令-------------  
        Transaction t = jedis.multi();//开始事务
        t.set("husband", "Tom");
        t.set("wife", "Mary");
        t.exec();//执行事务  


        //------discard 取消执行事务内命令---------  
        Transaction t2 = jedis.multi();
        t2.set("test", "0");
        t2 = jedis.multi();
        t2.set("test", "1");
        t2.discard();

        String husband = jedis.get("husband");
        String wife = jedis.get("wife");
        String test = jedis.get("test");
        System.out.println("husband:" + husband);
        System.out.println("wife:" + wife);
        System.out.println("test:" + test);  //null 原因：开启事务后未提交，则无结果  
        jedis.close();

    }

    /**
     * 测试watch
     *
     * @throws Exception
     */
    @Test
    public void testWatch() throws Exception {
        init();
        jedis.set("caicongyang", "goodboy");
        jedis.watch("caicongyang");
        Thread.sleep(3000L);
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("进入线程！");
                JedisShardInfo jedisShardInfo = new JedisShardInfo("47.92.66.13", 6379);
                jedisShardInfo.setPassword("123456");
                Jedis jedis1 = jedisShardInfo.createResource();
                Transaction multi = jedis1.multi();
                multi.set("caicongyang", "good");
                try {
                    List<Object> list = multi.exec();
                    System.out.println(list.get(0).toString() + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"); //NullPointerExecption
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(jedis1.get("caicongyang") + "==========================");//结果是：boy
                jedis1.close();
                System.out.println("离开线程！");
            }
        }).start();
        //未执行上面的事务;原因:我们调用jedis.watch(…)方法来监控key，如果调用后key值发生变化，则整个事务不会执行。
        Transaction multi = jedis.multi();
        multi.set("caicongyang", "boy");
        List<Object> list = multi.exec();
        //事务结果：异常应该上面的提交没有执行  
        System.out.println(list.get(0).toString() + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"); //NullPointerExecption
        String result = jedis.get("caicongyang");
        System.out.println(result); //结果是：boy  
    }

    /**
     * 持久化测试
     */
    @Test
    public void testPersis() {
        init();
        jedis.set("ccy", "handsome boy");
        jedis.persist("ccy");

        //重新启动机器依然存在  
        String value = jedis.get("ccy");
        System.out.println(value);
        jedis.close();
    }


    /**
     * 测试管道
     */
    @Test
    public void testPipelined() {
        init();
        Pipeline pipeline = jedis.pipelined();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            pipeline.set("p" + i, "p" + i);
        }
        //异步返回结果
        List<Object> results = pipeline.syncAndReturnAll();
        long end = System.currentTimeMillis();
        System.out.println("Pipelined SET: " + ((end - start) / 1000.0) + " seconds"); //10000次插入仅需不到1秒
        System.out.println(results.size());
        jedis.disconnect();


    }
}
