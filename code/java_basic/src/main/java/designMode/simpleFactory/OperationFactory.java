package designMode.simpleFactory;

/**
 * describe : 操作类
 * Created by jiadu on 2017/10/21 0021.
 */
public class OperationFactory {

    public static Operation creatOpertation(char symbol){
        Operation oper = null ;
        switch (symbol){
            case '+' :oper = new Addition();
                break;
            case '-' :oper = new Subtraction();
                break;
            case '*' :oper = new Multiplication();
                break;
            case '/' :oper = new Division();
                break;
        }
        return oper;
    }
}
