package interpreter;

import java.util.List;

public class ProcedureDeclNode extends ASTNode {

	private String procNa;
	private List<ASTNode> params;
	
	private ASTNode block;
	
	public ProcedureDeclNode(String procNa, List<ASTNode> params, ASTNode block) {
		this.procNa = procNa;
		this.block = block;
		this.params = params;
	}
	
	@Override
	void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	public String procNa() {
		return this.procNa;
	}
	
	public List<ASTNode> params() {
		return this.params;
	}
	
	public ASTNode block() {
		return this.block;
	}

}
