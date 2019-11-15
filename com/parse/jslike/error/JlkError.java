package error;

import scanner.Token;
import scanner.TokenType;

public class JlkError {

	public static boolean hasError = false;
	public static boolean hadRuntimeError = false;
	
	private JlkError() {}
	
	public static void error(int line, String message) {
		report(line, "", message);
	}
	
	private static void report(int line, String where, String message) {
		System.err.println(
				"line " + line +" Error "+ where+": "+ message);
		
		hasError = true;
	}
	
	public static void error(Token token, String message) {
	    if (token.type == TokenType.EOF) {
	      report(token.line, " at end", message);
	    } else {
	      report(token.line, " at '" + token.lexeme + "'", message);
	    }
	}
	
	public static void runtimeError(RuntimeError error) {
		System.err.println(error.getMessage() +
		        "\n[line " + error.token.line + "]");
		hadRuntimeError = true;
	}
}
