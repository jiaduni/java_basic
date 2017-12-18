package myreflect;

/**
 * describe :
 * Created by jiadu on 2017/5/29 0029.
 */
public class TestEntity {

    private String name;
    private Integer age;


    public TestEntity(){
        super();
    }

    public TestEntity(String name,Integer age){
        super();
        this.name=name;
        this.age=age;
    }

    private void printName(){
        System.out.println(this.name);
    }

    public void printAge(){
        System.out.println(this.age);
    }
}
