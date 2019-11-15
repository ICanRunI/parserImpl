package parser;

import java.util.HashMap;
import java.util.Map;

import error.RuntimeError;
import scanner.Token;

//上下文环境
public class Environment {

	private final Map<String, Object> values = new HashMap<String, Object>();
	public final Environment enclosing;
	
	public Environment() {
		this.enclosing = null;
	}
	
	public Environment(Environment enclosing) {
		this.enclosing = enclosing;
	}
	
	//声明
	public void define(String key, Object val) {
		values.put(key, val);
	}
	
	//查找不到时有三种处理方式
	//1、报错 2、给一个默认的值  比如 null 默认值对于解决 函数之间定义 和 调用时解析，类似js当中的函数可以调用比它后定义的函数
	//
	//这里暂时选择报错
	public Object lookup(Token token) {
		if(values.containsKey(token.lexeme)) {
			return values.get(token.lexeme);
		}
		
		if(enclosing != null) {
			return enclosing.lookup(token);
		}
		
		throw new RuntimeError(token,
		        "Undefined variable '" + token.lexeme + "'.");
	}
	
	//为变量分配值
	public void assign(Token name, Object value) {
		if (values.containsKey(name.lexeme)) {
		      values.put(name.lexeme, value);
		      return;
		}
		
		if(enclosing != null) {
			enclosing.assign(name, value);
			return;
		}

	    throw new RuntimeError(name,
	        "Undefined variable '" + name.lexeme + "'.");
	}
	
	public Object getAt(int distance, String name) {
	    return ancestor(distance).values.get(name);
	}
	
	public void assignAt(int distance, Token name, Object value) {
	    ancestor(distance).values.put(name.lexeme, value);
	}
	
	private Environment ancestor(int distance) {
	    Environment environment = this;
	    for (int i = 0; i < distance; i++) {
	      environment = environment.enclosing; 
	    }

	    return environment;
	}
}
