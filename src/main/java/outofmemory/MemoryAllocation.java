package outofmemory;

/**
 * describe : 对象一般分配到新生代的Eden区
 *             配置  -verbose:gc -Xms20m -Xmx20m -Xmn10m -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:+UseSerialGC
 * Created by jiadu on 2017/10/16 0016.
 */
public class MemoryAllocation {

    private static final int _1m = 1024 * 1024;

    public static void main(String[] args) {
        byte[] allocation1, allocation2, allocation3, allocation4;
        allocation1 = new byte[2 * _1m];
        allocation2 = new byte[2 * _1m];
        allocation3 = new byte[2 * _1m];
        allocation4 = new byte[2 * _1m];

    }
}
