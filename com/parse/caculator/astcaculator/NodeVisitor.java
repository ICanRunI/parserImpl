package astcaculator;

public class NodeVisitor implements Visitor {

	@Override
	public int visit(ASTNode ast) { 
		if(ast instanceof BinOp) {
			return visit((BinOp)ast);
		}
		if(ast instanceof Num) {
			return visit((Num)ast);
		}
		if(ast instanceof UnaryOp) {
			return visit((UnaryOp)ast);
		}
		throw new RuntimeException("ast½âÎö´íÎó");
	}

	@Override
	public int visit(BinOp binOp) {
		
		Token op = binOp.op();
		if(op.type() == Type.PLUS) {
			return visit(binOp.left()) + visit(binOp.right());
		}else if(op.type() == Type.MINUS) {
			return visit(binOp.left()) - visit(binOp.right());
		}else if(op.type() == Type.MULTIP) {
			return visit(binOp.left()) * visit(binOp.right());
		}else if(op.type() == Type.DIVIS) {
			return visit(binOp.left()) / visit(binOp.right());
		}
		
		throw new RuntimeException("visitBinop ½âÎö´íÎó");
	}

	@Override
	public int visit(Num num) {
		Token token = num.token();
		return Integer.valueOf(token.value());
	}

	@Override
	public int visit(UnaryOp unOp) {
		Token op = unOp.op();
		if(op.type() == Type.PLUS) {
			return visit(unOp.expr());
		}else if(op.type() == Type.MINUS){
			return -visit(unOp.expr());
		}
		throw new RuntimeException("visitUnaryOP ½âÎö´íÎó");
	}
	
}
