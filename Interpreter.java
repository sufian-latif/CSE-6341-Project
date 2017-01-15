
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.util.ArrayList;

public class Interpreter {
	public static void main(String[] args) throws IOException {
		PushbackReader reader = new PushbackReader(new InputStreamReader(System.in));
		LexicalAnalyzer lex = new LexicalAnalyzer();
        ArrayList<Token> literals = new ArrayList<Token>();
        ArrayList<Token> numerics = new ArrayList<Token>();
        int countLParen = 0, countRParen = 0;

		Token token;

		do {
			token = lex.getNextToken(reader);
			if(token.getType() == TokenType.LITERAL) {
			    literals.add(token);
            } else if(token.getType() == TokenType.NUMERIC) {
			    numerics.add(token);
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

        System.out.print("NUMERIC ATOMS: " + numerics.size());
        for (Token numeric : numerics) {
            System.out.print(", " + numeric.getValue());
        }
        System.out.println();

        System.out.println("OPEN PARENTHESES: " + countLParen);
        System.out.println("CLOSING PARENTHESES: " + countRParen);
    }
}
