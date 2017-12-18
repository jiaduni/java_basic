package designMode.simpleFactory;

import java.math.BigDecimal;

/**
 * describe : 加
 * Created by jiadu on 2017/10/21 0021.
 */
public class Addition extends Operation{

    @Override
    public double count() {
        BigDecimal a = new BigDecimal(numberA);
        BigDecimal b = new BigDecimal(numberB);
        return a.add(b).doubleValue();
    }
}
