package interpreter.command;

import interpreter.expr.Expr;
import interpreter.RuntimeException;

import java.util.ArrayList;

public class WriteCommand extends Command {
    private final boolean writeln;
    private final ArrayList<Expr> exprs;
    
    public WriteCommand (int line) {
        super(line);
        this.writeln = false;
        this.exprs = new ArrayList<>();
    }
    
    public WriteCommand (int line, boolean writeln) {
        super(line);
        this.writeln = writeln;
        this.exprs = new ArrayList<>();
    }
    
    public void addExpr (Expr expr) {
        this.exprs.add(expr);
    }

    @Override
    public void execute () throws RuntimeException {
        for (Expr expr : this.exprs)
            System.out.print(expr.expr().value());
        
        if (this.writeln)
            System.out.println("");
    }
}
