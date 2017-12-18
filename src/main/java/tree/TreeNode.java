package tree;

/**
 * describe :
 * Created by jiadu on 2017/10/23 0023.
 */
public class TreeNode {

    char val;
    TreeNode left = null;
    TreeNode right = null;

    public TreeNode getLeft() {
        return left;
    }

    public void setLeft(TreeNode left) {
        this.left = left;
    }

    public TreeNode getRight() {
        return right;
    }

    public void setRight(TreeNode right) {
        this.right = right;
    }

    TreeNode(char _val) {
        this.val = _val;
    }
}
