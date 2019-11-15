package interpret;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class InterpreterTest {

	public static void main(String[] args) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String text = "";
		Interpreter itp = null;
		Lexer lexer = null;
		int result = 0;
		while((text = reader.readLine()) != null) {
			lexer = new Lexer(text);
			itp = new Interpreter(lexer);
			result = itp.expr();
			System.out.println(text +" = "+ result);
		}
	}
}
