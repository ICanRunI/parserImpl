package parser.impl;

import java.util.List;

import ast.Return;
import ast.Stmt;
import parser.Environment;
import parser.JsLikeCallable;
import runtime.Interpreter;

public class JsLikeFunction implements JsLikeCallable {

	private final Stmt.Function declaration;
	
	private final Environment closure;
	
	private final boolean isInitializer;
	
	public JsLikeFunction(Stmt.Function declaration, Environment closure, boolean isInitializer) {
		this.closure = closure;
	    this.declaration = declaration;
	    this.isInitializer = isInitializer;
	}
	
	@Override
	public int arity() {
		
		return declaration.parameters.size();
	}

	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		Environment environment = new Environment(closure);
		for (int i = 0; i < declaration.parameters.size(); i++) {
		      environment.define(declaration.parameters.get(i).lexeme,
		          arguments.get(i));
		}
		try {
			interpreter.executeBlock(declaration.body, environment);
			
		} catch (Return returnValue) {
			return returnValue.value;
		}
		
		if (isInitializer) 
			return closure.getAt(0, "this");
		
		return null;
	}
	
	public JsLikeFunction bind(JsLikeInstance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);
        return new JsLikeFunction(declaration, environment, isInitializer);
    }
	
	@Override
	public String toString() {
	    return "<fn " + declaration.name.lexeme + ">";
	}

}
