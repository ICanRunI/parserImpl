package boot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import ast.Stmt;
import error.JlkError;
import runtime.Engine;


public class BootStrap {
	
	public static void main(String[] args) throws Exception{
		if(args.length > 1) {
			System.out.println("usage: jslike [script]");
		}else if(args.length == 1) {
			runFile(args[0]);
		}else {
			runPrompt();
		}
	}
	
	public static void runFile(String path) throws IOException {
//		System.out.println(Paths.get("aaa").toAbsolutePath());  E:\workSpace\jsLike\aaa
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		run(new String(bytes,Charset.defaultCharset()));
		if(JlkError.hasError) {
			System.exit(65);
		}
		if(JlkError.hadRuntimeError) {
			System.exit(70);
		}
	}
	
	public static void runPrompt() throws IOException {
		InputStreamReader input = new InputStreamReader(System.in);
	    BufferedReader reader = new BufferedReader(input);
	    for(;;) {
	    	System.out.println(">");
	    	run(reader.readLine());
	    	//即时运行的代码，防止终止程序
	    	JlkError.hasError = false;
	    }
	}
	
	public static void run(String text) {
		
		
		//暂时打印解析到的token
		/*for(Token token : tokens) {
			System.out.println(token);
		}*/
		
		Engine engine = new Engine();
		//语法分析
		List<Stmt> statements = engine.parse(text);
		if(JlkError.hasError) {
			return;
		}
		
		//语义分析
	    engine.resolve(statements);
	    
	    //解释执行
		engine.interpret(statements);
		
//		System.out.println(new AstPrinter().print(expr));
	}
}
