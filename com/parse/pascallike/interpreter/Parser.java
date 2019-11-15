package interpreter;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author felix.xtzhu 
 * @date 2018-2-7
 * 		program : compound_statement DOT
        compound_statement : BEGIN statement_list END
        statement_list : statement
                       | statement SEMI statement_list
        statement : compound_statement
                  | assignment_statement
                  | empty
        assignment_statement : variable ASSIGN expr
        empty :
        expr: term ((PLUS | MINUS) term)*
        term: factor ((MUL | DIV) factor)*
        factor : PLUS factor
               | MINUS factor
               | INTEGER
               | LPAREN expr RPAREN
               | variable
        variable: ID
        
        
   2018-02-25���£���ԭ�еĻ���������µ����ԣ�ʹ�õ��ļ���part10.txt
   program : PROGRAM variable SEMI block DOT

    block : declarations compound_statement

    declarations : VAR (variable_declaration SEMI)+
                 | empty

    variable_declaration : ID (COMMA ID)* COLON type_spec

    type_spec : INTEGER

    compound_statement : BEGIN statement_list END

    statement_list : statement
                   | statement SEMI statement_list

    statement : compound_statement
              | assignment_statement
              | empty

    assignment_statement : variable ASSIGN expr

    empty :

    expr : term ((PLUS | MINUS) term)*

    term : factor ((MUL | INTEGER_DIV | FLOAT_DIV) factor)*

    factor : PLUS factor
           | MINUS factor
           | INTEGER_CONST
           | REAL_CONST
           | LPAREN expr RPAREN
           | variable

    variable: ID
    
    2018-02-25���£���ӷ��ű�symbol table��֧�֣� �ļ�part11.txt
      Ҳ����ʹ�ñ�����Ҫ�������� �Լ����ͼ��
    ��ӷ��ű��� Symbol�����ࣩ  VarSymbol  BuiltinTypeSymbol SymbolTable 
    
    2018-02-26���£���� (PROCEDURE ID SEMI block SEMI)* �ӹ���Ƕ��
    program : PROGRAM variable SEMI block DOT
        block : declarations compound_statement
        declarations : VAR (variable_declaration SEMI)+
                     | (PROCEDURE ID SEMI block SEMI)*
                     | empty
        variable_declaration : ID (COMMA ID)* COLON type_spec
        type_spec : INTEGER
        compound_statement : BEGIN statement_list END
        statement_list : statement
                       | statement SEMI statement_list
        statement : compound_statement
                  | assignment_statement
                  | empty
        assignment_statement : variable ASSIGN expr
        empty :
        expr : term ((PLUS | MINUS) term)*
        term : factor ((MUL | INTEGER_DIV | FLOAT_DIV) factor)*
        factor : PLUS factor
               | MINUS factor
               | INTEGER_CONST
               | REAL_CONST
               | LPAREN expr RPAREN
               | variable
        variable: ID
        
     2018-03-04�����﷨
       program : PROGRAM variable SEMI block DOT
        block : declarations compound_statement
        declarations : (VAR (variable_declaration SEMI)+)*
           | (PROCEDURE ID (LPAREN formal_parameter_list RPAREN)? SEMI block SEMI)*
           | empty
        variable_declaration : ID (COMMA ID)* COLON type_spec
        formal_params_list : formal_parameters
                           | formal_parameters SEMI formal_parameter_list
        formal_parameters : ID (COMMA ID)* COLON type_spec
        type_spec : INTEGER
        compound_statement : BEGIN statement_list END
        statement_list : statement
                       | statement SEMI statement_list
        statement : compound_statement
                  | assignment_statement
                  | empty
        assignment_statement : variable ASSIGN expr
        empty :
        expr : term ((PLUS | MINUS) term)*
        term : factor ((MUL | INTEGER_DIV | FLOAT_DIV) factor)*
        factor : PLUS factor
               | MINUS factor
               | INTEGER_CONST
               | REAL_CONST
               | LPAREN expr RPAREN
               | variable
        variable: ID
        
        ����� 1��(LPAREN formal_parameter_list RPAREN)? �Ժ���������֧��
          2�� �ؼ��ֲ����ִ�Сд---V
          3������Ƕ�� ---V
          4���ֲ������Լ�ȫ�ֱ�����Χ���ֺͲ���---V
          5���������ű�---V
          6��������Ҫ�����ͼ�� ---V
        
 */
