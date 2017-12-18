package mythread;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * describe :
 * Created by jiadu on 2017/5/28 0028.
 */
public class Mythread {

    private static Lock lock = new ReentrantLock();
    Future<?> submit = null;

    @Test
    public void testLock() throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<?>> futures = new ArrayList<Future<?>>();
        int count =0;
        for (int i = 0; i < 100; i++) {
            submit = executor.submit(new TaskCallable("hh"));
            count++;
            futures.add(submit);
        }
        System.out.println("count============================================================="+count);
//        for (Future future : futures) {
//            boolean falg = future.isDone();
//            if (falg) {
//                try {
//                    System.out.println(future.get());
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        executor.shutdown();
    }
}
