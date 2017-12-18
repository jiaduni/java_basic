package designMode.adapter;

/**
 * describe : 通过内部包装一个adaptee对象，把源接口转换成目标接口
 * Created by jiadu on 2017/10/24 0024.
 */
public class Adapter extends Target {
    private Adaptee adaptee = new Adaptee();

    @Override
    public void request() {
        adaptee.specificRequest();
    }
}
