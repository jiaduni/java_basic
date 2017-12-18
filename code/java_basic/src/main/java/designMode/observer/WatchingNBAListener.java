package designMode.observer;

import java.util.Date;

/**
 * describe :
 * Created by jiadu on 2017/10/26 0026.
 */
public class WatchingNBAListener {

    private void stopWatchingTV(Date time){
        System.out.println("停止看电视了！"+"停止时间："+time.getTime());
    }
}
