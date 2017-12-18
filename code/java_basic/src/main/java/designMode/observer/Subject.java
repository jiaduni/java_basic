package designMode.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * describe : 抽象通知者
 * Created by jiadu on 2017/10/26 0026.
 */
public interface Subject {

    /**
     * 添加观察者
     * @param observer
     */
    void addObserver(Observer observer);

    /**
     * 移除观察者
     * @param observer
     */
    void removeObeserver(Observer observer);

    /**
     * 移除所有观察者
     */
    void removeAll();

    /**
     * data 是要通知给观察者的数据
     * 因为Object是所有类的父类，可以使用多态，当然 你也可以使用 泛型
     * @param data
     */
    void notifyAllObserver(Object data);

    /**
     * 单独 通知某一个观察者
     * @param observer
     * @param data
     *  data 是要通知给观察者的数据
     * 因为Object是所有类的父类，可以使用多态，当然 你也可以使用 泛型
     */
    void notify(Observer observer,Object data);
}
