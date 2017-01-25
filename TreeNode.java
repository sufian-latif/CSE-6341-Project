public class TreeNode {
    private Token token;
    private TreeNode left, right;
    private boolean isLeaf;

    public TreeNode(Token token) {
        this.token = token;
        isLeaf = true;
    }

    public TreeNode(TreeNode left, TreeNode right) {
        this.left = left;
        this.right = right;
        isLeaf = false;
    }

    public String toString() {
        if (isLeaf) return token.getValue();
        return "(" + left + "." + right + ")";
    }
}
