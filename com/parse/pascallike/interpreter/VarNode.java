package interpreter;

public class VarNode extends ASTNode {

	private Token token;
	
	public VarNode(Token token) {
		this.token = token;
	}
	
	@Override
	void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	public String value() {
		return token.value();
	}

}
