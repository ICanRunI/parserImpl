package interpreter;

import java.util.List;

public class VarDeclNode extends ASTNode {

	private List<VarNode> vn;
	private TypeNode tn;
	
	public VarDeclNode(List<VarNode> vn, TypeNode tn) {
		this.vn = vn;
		this.tn = tn;
	}
	
	@Override
	void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	public TypeNode tynode() {
		return tn;
	}
	
	public List<VarNode> vNode() {
		return vn;
	}

}
