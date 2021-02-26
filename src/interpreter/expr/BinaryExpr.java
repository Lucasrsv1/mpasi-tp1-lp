package interpreter.expr;

import interpreter.util.Utils;
import interpreter.value.RealValue;
import interpreter.value.Value;
import interpreter.RuntimeException;

public class BinaryExpr extends Expr {
    public enum BinaryOp { AddOp, SubOp, MulOp, DivOp, ModOp }
    
    private final Expr left;
    private final BinaryOp op;
    private final Expr right;

    public BinaryExpr (int line, Expr left, BinaryOp op, Expr right) {
        super(line);
        this.left = left;
        this.op = op;
        this.right = right;
    }
    
    @Override
    public Value expr () throws RuntimeException {
        double v1 = Utils.handleValue(this.left.expr());
        double v2 = Utils.handleValue(this.right.expr());
        
        switch (this.op) {
            case AddOp:
                return new RealValue(v1 + v2);
            case SubOp:
                return new RealValue(v1 - v2);
            case MulOp:
                return new RealValue(v1 * v2);
            case DivOp:
                return new RealValue(v1 / v2);
            case ModOp:
            default:
                return new RealValue(v1 % v2);
        }
    }
}
