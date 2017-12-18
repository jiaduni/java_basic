package designMode.responsibility;

/**
 * describe : 具体处理类，处理他能负责的请求，如果处理不了则把该请求给后继者
 * Created by jiadu on 2017/10/24 0024.
 */
public class ConcreteHandler2 extends Handler {
    @Override
    public void handleRequest(int request) {
        if (request >= 10 && request < 20) {
            System.out.println("ConcreteHandler2 已处理该请求:"+request);
        }else {
            if (successer == null) {
                System.out.println("无人能处理该请求！");
            }else {
                successer.handleRequest(request);
            }
        }
    }
}
