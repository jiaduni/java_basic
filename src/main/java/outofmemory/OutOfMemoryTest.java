package outofmemory;

import java.util.ArrayList;
import java.util.List;

/**
 * describe : 测是堆内存溢出错误，设置vm为 -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=E:\jvmDump
 * Created by jiadu on 2017/10/14 0014.
 */
public class OutOfMemoryTest {

    static class OOMObject{

    }

    public static void main(String[] args) {
        List<OOMObject> oomObjects = new ArrayList<OOMObject>();
        while (true){
            oomObjects.add(new OOMObject());
        }
    }

}
