package designMode.simpleFactory;

import java.math.BigDecimal;

/**
 * describe : 乘法
 * Created by jiadu on 2017/10/21 0021.
 */
public class Division extends Operation{

    @Override
    public double count() {
        BigDecimal a = new BigDecimal(numberA);
        BigDecimal b = new BigDecimal(numberB);
        return a.divide(b).doubleValue();
    }
}
