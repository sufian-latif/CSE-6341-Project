import java.io.IOException;
import java.io.PushbackReader;

public class LexicalAnalyzer {
	private  PushbackReader reader;
	Token current;

	public LexicalAnalyzer(PushbackReader reader) throws IOException {
		this.reader = reader;
		current = getNextToken();
	}

	public Token getCurrent() {
		return current;
	}

	public void moveToNext() throws IOException {
		current = getNextToken();
	}

	private boolean isWhitespace(int c) {
		return c == ' ' || c == '\r' || c == '\n';
	}

	private boolean isLetter(int c) {
		return c >= 'A' && c <= 'Z';
	}

	private boolean isDigit(int c) {
		return c >= '0' && c <= '9';
	}

	private Token getNextToken() throws IOException {
		int ch = reader.read();
		while(isWhitespace(ch)) ch = reader.read();

        if(ch == -1) return Token.EOF;
        if(ch == '(') return Token.LPAREN;
		if(ch == ')') return Token.RPAREN;

		StringBuilder tok = new StringBuilder();
        do {
            tok.append((char)ch);
            ch = reader.read();
        } while(isLetter(ch) || isDigit(ch));
        reader.unread(ch);

        if(isLetter(tok.charAt(0))) return new Token(TokenType.LITERAL, tok.toString());
        for (int i = 0; i < tok.length(); i++) {
            if(isLetter(tok.charAt(i))) return new Token(TokenType.ERROR, tok.toString());
        }
        return new Token(TokenType.NUMERIC, tok.toString());
	}
}
