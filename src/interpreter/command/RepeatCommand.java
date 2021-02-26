package interpreter.command;

import interpreter.expr.BoolExpr;
import interpreter.RuntimeException;

import java.util.ArrayList;

public class RepeatCommand extends Command {
    private final ArrayList<Command> cmds;
    private final BoolExpr cond;
    
    public RepeatCommand (int line, ArrayList<Command> cmds, BoolExpr cond) {
        super(line);
        this.cmds = cmds;
        this.cond = cond;
    }

    @Override
    public void execute () throws RuntimeException {
        do {
            for (Command cmd : this.cmds)
                cmd.execute();
        } while (!this.cond.expr());
    }
}
