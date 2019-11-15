package astcaculator;

//带有抽象语法树的版本
//根节点 ASTNode  BinOp是一个表达式的值如 1 + 2  num是数字
/**
 * 
 * @author felix.xtzhu 
 * @date 2018-2-4
 * 支持 1+1=2
 * 支持 1 + 2 = 3 空格skin 以及两位数以上 加减
 * 支持 多加 或 多乘 例如  1 + 2 + 3  或 2 * 2 * 2
 * 支持算数优先级和 带（）优先级 例如 1 + 2 * 3 或 3 * （7 + 3）
 * 支持  正（+） 负（-） 号 5 -- 2 = 7 或 + (- (3))
 */
public class Interpretor {

	private Parser parse;
	
	public Interpretor(Parser parse) {
		this.parse = parse;
	}
	
	public int _interpretor(Visitor visitor) {
		int result = 0;
		ASTNode node = parse.parse();
		result = visitor.visit(node);
		return result;
	}
}
