package interpreter.expr;

import interpreter.value.*;
import interpreter.util.Utils;
import interpreter.RuntimeException;

public class SingleBoolExpr extends BoolExpr {
    public enum RelOp { Equal, NotEqual, LowerThan, LowerEqual, GreaterThan, GreaterEqual }
    
    private final Expr left;
    private final RelOp op;
    private final Expr right;
    
    public SingleBoolExpr (int line, Expr left, RelOp op, Expr right) {
        super(line);
        this.left = left;
        this.op = op;
        this.right = right;
    }
    
    @Override
    public boolean expr () throws RuntimeException {
        Value left = this.left.expr();
        Value right = this.right.expr();

        if (this.op == RelOp.Equal)
            return left.value().equals(right.value());
        
        if (this.op == RelOp.NotEqual)
            return !left.value().equals(right.value());

        double v1 = Utils.handleValue(left);
        double v2 = Utils.handleValue(right);
        
        switch (this.op) {
            case LowerThan:
                return v1 < v2;
            case LowerEqual:
                return v1 <= v2;
            case GreaterThan:
                return v1 > v2;
            case GreaterEqual:
            default:
                return v1 >= v2;
        }
    }
}
