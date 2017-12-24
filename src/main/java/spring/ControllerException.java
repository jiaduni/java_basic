package spring;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * describe : 异常捕获
 * Created by jiadu on 2017/10/22 0022.
 */
@ControllerAdvice
public class ControllerException {

    @ExceptionHandler(NullPointerException.class)
    public String handlerNullException() {
        return "";//跳转到某个页面
    }
}
