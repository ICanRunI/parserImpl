package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ast.Expr;
import ast.Stmt;
import error.JlkError;
import error.ParseError;
import scanner.Token;
import scanner.TokenType;

import static scanner.TokenType.*;

/**
 *  
	program   → declaration* EOF ;
	

	declaration → classDecl
				| funDecl
				| varDecl		
            	| statement ;
    
    classDecl → "class" IDENTIFIER ( ">" IDENTIFIER )?
            "{" function* "}" ;
    
	varDecl → "var" IDENTIFIER ( "," IDENTIFIER)* ( "=" expression )? ";" ;
	
    statement → exprStmt
          | printStmt
          | forStmt
          | ifStmt
          | whileStmt
          | returnStmt
          | breakStmt
          | block ;
	
	funDecl  → "fun" function ;
    function → IDENTIFIER "(" parameters? ")" block ;
    parameters → IDENTIFIER ( "," IDENTIFIER )* ;
	returnStmt → "return" expression? ";" ;

	block     → "{" declaration* "}" ;
	
	ifStmt    → "if" "(" expression ")" statement ( "else" statement )? ;
    whileStmt → "while" "(" expression ")" statement ;
    forStmt   → "for" "(" ( varDecl | exprStmt | ";" )       例如 for (var i = 0; i < 10; i = i + 1) print i;
                      expression? ";"
                      expression? ")" statement ;
    
	exprStmt  → expression ";" ;
	printStmt → "print" expression ";" ;
   
    arguments → expression ( "," expression )* ;
    expression     →  assignment ;
    assignment     →  ( call "." )? identifier "=" assignment
                      | logic_or ;
    logic_or   → logic_and ( "or" logic_and )* ;
	logic_and  → equality ( "and" equality )* ; 
	
	equality       → comparison ( ( "!=" | "==" ) comparison )* ;
	comparison     → addition ( ( ">" | ">=" | "<" | "<=" ) addition )* ;
	addition       → multiplication ( ( "-" | "+" ) multiplication )* ;
	multiplication → unary ( ( "/" | "*" ) unary )* ;
	unary          → ( "!" | "-" ) unary | call;
	
	call           → primary ( "(" arguments? ")" | "." IDENTIFIER )* ;
	primary        → NUMBER | STRING | "false" | "true" | "nil"
               		| "(" expression ")" ;
               		| "super" "." IDENTIFIER | IDENTIFIER 
 */

public class Parser {

	private final List<Token> tokens;
	private int current = 0;

	public Parser(List<Token> tokens) {
	    this.tokens = tokens;
	}
	
	public List<Stmt> parse() {
	    /*try {
	      return expression();
	    } catch (ParseError error) {
	      return null;
	    }*/
		List<Stmt> statements = new ArrayList<Stmt>();
	    while (!isAtEnd()) {
	      statements.add(declaration());
	    }

	    return statements;
	}
	
	private Stmt declaration() {
	    try {
	      if (match(VAR)) return varDeclaration();

	      return statement();
	    } catch (ParseError error) {
	      //用于错误之后继续解析匹配到下一个正确的语法，以便在一次解析后展示更多的错误，而不是只展示一个错误
	      synchronize();
	      return null;
	    }
	}
	
	private Stmt varDeclaration() {
		Token name = consume(IDENTIFIER, "Expect variable name.");
		//assign表达式支持逗号
		List<Token> names = new ArrayList<Token>();
		names.add(name);
		while(match(COMMA)) {
			name = advance();
			names.add(name);
		}
		Expr initializer = null;
		
		if(match(EQUAL)) {
			initializer = expression();
		}
		consume(SEMICOLON, "Expect ';' after variable declaration.");
		
		return new Stmt.Var(names, initializer);
	}
	
	//很难从第一个token识别是否是表达式，所以这里默认当作表达式
	private Stmt statement() {
		
	    if (match(CLASS)) 
	       return classDeclaration();
		
		if(match(IF)) 
		   return ifStatement();
		
		if(match(FOR)) 
		   return forStatement();
		
		if(match(WHILE))
		   return whileStatement();
		
		if (match(FUN)) 
		   return function("function");
		
		if (match(RETURN)) 
		   return returnStatement();
		
		if(match(BREAK))
		   return breakStatement();
		
		if(match(PRINT)) 
		   return printStatement();
		
		if(match(LEFT_BRACE)) 
		   return new Stmt.Block(block());
		
		return expressionStatement();
	}
	
	public Stmt ifStatement() {
		consume(LEFT_PAREN, "Expect '(' after 'if'.");
		Expr condition = expression();
		consume(RIGHT_PAREN, "Expect ')' after if condition.");
		
		Stmt thenBranch = statement();
		Stmt elseBranch = null;
		if(match(ELSE)) {
			elseBranch = statement();
		}
		
		return new Stmt.If(condition, thenBranch, elseBranch);
	}
	
