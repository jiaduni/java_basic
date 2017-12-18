package mythread;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * describe :
 * Created by jiadu on 2017/5/28 0028.
 */
public class MythreadTwo {

    private static Lock lock = new ReentrantLock();
    static ExecutorService executor = Executors.newCachedThreadPool();
    private static Integer count = 0;

    @Test
    public void testLock() throws InterruptedException {
        final List<Integer> is = new ArrayList<Integer>();
        for (int i = 0; i < 100; i++) {
            final int finalI = i;
            executor.execute(new Runnable() {
                public void run() {
                    lock.lock();
                    System.out.println("获取锁");
                    try {
                        count = count + 1;
                        is.add(finalI);
                        System.out.println("i==="+ finalI +",count="+count+",当前线程："+Thread.currentThread().getName());
                    } catch (Exception e) {

                    } finally {
                        lock.unlock();
                        System.out.println("释放锁");
                    }
                }
            });
        }
//        CyclicBarrier barrier = new CyclicBarrier(3);线程墙
//        barrier.await();
        System.out.println("主线程结束");
        Thread.currentThread().sleep(1000);//主线程一结束，线程池就没有了(在main方法中无此问题)
        Collections.sort(is);
        System.out.println(is.size());
    }

    @Test
    public void testSyn() throws InterruptedException {
        final List<Integer> is = new ArrayList<Integer>();
        for (int i = 0; i < 100; i++) {
            final int finalI = i;
            executor.execute(new Runnable() {
                public void run() {
                    synchronized (count){
                        count = count + 1;
                        is.add(finalI);
                        System.out.println("i==="+ finalI +",count="+count+",当前线程："+Thread.currentThread().getName());
                    }
                }
            });
        }
        System.out.println("主线程结束");
        Thread.currentThread().sleep(2000);
        Collections.sort(is);
        System.out.println(is.size());
    }


//    public static void main(String[] args) {
//        final List<Integer> is = new ArrayList<Integer>();
//        for (int i = 0; i < 100; i++) {
//            final int finalI = i;
//            executor.execute(new Runnable() {
//                public void run() {
//                    lock.lock();
//                    System.out.println("获取锁");
//                    try {
//                        count = count + 1;
//                        is.add(finalI);
//                        System.out.println("i===" + finalI + ",count=" + count + ",当前线程：" + Thread.currentThread().getName());
//                    } catch (Exception e) {
//
//                    } finally {
//                        lock.unlock();
//                        System.out.println("释放锁");
//                    }
//                }
//            });
//        }
//        System.out.println("主线程结束");
////        Thread.currentThread().sleep(1000);//主线程一结束，线程池就没有了
////        Collections.sort(is);
////        System.out.println(is.size());
//    }
}
