package simplf;

import java.util.ArrayList;
import java.util.List;

public class Desugar implements Stmt.Visitor<Stmt> {
    public List<Stmt> desugar(List<Stmt> statements) {
        List<Stmt> result = new ArrayList<>();
        for (Stmt stmt : statements) {
            result.add(stmt.accept(this));
        }
        return result;
    }

    @Override
    public Stmt visitForStmt(Stmt.For stmt) {
        List<Stmt> statements = new ArrayList<>();
        
        if (stmt.init != null) {
            statements.add(new Stmt.Expression(stmt.init));
        }
        
        List<Stmt> bodyStmts = new ArrayList<>();
        bodyStmts.add(stmt.body);
        
        if (stmt.incr != null) {
            bodyStmts.add(new Stmt.Expression(stmt.incr));
        }
        
        Stmt whileLoop = new Stmt.While(stmt.cond, new Stmt.Block(bodyStmts));
        statements.add(whileLoop);
        
        return new Stmt.Block(statements);
    }

    @Override
    public Stmt visitExprStmt(Stmt.Expression stmt) {
        return stmt;
    }

    @Override
    public Stmt visitPrintStmt(Stmt.Print stmt) {
        return stmt;
    }

    @Override
    public Stmt visitVarStmt(Stmt.Var stmt) {
        return stmt;
    }

    @Override
    public Stmt visitBlockStmt(Stmt.Block stmt) {
        return new Stmt.Block(desugar(stmt.statements));
    }

    @Override
    public Stmt visitIfStmt(Stmt.If stmt) {
        return new Stmt.If(
            stmt.cond,
            stmt.thenBranch.accept(this),
            stmt.elseBranch != null ? stmt.elseBranch.accept(this) : null
        );
    }

    @Override
    public Stmt visitWhileStmt(Stmt.While stmt) {
        return new Stmt.While(stmt.cond, stmt.body.accept(this));
    }

    @Override
    public Stmt visitFunctionStmt(Stmt.Function stmt) {
        return new Stmt.Function(
            stmt.name,
            stmt.params,
            desugar(stmt.body)
        );
    }
}