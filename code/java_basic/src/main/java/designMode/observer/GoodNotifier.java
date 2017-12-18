package designMode.observer;

/**
 * describe : 具体通知者
 * Created by jiadu on 2017/10/26 0026.
 */
public class GoodNotifier extends Notifier {

    @Override
    public void addListener(Object object, String methodName, Object... args) {
        System.out.println("有新的同学委托尽职尽责的放哨人!");
        addEvent(object, methodName, args);
    }

}
