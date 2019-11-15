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
	    	//��ʱ���еĴ��룬��ֹ��ֹ����
	    	JlkError.hasError = false;
	    }
	}
	
	public static void run(String text) {
		
		
		//��ʱ��ӡ��������token
		/*for(Token token : tokens) {
			System.out.println(token);
		}*/
		
		Engine engine = new Engine();
		//�﷨����
		List<Stmt> statements = engine.parse(text);
		if(JlkError.hasError) {
			return;
		}
		
		//�������
	    engine.resolve(statements);
	    
	    //����ִ��
		engine.interpret(statements);
		
//		System.out.println(new AstPrinter().print(expr));
	}
}
