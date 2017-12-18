package designMode.decorator;

/**
 * describe : 装饰模式
 *       解释：动态地给一个对象添加一些额外的职责。
 * Created by jiadu on 2017/10/21 0021.
 */
public class DecoratorTest {

    public static void main(String[] args) {
        Person xc = new Person("小菜");

        Tshirts tshirts = new Tshirts();
        BigTrouser bigTrouser = new BigTrouser();
        Shoes shoes = new Shoes();

        tshirts.decorate(xc);
        bigTrouser.decorate(tshirts);
        shoes.decorate(bigTrouser);
        shoes.show();
    }
}
