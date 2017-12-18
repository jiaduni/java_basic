package designMode.bridging;

/**
 * describe : 桥接模式 (没理解到)
 *           将抽象部分与它的实现部分分离，是他们能够独立的变化
 * Created by jiadu on 2017/10/24 0024.
 */
public class BridgingTest {

    public static void main(String[] args) {
        Abstraction ab = new Abstraction();

        ab.setImpementor(new ConcreteImpementorA());
        ab.operation();

        ab.setImpementor(new ConcreteImpementorB());
        ab.operation();
    }
}
