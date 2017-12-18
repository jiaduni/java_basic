package designMode.template;

/**
 * describe : 模版模式
 * Created by jiadu on 2017/10/24 0024.
 */
public class TemplateTest {

    public static void main(String[] args) {
        AstractClass a = new ConcreteClassA();
        a.templateMethod();

        AstractClass b = new ConcreteClassB();
        b.templateMethod();
    }
}
