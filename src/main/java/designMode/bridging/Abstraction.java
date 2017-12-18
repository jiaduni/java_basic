package designMode.bridging;

/**
 * describe :
 * Created by jiadu on 2017/10/24 0024.
 */
public class Abstraction {
    protected Impementor impementor;

    public void setImpementor(Impementor impementor) {
        this.impementor = impementor;
    }

    public void operation(){
        impementor.operation();
    }
}
