package interpreter;

import java.util.HashMap;
import java.util.Map;

public class ScopedSymbolTable {

	private Map<String, Symbol> symtab;
	private String scopeNa;
	private int scopeLevel;
	private ScopedSymbolTable enclosingScope;
	
	public ScopedSymbolTable(String scopeNa, int scopeLevel, ScopedSymbolTable enclosingScope) {
		symtab = new HashMap<String, Symbol>();
		this.scopeLevel = scopeLevel;
		this.scopeNa = scopeNa;
		this.enclosingScope = enclosingScope;
		init_builtins();
		
	}
	
	private void init_builtins() {
		defind(new BuiltinTypeSymbol("INTEGER", Type.INTEGER));
		defind(new BuiltinTypeSymbol("REAL", Type.REAL));
		defind(new BuiltinTypeSymbol("real", Type.REAL));
		defind(new BuiltinTypeSymbol("integer", Type.REAL));
	}
	
	public void defind(Symbol symbol) {
		System.out.println("define: "+symbol.getName());
		symtab.put(symbol.getName(), symbol);
	}
	
	public Symbol lookup(String typeNa, boolean current_scope_only) {
		System.out.println("lookup: "+typeNa);
		Symbol symbol = symtab.get(typeNa);
		if(symbol != null) {
			
			return symbol;
		}
		
		if(current_scope_only) {
			return null;
		}
		
		if(enclosingScope != null) {
			return enclosingScope.lookup(typeNa, false);
		}
		
		return null;
	}
	
	public Map<String, Symbol> symtab() {
		return this.symtab;
	}
	
	public ScopedSymbolTable enclosingScope() {
		return this.enclosingScope;
	}
	
	public int level() {
		return this.scopeLevel;
	}
	
	
}

