package designMode.observer;

import java.util.Date;

/**
 * describe :
 * Created by jiadu on 2017/10/26 0026.
 */
public class WatchCartoonListener {

    private void stopPlayingGame(Date time){
        System.out.println("停止玩游戏了！"+"停止时间："+time.getTime());
    }
}
