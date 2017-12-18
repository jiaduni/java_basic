package myproxy;

import myproxy.impl.Boss;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * describe :
 * Created by jiadu on 2017/11/1 0001.
 */
public class CJLibProxy {

    public static void main(String[] args) {
        Mycglibproxy mycglibproxy = new Mycglibproxy();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Boss.class);
        enhancer.setCallback(mycglibproxy);

        IBoss iBoss = (IBoss) enhancer.create();
        System.out.println(iBoss.price());
    }
}

class Mycglibproxy  implements MethodInterceptor {

    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("++++++before " + methodProxy.getSuperName() + "++++++");
        System.out.println(method.getName());
        Object o1 = methodProxy.invokeSuper(o, objects);
        System.out.println("++++++after " + methodProxy.getSuperName() + "++++++");
        return o1;
    }
}
