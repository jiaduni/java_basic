package sort;

import org.junit.Test;

import java.util.Arrays;

/**
 * describe :  各种排序算法
 * Created by jiadu on 2017/10/20 0020.
 */
public class Sort {

    private static final int[] PARAM = {16, 8, 58, 45, 73, 12, 98, 64, 3, 9, 47};

    /**
     * 冒泡排序（可以说是交换排序，效率极差）
     */
    @Test
    public void bubblingSort() {
        for (int i = 0; i < PARAM.length; i++) {
            for (int j = i + 1; j < PARAM.length; j++) {
                int pre = PARAM[i];
                int next = PARAM[j];
                if (pre > next) {
                    PARAM[i] = next;
                    PARAM[j] = pre;
                }
            }
            System.out.println(Arrays.toString(PARAM));
        }
        System.out.println(Arrays.toString(PARAM));
    }

    /**
     * 正宗冒泡排序(效率比上一个稍微好一点)  从数组最后一个元素和前一个元素不停的比较
     * PARAM = {16, 8, 58, 45, 73, 12, 98, 64, 3, 9, 47};
     * 时间复杂度 n << 2
     */
    @Test
    public void bubblingSort_2() {
        boolean falg = true;//加上标记以提高效率，避免无意义的循环判断
        for (int i = 0; i < PARAM.length && falg; i++) {
            falg = false;
            for (int j = PARAM.length - 1; j > i; j--) {
                int last = PARAM[j];
                int pre = PARAM[j - 1];
                if (last < pre) {
                    PARAM[j] = pre;
                    PARAM[j - 1] = last;
                    falg = true;
                }
            }
            System.out.println(Arrays.toString(PARAM));
        }
        System.out.println(Arrays.toString(PARAM));
    }

    /**
     * 选择排序，用min保存当此循环的最小值所在下标，如果PARAM[min]的值不等于PARAM[i]的值，说明找到更小的值，交换
     * PARAM = {16, 8, 58, 45, 73, 12, 98, 64, 3, 9, 47};
     * 时间复杂度 n << 2
     */
    @Test
    public void selectSort() {
        int min, pre = 0;
        for (int i = 0; i < PARAM.length; i++) {
            min = i;
            for (int j = i + 1; j < PARAM.length; j++) {
                if (PARAM[min] > PARAM[j]) {
                    min = j;
                }
            }
            if (i != min) {
                pre = PARAM[i];
                PARAM[i] = PARAM[min];
                PARAM[min] = pre;
            }
            System.out.println(Arrays.toString(PARAM));
        }
        System.out.println(Arrays.toString(PARAM));
    }

    /**
     * 插入排序
     * PARAM = {16, 8, 58, 45, 73, 12, 98, 64, 3, 9, 47};
     */
    @Test
    public void insertSort() {
//        int t,j;
//        for(int i=1;i<PARAM.length;i++){
//            t=PARAM[i];//把第二的数据拿出
//            for(j=i-1;j>=0 && PARAM[j]>t;j--){//和前一个比较，如果前一个大
//                PARAM[j+1]=PARAM[j];//那么把前一个向后移一步
//            }
//            PARAM[j+1]=t;//j--后就是把最小的这个数放到
//            System.out.println(Arrays.toString(PARAM));
//        }

        for (int i = 0, j = i; i < PARAM.length - 1; j = ++i) {//jdk中源码写法
            int ai = PARAM[i + 1];//设置哨兵
            while (ai < PARAM[j]) {
                PARAM[j + 1] = PARAM[j];
                if (j-- == 0) {
                    break;
                }
            }
            PARAM[j + 1] = ai;
        }
        System.out.println(Arrays.toString(PARAM));
    }

    /**
     * 快速排序，20世纪十大算法之一
     * 1.选择一个关键值，使得它左边的数都比它小，右边的数都比它大，把这样的关键值我们叫做枢纽(pivot)
     * PARAM = {16, 8, 58, 45, 73, 12, 98, 64, 3, 9, 47};
     */
    @Test
    public void quickSort() {
        qSort(PARAM, 0, PARAM.length - 1);
        System.out.println(Arrays.toString(PARAM));
    }

    private void qSort(int[] l, int low, int high) {
        int pivot;
        if (low < high) {
            //1.选择一个关键值，使得它左边的数都比它小，右边的数都比它大，把这样的关键值我们叫做枢纽(pivot)
            pivot = partition(l, low, high);
            qSort(l, low, pivot - 1);//对低子表递归排序
            qSort(l, pivot + 1, high);//对高子表递归排序
        }
    }

    /**
     * @param l
     * @param low
     * @param high
     * @return PARAM = {16, 8, 58, 45, 73, 12, 98, 64, 3, 9, 47};
     */
    private int partition(int[] l, int low, int high) {
        int pivotkey = l[low];//把第一个值作为枢纽
        while (low < high) {
            while (low < high && l[high] >= pivotkey) {
                high--;
            }
            //将比枢纽小的记录交换到低端
            swap(l, low, high);
            while (low < high && l[low] <= pivotkey) {
                low++;
            }
            //将比枢纽大的记录交换到高端
            swap(l, low, high);
        }
        return low;
    }

    private void swap(int[] l, int low, int high) {
        int s = l[high];
        l[high] = l[low];
        l[low] = s;
    }
}
