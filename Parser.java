import java.io.IOException;

public class Parser {
    LexicalAnalyzer lex;

    public Parser(LexicalAnalyzer lex) {
        this.lex = lex;
    }

    public void parseStart() throws IOException {
        do {
            System.out.println(parseExpr());
        } while(!lex.getCurrent().getType().equals(TokenType.EOF));
    }

    private TreeNode parseExpr() throws IOException {
        if(lex.getCurrent().getType() == TokenType.LITERAL || lex.getCurrent().getType() == TokenType.NUMERIC) {
            Token token = lex.getCurrent();
            lex.moveToNext();
            return new TreeNode(token);
        } else if(lex.getCurrent().getType() == TokenType.LPAREN) {
            lex.moveToNext();
            return parseList();
        } else {
            if(lex.getCurrent().getType() == TokenType.ERROR) System.out.println("ERROR: Invalid Token " + lex.getCurrent());
            else System.out.println("ERROR: Unexpected Token " + lex.getCurrent());
            System.exit(0);
            return null;
        }
    }

    private TreeNode parseList() throws IOException {
        if(lex.getCurrent().getType() == TokenType.RPAREN) {
            lex.moveToNext();
            return new TreeNode(Token.NIL);
        }
        return new TreeNode(parseExpr(), parseList());
    }
}