	private Stmt classDeclaration() {
	    Token name = consume(IDENTIFIER, "Expect class name.");
	    Expr superclass = null;
	    //父类
	    if(match(GREATER)) {
	    	consume(IDENTIFIER, "Expect superclass name.");
	        superclass = new Expr.Variable(previous());
	    }
	    consume(LEFT_BRACE, "Expect '{' before class body.");

	    List<Stmt.Function> methods = new ArrayList<>();
	    List<Stmt.Function> classMethods = new ArrayList<>();
	    while (!check(RIGHT_BRACE) && !isAtEnd()) {
	      //添加类方法的支持
	      boolean isClassMethod = match(CLASS);
	      (isClassMethod ? classMethods : methods).add(function("method"));
	    }

	    consume(RIGHT_BRACE, "Expect '}' after class body.");

	    return new Stmt.Class(name, superclass, methods, classMethods);
	}
	
	private Stmt forStatement() {
		
		consume(LEFT_PAREN, "Expect '(' after 'for'.");
		Stmt initializer = null;
		if (match(SEMICOLON)) {
	      initializer = null;
	    } else if (match(VAR)) {
	      initializer = varDeclaration();
	    } else {
	      initializer = expressionStatement();
	    }
		
		Expr condition = null;
	    if (!check(SEMICOLON)) {
	      condition = expression();
	    }
	    consume(SEMICOLON, "Expect ';' after loop condition.");
	    
	    Expr increment = null;
	    if (!check(RIGHT_PAREN)) {
	      increment = expression();
	    }
	    consume(RIGHT_PAREN, "Expect ')' after for clauses.");
	    
	    Stmt body = statement();
	    
	    if (increment != null) {
	        body = new Stmt.Block(Arrays.asList(
	            body,
	            new Stmt.Expression(increment)));
	    }
	    
	    if (condition == null) 
	    	condition = new Expr.Literal(true);
	    
	    body = new Stmt.While(condition, body);

	    if (initializer != null) {
	        body = new Stmt.Block(Arrays.asList(initializer, body));
	    }
	    
	    return body;
	}
	
	private Stmt whileStatement() {
		consume(LEFT_PAREN, "Expect '(' after 'while'.");
		Expr condition = expression();
		consume(RIGHT_PAREN, "Expect ')' after while condition.");
		Stmt body = statement();
		
		return new Stmt.While(condition, body);
		
	}
	
	private Stmt.Function function(String kind) {
	    Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
	    consume(LEFT_PAREN, "Expect '(' after " + kind + " name.");
	    
	    List<Token> parameters = new ArrayList<>();
	    if (!check(RIGHT_PAREN)) {
	      do {
	        if (parameters.size() >= 32) {
	          error(peek(), "Cannot have more than 8 parameters.");
	        }

	        parameters.add(consume(IDENTIFIER, "Expect parameter name."));
	      } while (match(COMMA));
	    }
	    consume(RIGHT_PAREN, "Expect ')' after parameters.");
	    
	    consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
	    List<Stmt> body = block();
	    return new Stmt.Function(name, parameters, body);
	}
	
	private Stmt returnStatement() {
	    Token keyword = previous();
	    Expr value = null;
	    if (!check(SEMICOLON)) {
	      value = expression();
	    }

	    consume(SEMICOLON, "Expect ';' after return value.");
	    return new Stmt.Return(keyword, value);
	}
	
	private Stmt breakStatement() {
		consume(SEMICOLON, "Expect ';' after break.");
		
		return new Stmt.Break();
	}
	
	private List<Stmt> block() {
		List<Stmt> statements = new ArrayList<Stmt>();
		
		while(!check(RIGHT_BRACE) && !isAtEnd()) {
			statements.add(declaration());
		}
		consume(RIGHT_BRACE,  "Expect '}' after block.");
		
		return statements;
	}
	

	private Stmt printStatement() {
	    Expr value = expression();
	    consume(SEMICOLON, "Expect ';' after value.");
	    return new Stmt.Print(value);
	}
	
	private Stmt expressionStatement() {
	    Expr expr = expression();
	    consume(SEMICOLON, "Expect ';' after expression.");
	    return new Stmt.Expression(expr);
	}
	
	private Expr expression() {
		return assignment();
		
	}
	
	private Expr assignment() {
		Expr expr = or();
		
		if(match(EQUAL)) {
			Token equals = previous();
		    Expr value = assignment();
		    
		    if (expr instanceof Expr.Variable) {
		    	Token name = ((Expr.Variable)expr).name;
		        return new Expr.Assign(name, value);
		    }else if(expr instanceof Expr.Get) {
		    	Expr.Get get = (Expr.Get)expr;
		        return new Expr.Set(get.object, get.name, value);
		    }
		    
		    throw error(equals, "Invalid assignment target.");
		}
		
	    return expr;
	}
	
