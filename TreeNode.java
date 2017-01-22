public class TreeNode {
    Token token;
    TreeNode left, right;

    public TreeNode(Token token) {
        this.token = token;
        left = right = null;
    }

    public TreeNode(TreeNode left, TreeNode right) {
        this.left = left;
        this.right = right;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    public String toString() {
        if(isLeaf()) return token.getValue();
        return "(" + left + "." + right + ")";
    }
}
