package parser;

import java.util.HashMap;
import java.util.Map;

import error.RuntimeError;
import scanner.Token;

//�����Ļ���
public class Environment {

	private final Map<String, Object> values = new HashMap<String, Object>();
	public final Environment enclosing;
	
	public Environment() {
		this.enclosing = null;
	}
	
	public Environment(Environment enclosing) {
		this.enclosing = enclosing;
	}
	
	//����
	public void define(String key, Object val) {
		values.put(key, val);
	}
	
	//���Ҳ���ʱ�����ִ���ʽ
	//1������ 2����һ��Ĭ�ϵ�ֵ  ���� null Ĭ��ֵ���ڽ�� ����֮�䶨�� �� ����ʱ����������js���еĺ������Ե��ñ�������ĺ���
	//
	//������ʱѡ�񱨴�
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
	
	//Ϊ��������ֵ
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
