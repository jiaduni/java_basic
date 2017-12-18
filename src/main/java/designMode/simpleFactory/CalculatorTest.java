package designMode.simpleFactory;

import java.io.IOException;
import java.util.Scanner;

/**
 * describe : 设计一个科学计算器,简单工厂模式
 * Created by jiadu on 2017/10/21 0021.
 */
public class CalculatorTest {


    public static void main(String[] args) throws IOException {
        Operation operation = OperationFactory.creatOpertation('*');
        operation.setNumberA(5);
        operation.setNumberB(2);
        System.out.println(operation.count());
    }
}
