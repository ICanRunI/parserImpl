package runtime;

import java.util.List;

import ast.Stmt;
import parser.Parser;
import scanner.Scanner;
import scanner.Token;

public class Engine {

	private static final Interpreter interpreter = new Interpreter();
	private static final Resolver resolver = new Resolver(interpreter);
	
	public List<Stmt> parse(String source) {
		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.scanTokens();
		Parser parser = new Parser(tokens);
		return parser.parse();
	}
	
	public void resolve(List<Stmt> statements) {
		resolver.resolve(statements);
	}
	
	public void interpret(List<Stmt> statements) {
		interpreter.interpret(statements);
	}
	
	
	
}
