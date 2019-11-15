package interpreter;

public class ProgramNode extends ASTNode {

	private String proNa;
	private ASTNode block;
	
	public ProgramNode(String proNa, ASTNode block) {
		this.proNa = proNa;
		this.block = block;
	}
	
	@Override
	void accept(Visitor visitor) {
		visitor.visit(this);

	}
	
	public ASTNode block() {
		return this.block;
	}
	
	public String programName() {
		return proNa;
	}

}
