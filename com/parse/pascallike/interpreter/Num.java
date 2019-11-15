package interpreter;

public class Num extends ASTNode {
	
	private Token token;
	
	public Num(Token token) {
		this.token = token;
	}

	@Override
	void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	public Token token() {
		return this.token;
	}

}
