package interpreter;

public abstract class ASTNode {

	abstract void accept(Visitor visitor);
}
