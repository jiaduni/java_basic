package search;

import java.util.Random;
import java.util.TreeMap;

/**
 * describe : 二分查找法 查找对象需排好序
 * Created by jiadu on 2017/10/20 0020.
 */
public class BinarySearch {

    private static final int PARAM[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
            40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61,
            62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83,
            84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100};

    public static void main(String[] args) {
        Random random = new Random();
        int rint = random.nextInt(100);//随机数
        int end = PARAM.length - 1;
        int start = 0;
        int index = -1;
        int count = 0;
        while (start <= end) {
            int mid = (end + start) >>> 1;
            if (rint > PARAM[mid]) {
                start = mid + 1;
            } else if (rint < PARAM[mid]) {
                end = mid - 1;
            } else {
                index = mid;
                count++;
                break;
            }
            count++;
        }
        System.out.println("下标为：" + index + ",查找次数为：" + count + ",随机数为：" + rint);
    }
}
