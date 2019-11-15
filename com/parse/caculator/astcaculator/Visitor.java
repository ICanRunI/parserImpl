package astcaculator;

public interface Visitor {
	
	int visit(ASTNode ast);

	int visit(BinOp binOp);
	
	int visit(Num num);
	
	int visit(UnaryOp unOp);
}
