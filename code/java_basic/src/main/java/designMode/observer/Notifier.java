package designMode.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * describe : 抽象通知者
 * Created by jiadu on 2017/10/26 0026.
 */
abstract class Notifier {
    //是用一个List
    private List<Event> objects = new ArrayList<Event>();

    /**
     * 增加需要帮忙 放哨 的 学生
     *
     * @param object 要执行方法的对象
     * @param methodName 执行方法 的方法名
     * @param args   执行方法的参数
     */
    public abstract void addListener(Object object, String methodName,
                                     Object... args);


    //添加某个对象要执行的事件，及需要的参数
    public void addEvent(Object object, String methodName, Object... args) {
        objects.add(new Event(object, methodName, args));
    }
    /**
     * 告诉所有要帮忙放哨的学生：老师来了
     */
    public void notifyX() throws Exception {
        for (Event e : objects) {
            e.invoke();
        }
    }
}
