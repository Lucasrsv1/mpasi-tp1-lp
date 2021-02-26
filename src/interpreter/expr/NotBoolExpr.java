package interpreter.expr;

import interpreter.RuntimeException;

public class NotBoolExpr extends BoolExpr {
    private final BoolExpr exprBool;
    
    public NotBoolExpr (int line, BoolExpr expr) {
        super(line);
        this.exprBool = expr;
    }
    
    @Override
    public boolean expr () throws RuntimeException {
        return !this.exprBool.expr();
    }
}
