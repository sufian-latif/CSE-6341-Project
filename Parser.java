import java.io.IOException;

public class Parser {
    private LexicalAnalyzer lex;

    public Parser(LexicalAnalyzer lex) {
        this.lex = lex;
    }

    public void parseStart() throws IOException {
        do {
            System.out.println(parseExpr());
        } while (!lex.getCurrent().getType().equals(TokenType.EOF));
    }

    private TreeNode parseExpr() throws IOException {
        Token token = lex.getCurrent();

        if (token.getType() == TokenType.LITERAL || token.getType() == TokenType.NUMERIC) {
            lex.moveToNext();
            return new TreeNode(token);
        } else if (token.getType() == TokenType.LPAREN) {
            lex.moveToNext();
            return parseList();
        } else {
            if (token.getType() == TokenType.ERROR)
                System.out.println("ERROR: Invalid Token " + token);
            else System.out.println("ERROR: Unexpected Token " + token);
            System.exit(0);
            return null;
        }
    }

    private TreeNode parseList() throws IOException {
        if (lex.getCurrent().getType() == TokenType.RPAREN) {
            lex.moveToNext();
            return new TreeNode(Token.NIL);
        }
        return new TreeNode(parseExpr(), parseList());
    }
}
