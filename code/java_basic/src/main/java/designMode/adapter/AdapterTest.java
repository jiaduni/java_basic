package designMode.adapter;

/**
 * describe : 适配器模式
 *       解释：将一个类的接口转换成客户希望的另外一个接口，就是使得原本由于接口不兼容
 *             而不能一起工作的那些类一起工作
 * Created by jiadu on 2017/10/24 0024.
 */
public class AdapterTest {

    public static void main(String[] args) {
        Target target = new Adapter();
        target.request();
    }
}
