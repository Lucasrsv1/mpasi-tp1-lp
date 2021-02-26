package interpreter.expr;

import interpreter.value.Value;
import interpreter.RuntimeException;

public abstract class Expr {
    private final int line;

    protected Expr (int line) {
        this.line = line;
    }
    
    protected int getLine () {
        return this.line;
    }

    public abstract Value expr () throws RuntimeException;
}
