package designMode.responsibility;

/**
 * describe : 职责链请求，有点像流程
 *           解释：使多个对象都有机会处理请求，从而避免请求的发送者和接受者之间的耦合关系，
 *                 将这个对象连成一条链，并沿着这条链传递该请求，直到有一个对象处理它为止
 * Created by jiadu on 2017/10/24 0024.
 */
public class ResponsibilityTest {
    public static void main(String[] args) {
        Handler h1 = new ConcreteHandler1();
        Handler h2 = new ConcreteHandler2();
        Handler h3 = new ConcreteHandler3();

        h1.setSuccesser(h2);
        h2.setSuccesser(h3);

        h1.handleRequest(20);
    }
}
