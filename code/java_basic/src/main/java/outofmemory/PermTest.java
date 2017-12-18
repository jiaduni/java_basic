package outofmemory;

import java.util.ArrayList;
import java.util.List;

/**
 * describe : 测试方法区内存异常 设置为
 * jdk 1.7设置为：-XX:PermSize=10M -XX:MaxPermSize=10M
 * jdk 1.8设置为：-XX:MetaspaceSize=8m -XX:MaxMetaspaceSize=8m
 * Created by jiadu on 2017/10/14 0014.
 */
public class PermTest {

    public static void main(String[] args) {
        List<String> list = new ArrayList<String>();
        int i = 0;
        while (true) {
            list.add(String.valueOf(i++).intern());
        }
    }
}
