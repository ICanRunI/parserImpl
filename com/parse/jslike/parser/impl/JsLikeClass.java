package parser.impl;

import java.util.List;
import java.util.Map;

import parser.JsLikeCallable;
import runtime.Interpreter;

public class JsLikeClass extends JsLikeInstance implements JsLikeCallable{

	public final String name;
	
	private final Map<String, JsLikeFunction> methods;
	
	private final JsLikeClass superClass;
	
    public JsLikeClass(String name, JsLikeClass superClass, Map<String, JsLikeFunction> methods, JsLikeClass metaclass) {
    	super(metaclass);
    	this.name = name;
    	this.superClass = superClass;
    	this.methods = methods;
    	
    }
    
    public JsLikeFunction findMethod(JsLikeInstance instance, String name) {
    	if(methods.containsKey(name)) {
    		return methods.get(name).bind(instance);
    	}
    	
    	if(superClass != null) {
    		return superClass.findMethod(instance, name);
    	}
    	
    	return null;
    }
    
    @Override
    public String toString() {
        return name;
    }

	@Override
	public int arity() {
		JsLikeFunction initializer = methods.get("init");
	    if (initializer == null) return 0;
	    return initializer.arity();
	}

	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		JsLikeInstance instance = new JsLikeInstance(this);
		
		JsLikeFunction initializer = methods.get("init");
	    if (initializer != null) {
	      initializer.bind(instance).call(interpreter, arguments);
	    }
	    
		return instance;
	}
}
