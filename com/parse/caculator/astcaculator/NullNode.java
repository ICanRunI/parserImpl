package astcaculator;

public class NullNode extends ASTNode {

	@Override
	void accept(Visitor visitor) {
		visitor.visit(this);
	}

}
