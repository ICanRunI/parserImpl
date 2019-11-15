package interpreter;

import java.util.List;
import java.util.Map;

public class SemanticAnalyzer implements Visitor {
	
	private ScopedSymbolTable currentScope;
	
	public Map<String, Symbol> symble() {
		return currentScope.symtab();
	}

	@Override
	public Number visit(ASTNode ast) {
		if(ast instanceof ProgramNode) {
			visit((ProgramNode)ast);
		}
		if(ast instanceof Compound) {
			visit((Compound)ast);
		}
		if(ast instanceof ProcedureDeclNode) {
			visit((ProcedureDeclNode)ast);
		}
		if(ast instanceof BlockNode) {
			visit((BlockNode)ast);
		}
		if(ast instanceof BinOp) {
			return visit((BinOp)ast);
		}
		if(ast instanceof UnaryOp) {
			return visit((UnaryOp)ast);
		}
		if(ast instanceof VarDeclNode) {
			visit((VarDeclNode)ast);
		}
		if(ast instanceof AssignNode) {
			visit((AssignNode)ast);
		}
		if(ast instanceof VarNode) {
			visit((VarNode)ast);
		}
		if(ast instanceof TypeNode) {
			visit((TypeNode)ast);
		}
		if(ast instanceof NoOp) {
			visit((NoOp)ast);
		}
		return null;
	}

	@Override
	public void visit(Compound com) {
		List<ASTNode> childs = com.children();
		for(ASTNode child : childs) {
			visit(child);
		}

	}
	
	@Override
	public void visit(ProcedureDeclNode procDeclNode) {
		String procNa = procDeclNode.procNa();
		List<ASTNode> params = procDeclNode.params();
		System.out.println("enter scope: "+procNa);
		ScopedSymbolTable procedureScope =  new ScopedSymbolTable(procNa, this.currentScope.level()+1, currentScope);
		
		ProcedureSymbol procSymbol = new ProcedureSymbol(procNa, null, params);
		currentScope.defind(procSymbol);
		this.currentScope = procedureScope;
		if(params != null) {
			for(ASTNode param : params) {
				ParamNode p = (ParamNode)param;
				List<VarNode> varNodes = p.varNode();
				if(varNodes != null) {
					for(VarNode varN : varNodes) {
						Symbol s = this.currentScope.lookup(p.typeNode().type().value(), false);
						VarSymbol v = new VarSymbol(varN.value(), s.getType());
						currentScope.defind(v);
					}
				}
			}
		}
		visit(procDeclNode.block());
		
		this.currentScope = currentScope.enclosingScope();
		
		System.out.println("leave scope: "+ procNa);
	}

	@Override
	public Number visit(BinOp binOp) {
		Object left = visit(binOp.left());
		Object right = visit(binOp.right());
		if(!(left instanceof Number) || !(right instanceof Number)) {
			throw new RuntimeException("不是数值类型不能参与运算！");
		}
		
		return null;
	}

	@Override
	public Number visit(Num num) {
		return null;
	}

	@Override
	public Number visit(UnaryOp unOp) {
		visit(unOp.expr());
		return null;
	}

	@Override
	public Number visit(VarNode var) {
		String varNa = var.value();
		Symbol symbol = currentScope.lookup(varNa,false);
		if(symbol == null) {
			throw new RuntimeException("变量"+varNa+"还未声明，请先声明在使用");
		}
		return null;
	}

	@Override
	public void visit(AssignNode assign) {
		String varNa = assign.left().value();
		Symbol symbol = currentScope.lookup(varNa, false);
		if(symbol == null) {
			throw new RuntimeException("变量"+varNa+"还未声明，请先声明在使用");
		}
		visit(assign.right());
	}

	@Override
	public void visit(NoOp np) {

	}

	@Override
	public void visit(BlockNode bn) {
		for(ASTNode dec : bn.decNodes()) {
			visit(dec);
		}
		visit(bn.compoundStates());

	}

	@Override
	public void visit(ProgramNode prona) {
		String proNa = prona.programName();
		System.out.println("enter global scope；"+proNa);
		ScopedSymbolTable globalScope = new ScopedSymbolTable("global", 1, null);
		currentScope = globalScope;
		
		visit(prona.block());
		
		System.out.println(globalScope);
		this.currentScope = globalScope.enclosingScope();
		
		System.out.println("leave global scope;"+proNa);
	}

	@Override
	public void visit(TypeNode tn) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(VarDeclNode vdnode) {
		String type_name = vdnode.tynode().type().value();
		Symbol symbol = currentScope.lookup(type_name, false);
		List<VarNode> vns = vdnode.vNode();
		
		for(VarNode vn : vns) {
			if(currentScope.lookup(type_name, true) == null) {
				throw new RuntimeException("错误，不支持该类型的变量: "+type_name);
			}
			currentScope.defind(new VarSymbol(vn.value(), symbol.getType()));
		}
	}
	

}
