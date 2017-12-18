package designMode.observer;

/**
 * describe : 抽象观察者
 * Created by jiadu on 2017/10/26 0026.
 */
public interface Observer {
    /**
     *
     * @param subject 被观察者
     * @param data    被观察者传递给观察者的 数据
     */
    void update(Subject subject,Object data);
}
