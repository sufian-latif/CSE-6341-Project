public class Evaluator {

    // constants
    private static final String T = "T";
    private static final String NIL = "NIL";
    private static final String PLUS = "PLUS";
    private static final String MINUS = "MINUS";
    private static final String TIMES = "TIMES";
    private static final String LESS = "LESS";
    private static final String GREATER = "GREATER";
    private static final String EQ = "EQ";
    private static final String ATOM = "ATOM";
    private static final String INT = "INT";
    private static final String NULL = "NULL";
    private static final String CAR = "CAR";
    private static final String CDR = "CDR";
    private static final String CONS = "CONS";
    private static final String QUOTE = "QUOTE";
    private static final String COND = "COND";

    public TreeNode eval(TreeNode root) throws Exception {
        if (root.isLeaf()) {
            return evalAtom(root);
        }

        return evalList(root);
    }

    private TreeNode evalAtom(TreeNode node) throws Exception {
        if (node.getToken().getValue().equals(T) ||
                node.getToken().getValue().equals(NIL) ||
                node.getToken().getType() == TokenType.NUMERIC) {
            return node;
        }

        throw new Exception("Invalid atom: " + node.getToken().getValue());
    }

    private boolean isList(TreeNode root) {
        return (root.isLeaf() && root.getToken().getValue().equals(NIL)) || isList(root.getRight());
    }

    private int getLength(TreeNode root) {
        return root.isLeaf() ? 0 : 1 + getLength(root.getRight());
    }

    private TreeNode evalList(TreeNode root) throws Exception {
        if (!isList(root)) {
            throw new Exception("Not a list: " + root);
        }

        if(getLength(root) < 2) {
            throw new Exception("List contains less than 2 elements: " + root);
        }

        String func = root.getLeft().getToken().getValue();

        switch (func) {
            case CAR:
            case CDR:
            case CONS:
            case PLUS:
            case MINUS:
            case TIMES:
            case LESS:
            case GREATER:
            case EQ:
            case ATOM:
            case INT:
            case NULL:
            case QUOTE:
            case COND:

            default:
                throw new Exception("Invalid function: " + func);
        }
    }
}
