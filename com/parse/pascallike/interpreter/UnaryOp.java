package interpreter;

//支持 正（+） 负（-） 号
public class UnaryOp extends ASTNode {

	private ASTNode expr;
	private Token op;
	
	public UnaryOp(Token op, ASTNode expr) {
		this.op = op;
		this.expr = expr;
	}
	
	@Override
	void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public ASTNode expr() {
		return this.expr;
	}
	
	public Token op() {
		return this.op;
	}
}
