package designMode.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * describe : 具体的通知者
 * Created by jiadu on 2017/10/26 0026.
 */
public class ConcreteSubject implements Subject {

    List<Observer> mList = new ArrayList<Observer>();

    @Override
    public void addObserver(Observer observer) {
        // 确保相同的观察者只含有一个
        if (observer == null) {
            throw new NullPointerException("observer == null");
        }

        if (!mList.contains(observer)) {
            mList.add(observer);
        }
    }

    @Override
    public void removeObeserver(Observer observer) {
        mList.remove(observer);
    }

    @Override
    public void removeAll() {
        mList.clear();
    }

    @Override
    public void notifyAllObserver(Object data) {
        for (Observer observer : mList) {
            observer.update(this, data);
        }
    }

    @Override
    public void notify(Observer observer, Object data) {
        if (observer != null) {
            observer.update(this, data);
        }
    }
}
