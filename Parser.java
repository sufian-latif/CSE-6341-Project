import java.io.IOException;

public class Parser {
    LexicalAnalyzer lex;

    public Parser(LexicalAnalyzer lex) {
        this.lex = lex;
    }

    public void parseStart() throws IOException {
        do {
            parseExpr();
            System.out.println();
        } while(!lex.getCurrent().getType().equals(TokenType.EOF));
    }

    public void parseExpr() throws IOException {
        if(lex.getCurrent().getType().equals(TokenType.LITERAL) || lex.getCurrent().getType().equals(TokenType.NUMERIC)) {
            System.out.print(" " + lex.getCurrent().getValue() + " ");
            lex.moveToNext();
        } else if(lex.getCurrent().getType().equals(TokenType.LPAREN)) {
            System.out.print("(");
            lex.moveToNext();
            while(!lex.getCurrent().getType().equals(TokenType.RPAREN)) {
                parseExpr();
            }
            System.out.print(")");
            lex.moveToNext();
        } else {
            System.out.println("Parsing error");
            System.exit(0);
        }
    }
}
