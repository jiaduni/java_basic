package myproxy;



import myproxy.impl.Boss;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * describe : jdk动态代理
 * Created by jiadu on 2017/5/29 0029.
 */
public class Myproxy {
    public static void main(String[] args) {
        IBoss result = (IBoss) Proxy.newProxyInstance(Boss.class.getClassLoader(), Boss.class.getInterfaces(), new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Integer re = (Integer) method.invoke(Boss.class.newInstance(), args);
                return re - 100;
            }
        });
        System.out.println(result.price());
    }
}
