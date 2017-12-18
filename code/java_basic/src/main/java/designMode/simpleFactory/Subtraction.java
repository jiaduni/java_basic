package designMode.simpleFactory;

import java.math.BigDecimal;

/**
 * describe : 减法
 * Created by jiadu on 2017/10/21 0021.
 */
public class Subtraction extends Operation{

    @Override
    public double count() {
        BigDecimal a = new BigDecimal(numberA);
        BigDecimal b = new BigDecimal(numberB);
        return a.subtract(b).doubleValue();
    }
}
