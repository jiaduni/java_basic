package designMode.decorator;

/**
 * describe :
 * Created by jiadu on 2017/10/21 0021.
 */
public class Finey extends Person {

    protected Person component;

    public void decorate(Person pc) {
        component = pc;
    }

    @Override
    public void show() {
        if (component != null) {
            component.show();
        }
    }
}
