public class Evaluator {

    public TreeNode eval(TreeNode s, TreeNode aList, TreeNode dList) throws Exception {
        if (s.isLeaf()) {
            return evalAtom(s, aList);
        }

        return evalList(s, aList, dList);
    }

    private TreeNode evalAtom(TreeNode s, TreeNode aList) throws Exception {
        Token token = s.getToken();

        if (token.getValue().equals(Constants.T) ||
                token.getValue().equals(Constants.NIL) ||
                token.getType() == TokenType.NUMERIC) {
            return s;
        }

        return getVal(token, aList);
    }

    private TreeNode evalList(TreeNode s, TreeNode aList, TreeNode dList) throws Exception {
        if (!isList(s)) {
            throw new Exception("Not a list: " + s);
        }

        if (!s.getLeft().isLeaf()) {
            throw new Exception("Function expected, found " + s.getLeft());
        }

        String func = s.getLeft().getToken().getValue();

        switch (func) {
            case Constants.CAR:
            case Constants.CDR:
                return evalListOp(s, aList, dList);
            case Constants.CONS:
                return evalCons(s, aList, dList);
            case Constants.PLUS:
            case Constants.MINUS:
            case Constants.TIMES:
            case Constants.LESS:
            case Constants.GREATER:
            case Constants.EQ:
                return evalArithmetic(s, aList, dList);
            case Constants.ATOM:
            case Constants.INT:
            case Constants.NULL:
                return evalToken(s, aList, dList);
            case Constants.QUOTE:
                return s.getRight().getLeft();
            case Constants.COND:
                return evalCond(s, aList, dList);
            case Constants.DEFUN:
                return evalDefun(s);
            default:
                return evalCustom(s, aList, dList);
        }
    }

    private boolean isKeyword(String s) {
        for (String word : Constants.keywords) {
            if (s.equals(word)) {
                return true;
            }
        }
        return false;
    }

    private TreeNode getVal(Token token, TreeNode aList) throws Exception {
        for (TreeNode t = aList; !t.isLeaf(); t = t.getRight()) {
            TreeNode pair = t.getLeft();

            if (pair.getLeft().getToken().getValue().equals(token.getValue())) {
                return pair.getRight();
            }
        }

        throw new Exception("Unbound atom: " + token.getValue());
    }

    private TreeNode addPairs(TreeNode x, TreeNode y, TreeNode aList) {
        if (length(x) == 0) {
            return aList;
        }

        TreeNode pair = new TreeNode(x.getLeft(), y.getLeft());
        TreeNode rest = addPairs(x.getRight(), y.getRight(), aList);
        return new TreeNode(pair, rest);
    }

    private boolean isList(TreeNode s) {
        return (isNil(s) || isList(s.getRight()));
    }

    private int length(TreeNode s) {
        return s.isLeaf() ? 0 : 1 + length(s.getRight());
    }

    private boolean isNil(TreeNode s) {
        return s.isLeaf() && s.getToken().getValue().equals(Constants.NIL);
    }

