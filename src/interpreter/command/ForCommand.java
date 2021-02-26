package interpreter.command;

import interpreter.expr.Expr;
import interpreter.expr.Variable;
import interpreter.value.RealValue;
import interpreter.RuntimeException;
import interpreter.util.Utils;
import interpreter.value.IntegerValue;

public class ForCommand extends Command {
    private final Variable var;
    private final Expr src;
    private final Expr dst;
    private final Command cmd;
    
    public ForCommand (int line, Variable var, Expr src, Expr dst, Command cmd) {
        super(line);
        this.var = var;
        this.src = src;
        this.dst = dst;
        this.cmd = cmd;
    }

    @Override
    public void execute () throws RuntimeException {
        double start = Utils.handleValue(this.src.expr());
        double end = Utils.handleValue(this.dst.expr());

        if (this.var == null)
            throw new RuntimeException(String.format("%02d", this.getLine()) + ": Operação inválida. Variável não encontrada.");

        if (start % 1 > 0)
            this.var.setValue(new RealValue(start));
        else
            this.var.setValue(new IntegerValue((int) start));
            
        for (double i = start; i <= end; i++) {
            this.cmd.execute();
            
            if (i % 1 > 0)
                this.var.setValue(new RealValue(i + 1));
            else
                this.var.setValue(new IntegerValue((int) i + 1));
        }
    }
}
