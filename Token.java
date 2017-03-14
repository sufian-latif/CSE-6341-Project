public class Token {
    private TokenType type;
    private String value;

    public static final Token EOF = new Token(TokenType.EOF, Constants.EOF);
    public static final Token LPAREN = new Token(TokenType.LPAREN, Constants.LPAREN);
    public static final Token RPAREN = new Token(TokenType.RPAREN, Constants.RPAREN);
    public static final Token NIL = new Token(TokenType.LITERAL, Constants.NIL);
    public static final Token T = new Token(TokenType.LITERAL, Constants.T);

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public String toString() {
        return value;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
