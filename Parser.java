import java.io.IOException;

public class Parser {
    private LexicalAnalyzer lex;
    private Evaluator evaluator;
    private TreeNode dList;

    public Parser(LexicalAnalyzer lex) {
        this.lex = lex;
        evaluator = new Evaluator();
        dList = TreeNode.NIL;
    }

    public void parseStart() throws IOException {
        do {
            try {
                TreeNode expr = parseExpr();

                if(!expr.isLeaf() && expr.getLeft().isLeaf()
                        && expr.getLeft().getToken().getValue().equals(Constants.DEFUN)) {
                    TreeNode newFunc = evaluator.eval(expr, TreeNode.NIL, dList);
                    dList = new TreeNode(newFunc, dList);
                    System.out.println(newFunc.getLeft());
                    //System.out.println(dList);
                } else {
                    System.out.println(evaluator.eval(expr, TreeNode.NIL, dList));
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
                System.exit(0);
            }
        } while (!lex.getCurrent().getType().equals(TokenType.EOF));
    }

    private TreeNode parseExpr() throws Exception {
        Token token = lex.getCurrent();

        if (token.getType() == TokenType.LITERAL || token.getType() == TokenType.NUMERIC) {
            lex.moveToNext();
            return new TreeNode(token);
        } else if (token.getType() == TokenType.LPAREN) {
            lex.moveToNext();
            return parseList();
        } else {
            if (token.getType() == TokenType.ERROR) {
                throw new Exception("Invalid Token: " + token);
            } else {
                throw new Exception("Unexpected Token: " + token);
            }
        }
    }

    private TreeNode parseList() throws Exception {
        if (lex.getCurrent().getType() == TokenType.RPAREN) {
            lex.moveToNext();
            return new TreeNode(Token.NIL);
        }
        return new TreeNode(parseExpr(), parseList());
    }
}
