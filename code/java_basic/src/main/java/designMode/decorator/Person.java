package designMode.decorator;

/**
 * describe : 装饰模式
 * Created by jiadu on 2017/10/21 0021.
 */
public class Person {

    private String name;

    public Person() {
    }

    public Person(String name) {
        this.name = name;
    }

    public void show() {
        System.out.printf("装扮的" + name);
    }
}
