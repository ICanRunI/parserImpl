package interpreter;


/**
 * 
 * @author felix.xtzhu 
 * @date 2018-2-26
 * 
 * 2018-02-26 ��� PROCEDURE ����
 */
public enum Type {
	INTEGER, //��������
	REAL, //С�� ����
	INTEGER_CONST, //��������
	REAL_CONST, //С������
	PLUS, 
	MINUS, 
	MUL,
	INTEGER_DIV,//��������
	FLOAT_DIV,//С������
//	DIV,
	LPAREN, 
	RPAREN, 
	ID, 
	ASSIGN,
	BEGIN, 
	END, 
	SEMI, 
	DOT, 
	PROGRAM,
	VAR,
	COLON,//ð��
	COMMA,
	NONE,
	PROCEDURE,
	EOF
}
