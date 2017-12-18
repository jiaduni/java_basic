package zookeeper;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.junit.Test;

import java.util.List;

/**
 * @author dujia
 * @version 2017年12月20日  09:26
 */
public class ZkclientTest {

    private static ZkClient zkClient;

    private static String createBasePath = "/zkClient-test";

    private static ZkClient getZkInstance() {
        if (zkClient == null) {
            synchronized (ZkclientTest.class) {
                if (zkClient == null) {
                    zkClient = new ZkClient("localhost:2181", 5000);//链接地址和超时时间
                }
            }
        }
        return zkClient;
    }

    //创建持久节点，并且自动创建父节点
    @Test
    public void createPersistent() {
        getZkInstance().createPersistent(createBasePath + "/c1", true);
    }

    //删除节点(递归删除)
    @Test
    public void delete() {
        System.out.println(getZkInstance().getChildren(createBasePath));
//        getZkInstance().deleteRecursive("");
    }

    //监听子节点
    @Test
    public void listener() throws InterruptedException {
        ZkClient zkClient = getZkInstance();
        String path = createBasePath + "yyyy";
        zkClient.subscribeChildChanges(path, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentpath, List<String> currentChilds) throws Exception {
                System.out.println("parentpath:" + parentpath + ",currentChilds:" + currentChilds);
            }
        });
        zkClient.createPersistent(path);
        Thread.sleep(1000);
        System.out.println(zkClient.getChildren(path));
        zkClient.createPersistent(path + "/c2");
        Thread.sleep(1000);
        zkClient.delete(path + "/c2");
        Thread.sleep(1000);
        zkClient.delete(path);
        Thread.sleep(Integer.MAX_VALUE);
    }

    //监听数据内容
    @Test
    public void listenerData() throws InterruptedException {
        ZkClient zkClient = getZkInstance();
        String path = "/zk-book";
        zkClient.createEphemeral(path, "123");
        zkClient.subscribeDataChanges(path, new IZkDataListener() {
            @Override
            public void handleDataChange(String datapath, Object data) throws Exception {
                System.out.println("node " + datapath + " changed,new Data:" + data);
            }

            @Override
            public void handleDataDeleted(String datapath) throws Exception {
                System.out.println("node " + datapath + " deleted");
            }
        });
        System.out.println(zkClient.readData(path) + "");
        zkClient.writeData(path, "789");
        Thread.sleep(1000);
        zkClient.delete(path);
        Thread.sleep(Integer.MAX_VALUE);
    }

    //检测节点是否存在
    @Test
    public void exists() {
        System.out.println(getZkInstance().exists(createBasePath));
    }
}
