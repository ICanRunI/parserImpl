package astcaculator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InterpretorTest {

	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String text = "";
		Lexer lexer = null;
		Parser parse = null;
		Visitor visitor = null;
		Interpretor ip = null;
		int result = 0;
		while((text = reader.readLine()) != null) {
			lexer = new Lexer(text);
			parse = new Parser(lexer);
			visitor = new NodeVisitor();
			ip = new Interpretor(parse);
			result = ip._interpretor(visitor);
			System.out.println(result);
		}
	}
}
