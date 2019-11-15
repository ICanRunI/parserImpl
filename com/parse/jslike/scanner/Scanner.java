package scanner;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import error.JlkError;

import static scanner.TokenType.*;

/**
 * 
 * @author felix.xtzhu 
 * @date 2018-3-13
 * 
 */
public class Scanner {
	
	private final String sources;
	private final List<Token> tokens = new ArrayList<Token>();
	
	private int current = 0;
	private int start = 0;
	private int line = 1;
	
	private static final Map<String, TokenType> keywords;

	static {
	    keywords = new HashMap<>();
	    keywords.put("and",    AND);
	    keywords.put("class",  CLASS);
	    keywords.put("else",   ELSE);
	    keywords.put("false",  FALSE);
	    keywords.put("for",    FOR);
	    keywords.put("fun",    FUN);
	    keywords.put("if",     IF);
	    keywords.put("nil",    NIL);
	    keywords.put("or",     OR);
	    keywords.put("print",  PRINT);
	    keywords.put("return", RETURN);
	    keywords.put("super",  SUPER);
	    keywords.put("this",   THIS);
	    keywords.put("true",   TRUE);
	    keywords.put("var",    VAR);
	    keywords.put("while",  WHILE);
	    keywords.put("break",  BREAK);
	 }
	
	public Scanner(String sources) {
		this.sources = sources;
	}
	
	public List<Token> scanTokens() {
		while(!isAtend()) {
			start = current;
			
			scanToken();
		}
		tokens.add(new Token(EOF, "", null, line));
		return tokens;
	}
	
	private void scanToken() {
		char c = advance();
		switch(c) {
			case '(':
				addToken(LEFT_PAREN); break;
			case ')':
				addToken(RIGHT_PAREN); break;
			case '{':
				addToken(LEFT_BRACE); break;
			case '}':
				addToken(RIGHT_BRACE); break;
			case ',': 
				addToken(COMMA); break;
			case '.': 
		    	addToken(DOT); break;
		    case '-': 
		    	addToken(MINUS); break;
		    case '+': 
		    	addToken(PLUS); break;
		    case ';': 
		    	addToken(SEMICOLON); break;
		    case '*': 
		    	addToken(STAR); break;
		    case '=':
		    	//向前扫描一个字符，可能是 = 可能是 ==
		    	addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
		    case '<':
		    	addToken(match('=') ? GREATER_EQUAL : LESS); break;
		    case '>':
		    	addToken(match('=') ? LESS_EQUAL : GREATER); break;
		    case '!':
		    	addToken(match('=') ? BANG_EQUAL : BANG); break;
		    case '/':
		    	//行注释
		    	if(match('/')) {
		    		while(peek() != '\n' && !isAtend()) advance();
		    	}else if(match('*')) {
		    		//块注释
		    		
		    	}else {
		    		//除法
		    		addToken(SLASH);
		    	}
		    	break;
		    case '"':
		    	//字符串字面量
		    	scanString();
		    	break;
		    case ' ':
		    case '\r':
		    case '\t':
		      break;
		    case '\n':
		    	line++;
		      break;
		      
		    default:
		    	if(isDigit(c)) {
		    		scanNumber();
		    	}else if (isAlpha(c)) {
		            identifier();
		        }else {
		    		JlkError.error(line, "Unexpected character.");
		    	}
		}
	}
	
	private void scanString() {
		//字符串的换行没有像java那样需要 +
		//这里用简单的处理方式，只要在  ""的包裹下就是字符串了
		while(peek() != '"' && !isAtend()) {
			if(peek() == '\n') {
				line++;
			}
			advance();
		}
		
		if(isAtend()) {
			JlkError.error(line, "Unterminated string.");
			return;
		}
		
		//跳过 "
		advance();
		
		String value = sources.substring(start + 1, current - 1);
		addToken(STRING, value);
	}
	
	private void scanNumber() {
		while(isDigit(peek())) {
			advance();
		}
		
		if(peek() == '.' && isDigit(peekNext())) {
			advance();
			
			while(isDigit(peek())) {
				advance();
			}
		}
		String value = sources.substring(start, current);
		addToken(NUMBER, Double.parseDouble(value));
	}
	
	public void identifier() {
		while(isAlphaNumeric(peek())) {
			advance();
		}
		String text = sources.substring(start, current);
		
		TokenType type = keywords.get(text);
		if(type == null) {
			type = IDENTIFIER;
		}
		addToken(type);
	}
	
	private char peekNext() {
		if(current + 1 >= sources.length()) {
			return '\0';
		}
		return sources.charAt(current + 1);
	}
	
	//类似lookahead //这里只向前看1个字符
	private char peek() {
		if(isAtend()) {
			return '\0';
		}else {
			return sources.charAt(current);
		}
		
	}
	
	private boolean isAlphaNumeric(char c) {
		return isDigit(c) || isAlpha(c);
	}
	
	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
	
	private boolean isAlpha(char c) {
		return c >= 'a' && c <= 'z' ||
			   c >= 'A' && c <= 'Z'||
			   c == '_';
 	}
	
	private boolean match(char expected) {
		if(isAtend()) {
			return false;
		}
		if(sources.charAt(current) != expected) {
			return false;
		}
		current++;
		
		return true;
	}
	
	private void addToken(TokenType type) {
		addToken(type, null);
	}
	
	public void addToken(TokenType type, Object literal) {
		String text = sources.substring(start, current);
		tokens.add(new Token(type, text, literal, line));
	}
	
	private char advance() {
		current++;
		return sources.charAt(current-1);
	}
	
	private boolean isAtend() {
		return current >= sources.length();
	}
}
