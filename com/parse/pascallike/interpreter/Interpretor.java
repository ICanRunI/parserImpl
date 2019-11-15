package interpreter;

import java.util.HashMap;
import java.util.Map;

public class Interpretor {

	private ASTNode node;
	private static Map<String, Object> global_symbol = new HashMap<String, Object>();
	
	public Interpretor(ASTNode node) {
		this.node = node;
		global_symbol.clear();
	}
	
	public void _interpretor(Visitor visit) {
		
		visit.visit(this.node);
	}
	
	public static Map<String, Object> globalSymbol() {
		return global_symbol;
	}
	
	
}
