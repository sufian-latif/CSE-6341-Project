import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;

public class Interpreter {
	public static void main(String[] args) throws IOException {
		PushbackReader reader = new PushbackReader(new InputStreamReader(System.in));
        LexicalAnalyzer lex = new LexicalAnalyzer(reader);
        Parser parser = new Parser(lex);

        parser.parseStart();
    }
}
