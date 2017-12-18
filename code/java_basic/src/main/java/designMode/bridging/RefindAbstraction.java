package designMode.bridging;

/**
 * describe :
 * Created by jiadu on 2017/10/24 0024.
 */
public class RefindAbstraction extends Abstraction {
    @Override
    public void operation() {
        impementor.operation();
    }
}
