package interpret;

/**
 * 
 * @author felix.xtzhu 
 * @date 2018-2-2
 * ����parse tree�İ汾
 * 
 * ֧�� 1+1=2
 * ֧�� 1 + 2 = 3 �ո�skin �Լ���λ������ �Ӽ�
 * ֧�� ��� �� ��� ����  1 + 2 + 3  �� 2 * 2 * 2
 * ֧���������ȼ��� ���������ȼ� ���� 1 + 2 * 3 �� 3 * ��7 + 3��
 *  
 * 
 */
public class Interpreter {

	
	private Token currentToken;
	private Lexer lexer;
	
	
	public Interpreter(Lexer lexer) {
		this.lexer = lexer;
		this.currentToken = lexer.getNextToken();
	}
	
	public int expr() {
		int result = 0;
		
		result = term();
		
		while(this.currentToken.type() == Type.PLUS || this.currentToken.type() == Type.MINUS) {
			if(this.currentToken.type() == Type.PLUS) {
				eat(Type.PLUS);
				result = result + term();
			}
			if(this.currentToken.type() == Type.MINUS) {
				eat(Type.MINUS);
				result = result - term();
			}
		}
		return result;
	}
	
	//���ȼ���
	private int term() {
		int result = factor();
		while(this.currentToken.type() == Type.MULTIP || this.currentToken.type() == Type.DIVIS) {
			if(this.currentToken.type() == Type.MULTIP) {
				eat(Type.MULTIP);
				result = result * factor();
			}
			if(this.currentToken.type() == Type.DIVIS) {
				eat(Type.DIVIS);
				result = result / factor();
			}
		}
		return result;
	}
	
	//���ȼ���
	private int factor() {
		Token tk = this.currentToken;
		if(this.currentToken.type() == Type.INTERGER) {
			
			eat(Type.INTERGER);
			return Integer.valueOf(tk.value());
		}else {
			//����������־�������
			eat(Type.LPAREN);
			int result = expr();
			eat(Type.RPAREN);
			return result;
		}
	}
	
	private void eat(Type type) {
		if(this.currentToken.type() == type) {
			this.currentToken = lexer.getNextToken();
		}else {
			throw new RuntimeException("���������﷨��֧��");
		}
	}
	
}
