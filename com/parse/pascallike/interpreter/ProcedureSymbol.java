package interpreter;

import java.util.List;

public class ProcedureSymbol extends Symbol {
	
	private List<ASTNode> params;

	public ProcedureSymbol(String name, Type type, List<ASTNode> params) {
		super(name, type);
		this.params = params;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("name:").append(getName())
		  .append("type:").append(getType())
		  .append("params:").append(this.params.toString());
		return sb.toString();
	}
}
