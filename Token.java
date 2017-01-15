public class Token {
    private TokenType type;
    private String value;

    public static final Token EOF = new Token(TokenType.EOF, null);
    public static final Token LPAREN = new Token(TokenType.LPAREN, "(");
    public static final Token RPAREN = new Token(TokenType.RPAREN, ")");

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public String toString() {
        return "(" + type + ", " + value + ")";
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
