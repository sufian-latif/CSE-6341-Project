import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.util.ArrayList;

public class Interpreter {
	public static void main(String[] args) throws IOException {
		PushbackReader reader = new PushbackReader(new InputStreamReader(System.in));
		LexicalAnalyzer lex = new LexicalAnalyzer();
        ArrayList<Token> literals = new ArrayList<Token>();
        int sumNumeric = 0, countNumeric = 0;
        int countLParen = 0, countRParen = 0;

		Token token;

		do {
			token = lex.getNextToken(reader);
			if(token.getType() == TokenType.LITERAL) {
			    literals.add(token);
            } else if(token.getType() == TokenType.NUMERIC) {
                countNumeric++;
			    sumNumeric += Integer.parseInt(token.getValue());
            } else if(token.getType() == TokenType.LPAREN) {
			    countLParen++;
            } else if(token.getType() == TokenType.RPAREN) {
			    countRParen++;
            } else if(token.getType() == TokenType.ERROR) {
			    System.out.println("ERROR: Invalid token " + token.getValue());
			    System.exit(0);
            }
		} while(token.getType() != TokenType.EOF);

        System.out.print("LITERAL ATOMS: " + literals.size());
        for (Token literal : literals) {
            System.out.print(", " + literal.getValue());
        }
        System.out.println();

        System.out.println("NUMERIC ATOMS: " + countNumeric + ", " + sumNumeric);
        System.out.println("OPEN PARENTHESES: " + countLParen);
        System.out.println("CLOSING PARENTHESES: " + countRParen);
    }
}
