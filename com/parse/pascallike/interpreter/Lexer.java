package interpreter;

import java.util.HashMap;
import java.util.Map;

public class Lexer {

	private String text;
	private int pos;
	private char currentChar;
	
	private static Map<String, Token> RESERVED_KEYWORDS = new HashMap<String, Token>();
	
	static {
		//关键字声明
		RESERVED_KEYWORDS.put("BEGIN", new Token(Type.BEGIN, "BEGIN"));
		RESERVED_KEYWORDS.put("END", new Token(Type.END, "END"));
		RESERVED_KEYWORDS.put("PROGRAM", new Token(Type.PROGRAM, "PROGRAM"));
		RESERVED_KEYWORDS.put("VAR", new Token(Type.VAR, "VAR"));
		RESERVED_KEYWORDS.put("DIV", new Token(Type.INTEGER_DIV, "DIV"));
		RESERVED_KEYWORDS.put("INTEGER", new Token(Type.INTEGER, "INTEGER"));
		RESERVED_KEYWORDS.put("REAL", new Token(Type.REAL, "REAL"));
		RESERVED_KEYWORDS.put("PROCEDURE", new Token(Type.PROCEDURE, "PROCEDURE"));
		RESERVED_KEYWORDS.put("procedure", new Token(Type.PROCEDURE, "procedure"));
		RESERVED_KEYWORDS.put("integer", new Token(Type.INTEGER, "integer"));
		RESERVED_KEYWORDS.put("real", new Token(Type.REAL, "real"));
		RESERVED_KEYWORDS.put("begin", new Token(Type.BEGIN, "begin"));
		RESERVED_KEYWORDS.put("end", new Token(Type.END, "end"));
		RESERVED_KEYWORDS.put("program", new Token(Type.PROGRAM, "program"));
		RESERVED_KEYWORDS.put("var", new Token(Type.VAR, "var"));
		RESERVED_KEYWORDS.put("div", new Token(Type.INTEGER_DIV, "div"));
	}
	
	public Lexer(String text) {
		this.text = text;
		this.pos = 0;
		this.currentChar = text.charAt(pos);
	}
	
	public Token get_next_token() {
		
		while(this.pos != Integer.MIN_VALUE) {
			skipWhiteSpace();//去除空白符
			skipEnter();
			//去除注释
			if(this.currentChar == '{') {
				next_char();
				skipComment();
				continue;
			}
			if(isDigit(this.currentChar)) {
//				return new Token(Type.INTEGER, integer());
				return number();
			}
			//声明的变量
			if(isAlnum(this.currentChar)) {
				return _id();
			}
			
			if(this.currentChar == ':' && peek() == '=') {
				next_char();
				next_char();
				return new Token(Type.ASSIGN, ":=");
			}
			
			if(this.currentChar == ';') {
				next_char();
				return new Token(Type.SEMI, ";");
			}
			
			if(this.currentChar == ':') {
				next_char();
				return new Token(Type.COLON, ":");
			}
			
			if(this.currentChar == ',') {
				next_char();
				return new Token(Type.COMMA, ",");
			}
			
			if(this.currentChar == '+') {
				next_char();
				return new Token(Type.PLUS, "+");
			}
			if(this.currentChar == '-') {
				next_char();
				return new Token(Type.MINUS, "-");
			}
			if(this.currentChar == '*') {
				next_char();
				return new Token(Type.MUL, "*");
			}
			if(this.currentChar == '/') {
				next_char();
//				return new Token(Type.DIV, "/");
				return new Token(Type.FLOAT_DIV, "/");
			}
			
			if(this.currentChar == '(') {
				next_char();
				return new Token(Type.LPAREN, "(");
			}
			
			if(this.currentChar == ')') {
				next_char();
				return new Token(Type.RPAREN, ")");
			}
			
			if(this.currentChar == '.') {
				next_char();
				return new Token(Type.DOT, ".");
			}
		}
		
		return new Token(Type.EOF, null);
	}
	
	//Return a (multidigit) integer or float consumed from the input.
	private Token number() {
		StringBuffer sb = new StringBuffer();
		while(isDigit(this.currentChar) && this.pos != Integer.MIN_VALUE) {
			sb.append(this.currentChar);
			next_char();
		}
		//处理小数
		if(this.currentChar == '.') {
			sb.append(this.currentChar);
			next_char();
			
			while(isDigit(this.currentChar) && this.pos != Integer.MIN_VALUE) {
				sb.append(this.currentChar);
				next_char();
			}
			return new Token(Type.REAL_CONST, sb.toString());
		}else {
			return new Token(Type.INTEGER_CONST, sb.toString());
		}
	}
	//返回扫描到的数字
	/*private String integer() {
		StringBuffer sb = new StringBuffer();
		while(isDigit(this.currentChar) && this.pos != Integer.MIN_VALUE) {
			sb.append(this.currentChar);
			next_char();
		}
		
		return sb.toString();
	}*/
	
	//返回扫描到的变量
	private Token _id() {
		StringBuffer sb = new StringBuffer();
		while(isAlnum(this.currentChar) || isDigit(this.currentChar) && this.pos != Integer.MIN_VALUE) {
			sb.append(this.currentChar);
			next_char();
		}
		Token token = RESERVED_KEYWORDS.get(sb.toString());
		return token != null ? token : new Token(Type.ID, sb.toString());
	}
	
	private void next_char() {
		pos++;
		if(pos >= text.length()) {
			pos = Integer.MIN_VALUE;
			return;
		}
		this.currentChar = text.charAt(pos);
		
	}
	
	//超前扫描一个字符
	private char peek() {
		int peek_pos = this.pos + 1;
		if(peek_pos >= text.length()) {
			//字符到末尾了。
			return '\0';
		}
		return text.charAt(peek_pos);
	}
	
	//忽略注释
	private void skipComment() {
		while(this.currentChar != '}') {
			next_char();
		}
		//跳过 }
		next_char();
	}
	
	private void skipWhiteSpace() {
		while(this.currentChar == ' ' && this.pos != Integer.MIN_VALUE) {
			next_char();
		}
	}
	
	private void skipEnter() {
		while(this.currentChar == '\n' 
				|| this.currentChar == '\t' && this.pos != Integer.MIN_VALUE) {
			next_char();
		}
	}
	
	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
	
	private boolean isAlnum(char c) {
		return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
	}
	
	private void error(String msg) {
		throw new RuntimeException(msg);
	}
}
