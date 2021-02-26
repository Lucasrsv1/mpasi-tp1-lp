package interpreter.command;

import interpreter.expr.Expr;
import interpreter.value.Value;
import interpreter.RuntimeException;

import java.util.ArrayList;

public class CaseCommand extends Command {
    public static class CaseOption {
        public Value value;
        public Command cmd;
        
        public CaseOption (Value value, Command cmd) {
            this.value = value;
            this.cmd = cmd;
        }
    }
    
    private final Expr expr;
    private final ArrayList<CaseOption> options;
    private Command otherwise;
    
    public CaseCommand (int line, Expr expr) {
        super(line);
        this.expr = expr;
        this.options = new ArrayList<>();
    }
    
    public void addOption (CaseOption option) {
        this.options.add(option);
    }
    
    public void setOtherwise (Command cmd) {
        this.otherwise = cmd;
    }

    @Override
    public void execute () throws RuntimeException {
        Value value = this.expr.expr();
        for (CaseOption opt : this.options) {
            if (opt.value.value().equals(value.value())) {
                opt.cmd.execute();
                return;
            }
        }
        
        if (this.otherwise != null)
            this.otherwise.execute();
    }
}
