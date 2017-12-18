package designMode.simpleFactory;

/**
 * describe : 运算类
 * Created by jiadu on 2017/10/21 0021.
 */
public class Operation {

    protected double numberA;
    protected double numberB;

    public double getNumberA() {
        return numberA;
    }

    public void setNumberA(double numberA) {
        this.numberA = numberA;
    }

    public double getNumberB() {
        return numberB;
    }

    public void setNumberB(double numberB) {
        this.numberB = numberB;
    }

    public double count(){
        return 0;
    }
}
