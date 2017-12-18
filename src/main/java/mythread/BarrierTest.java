package mythread;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author dujia
 * @version 2017年12月20日  15:26
 */
public class BarrierTest {
    static CyclicBarrier barrier = new CyclicBarrier(3);
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.submit(new Thread(new Runner("一号选手")));
        executor.submit(new Thread(new Runner("二号选手")));
        executor.submit(new Thread(new Runner("三号选手")));
        executor.shutdown();
    }
}

class Runner implements Runnable{

    private String name;

    public Runner(String name){
        this.name=name;
    }

    @Override
    public void run() {
        System.out.println(name+" 准备好了");
        try {
            BarrierTest.barrier.await();
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(name+" 起跑！同时起跑时间为："+System.currentTimeMillis());
    }
}
