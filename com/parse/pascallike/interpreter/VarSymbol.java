package interpreter;

public class VarSymbol extends Symbol {

	public VarSymbol(String name, Type type) {
		super(name, type);
	}

	@Override
	public String toString() {
		return getType().toString();
	}

	
}
