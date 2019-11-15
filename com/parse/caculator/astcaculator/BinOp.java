package astcaculator;

public class BinOp extends ASTNode {

	private ASTNode left;
	private Token op;
	private ASTNode right;
	
	public BinOp(ASTNode left, Token op, ASTNode right) {
		this.left = left;
		this.op = op;
		this.right = right;
	}

	@Override
	void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	public ASTNode left() {
		return this.left;
	}
	
	public Token op() {
		return this.op;
	}
	
	public ASTNode right() {
		return this.right;
	}
}
