package designMode.single;

/**
 * describe : 单例模式  线程安全  并且效率高
 * Created by jiadu on 2017/10/21 0021.
 */
public class Singleton {

    private static Singleton instance;

    private Singleton() {

    }

    public static Singleton getInstance() {


        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
