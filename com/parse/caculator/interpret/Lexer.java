package interpret;

public class Lexer {
	
	private String text;
	private int pos;
	private char currentChar;

	public Lexer(String text) {
		this.text = text;
		this.currentChar = text.charAt(0);
	}
	
	public Token getNextToken() {
		
		if(this.pos == Integer.MIN_VALUE) {
			return new Token(Type.EOF, null);
		}
		
		skipWhiteSpace();
		
		char c = this.currentChar;
		
		if(isDigit(c)) {
			return new Token(Type.INTERGER, scanDigit());
		}
		
		if(c == '+') {
			nextChar();
			return new Token(Type.PLUS, new StringBuffer().append(c).toString());
		}
		if(c == '-') {
			nextChar();
			return new Token(Type.MINUS, new StringBuffer().append(c).toString());
		}
		if(c == '*') {
			nextChar();
			return new Token(Type.MULTIP, new StringBuffer().append(c).toString());
		}
		if(c == '/') {
			nextChar();
			return new Token(Type.DIVIS, new StringBuffer().append(c).toString());
		}
		if(c == '(') {
			nextChar();
			return new Token(Type.LPAREN, new StringBuffer().append(c).toString());
		}
		if(c == ')') {
			nextChar();
			return new Token(Type.RPAREN, new StringBuffer().append(c).toString());
		}
		throw new RuntimeException("不支持的token");
	}

	private void skipWhiteSpace() {
		while(this.currentChar == ' ') {
			nextChar();
		}
	}
	
	private String scanDigit() {
		StringBuffer sb = new StringBuffer();
		while(isDigit(this.currentChar)) {
			sb.append(this.currentChar);
			nextChar();
			if(pos == Integer.MIN_VALUE) {
				break;
			}
		}
		return sb.toString();
	}
	
	private boolean isDigit(char ch) {
		return ch >= '0' && ch <= '9';
	}
	
	private void nextChar() {
		pos++;
		if(pos >= text.length()) {
			pos = Integer.MIN_VALUE;
			return;
		}
		
		this.currentChar = text.charAt(pos);
	}
}
