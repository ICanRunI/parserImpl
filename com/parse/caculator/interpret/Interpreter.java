package interpret;

/**
 * 
 * @author felix.xtzhu 
 * @date 2018-2-2
 * 这是parse tree的版本
 * 
 * 支持 1+1=2
 * 支持 1 + 2 = 3 空格skin 以及两位数以上 加减
 * 支持 多加 或 多乘 例如  1 + 2 + 3  或 2 * 2 * 2
 * 支持算数优先级和 带（）优先级 例如 1 + 2 * 3 或 3 * （7 + 3）
 *  
 * 
 */
public class Interpreter {

	
	private Token currentToken;
	private Lexer lexer;
	
	
	public Interpreter(Lexer lexer) {
		this.lexer = lexer;
		this.currentToken = lexer.getNextToken();
	}
	
	public int expr() {
		int result = 0;
		
		result = term();
		
		while(this.currentToken.type() == Type.PLUS || this.currentToken.type() == Type.MINUS) {
			if(this.currentToken.type() == Type.PLUS) {
				eat(Type.PLUS);
				result = result + term();
			}
			if(this.currentToken.type() == Type.MINUS) {
				eat(Type.MINUS);
				result = result - term();
			}
		}
		return result;
	}
	
	//优先级低
	private int term() {
		int result = factor();
		while(this.currentToken.type() == Type.MULTIP || this.currentToken.type() == Type.DIVIS) {
			if(this.currentToken.type() == Type.MULTIP) {
				eat(Type.MULTIP);
				result = result * factor();
			}
			if(this.currentToken.type() == Type.DIVIS) {
				eat(Type.DIVIS);
				result = result / factor();
			}
		}
		return result;
	}
	
	//优先级高
	private int factor() {
		Token tk = this.currentToken;
		if(this.currentToken.type() == Type.INTERGER) {
			
			eat(Type.INTERGER);
			return Integer.valueOf(tk.value());
		}else {
			//如果不是数字就是括号
			eat(Type.LPAREN);
			int result = expr();
			eat(Type.RPAREN);
			return result;
		}
	}
	
	private void eat(Type type) {
		if(this.currentToken.type() == type) {
			this.currentToken = lexer.getNextToken();
		}else {
			throw new RuntimeException("解析错误，语法不支持");
		}
	}
	
}
