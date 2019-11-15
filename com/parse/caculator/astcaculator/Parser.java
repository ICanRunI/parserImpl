package astcaculator;

/**
 * 
 * @author felix.xtzhu 
 * @date 2018-2-5
 */
public class Parser {

	private Lexer lexer;
	private Token currentToken;
	
	public Parser(Lexer lexer) {
		this.lexer = lexer;
		this.currentToken = lexer.getNextToken();
	}
	
	public ASTNode parse() {
		return expr();
	}
	
	private ASTNode expr() {
		ASTNode node = term();
		while(this.currentToken.type() == Type.PLUS 
				|| this.currentToken.type() == Type.MINUS) {
			Token op = this.currentToken;
			if(this.currentToken.type() == Type.PLUS) {
				eat(Type.PLUS);
			}
			if(this.currentToken.type() == Type.MINUS) {
				eat(Type.MINUS);
			}
			node = new BinOp(node, op, term());
		}
		return node;
	}
	
	private ASTNode term() {
		ASTNode node = factor();
		
		while(this.currentToken.type() == Type.MULTIP || this.currentToken.type() == Type.DIVIS) {
			Token op = this.currentToken;
			if(this.currentToken.type() == Type.MULTIP) {
				eat(Type.MULTIP);
			}
			if(this.currentToken.type() == Type.DIVIS) {
				eat(Type.DIVIS);
			}
			node = new BinOp(node, op, factor());
		}
		return node;
	}
	
	private ASTNode factor() {
		Token token = this.currentToken;
		if(token.type() == Type.INTERGER) {
			eat(Type.INTERGER);
			return new Num(token);
		}else if(token.type() == Type.LPAREN) {
			eat(Type.LPAREN);
			ASTNode node = expr();
			eat(Type.RPAREN);
			return node;
		}else if(token.type() == Type.PLUS) {
			eat(Type.PLUS);
			return new UnaryOp(token, factor());
		}else if(token.type() == Type.MINUS) {
			eat(Type.MINUS);
			return new UnaryOp(token, factor());
		}
		throw new RuntimeException("factor 解析错误");
	}
	
	private void eat(Type type) {
		if(this.currentToken.type() == type) {
			this.currentToken = lexer.getNextToken();
		}else {
			throw new RuntimeException("不支持的语法");
		}
	}
}

