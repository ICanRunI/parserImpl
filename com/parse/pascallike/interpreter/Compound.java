package interpreter;

import java.util.ArrayList;
import java.util.List;

public class Compound extends ASTNode {
	
	private List<ASTNode> children;
	
	public Compound() {
		this.children = new ArrayList<ASTNode>();
	}

	@Override
	void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	public void addChild(ASTNode astNode) {
		children.add(astNode);
	}
	
	public List<ASTNode> children() {
		return this.children;
	}

}
