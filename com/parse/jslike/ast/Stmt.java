package ast;

import java.util.List;

import scanner.Token;

public abstract class Stmt {
 public interface Visitor<T> {
    T visitBlockStmt(Block stmt);
    T visitExpressionStmt(Expression stmt);
    T visitIfStmt(If stmt);
    T visitPrintStmt(Print stmt);
    T visitVarStmt(Var stmt);
    T visitClassStmt(Class stmt);
    T visitReturnStmt(Return stmt);
    T visitBreakStmt(Break stmt);
    T visitWhileStmt(While stmt);
    T visitFunctionStmt(Function stmt);
  }
 public static class Block extends Stmt {
     public final List<Stmt> statements;
    public Block(List<Stmt> statements) {
      this.statements = statements;
    }

     public <T> T accept(Visitor<T> visitor) {
      return visitor.visitBlockStmt(this);
    }

  }
 public static class Expression extends Stmt {
     public final Expr expression;
    public Expression(Expr expression) {
      this.expression = expression;
    }

     public <T> T accept(Visitor<T> visitor) {
      return visitor.visitExpressionStmt(this);
    }

  }
 public static class If extends Stmt {
     public final Expr condition;
     public final Stmt thenBranch;
     public final Stmt elseBranch;
    public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
      this.condition = condition;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }

     public <T> T accept(Visitor<T> visitor) {
      return visitor.visitIfStmt(this);
    }

  }
 public static class Print extends Stmt {
     public final Expr expression;
    public Print(Expr expression) {
      this.expression = expression;
    }

     public <T> T accept(Visitor<T> visitor) {
      return visitor.visitPrintStmt(this);
    }

  }
 public static class Var extends Stmt {
     public final List<Token> name;
     public final Expr initializer;
    public Var(List<Token> name, Expr initializer) {
      this.name = name;
      this.initializer = initializer;
    }

     public <T> T accept(Visitor<T> visitor) {
      return visitor.visitVarStmt(this);
    }

  }
 public static class Class extends Stmt {
     public final Token name;
     public final Expr superclass;
     public final List<Stmt.Function> methods;
     public final List<Stmt.Function> classMethods;
    public Class(Token name, Expr superclass, List<Stmt.Function> methods, List<Stmt.Function> classMethods) {
      this.name = name;
      this.superclass = superclass;
      this.methods = methods;
      this.classMethods = classMethods;
    }

     public <T> T accept(Visitor<T> visitor) {
      return visitor.visitClassStmt(this);
    }

  }
 public static class Return extends Stmt {
     public final Token keyword;
     public final Expr value;
    public Return(Token keyword, Expr value) {
      this.keyword = keyword;
      this.value = value;
    }

     public <T> T accept(Visitor<T> visitor) {
      return visitor.visitReturnStmt(this);
    }

  }
 public static class Break extends Stmt {
    public Break() {
    }

     public <T> T accept(Visitor<T> visitor) {
      return visitor.visitBreakStmt(this);
    }

  }
 public static class While extends Stmt {
     public final Expr condition;
     public final Stmt body;
    public While(Expr condition, Stmt body) {
      this.condition = condition;
      this.body = body;
    }

     public <T> T accept(Visitor<T> visitor) {
      return visitor.visitWhileStmt(this);
    }

  }
 public static class Function extends Stmt {
     public final Token name;
     public final List<Token> parameters;
     public final List<Stmt> body;
    public Function(Token name, List<Token> parameters, List<Stmt> body) {
      this.name = name;
      this.parameters = parameters;
      this.body = body;
    }

     public <T> T accept(Visitor<T> visitor) {
      return visitor.visitFunctionStmt(this);
    }

  }

     public abstract <T> T accept(Visitor<T> visitor);
}
