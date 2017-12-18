package tree;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.zip.Inflater;

/**
 * describe : 二叉树 深度宽度计算
 * Created by jiadu on 2017/10/23 0023.
 */
public class DichotomyTest {

    public static void main(String[] args) {
        TreeNode treeNode = new TreeNode('1');//第一层（root）

        //---------------------第二层---------------------
        TreeNode childNode_1 = new TreeNode('2');
        treeNode.setLeft(childNode_1);
        TreeNode childNode_2 = new TreeNode('3');
        treeNode.setRight(childNode_2);

        //--------------------第三层 ------------------------
        TreeNode childNode_1_1 = new TreeNode('4');
        childNode_1.setLeft(childNode_1_1);
        TreeNode childNode_1_2 = new TreeNode('5');
        childNode_1.setRight(childNode_1_2);

        TreeNode childNode_2_1 = new TreeNode('6');
        childNode_2.setLeft(childNode_2_1);
        TreeNode childNode_2_2 = new TreeNode('7');
        childNode_2.setRight(childNode_2_2);
        //--------------------第三层 ------------------------

        //-------------------第四层
        TreeNode childNode_2_2_1 = new TreeNode('8');
        childNode_2_2.setLeft(childNode_2_2_1);

        System.out.println(Integer.MAX_VALUE+1+","+Integer.MAX_VALUE);
        System.out.println(getMaxDepth(treeNode));
        System.out.println(getMaxWidth(treeNode));
    }

    // 获取最大深度
    public static int getMaxDepth(TreeNode root) {
        if (root == null)
            return 0;
        else {
            int left = getMaxDepth(root.left);
            int right = getMaxDepth(root.right);
            return 1 + Math.max(left, right);
        }
    }

    // 获取最大宽度
    public static int getMaxWidth(TreeNode root) {
        if (root == null)
            return 0;

        Queue<TreeNode> queue = new ArrayDeque<TreeNode>();
        int maxWitdth = 1; // 最大宽度
        queue.add(root); // 入队

        while (true) {
            int len = queue.size(); // 当前层的节点个数
            if (len == 0)
                break;
            while (len > 0) {// 如果当前层，还有节点
                TreeNode t = queue.poll();
                len--;
                if (t.left != null)
                    queue.add(t.left); // 下一层节点入队
                if (t.right != null)
                    queue.add(t.right);// 下一层节点入队
            }
            maxWitdth = Math.max(maxWitdth, queue.size());
        }
        return maxWitdth;
    }
}
