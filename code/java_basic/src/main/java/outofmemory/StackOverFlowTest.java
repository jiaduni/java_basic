package outofmemory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * describe : 测试虚拟机栈的内存错误  设置 vm： -Xss128k
 * Created by jiadu on 2017/10/14 0014.
 */
public class StackOverFlowTest {

    private int stacklength = 1;

    public void stackLeak() {
        stacklength++;
        stackLeak();
    }

    public static void main(String[] args) throws Throwable {
        StackOverFlowTest stackOverFlowTest = new StackOverFlowTest();
        try {
            stackOverFlowTest.stackLeak();
        } catch (Throwable e) {
            System.out.println("stack length:" + stackOverFlowTest.stacklength);
            throw e;
        }

        AtomicInteger race = new AtomicInteger(0);
        race.incrementAndGet();
    }
}
