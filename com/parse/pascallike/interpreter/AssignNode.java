package interpreter;

public class AssignNode extends ASTNode {
	
	private VarNode left;
	private Token token;
	private ASTNode right;
	
	public AssignNode(VarNode left, Token token, ASTNode right) {
		this.left = left;
		this.token = token;
		this.right = right;
	}

	@Override
	void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public VarNode left() {
		return left;
	}

	public Token token() {
		return token;
	}

	public ASTNode right() {
		return right;
	}
}
