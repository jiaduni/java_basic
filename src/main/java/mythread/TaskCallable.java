package mythread;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * describe :
 * Created by jiadu on 2017/5/28 0028.
 */
public class TaskCallable implements Callable<String> {
    private static Lock lock = new ReentrantLock();
    private String key;

    public TaskCallable(String key) {
        this.key = key;

    }

    public String call() throws InterruptedException {
        boolean il = lock.tryLock(2000, TimeUnit.SECONDS);
        StringBuffer serialNumber = new StringBuffer();
        try {
            if(il){
                serialNumber.append(key);
                serialNumber.append(System.nanoTime());
                System.out.println("获取锁"+","+serialNumber.toString() + ","+Thread.currentThread().getName());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
            System.out.println("释放锁");
        }
        return serialNumber.toString() + ","+Thread.currentThread().getName();
    }
}
