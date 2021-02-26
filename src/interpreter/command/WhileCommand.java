package interpreter.command;

import interpreter.expr.BoolExpr;
import interpreter.RuntimeException;

public class WhileCommand extends Command {
    private final BoolExpr cond;
    private final Command cmds;
    
    public WhileCommand (int line, BoolExpr cond, Command cmds) {
        super(line);
        this.cond = cond;
        this.cmds = cmds;
    }

    @Override
    public void execute () throws RuntimeException {
        while (this.cond.expr())
            this.cmds.execute();
    }
}
