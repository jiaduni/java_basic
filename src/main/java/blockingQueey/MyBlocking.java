package blockingQueey;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * describe :
 * Created by jiadu on 2017/5/28 0028.
 */
public class MyBlocking {

    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    static final BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<Integer>(100000);

    public static void main(String[] args) throws InterruptedException {

        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 100000; i++) {
            final int fi = i;
            executorService.execute(new Runnable() {
                public void run() {
                    try {
                        lock.writeLock().lock();
                        blockingQueue.add(fi);
                        System.out.println("放入第：" + (fi) + "个数据");
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        lock.writeLock().unlock();
                    }
                }
            });
        }
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        while (true) {
            threadPool.execute(new Runnable() {
                public void run() {
                    try {
                        lock.readLock().lock();
                        Integer theTake = null;
                        theTake = blockingQueue.poll(1000, TimeUnit.SECONDS);
                        System.out.println("获取到：" + theTake);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        lock.readLock().unlock();
                    }
                }
            });
        }
    }
}
