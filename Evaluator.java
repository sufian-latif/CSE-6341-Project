public class Evaluator {

    public TreeNode eval(TreeNode s) throws Exception {
        if (s.isLeaf()) {
            return evalAtom(s);
        }

        return evalList(s);
    }

    private TreeNode evalAtom(TreeNode s) throws Exception {
        if (s.getToken().getValue().equals(Constants.T) ||
                s.getToken().getValue().equals(Constants.NIL) ||
                s.getToken().getType() == TokenType.NUMERIC) {
            return s;
        }

        throw new Exception("Invalid atom: " + s.getToken().getValue());
    }

    private TreeNode evalList(TreeNode s) throws Exception {
        if (!isList(s)) {
            throw new Exception("Not a list: " + s);
        }

        if (getLength(s) < 2) {
            throw new Exception("List contains less than 2 elements: " + s);
        }

        if (!s.getLeft().isLeaf()) {
            throw new Exception("Function expected, found " + s.getLeft());
        }

        String func = s.getLeft().getToken().getValue();

        switch (func) {
            case Constants.CAR:
            case Constants.CDR:
                return evalListOp(s);
            case Constants.CONS:
                return evalCons(s);
            case Constants.PLUS:
            case Constants.MINUS:
            case Constants.TIMES:
            case Constants.LESS:
            case Constants.GREATER:
            case Constants.EQ:
                return evalArithmetic(s);
            case Constants.ATOM:
            case Constants.INT:
            case Constants.NULL:
                return evalToken(s);
            case Constants.QUOTE:
                return s.getRight().getLeft();
            case Constants.COND:
                return evalCond(s);
            default:
                throw new Exception("Invalid function: " + func);
        }
    }

    private boolean isList(TreeNode s) {
        return (isNil(s) || isList(s.getRight()));
    }

    private int getLength(TreeNode s) {
        return s.isLeaf() ? 0 : 1 + getLength(s.getRight());
    }

    private boolean isNil(TreeNode s) {
        return s.isLeaf() && s.getToken().getValue().equals(Constants.NIL);
    }

    private TreeNode evalListOp(TreeNode s) throws Exception {
        if (getLength(s) != 2) {
            throw new Exception(s.getLeft() + ": Expected 2 elements, found " + getLength(s) + " in " + s);
        }

        String func = s.getLeft().getToken().getValue();
        TreeNode s1 = eval(s.getRight().getLeft());

        if (s1.isLeaf()) {
            throw new Exception(s.getLeft() + ": " + s1 + " is an atom");
        }

        switch (func) {
            case Constants.CAR:
                return s1.getLeft();
            case Constants.CDR:
                return s1.getRight();
            default:
                throw new Exception("Invalid function: " + func);
        }
    }

    private TreeNode evalCons(TreeNode s) throws Exception {
        if (getLength(s) != 3) {
            throw new Exception(s.getLeft() + ": Expected 3 elements, found " + getLength(s) + " in " + s);
        }

        TreeNode s1 = eval(s.getRight().getLeft());
        TreeNode s2 = eval(s.getRight().getRight().getLeft());

        return new TreeNode(s1, s2);
    }

    private TreeNode evalArithmetic(TreeNode s) throws Exception {
        if (getLength(s) != 3) {
            throw new Exception(s.getLeft() + ": Expected 3 elements, found " + getLength(s) + " in " + s);
        }

        String func = s.getLeft().getToken().getValue();
        TreeNode s1 = eval(s.getRight().getLeft());
        TreeNode s2 = eval(s.getRight().getRight().getLeft());

        if (func.equals(Constants.EQ) &&
                s1.isLeaf() && s1.getToken().getType() == TokenType.LITERAL &&
                s2.isLeaf() && s2.getToken().getType() == TokenType.LITERAL) {
            return s1.getToken().getValue().equals(s2.getToken().getValue()) ? TreeNode.T : TreeNode.NIL;
        }

        if (s1.getToken().getType() != TokenType.NUMERIC) {
            throw new Exception(func + ": " + s1 + "is not a numeric atom");
        }
        if (s2.getToken().getType() != TokenType.NUMERIC) {
            throw new Exception(func + ": " + s2 + "is not a numeric atom");
        }

        int i1 = Integer.parseInt(s1.getToken().getValue());
        int i2 = Integer.parseInt(s2.getToken().getValue());
        switch (func) {
            case Constants.PLUS:
                return new TreeNode(new Token(TokenType.NUMERIC, Integer.toString(i1 + i2)));
            case Constants.MINUS:
                return new TreeNode(new Token(TokenType.NUMERIC, Integer.toString(i1 - i2)));
            case Constants.TIMES:
                return new TreeNode(new Token(TokenType.NUMERIC, Integer.toString(i1 * i2)));
            case Constants.LESS:
                return i1 < i2 ? TreeNode.T : TreeNode.NIL;
            case Constants.GREATER:
                return i1 > i2 ? TreeNode.T : TreeNode.NIL;
            case Constants.EQ:
                return i1 == i2 ? TreeNode.T : TreeNode.NIL;
            default:
                throw new Exception("Invalid function: " + func);
        }

    }

    private TreeNode evalToken(TreeNode s) throws Exception {
        if (getLength(s) != 2) {
            throw new Exception(s.getLeft() + ": Expected 2 elements, found " + getLength(s) + " in " + s);
        }

        String func = s.getLeft().getToken().getValue();
        TreeNode s1 = eval(s.getRight().getLeft());

        switch (func) {
            case Constants.ATOM:
                return s1.isLeaf() ? TreeNode.T : TreeNode.NIL;
            case Constants.INT:
                return s1.isLeaf() && s1.getToken().getType() == TokenType.NUMERIC ? TreeNode.T : TreeNode.NIL;
            case Constants.NULL:
                return isNil(s1) ? TreeNode.T : TreeNode.NIL;
            default:
                throw new Exception("Invalid function: " + func);
        }
    }

    private TreeNode evalCond(TreeNode s) throws Exception {
        for (TreeNode t = s.getRight(); !t.isLeaf(); t = t.getRight()) {
            if (!isList(t.getLeft())) {
                throw new Exception(t.getLeft() + " is not a list");
            }
            if (getLength(t.getLeft()) != 2) {
                throw new Exception(t.getLeft() + ": Expected 2 elements, found " + getLength(t.getLeft()));
            }
        }

        for (TreeNode t = s.getRight(); !t.isLeaf(); t = t.getRight()) {
            TreeNode b = eval(t.getLeft().getLeft());
            TreeNode e = eval(t.getLeft().getRight().getLeft());

            if (!isNil(b)) {
                return e;
            }
        }

        throw new Exception("No condition matched");
    }


}
