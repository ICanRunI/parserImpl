package interpret;

public class Token {

	private Type type;
	private String value;
	
	public Token(Type type, String value) {
		this.type = type;
		this.value = value;
	}
	
	public Type type() {
		return type;
	}
	
	public String value() {
		return value;
	}
}
