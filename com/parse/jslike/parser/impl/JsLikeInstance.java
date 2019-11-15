package parser.impl;

import java.util.HashMap;
import java.util.Map;

import error.RuntimeError;
import scanner.Token;

public class JsLikeInstance {

	private JsLikeClass kclass;
	
	private final Map<String, Object> fields = new HashMap<>();
	
	public JsLikeInstance(JsLikeClass kclass) {
		this.kclass = kclass;
	}
	
	public Object get(Token name) {
		if (fields.containsKey(name.lexeme)) {
		      return fields.get(name.lexeme);
		}
		
		JsLikeFunction method = kclass.findMethod(this, name.lexeme);
		
	    if (method != null) 
	    	return method;
		
		throw new RuntimeError(name, 
		        "Undefined property '" + name.lexeme + "'.");
	}
	
	public void set(Token name, Object value) {
	    fields.put(name.lexeme, value);
	}
	
	@Override
	public String toString() {
	    return kclass.name + " instance";
	}
}
