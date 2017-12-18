package myreflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * describe :
 * Created by jiadu on 2017/5/29 0029.
 */
public class MyFlect {
    public static void main(String[] args) {
        try {
            Class obj = Class.forName("cn.itcast.myreflect.TestEntity");
            Constructor constructor = obj.getConstructor(String.class,Integer.class);
            Object object = constructor.newInstance("dujia",30);
            Method privateMethod = obj.getDeclaredMethod("printName");
            privateMethod.setAccessible(true);
            privateMethod.invoke(object);


            Method publicMethod = obj.getDeclaredMethod("printAge");
            publicMethod.invoke(object);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
