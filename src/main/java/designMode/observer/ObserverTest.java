package designMode.observer;

/**
 * describe : 观察者模式（又叫发布-订阅模式）
 * 解释：定义了一种一对多的依赖关系，让多个观察者对象同时监听某一个主题对象，这个主题对象在状态发生变化时，
 * 会通知所有观察者，使它们能自己更新自己
 * Created by jiadu on 2017/10/26 0026.
 */
public class ObserverTest {
    public static void main(String[] args) {
        ConcreteSubject concreteSubject = new ConcreteSubject();

        CartoonObserver cartoonObserver = new CartoonObserver();
        NBAObserver nbaObserver = new NBAObserver();

        concreteSubject.addObserver(cartoonObserver);
        concreteSubject.addObserver(nbaObserver);

        concreteSubject.notifyAllObserver("老师来了");

    }
}
