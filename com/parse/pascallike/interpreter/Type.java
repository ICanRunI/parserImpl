package interpreter;


/**
 * 
 * @author felix.xtzhu 
 * @date 2018-2-26
 * 
 * 2018-02-26 添加 PROCEDURE 过程
 */
public enum Type {
	INTEGER, //整数变量
	REAL, //小数 变量
	INTEGER_CONST, //整数常量
	REAL_CONST, //小数常量
	PLUS, 
	MINUS, 
	MUL,
	INTEGER_DIV,//整数除法
	FLOAT_DIV,//小数除法
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
	COLON,//冒号
	COMMA,
	NONE,
	PROCEDURE,
	EOF
}
