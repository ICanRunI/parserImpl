package interpreter;

import java.util.List;

public class ParamNode extends ASTNode {
	
	private List<VarNode> varNode;
	
	private TypeNode tpNode;
	
	public ParamNode(List<VarNode> varNode, TypeNode tpNode) {
		this.varNode = varNode;
		this.tpNode = tpNode;
	}

	@Override
	void accept(Visitor visitor) {
		
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if(varNode != null) {
			for(VarNode v : varNode) {
				sb.append("var:")
				  .append(v.value()).append(",");
			}
		}
		sb.append("; type:").append(tpNode.type().value());
		return sb.toString();
	}

	public List<VarNode> varNode() {
		return this.varNode;
	}
	
	public TypeNode typeNode() {
		return this.tpNode;
	}
}
