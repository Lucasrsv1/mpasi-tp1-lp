package interpreter.expr;

import interpreter.RuntimeException;

public class CompositeBoolExpr extends BoolExpr {
    public enum BoolOp { And, Or }
    
    private final BoolExpr left;
    private final BoolOp op;
    private final BoolExpr right;
    
    public CompositeBoolExpr (int line, BoolExpr left, BoolOp op, BoolExpr right) {
        super(line);
        this.left = left;
        this.op = op;
        this.right = right;
    }
    
    @Override
    public boolean expr () throws RuntimeException {
        switch (this.op) {
            case And:
                return this.left.expr() && this.right.expr();
            case Or:
            default:
                return this.left.expr() || this.right.expr();
        }
    }
}
