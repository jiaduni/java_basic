package spring;

import javax.validation.constraints.NotNull;

/**
 * describe :
 * Created by jiadu on 2017/10/22 0022.
 */
public class ValidationVO {

    @NotNull(message = "${message.name}")
    private String name;
}
