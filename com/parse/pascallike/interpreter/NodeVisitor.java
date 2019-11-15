package interpreter;

import java.util.List;

public class NodeVisitor implements Visitor {

	@Override
	public Number visit(ASTNode ast) {
		if(ast instanceof ProgramNode) {
			visit((ProgramNode)ast);
		}
		if(ast instanceof BlockNode) {
			visit((BlockNode)ast);
		}
		if(ast instanceof Compound) {
			visit((Compound)ast);
		}
		if(ast instanceof BinOp) {
			return visit((BinOp)ast);
		}
		if(ast instanceof UnaryOp) {
			return visit((UnaryOp)ast);
		}
		if(ast instanceof VarNode) {
			return visit((VarNode)ast);
		}
		if(ast instanceof AssignNode) {
			visit((AssignNode)ast);
		}
		if(ast instanceof NoOp)  {
			visit((NoOp)ast);
		}
		if(ast instanceof Num) {
			return visit((Num)ast);
		}
		return null;
	}
	
	@Override
	public void visit(ProgramNode prona) {
		
		visit(prona.block());
	}
	
	@Override
	public void visit(ProcedureDeclNode procDeclNode) {
		
	}

	@Override
	public void visit(Compound com) {
		List<ASTNode> childs = com.children();
		for(ASTNode child : childs) {
			visit(child);
		}
		
	}

	@Override
	public void visit(BlockNode bn) {
		for(ASTNode dec : bn.decNodes()) {
			visit(dec);
		}
		
		visit(bn.compoundStates());
	}
	

	@Override
	public void visit(TypeNode tn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(VarDeclNode vdnode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Number visit(BinOp binOp) {
		Number left = visit(binOp.left());
		Number right = visit(binOp.right());
		
		if(binOp.op().type() == Type.PLUS) {
			if(left instanceof Float) {
				return left.floatValue() + right.floatValue();
			}else {
				return left.intValue() + right.intValue();
			}
		}else if(binOp.op().type() == Type.MINUS) {
			if(left instanceof Float) {
				return left.floatValue() - right.floatValue();
			}else {
				return left.intValue() - right.intValue();
			}
		}else if(binOp.op().type() == Type.MUL) {
			if(left instanceof Float) {
				return left.floatValue() * right.floatValue();
			}else {
				return left.intValue() * right.intValue();
			}
		}else if(binOp.op().type() == Type.INTEGER_DIV) {
			return left.intValue() / right.intValue();
		}else if(binOp.op().type() == Type.FLOAT_DIV) {
			return left.floatValue() / right.floatValue();
		}else {
			throw new RuntimeException("visit Binop错误");
		}
	}

	@Override
	public Number visit(Num num) {
		Token token = num.token();
		if(token.type() == Type.INTEGER_CONST) {
			return Integer.valueOf(token.value());
			
		}else {
			return Float.valueOf(token.value());
		}
	}

	@Override
	public Number visit(UnaryOp unOp) {
		Number expr = visit(unOp.expr());
		if(unOp.op().type() == Type.PLUS) {
			if(expr instanceof Float) {
				return expr.floatValue();
			}else {
				return expr.intValue();
			}
		}else if(unOp.op().type() == Type.MINUS) {
			if(expr instanceof Float) {
				return -expr.floatValue();
			}else {
				return -expr.intValue();
			}
		}
		throw new RuntimeException("visit UnaryOp错误");
	}

	@Override
	public Number visit(VarNode var) {
		String key = var.value();
		Object value = Interpretor.globalSymbol().get(key);
		if(value != null) {
			if(value instanceof Float) {
				return (Float)value;
			}else {
				return (Integer)value;
			}
		}else {
			Interpretor.globalSymbol().put(key, new Object());
		}
		
		return null;
	}

	@Override
	public void visit(AssignNode assign) {

		String key = assign.left().value();
		
		Interpretor.globalSymbol().put(key,visit(assign.right()));
	}

	@Override
	public void visit(NoOp np) {
		//不做任何事
	}

}
