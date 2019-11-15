package interpreter;

public interface Visitor {
	
	Number visit(ASTNode ast);
	
	void visit(ProcedureDeclNode procDeclNode);
	
	void visit(Compound com);

	Number visit(BinOp binOp);
	
	Number visit(Num num);
	
	Number visit(UnaryOp unOp);
	
	Number visit(VarNode var);
	
	void visit(AssignNode assign);
	
	void visit(NoOp np);
	
	void visit(BlockNode bn);
	
	void visit(ProgramNode prona);
	
	void visit(TypeNode tn);
	
	void visit(VarDeclNode vdnode);
	
}
