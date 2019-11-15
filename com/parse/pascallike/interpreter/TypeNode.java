package interpreter;

public class TypeNode extends ASTNode{
	
	private Token type;
	
	public TypeNode(Token type) {
		this.type = type;
	}

	@Override
	void accept(Visitor visitor) {
		
	}
	
	public Token type() {
		return type;
	}

}
