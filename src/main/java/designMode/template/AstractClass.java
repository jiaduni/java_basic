package designMode.template;

/**
 * describe : 模版模式
 * Created by jiadu on 2017/10/24 0024.
 */
abstract class AstractClass {

    public abstract void primitiveOperation1();
    public abstract void primitiveOperation2();

    public void templateMethod(){
        primitiveOperation1();
        primitiveOperation2();
        System.out.println("调用模版！");
    }

}
