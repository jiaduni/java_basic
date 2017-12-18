package outofmemory;

/**
 * describe :
 * Created by jiadu on 2017/10/18 0018.
 */
public class OverLoadTest {

//    private static String method(List<String> list){
//        return "";
//    }
//
//    private static void method(List<Integer> list){
//
//    }

    public static void main(String[] args) {
        Integer a = 1;
        Integer b = 2;
        Integer c = 3;
        Integer d = 3;
        Integer e = 321;
        Integer f = 321;
        Long g = 3l;
        System.out.println(c == d);//true
        System.out.println(e == f);//false
        System.out.println(c == (a + b));//true
        System.out.println(c.equals(a + b));//true
        System.out.println(g == (a + b));//true
        System.out.println(g.equals(a + b));//false
    }
}
