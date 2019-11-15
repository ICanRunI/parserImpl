package runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ast.BreakException;
import ast.Expr;
import ast.Expr.Assign;
import ast.Expr.Call;
import ast.Expr.Get;
import ast.Expr.Logical;
import ast.Expr.Set;
import ast.Expr.Super;
import ast.Expr.This;
import ast.Expr.Variable;
import ast.Return;
import ast.Stmt;
import ast.Expr.Binary;
import ast.Expr.Grouping;
import ast.Expr.Literal;
import ast.Expr.Unary;
import ast.Stmt.Block;
import ast.Stmt.Class;
import ast.Stmt.Expression;
import ast.Stmt.Function;
import ast.Stmt.If;
import ast.Stmt.Print;
import ast.Stmt.Var;
import ast.Stmt.While;
import error.JlkError;
import error.RuntimeError;
import parser.Environment;
import parser.JsLikeCallable;
import parser.impl.JsLikeClass;
import parser.impl.JsLikeFunction;
import parser.impl.JsLikeInstance;
import scanner.Token;
import scanner.TokenType;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void>{
	
	public Environment globals = new Environment();//global上下文
	
	private Environment environment = globals;
	
	private final Map<Expr, Integer> locals = new HashMap<Expr, Integer>();
	
	public Interpreter() {
		globals.define("clock", new JsLikeCallable() {
		      @Override
		      public int arity() {
		        return 0;
		      }

		      @Override
		      public Object call(Interpreter interpreter,
		                         List<Object> arguments) {
		        return (double)System.currentTimeMillis() / 1000.0;
		      }
		});
	}
	
	public void interpret(List<Stmt> statements) {
		try {
			for(Stmt statement : statements) {
				execute(statement);
			}
		} catch (RuntimeError error) {
			JlkError.runtimeError(error);
		}
	}
	
	public void resolve(Expr expr, int depth) {
	    locals.put(expr, depth);
	}
	
	private void execute(Stmt stmt) {
		stmt.accept(this);
	}
	
	@Override
	public Void visitExpressionStmt(Expression stmt) {
		evaluate(stmt.expression);
		return null;
	}

	@Override
	public Void visitPrintStmt(Print stmt) {
		Object value = evaluate(stmt.expression);
	    System.out.println(stringify(value));
		return null;
	}
	
	@Override
	public Void visitClassStmt(Class stmt) {
		environment.define(stmt.name.lexeme, null);
		Map<String, JsLikeFunction> classMethods = new HashMap<>();
		Object superClass = null;
		
		for (Stmt.Function method : stmt.classMethods) {
			JsLikeFunction function = new JsLikeFunction(method, environment, false);
		    classMethods.put(method.name.lexeme, function);
		}
		
		JsLikeClass metaclass = new JsLikeClass(stmt.name.lexeme + " metaclass", null, classMethods, null);
		
		if(stmt.superclass != null) {
			superClass = evaluate(stmt.superclass);
			if(!(superClass instanceof JsLikeClass)) {
				throw new RuntimeError(stmt.name,
			            "Superclass must be a class.");
			}
			environment = new Environment(environment);
		    environment.define("super", superClass);
		}
		Map<String, JsLikeFunction> methods = new HashMap<>();
		for (Stmt.Function method : stmt.methods) {
	      JsLikeFunction function = new JsLikeFunction(method, environment, 
	    		              method.name.lexeme.equals("init"));
	      
	      methods.put(method.name.lexeme, function);
	    }
		JsLikeClass kclass = new JsLikeClass(stmt.name.lexeme, (JsLikeClass)superClass, methods, metaclass);
		if (superClass != null) {
		      environment = environment.enclosing;
		}
		environment.assign(stmt.name, kclass);
		
		return null;
	}
	
	@Override
	public Void visitIfStmt(If stmt) {
		if(isTruthy(evaluate(stmt.condition))) {
			execute(stmt.thenBranch);
		}else if(stmt.elseBranch != null) {
			execute(stmt.elseBranch);
		}
		return null;
	}
	
	@Override
	public Void visitWhileStmt(While stmt) {
		while(isTruthy(evaluate(stmt.condition))) {
			try {
				execute(stmt.body);
			} catch (BreakException breakException) {
				break;
			}
		}
		return null;
	}
	
	@Override
	public Object visitLogicalExpr(Logical expr) {
		Object left = evaluate(expr.left);
		
		if(expr.operator.type == TokenType.OR) {
			if (isTruthy(left)) return left;
		}else {
			if (!isTruthy(left)) return left;
		}
		
		return evaluate(expr.right);
	}
	
	@Override
	public Void visitFunctionStmt(Function stmt) {
		JsLikeFunction function = new JsLikeFunction(stmt, environment, false);
	    environment.define(stmt.name.lexeme, function);
	    
		return null;
	}
	
	@Override
	public Void visitReturnStmt(Stmt.Return stmt) {
		Object value = null;
	    if (stmt.value != null) value = evaluate(stmt.value);

	    throw new Return(value);
	}
	
	@Override
	public Void visitBreakStmt(BreakException stmt) {
		throw new BreakException();
	}
	
	@Override
	public Void visitBlockStmt(Block stmt) {
		//block的上下文包含父级上下文
		executeBlock(stmt.statements, new Environment(environment));
		return null;
	}
	
	@Override
	public Void visitVarStmt(Var stmt) {
		Object value = null;
		if(stmt.initializer != null) {
			value = evaluate(stmt.initializer);
		}
		
		for(Token name : stmt.name) {
			environment.define(name.lexeme, value);
		}
		
		return null;
	}

	@Override
	public Object visitVariableExpr(Variable expr) {
		
		return lookUpVariable(expr.name, expr);
//		return environment.lookup(expr.name);
		
	}
	
	
	
	@Override
	public Object visitAssignExpr(Assign expr) {
		Object value = evaluate(expr.value);
		Integer distance = locals.get(expr);
		if (distance != null) {
		    environment.assignAt(distance, expr.name, value);
		} else {
		    globals.assign(expr.name, value);
		}
		
//		environment.assign(expr.name, value);
		return value;
	}
	
	@Override
	public Object visitCallExpr(Call expr) {
		Object callee = evaluate(expr.callee);
		List<Object> arguments = new ArrayList<Object>();
	    for (Expr argument : expr.arguments) { 
	      arguments.add(evaluate(argument));
	    }
	    //防止不是函数调用  "helle world"(); X
	    if(!(callee instanceof JsLikeCallable)) {
	    	throw new RuntimeError(expr.paren,
	    	          "Can only call functions and classes.");
	    }
	    
	    JsLikeCallable function = (JsLikeCallable)callee;
	    //函数定义参数与实际传参数量校验
	    if (arguments.size() != function.arity()) {
	        throw new RuntimeError(expr.paren, "Expected " +
	            function.arity() + " arguments but got " +
	            arguments.size() + ".");
	    }
	    return function.call(this, arguments);
	}


	@Override
	public Object visitBinaryExpr(Binary expr) {
		 Object left = evaluate(expr.left);
		 Object right = evaluate(expr.right);
		 switch (expr.operator.type) {
		 	case GREATER:
		 		checkNumberOperands(expr.operator, left, right);
		        return (double)left > (double)right;
		    case GREATER_EQUAL:
		    	checkNumberOperands(expr.operator, left, right);
		        return (double)left >= (double)right;
		    case LESS:
		    	checkNumberOperands(expr.operator, left, right);
		        return (double)left < (double)right;
		    case LESS_EQUAL:
		    	checkNumberOperands(expr.operator, left, right);
		        return (double)left <= (double)right;
		    case BANG_EQUAL: return !isEqual(left, right);
		    case EQUAL_EQUAL: return isEqual(left, right);
	      	case MINUS:
	      		checkNumberOperands(expr.operator, left, right);
	      		return (double)left - (double)right;
	      	case SLASH:
	      		checkNumberOperands(expr.operator, left, right);
	      		if((double)right == (double)0) {
	      			throw new RuntimeError(expr.operator,
		                    "除数不能是0.");
	      		}
	      		return (double)left / (double)right;
	      	case STAR:
	      		checkNumberOperands(expr.operator, left, right);
	      		return (double)left * (double)right;
	      	case PLUS:
	      		if (left instanceof Double && right instanceof Double) {
	                return (double)left + (double)right;
	            } 
	            if (left instanceof String && right instanceof String) {
	                return (String)left + (String)right;
	            }
	            if (left instanceof String || right instanceof String) {
	                return left.toString() + right.toString();
	            }
	            throw new RuntimeError(expr.operator,
	                    "操作数必须是数字或字符串.");
	    }
		 
		 return null;
	}

	@Override
	public Object visitGroupingExpr(Grouping expr) {
		return evaluate(expr.expression);
	}

	@Override
	public Object visitLiteralExpr(Literal expr) {
		
		return expr.value;
	}
	
	@Override
	public Object visitSuperExpr(Super expr) {
		int distance = locals.get(expr);
		JsLikeClass superclass = (JsLikeClass)environment.getAt(
		        distance, "super");
		JsLikeInstance instance = (JsLikeInstance)environment.getAt(distance - 1, "this");
		
		JsLikeFunction method = superclass.findMethod(instance, expr.method.lexeme);
				
		if (method == null) {
		      throw new RuntimeError(expr.method,
		          "Undefined property '" + expr.method.lexeme + "'.");
		}
		
		return method;
	}
	
	@Override
	public Object visitThisExpr(This expr) {
		
		return lookUpVariable(expr.keyword, expr);
	}

	@Override
	public Object visitUnaryExpr(Unary expr) {
		Object right = expr.accept(this);
		switch (expr.operator.type) {
			case BANG:
	        return !isTruthy(right);
	      case MINUS:
	    	//运行时错误检查
	    	checkNumberOperand(expr.operator, right);
	        return -(double)right;
	    }

		return (double)right;
	}
	
	private boolean isEqual(Object a, Object b) {
	    if (a == null && b == null) return true;
	    if (a == null) return false;

	    return a.equals(b);
	}
	
	private void checkNumberOperands(Token operator,
            Object left, Object right) {
		if (left instanceof Double && right instanceof Double) return;
		
		throw new RuntimeError(operator, "Operands must be numbers.");
	}
	
	private void checkNumberOperand(Token operator, Object operand) {
	    if (operand instanceof Double) return;
	    throw new RuntimeError(operator, "Operand must be a number.");
	}
	
	private boolean isTruthy(Object object) {
	    if (object == null) return false;
	    if (object instanceof Boolean) return (boolean)object;
	    return true;
	}
	
	private String stringify(Object value) {
		if(value == null) {
			return "nil";
		}
		if(value instanceof Double) {
			String text = value.toString();
			if(text.endsWith(".0")) {
				text = text.substring(0, text.length() - 2);
			}
			return text;
		}

		return value.toString();
	}

	private Object evaluate(Expr expr) {
	    return expr.accept(this);
	}
	
	public void executeBlock(List<Stmt> statements, Environment environment) {
		Environment prevous = this.environment;
		try {
			this.environment = environment;
			
			for(Stmt statement : statements) {
				execute(statement);
			}
		} finally {
			this.environment = prevous;
		}
		
	}
	
	private Object lookUpVariable(Token name, Expr expr) {
		Integer distance = locals.get(expr);
		if (distance != null) {
		    return environment.getAt(distance, name.lexeme);
	    } else {
	        return globals.lookup(name);
	    }
	}

	@Override
	public Object visitGetExpr(Get expr) {
		Object object = evaluate(expr.object);
		if(object instanceof JsLikeInstance) {
			return ((JsLikeInstance) object).get(expr.name);
		}
		
		//如果不是对象，不允许访问字段
		throw new RuntimeError(expr.name,
		        "Only instances have properties.");
	}
	
	@Override
	public Object visitSetExpr(Set expr) {
		Object object = evaluate(expr.object);

	    if (!(object instanceof JsLikeInstance)) { 
	      throw new RuntimeError(expr.name, "Only instances have fields.");
	    }

	    Object value = evaluate(expr.value);
	    ((JsLikeInstance)object).set(expr.name, value);
	    return value;
	}

	@Override
	public Void visitBreakStmt(Stmt.Break stmt) {
		// TODO Auto-generated method stub
		return null;
	}

}
