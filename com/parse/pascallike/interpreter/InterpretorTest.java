package interpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InterpretorTest {

	public static void main(String[] args) throws IOException {
//			InputStream is = InterpretorTest.class.getClassLoader().getResourceAsStream("com/parser/pascallike/assigment.txt");
//		InputStream is = InterpretorTest.class.getClassLoader().getResourceAsStream("com/parser/pascallike/part10.txt");
		//在之前的基础上添加了符号表的支持
//		InputStream is = InterpretorTest.class.getClassLoader().getResourceAsStream("com/parser/pascallike/part11.txt");
		//在之前的基础上添加了子程序procedure 
//		InputStream is = InterpretorTest.class.getClassLoader().getResourceAsStream("com/parser/pascallike/part12.txt");
//		InputStream is = InterpretorTest.class.getClassLoader().getResourceAsStream("com/parser/pascallike/part13.txt");
		InputStream is = InterpretorTest.class.getClassLoader().getResourceAsStream("interpreter/part14.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String text = "";
		Lexer lexer = null;
		Parser parse = null;
		Visitor visitor = null;
		SemanticAnalyzer symbolVisitor = null;
		Interpretor ip = null;
		StringBuffer sb = new StringBuffer();
		
		while((text = reader.readLine()) != null) {
			sb.append(text);
		}
		lexer = new Lexer(sb.toString());
		parse = new Parser(lexer);
		ASTNode node = parse.parse();
		visitor = new NodeVisitor();
		symbolVisitor = new SemanticAnalyzer();//符号表遍历ast树
		symbolVisitor.visit(node);
//		System.out.println("symbol table Content:");
//		
//		System.out.println(symbolVisitor.symble());
//		
//		System.out.println("run-time globle memory Content:");
		
		ip = new Interpretor(node);
		ip._interpretor(visitor);
//		System.out.println(ip.globalSymbol());
	}
}
