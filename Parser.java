import java.io.IOException;

public class Parser {
    private LexicalAnalyzer lex;
    private TypeChecker typeChecker;

    public Parser(LexicalAnalyzer lex) {
        this.lex = lex;
        typeChecker = new TypeChecker();
    }

    public void parseStart() throws IOException {
        do {
            try {
                TreeNode s = parseExpr();
                if (!typeChecker.isWellTyped(s)) {
                    throw new Exception("TYPE ERROR: Expression is not well-typed");
                }

                if (!typeChecker.isEmptyListSafe(s)) {
                    throw new Exception("EMPTY LIST ERROR: CAR/CDR on empty list");
                }
                System.out.println(s);
            } catch (Exception e) {
                System.out.println(e.getMessage());
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