public class Parser {

	private Lexer lexer;
	private Token currentToken;
	
	public Parser(Lexer lexer) {
		this.lexer = lexer;
		this.currentToken = lexer.get_next_token();
	}
	
	public ASTNode parse() {
		ASTNode node = program();
		
		if(this.currentToken.type() != Type.EOF) {
			error("interpreter ����");
		}
		return node;
	}
	
	private ASTNode program() {
		eat(Type.PROGRAM);
		VarNode varNode = variable();
		String programNa = varNode.value();
		eat(Type.SEMI);
		
		ASTNode blockNode = block();
		
		ASTNode programNode = new ProgramNode(programNa, blockNode);
		
		eat(Type.DOT);
		
		return programNode;
	}
	
	private ASTNode block() {
		List<ASTNode> declaration = declarations();
		
		ASTNode compound = compound_statement();
		
		ASTNode blockNode = new BlockNode(declaration, compound);
		
		return blockNode;
	}
	
	private List<ASTNode> declarations() {
		List<ASTNode> declaration = new ArrayList<ASTNode>();
		
		while(true) {
			if(this.currentToken.type() == Type.VAR) {
				eat(Type.VAR);
				while(this.currentToken.type() == Type.ID) {
					ASTNode vardec = variable_declaration();
					declaration.add(vardec);
					eat(Type.SEMI);
				}
				continue;
			}
			
			if(this.currentToken.type() == Type.PROCEDURE) {
				eat(Type.PROCEDURE);
				String procNa = this.currentToken.value();
				eat(Type.ID);
				
				List<ASTNode> params = new ArrayList<ASTNode>();
				if(this.currentToken.type() == Type.LPAREN) {
					eat(Type.LPAREN);
					params = formal_parameter_list();
					eat(Type.RPAREN);
				}
				
				eat(Type.SEMI);
				ASTNode blockNode = block();
				ASTNode procDecNode = new ProcedureDeclNode(procNa, params, blockNode);
				declaration.add(procDecNode);
				eat(Type.SEMI);
			}else {
				break;
			}
		}
		
		return declaration;
	}
	
	private List<ASTNode> formal_parameter_list() {
		if(this.currentToken.type() != Type.ID) {
			return null;
		}
		
		ASTNode param = formal_parameters();
		List<ASTNode> params = new ArrayList<ASTNode>();
		params.add(param);
		while(this.currentToken.type() == Type.SEMI) {
			eat(Type.SEMI);
			param = formal_parameters();
			params.add(param);
		}
		return params;
	}
	
	private ASTNode formal_parameters() {
		
		Token vartoken = this.currentToken;
		eat(Type.ID);
		
		List<VarNode> varNodes = new ArrayList<VarNode>();
		VarNode varnode = new VarNode(vartoken);
		varNodes.add(varnode);
		while(this.currentToken.type() == Type.COMMA) {
			eat(Type.COMMA);
			vartoken = this.currentToken;
			varnode = new VarNode(vartoken);
			varNodes.add(varnode);
			eat(Type.ID);
		}
		eat(Type.COLON);
		TypeNode tyNode = type_spec();
		
		ParamNode paramNode = new ParamNode(varNodes, tyNode);
		
		return paramNode;
		
	}
	
	private ASTNode variable_declaration() {
		List<VarNode> varNodes = new ArrayList<VarNode>();
		varNodes.add(variable());
		
		//�������ͬʱ����
		while(this.currentToken.type() == Type.COMMA) {
			eat(Type.COMMA);
			varNodes.add(variable());
		}
		eat(Type.COLON);
		
		TypeNode tn = type_spec();
		
		return new VarDeclNode(varNodes,tn);
	}
	
