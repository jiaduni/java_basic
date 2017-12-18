package designMode.responsibility;

/**
 * describe :
 * Created by jiadu on 2017/10/24 0024.
 */
abstract class Handler {

    protected Handler successer;//设置继承者

    public void setSuccesser(Handler successer) {
        this.successer = successer;
    }

    public abstract void handleRequest(int request);//处理请求的抽象方法
}
