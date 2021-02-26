package interpreter.expr;

import interpreter.RuntimeException;

public abstract class BoolExpr {
    private final int line;

    protected BoolExpr (int line) {
        this.line = line;
    }
    
    protected int getLine () {
        return this.line;
    }

    public abstract boolean expr () throws RuntimeException;
}
