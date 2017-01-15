public class Token {
    private String type;
    private String value;

    public static final Token EOF = new Token("EOF", null);
    public static final Token LPAREN = new Token("LPAREN", "(");
    public static final Token RPAREN = new Token("RPAREN", ")");

    public Token(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String toString() {
        return "(" + type + ", " + value + ")";
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
