package interpreter;

import java.util.List;

public class BlockNode extends ASTNode {
	
	private List<ASTNode> declaration_nodes;
	private ASTNode compound_statement_node;
	
	public BlockNode(List<ASTNode> declaration, ASTNode compound) {
		this.declaration_nodes = declaration;
		this.compound_statement_node = compound;
	}

	@Override
	void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	public List<ASTNode> decNodes() {
		return this.declaration_nodes;
	}
	
	public ASTNode compoundStates() {
		return this.compound_statement_node;
	}

}
