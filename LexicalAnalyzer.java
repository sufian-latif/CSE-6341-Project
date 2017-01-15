import java.io.IOException;
import java.io.PushbackReader;

public class LexicalAnalyzer {

	private boolean isWhitespace(int c) {
		char ch = (char) c;
		return ch == ' ' || ch == '\r' || ch == '\n';
	}

	private boolean isLetter(int c) {
		return c >= 'A' && c <= 'Z';
	}

	private boolean isDigit(int c) {
		return c >= '0' && c <= '9';
	}

	public Token getNextToken(PushbackReader reader) throws IOException {
		int ch = reader.read();

		if(isWhitespace(ch)) {
			 do {
				ch = reader.read();
			} while(isWhitespace(ch));
		}

        if(ch == -1) {
            return Token.EOF;
        } else if(ch == '(') {
			return Token.LPAREN;
		} else if(ch == ')') {
			return Token.RPAREN;
		} else {
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
}
