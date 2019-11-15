package interpreter;

public class BuiltinTypeSymbol extends Symbol {

	public BuiltinTypeSymbol(String name, Type type) {
		super(name, type);
	}

	@Override
	public String toString() {
		return getType().toString();
	}
}
