public class Evaluator {

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

    public TreeNode eval(TreeNode s) throws Exception {
        if (s.isLeaf()) {
            return evalAtom(s);
        }

        return evalList(s);
    }

    private TreeNode evalAtom(TreeNode s) throws Exception {
        if (s.getToken().getValue().equals(T) ||
                s.getToken().getValue().equals(NIL) ||
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

        String func = s.getLeft().getToken().getValue();

        switch (func) {
            case CAR:
            case CDR:
                return evalListOp(s);
            case CONS:
                return evalCons(s);
            case PLUS:
            case MINUS:
            case TIMES:
            case LESS:
            case GREATER:
            case EQ:
                return evalArithmetic(s);
            case ATOM:
            case INT:
            case NULL:
                return evalToken(s);
            case QUOTE:
                return s.getRight().getLeft();
            case COND:
                return evalCond(s);
            default:
                throw new Exception("Invalid function: " + func);
        }
    }

    private boolean isList(TreeNode s) {
        return (s.isLeaf() && s.getToken().getValue().equals(NIL)) || isList(s.getRight());
    }

    private int getLength(TreeNode s) {
        return s.isLeaf() ? 0 : 1 + getLength(s.getRight());
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
            case CAR:
                return s1.getLeft();
            case CDR:
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

        if(func.equals(EQ) &&
                s1.isLeaf() && s1.getToken().getType() == TokenType.LITERAL &&
                s2.isLeaf() && s2.getToken().getType() == TokenType.LITERAL) {
            return new TreeNode(new Token(TokenType.LITERAL,
                    s1.getToken().getValue().equals(s2.getToken().getValue()) ? T : NIL));
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
            case PLUS:
                return new TreeNode(new Token(TokenType.NUMERIC, Integer.toString(i1 + i2)));
            case MINUS:
                return new TreeNode(new Token(TokenType.NUMERIC, Integer.toString(i1 - i2)));
            case TIMES:
                return new TreeNode(new Token(TokenType.NUMERIC, Integer.toString(i1 * i2)));
            case LESS:
                return new TreeNode(new Token(TokenType.LITERAL, i1 < i2 ? T : NIL));
            case GREATER:
                return new TreeNode(new Token(TokenType.LITERAL, i1 > i2 ? T : NIL));
            case EQ:
                return new TreeNode(new Token(TokenType.LITERAL, i1 == i2 ? T : NIL));
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
            case ATOM:
                return new TreeNode(new Token(TokenType.LITERAL, s1.isLeaf() ? T : NIL));
            case INT:
                return new TreeNode(new Token(TokenType.LITERAL,
                        s1.isLeaf() && s1.getToken().getType() == TokenType.NUMERIC ? T : NIL));
            case NULL:
                return new TreeNode(new Token(TokenType.LITERAL,
                        s1.isLeaf() && s1.getToken().getValue().equals(NIL)  ? T : NIL));
            default:
                throw new Exception("Invalid function: " + func);
        }
    }

    private TreeNode evalCond(TreeNode s) throws Exception {
        for(TreeNode t = s.getRight(); !t.isLeaf(); t = t.getRight()) {
            if(!isList(t.getLeft())) {
                throw new Exception(t.getLeft() + " is not a list");
            }
            if(getLength(t.getLeft()) != 2) {
                throw new Exception(t.getLeft() + ": Expected 2 elements, found " + getLength(t.getLeft()));
            }
        }

        for(TreeNode t = s.getRight(); !t.isLeaf(); t = t.getRight()) {
            TreeNode b = eval(t.getLeft().getLeft());
            TreeNode e = eval(t.getLeft().getRight().getLeft());

            if(!b.getToken().getValue().equals(NIL)) {
                return e;
            }
        }

        throw new Exception("No condition matched");
    }
}
