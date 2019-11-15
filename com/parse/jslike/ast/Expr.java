package ast;

import java.util.List;

import scanner.Token;

public abstract class Expr {
 public interface Visitor<T> {
    T visitBinaryExpr(Binary expr);
    T visitGroupingExpr(Grouping expr);
    T visitGetExpr(Get expr);
    T visitCallExpr(Call expr);
    T visitLiteralExpr(Literal expr);
    T visitLogicalExpr(Logical expr);
    T visitSetExpr(Set expr);
    T visitUnaryExpr(Unary expr);
    T visitThisExpr(This expr);
    T visitSuperExpr(Super expr);
    T visitVariableExpr(Variable expr);
    T visitAssignExpr(Assign expr);
	T visitBreakStmt(BreakException stmt);
  }
 public static class Binary extends Expr {
     public final Expr left;
     public final Token operator;
     public final Expr right;
    public Binary(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

     public <T> T accept(Visitor<T> visitor) {
      return visitor.visitBinaryExpr(this);
    }

  }
 public static class Grouping extends Expr {
     public final Expr expression;
    public Grouping(Expr expression) {
      this.expression = expression;
    }

     public <T> T accept(Visitor<T> visitor) {
      return visitor.visitGroupingExpr(this);
    }

  }
 public static class Get extends Expr {
     public final Expr object;
     public final Token name;
    public Get(Expr object, Token name) {
      this.object = object;
      this.name = name;
    }

     public <T> T accept(Visitor<T> visitor) {
      return visitor.visitGetExpr(this);
    }

  }
 public static class Call extends Expr {
     public final Expr callee;
     public final Token paren;
     public final List<Expr> arguments;
    public Call(Expr callee, Token paren, List<Expr> arguments) {
      this.callee = callee;
      this.paren = paren;
      this.arguments = arguments;
    }

     public <T> T accept(Visitor<T> visitor) {
      return visitor.visitCallExpr(this);
    }

  }
 public static class Literal extends Expr {
     public final Object value;
    public Literal(Object value) {
      this.value = value;
    }

     public <T> T accept(Visitor<T> visitor) {
      return visitor.visitLiteralExpr(this);
    }

  }
 public static class Logical extends Expr {
     public final Expr left;
     public final Token operator;
     public final Expr right;
    public Logical(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

     public <T> T accept(Visitor<T> visitor) {
      return visitor.visitLogicalExpr(this);
    }

  }
 public static class Set extends Expr {
     public final Expr object;
     public final Token name;
     public final Expr value;
    public Set(Expr object, Token name, Expr value) {
      this.object = object;
      this.name = name;
      this.value = value;
    }

     public <T> T accept(Visitor<T> visitor) {
      return visitor.visitSetExpr(this);
    }

  }
 public static class Unary extends Expr {
     public final Token operator;
     public final Expr right;
    public Unary(Token operator, Expr right) {
      this.operator = operator;
      this.right = right;
    }

     public <T> T accept(Visitor<T> visitor) {
      return visitor.visitUnaryExpr(this);
    }

  }
 public static class This extends Expr {
     public final Token keyword;
    public This(Token keyword) {
      this.keyword = keyword;
    }

     public <T> T accept(Visitor<T> visitor) {
      return visitor.visitThisExpr(this);
    }

  }
 public static class Super extends Expr {
     public final Token keyword;
     public final Token method;
    public Super(Token keyword, Token method) {
      this.keyword = keyword;
      this.method = method;
    }

     public <T> T accept(Visitor<T> visitor) {
      return visitor.visitSuperExpr(this);
    }

  }
 public static class Variable extends Expr {
     public final Token name;
    public Variable(Token name) {
      this.name = name;
    }

     public <T> T accept(Visitor<T> visitor) {
      return visitor.visitVariableExpr(this);
    }

  }
 public static class Assign extends Expr {
     public final Token name;
     public final Expr value;
    public Assign(Token name, Expr value) {
      this.name = name;
      this.value = value;
    }

     public <T> T accept(Visitor<T> visitor) {
      return visitor.visitAssignExpr(this);
    }

  }

     public abstract <T> T accept(Visitor<T> visitor);
}