	private Expr or() {
		Expr expr = and();
		
		while(match(OR)) {
			Token operator = previous();
			Expr right = and();
			expr = new Expr.Logical(expr, operator, right);
		}
		
		return expr;
	}
	
	private Expr and() {
	    Expr expr = equality();

	    while (match(AND)) {
	      Token operator = previous();
	      Expr right = equality();
	      expr = new Expr.Logical(expr, operator, right);
	    }

	    return expr;
	}
	
	private Expr equality() {
		Expr expr = comparison();
		
		while(match(BANG_EQUAL, EQUAL_EQUAL)) {
			Token operator = previous();
			Expr right = comparison();
		    expr = new Expr.Binary(expr, operator, right);
		}
		
		return expr;
	}
	
	private Expr comparison() {
		Expr expr = addition();
		while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
		      Token operator = previous();
		      Expr right = addition();
		      expr = new Expr.Binary(expr, operator, right);
		}
		return expr;
	}
	
	private Expr addition() {
		Expr expr = multiplication();

	    while (match(MINUS, PLUS)) {
	      Token operator = previous();
	      Expr right = multiplication();
	      expr = new Expr.Binary(expr, operator, right);
	    }

	    return expr;
	}
	
	private Expr multiplication() {
		Expr expr = unary();

	    while (match(SLASH, STAR)) {
	      Token operator = previous();
	      Expr right = unary();
	      expr = new Expr.Binary(expr, operator, right);
	    }

	    return expr;
	}
	
	private Expr unary() {
		if (match(BANG, MINUS)) {
		    Token operator = previous();
		    Expr right = unary();
		    return new Expr.Unary(operator, right);
		}

		return call();
	}
	
	private Expr call() {
	    Expr expr = primary();

	    while (true) {
	      if (match(LEFT_PAREN)) {
	    	//做双重检查，是否两次函数调用 func(arg)();
	        expr = finishCall(expr);
	      } else if(match(DOT)) {
	    	  Token name = consume(IDENTIFIER,
	    	            "Expect property name after '.'.");
	    	  expr = new Expr.Get(expr, name);
	      }else {
	        break;
	      }
	    }

	    return expr;
	}
	
	private Expr finishCall(Expr callee) {
	    List<Expr> arguments = new ArrayList<>();
		if(!check(RIGHT_PAREN)) {
			do{
				//函数最大参数长度
				if (arguments.size() >= 32) {
			          error(peek(), "Cannot have more than 32 arguments.");
			    }
				arguments.add(expression());
			}while(match(COMMA));
		}
		Token paren = consume(RIGHT_PAREN, "Expect ')' after arguments.");
		return new Expr.Call(callee, paren, arguments);
	}
	
	private Expr primary() {
		if (match(FALSE)) return new Expr.Literal(false);
	    if (match(TRUE)) return new Expr.Literal(true);
	    if (match(NIL)) return new Expr.Literal(null);
	    
	    if (match(NUMBER, STRING)) {
	        return new Expr.Literal(previous().literal);
	    }
	    
	    if(match(SUPER)) {
	    	Token keyword = previous();
	    	consume(DOT, "Expect '.' after 'super'.");
	        Token method = consume(IDENTIFIER,
	            "Expect superclass method name.");
	        
	        return new Expr.Super(keyword, method);
	    }
	    
	    if (match(LEFT_PAREN)) {
	        Expr expr = expression();
	        consume(RIGHT_PAREN, "Expect ')' after expression.");
	        return new Expr.Grouping(expr);
	    }
	    if (match(THIS)) 
	    	return new Expr.This(previous());
	    
	    if(match(IDENTIFIER)) {
	    	return new Expr.Variable(previous());
	    }
	    
	    throw error(peek(), "Expect expression.");
	}
	
	private void synchronize() {
		advance();
		
		while(!isAtEnd()) {
			if(previous().type == SEMICOLON) return;
					
			switch(peek().type) {
				case CLASS:
		        case FUN:
		        case VAR:
		        case FOR:
		        case IF:
		        case WHILE:
		        case PRINT:
		        case RETURN:
		        return;
			}
			
			advance();
		}
	}
	
	private boolean match(TokenType... types) {
		for(TokenType type : types) {
			if(check(type)) {
				advance();
				return true;
			}
			
		}
		
		return false;
	}
	
	private Token consume(TokenType type, String message) {
	    if (check(type)) return advance();

	    throw error(peek(), message);
	}
	
	private ParseError error(Token token, String message) {
		JlkError.error(token, message);
	    return new ParseError();
	}
	
	private boolean check(TokenType tokenType) {
	    if (isAtEnd()) return false;
	    return peek().type == tokenType;
	}
	
	private Token advance() {
	    if (!isAtEnd()) current++;
	    return previous();
	}
	
	private Token previous() {
	    return tokens.get(current - 1);
	}
	
	private Token peek() {
	    return tokens.get(current);
	}
	
	private boolean isAtEnd() {
	    return peek().type == EOF;
	}
}