	private ASTNode compound_statement() {
		
		eat(Type.BEGIN);
		List<ASTNode> nodes = statement_list();
		eat(Type.END);
		
		Compound compound = new Compound();
		
		for(ASTNode node : nodes) {
			compound.addChild(node);
		}
		
		return compound;
	}
	
	private List<ASTNode> statement_list() {
		ASTNode statnode = statement();
		List<ASTNode> nodes = new ArrayList<ASTNode>();
		nodes.add(statnode);
		while(this.currentToken.type() == Type.SEMI) {
			eat(Type.SEMI);
			nodes.add(statement());
		}
		
		//֮��Ӧ����end
		if(this.currentToken.type() == Type.ID) {
			error("statement_list �﷨����");
		}
		
		return nodes;
	}
	
	private ASTNode statement() {
		
		Token token = this.currentToken;
		ASTNode node;
		if(token.type() == Type.ID) {
			node = assignment_statement();
		}else if(token.type() == Type.BEGIN) {
			node = compound_statement();
		}else {
			node = empty();
		}
		return node;
	}
	
	private ASTNode assignment_statement() {
		VarNode left = variable();
		
		Token token = this.currentToken;
		eat(Type.ASSIGN);
		ASTNode right = expr();
		
		ASTNode node = new AssignNode(left, token, right);
		
		return node;
	}
	
	private ASTNode expr() {
		ASTNode node = term();
		while(this.currentToken.type() == Type.PLUS || this.currentToken.type() == Type.MINUS) {
			Token token = this.currentToken;
			if(token.type() == Type.PLUS) {
				eat(Type.PLUS);
			}
			if(token.type() == Type.MINUS) {
				eat(Type.MINUS);
			}
			node = new BinOp(node, token, term());
		}
		return node;
	}
	
	private ASTNode term() {
		ASTNode node = factor();
		while(this.currentToken.type() == Type.MUL 
				|| this.currentToken.type() == Type.INTEGER_DIV
				|| this.currentToken.type() == Type.FLOAT_DIV ) {
			Token token = this.currentToken;
			if(token.type() == Type.MUL) {
				eat(Type.MUL);
			}
			if(token.type() == Type.INTEGER_DIV) {
				eat(Type.INTEGER_DIV);
			}
			if(token.type() == Type.FLOAT_DIV) {
				eat(Type.FLOAT_DIV);
			}
			node = new BinOp(node, token, factor());
		}
		return node;
	}
	
	private ASTNode factor() {
		/**
		 * factor : PLUS factor
                  | MINUS factor
                  | INTEGER_CONST
                  | REAL_CONST
                  | LPAREN expr RPAREN
                  | variable
		 */
		Token token = this.currentToken;
		if(token.type() == Type.INTEGER_CONST) {
			eat(Type.INTEGER_CONST);
			return new Num(token);
		}else if(token.type() == Type.REAL_CONST) {
			eat(Type.REAL_CONST);
			return new Num(token);
		}else if(token.type() == Type.PLUS) {
			eat(Type.PLUS);
			return new UnaryOp(token, factor());
		}else if(token.type() == Type.MINUS) {
			eat(Type.MINUS);
			return new UnaryOp(token, factor());
		}else if(token.type() == Type.LPAREN) {
			eat(Type.LPAREN);
			ASTNode node = expr();
			eat(Type.RPAREN);
			return node;
		}else {
			ASTNode node = variable();
			return node;
		}
		
	}
	
	private ASTNode empty() {
		return new NoOp();
	}
	
	private VarNode variable() {
		Token token = this.currentToken;
		eat(Type.ID);
		return new VarNode(token);
	}
	
	private TypeNode type_spec() {
		
		Token type = this.currentToken;
		
		if(this.currentToken.type() == Type.INTEGER) {
			eat(Type.INTEGER);
		}else {
			eat(Type.REAL);
		}
		
		return new TypeNode(type);
	}
	
	private void eat(Type type) {
		if(this.currentToken.type() == type) {
			this.currentToken = lexer.get_next_token();
		}else {
			error("�﷨��������");
		}
	}
	
	//ͳһ��������
	private void error(String msg) {
		throw new RuntimeException(msg);
	}
}
