package zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author dujia
 * @version 2017年12月20日  10:32
 */
public class CutatorTest {

    private CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181", 5000, 3000, new ExponentialBackoffRetry(1000, 3));

    /**
     * 获取链接
     *
     * @throws InterruptedException
     */
    public CuratorFramework getZkInstance() throws InterruptedException {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);//初始sleep时间，最大重试次数
        CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181", 5000, 3000, retryPolicy);
//        client.start();
//        client.usingNamespace("base");//使用默认根目录
//        System.out.println("启动成功>>>>>>>>>>>>>>>>>>>>>>");
//        Thread.sleep(Integer.MAX_VALUE);
        return client;
    }

    /**
     * 创建一个临时节点并添加数据,断开连接后就会自动删除
     *
     * @throws Exception
     */
    @Test
    public void create() throws Exception {
        client.start();
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/cutatorTest", "init".getBytes());
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 删除节点
     */
    @Test
    public void delete() throws Exception {
        client.start();
        client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/deleTest", "delete".getBytes());
        System.out.println("创建成功");
        Thread.sleep(1000);
//        client.delete().forPath()
//        client.delete().guaranteed()//保证删除
        Stat stat = new Stat();
        System.out.println(new String(client.getData().storingStatIn(stat).forPath("/deleTest")));
        client.delete().deletingChildrenIfNeeded().withVersion(stat.getVersion()).forPath("/deleTest");//递归删除
        System.out.println("删除成功");
    }

    /**
     * 读取数据
     */
    @Test
    public void read() throws Exception {
        client.start();
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/readTest", "read".getBytes());
        Stat stat = new Stat();
        System.out.println(new String(client.getData().storingStatIn(stat).forPath("/readTest")));
    }

    /**
     * 更新数据
     */
    @Test
    public void setData() throws Exception {
        client.start();
        String path = "/updateTest";
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, "update".getBytes());
        Stat stat = new Stat();
        client.getData().storingStatIn(stat).forPath(path);
        System.out.println("Success set node for :" + path + ",new version :" +
                client.setData().withVersion(stat.getVersion()).forPath(path, "to".getBytes()).getAversion());
        try {
            client.setData().withVersion(stat.getVersion()).forPath(path);
        } catch (Exception e) {
            System.out.println("Fail set node due to " + e.getMessage());
        }
    }

    /**
     * 异步处理,返回code:0.成功 -4.链接断开 -110.节点已存在 -112.会话已过期
     */
    @Test
    public void sync() throws Exception {
        CountDownLatch semaphore = new CountDownLatch(2);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        client.start();
        System.out.println("Main thread :" + Thread.currentThread().getName());
        String path = "/syncTest";
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                System.out.println("Event[code:" + curatorEvent.getResultCode() + ",type:" + curatorEvent.getType() + "]");
                System.out.println("Thread processResult:" + Thread.currentThread().getName());
                semaphore.countDown();
            }
        }, executorService).forPath(path, "init".getBytes());//传入线程

        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                System.out.println("Event[code:" + curatorEvent.getResultCode() + ",type:" + curatorEvent.getType() + "]");
                System.out.println("Thread processResult:" + Thread.currentThread().getName());
                semaphore.countDown();
            }
        }).forPath(path, "init".getBytes());

        semaphore.await();

        executorService.shutdown();
    }

    /**
     * 节点监听
     */
    @Test
    public void nodeCacheListener() throws Exception {
        client.start();
        String path = "/nodeCacheTest";
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, "nodeCache".getBytes());
        NodeCache cache = new NodeCache(client, path, false);//不进行压缩
        cache.start(true);
        cache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("Node data update ,new Data:" + new String(cache.getCurrentData().getData()));
            }
        });
        client.setData().forPath(path, "u".getBytes());
        Thread.sleep(1000);
        client.delete().deletingChildrenIfNeeded().forPath(path);
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 子节点监听
     */
    @Test
    public void pathChildenListener() throws Exception {
        client.start();
        String path = "/pathChilden";
        PathChildrenCache cache = new PathChildrenCache(client, path, true);
        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
                switch (event.getType()) {
                    case CHILD_ADDED:
                        System.out.println("CHILD_ADDED," + event.getData().getPath());
                        break;
                    case CHILD_UPDATED:
                        System.out.println("CHILD_UPDATED," + event.getData().getPath());
                        break;
                    case CHILD_REMOVED:
                        System.out.println("CHILD_REMOVED," + event.getData().getPath());
                        break;
                }
            }
        });
        client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
        Thread.sleep(1000);
        client.create().withMode(CreateMode.EPHEMERAL).forPath(path + "/c1");
        Thread.sleep(1000);
        client.delete().forPath(path + "/c1");
        Thread.sleep(1000);
        client.delete().forPath(path);
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * master选举
     */
    @Test
    public void master() throws InterruptedException {
        client.start();
        String path = "/master";
        LeaderSelector selector = new LeaderSelector(client, path, new LeaderSelectorListenerAdapter() {
            @Override
            public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
                System.out.println("成为Master角色");
                Thread.sleep(3000);
                System.out.println("完成master操作，释放Master权利");
            }
        });
        selector.autoRequeue();
        selector.start();
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 分布式锁
     */
    @Test
    public void lock() throws InterruptedException {
        client.start();
        String path = "/lock";
        InterProcessMutex lock = new InterProcessMutex(client, path);
        CountDownLatch down = new CountDownLatch(1);
        for (int i = 0; i < 30; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        down.await();
                        lock.acquire();//获取锁
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
                    String orderNo = sdf.format(new Date());
                    System.out.println("生成的订单号是：" + orderNo);

                    try {
                        lock.release();//释放锁
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        down.countDown();
        Thread.sleep(3000);
    }

    /**
     * 分布式计数器
     */
    @Test
    public void count() throws Exception {
        client.start();
        DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(client, "/count", new RetryNTimes(3, 1000));
        AtomicValue<Integer> atomicValue = atomicInteger.add(1);
        System.out.println("Result:" + atomicValue.succeeded());
        System.out.println("data:" + atomicValue.postValue());
    }

    static DistributedBarrier barrier;

    /**
     * 分布式barrier
     */
    @Test
    public void barrier() throws Exception {
        String path = "/barrier";
        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        CuratorFramework client = getZkInstance();
                        client.start();
                        barrier = new DistributedBarrier(client, path);
                        System.out.println(Thread.currentThread().getName() + " 号barrier设置");
                        barrier.setBarrier();
                        barrier.waitOnBarrier();
                        System.out.println(Thread.currentThread().getName() + " 启动...");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        Thread.sleep(2000);
        barrier.removeBarrier();
        Thread.sleep(2000);//不睡眠的话，主线程会结束
    }

}
