package parser;

import java.util.List;

import runtime.Interpreter;

public interface JsLikeCallable {

	int arity();
	
	Object call(Interpreter interpreter, List<Object> arguments);
}