    private TreeNode evalListOp(TreeNode s, TreeNode aList, TreeNode dList) throws Exception {
        if (length(s) != 2) {
            throw new Exception(s.getLeft() + ": Expected 2 elements, found " + length(s) + " in " + s);
        }

        String func = s.getLeft().getToken().getValue();
        TreeNode s1 = eval(s.getRight().getLeft(), aList, dList);

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

    private TreeNode evalCons(TreeNode s, TreeNode aList, TreeNode dList) throws Exception {
        if (length(s) != 3) {
            throw new Exception(s.getLeft() + ": Expected 3 elements, found " + length(s) + " in " + s);
        }

        TreeNode s1 = eval(s.getRight().getLeft(), aList, dList);
        TreeNode s2 = eval(s.getRight().getRight().getLeft(), aList, dList);

        return new TreeNode(s1, s2);
    }

    private TreeNode evalArithmetic(TreeNode s, TreeNode aList, TreeNode dList) throws Exception {
        if (length(s) != 3) {
            throw new Exception(s.getLeft() + ": Expected 3 elements, found " + length(s) + " in " + s);
        }

        String func = s.getLeft().getToken().getValue();
        TreeNode s1 = eval(s.getRight().getLeft(), aList, dList);
        TreeNode s2 = eval(s.getRight().getRight().getLeft(), aList, dList);

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

    private TreeNode evalToken(TreeNode s, TreeNode aList, TreeNode dList) throws Exception {
        if (length(s) != 2) {
            throw new Exception(s.getLeft() + ": Expected 2 elements, found " + length(s) + " in " + s);
        }

        String func = s.getLeft().getToken().getValue();
        TreeNode s1 = eval(s.getRight().getLeft(), aList, dList);

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

    private TreeNode evalCond(TreeNode s, TreeNode aList, TreeNode dList) throws Exception {
        for (TreeNode t = s.getRight(); !t.isLeaf(); t = t.getRight()) {
            if (!isList(t.getLeft())) {
                throw new Exception(t.getLeft() + " is not a list");
            }
            if (length(t.getLeft()) != 2) {
                throw new Exception(t.getLeft() + ": Expected 2 elements, found " + length(t.getLeft()));
            }
        }

        for (TreeNode t = s.getRight(); !t.isLeaf(); t = t.getRight()) {
            TreeNode b = eval(t.getLeft().getLeft(), aList, dList);
            TreeNode e = eval(t.getLeft().getRight().getLeft(), aList, dList);

            if (!isNil(b)) {
                return e;
            }
        }

        throw new Exception("No condition matched");
    }

    private TreeNode evalDefun(TreeNode s) throws Exception {
        if (length(s) != 4) {
            throw new Exception(s.getLeft() + ": Expected 4 elements, found " + length(s) + " in " + s);
        }

        TreeNode name = s.getRight().getLeft();
        if (!name.isLeaf() || name.getToken().getType() != TokenType.LITERAL) {
            throw new Exception(name + " is not a literal atom");
        }
        if (isKeyword(name.getToken().getValue())) {
            throw new Exception("Function name is a keyword: " + name.getToken().getValue());
        }

        TreeNode params = s.getRight().getRight().getLeft();
        if (!isList(params)) {
            throw new Exception(params + " is not a list");
        }

        for (TreeNode t = params; !t.isLeaf(); t = t.getRight()) {
            TreeNode fp = t.getLeft();

            if (!fp.isLeaf() || fp.getToken().getType() != TokenType.LITERAL) {
                throw new Exception(name + " " + params + " : " + fp + " is not a literal atom");
            }
            if (isKeyword(fp.getToken().getValue())) {
                throw new Exception(name + " " + params + " : " + fp + " is a keyword");
            }

            for (TreeNode tt = params; !tt.isLeaf() && tt != t; tt = tt.getRight()) {
                if(tt.getLeft().getToken().getValue().equals(fp.getToken().getValue())) {
                    throw new Exception("Duplicate formal parameter " + fp.getToken().getValue());
                }
            }
        }

        TreeNode body = s.getRight().getRight().getRight().getLeft();

        return new TreeNode(name, new TreeNode(params, body));
    }

    private TreeNode evalCustom(TreeNode s, TreeNode aList, TreeNode dList) throws Exception {
        TreeNode fName = s.getLeft();
        TreeNode func = getVal(fName.getToken(), dList);

        TreeNode formals = func.getLeft();
        TreeNode actuals = evalParams(s.getRight(), aList, dList);

        if (length(formals) != length(actuals)) {
            throw new Exception(fName + ": " + length(formals) + " parameters expected, found " + length(actuals));
        }

        return eval(func.getRight(), addPairs(formals, actuals, aList), dList);
    }

    private TreeNode evalParams(TreeNode s, TreeNode aList, TreeNode dList) throws Exception {
        if(length(s) == 0) {
            return TreeNode.NIL;
        }

        return new TreeNode(eval(s.getLeft(), aList, dList), evalParams(s.getRight(), aList, dList));
    }
}
