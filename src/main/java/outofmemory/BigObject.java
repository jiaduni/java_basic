package outofmemory;

/**
 * describe : 大对象直接放入老年代，主要是为了避免 在Eden区和两个survivor区之间发生大量的内存复制
 * （主要是由于新生代是收集内存用的是复制算法）
 * <p>
 * 配置  (把大于3m的对象直接放入老年代，PretenureSizeThreshold只有Serial和ParNew两款收集器有效)
 * -verbose:gc -Xms20m -Xmx20m -Xmn10m -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:+UseSerialGC
 * -XX:PretenureSizeThreshold=3145728
 * Created by jiadu on 2017/10/16 0016.
 */
public class BigObject {

    private static final int _1m = 1024 * 1024;

    public static void main(String[] args) {
        byte[] allocation;
        allocation = new byte[4 * _1m];
    }
}
