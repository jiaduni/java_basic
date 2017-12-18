package designMode.observer;

/**
 * describe :
 * Created by jiadu on 2017/10/26 0026.
 */
public class CartoonObserver implements Observer{
    @Override
    public void update(Subject subject, Object data) {
        System.out.println( " 我是"+this.getClass().
                getSimpleName()+"，  "+data+"别看漫画了");
    }
}
