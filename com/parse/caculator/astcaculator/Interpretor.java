package astcaculator;

//���г����﷨���İ汾
//���ڵ� ASTNode  BinOp��һ�����ʽ��ֵ�� 1 + 2  num������
/**
 * 
 * @author felix.xtzhu 
 * @date 2018-2-4
 * ֧�� 1+1=2
 * ֧�� 1 + 2 = 3 �ո�skin �Լ���λ������ �Ӽ�
 * ֧�� ��� �� ��� ����  1 + 2 + 3  �� 2 * 2 * 2
 * ֧���������ȼ��� ���������ȼ� ���� 1 + 2 * 3 �� 3 * ��7 + 3��
 * ֧��  ����+�� ����-�� �� 5 -- 2 = 7 �� + (- (3))
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
