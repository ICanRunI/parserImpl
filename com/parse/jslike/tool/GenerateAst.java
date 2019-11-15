package tool;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

//����ast������Ĺ�����
public class GenerateAst {

	public static void main(String[] args) throws IOException {
	    if (args.length != 1) {
	      System.err.println("Usage: generate_ast <output directory>");
	      System.exit(1);
	    }
	    //�������·��
	    String outputDir = args[0];
	    
	    defineAst(outputDir, "Expr", Arrays.asList(
	    	      "Binary   : Expr left, Token operator, Expr right",
	    	      "Grouping : Expr expression",
	    	      "Get      : Expr object, Token name",
	    	      "Call     : Expr callee, Token paren, List<Expr> arguments",
	    	      "Literal  : Object value",
	    	      "Logical  : Expr left, Token operator, Expr right",
	    	      "Set      : Expr object, Token name, Expr value",
	    	      "Unary    : Token operator, Expr right",
	    	      "This     : Token keyword",
	    	      "Super    : Token keyword, Token method",
	    	      "Variable : Token name",
	    	      "Assign   : Token name, Expr value"
	    	    ));
	    
	    defineAst(outputDir, "Stmt", Arrays.asList(
	    		  "Block      : List<Stmt> statements",
	    		  "Expression : Expr expression",
	    		  "If         : Expr condition, Stmt thenBranch, Stmt elseBranch",
	    	      "Print      : Expr expression",
	    	      "Var        : List<Token> name, Expr initializer",
	    	      "Class      : Token name, Expr superclass, List<Stmt.Function> methods, List<Stmt.Function> classMethods",
	    	      "Return     : Token keyword, Expr value",
	    	      "Break      : ",
	    	      "While      : Expr condition, Stmt body",
	    	      "Function   : Token name, List<Token> parameters, List<Stmt> body"
	    	    ));
	}
	
	/**
	 * 
	 * @author felix.xtzhu
	 * @date 2018-3-15  
	 * @param @param outputDir
	 * @param @param baseName ��������
	 * @param @param types �������� �Լ����� ����
	 * @param @throws IOException
	 *
	 */
	private static void defineAst(
		      String outputDir, String baseName, List<String> types)
		      throws IOException {
		String path = outputDir + File.separator + baseName + ".java";
	    PrintWriter writer = new PrintWriter(path, "UTF-8");
	    writer.println("package com.intepreter.ast;");
	    writer.println("");
	    writer.println("import java.util.List;");
	    writer.println("");
	    writer.println("public abstract class " + baseName + " {");
	    
	    defineVisitor(writer, baseName, types);
	    
	    
	    
	    for(String type : types) {
	    	String className = type.split(":")[0].trim();
	        String fields = type.split(":")[1].trim(); 
	        defineType(writer, baseName, className, fields);
	    }
	    writer.println("");
	    writer.println("     public abstract <T> T accept(Visitor<T> visitor);");
	    
	    writer.println("}");
	    writer.close();
	}
	
	private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
		writer.println(" public interface Visitor<T> {");
		for (String type : types) {
	      String typeName = type.split(":")[0].trim();
	      writer.println("    T visit" + typeName + baseName + "(" +
	          typeName + " " + baseName.toLowerCase() + ");");
	    }
		writer.println("  }");
	}
	
	private static void defineType(
		      PrintWriter writer, String baseName,
		      String className, String fieldList) {
			writer.println(" public static class " + className + " extends " +
		        baseName + " {");
			
			String[] fields = fieldList.split(", ");
			if(!"".equals(fields[0])) {
				for (String field : fields) {
				      writer.println("     public final " + field + ";");
				}
			}
			

		   //������
		    writer.println("    public " + className + "(" + fieldList + ") {");

		    //���Ը�ֵ
		    
		    if(!"".equals(fields[0])) {
		    	
		    	for (String field : fields) {
				      String name = field.split(" ")[1];
				      writer.println("      this." + name + " = " + name + ";");
				 }
		    	
		    	
		    }
		    

		    writer.println("    }");
		    
		    writer.println();
		    writer.println("     public <T> T accept(Visitor<T> visitor) {");
		    writer.println("      return visitor.visit" +
		        className + baseName + "(this);");
		    writer.println("    }");

		    //���Զ���
		    writer.println();

		    writer.println("  }");
	}
}
