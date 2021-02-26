package interpreter.command;

import interpreter.RuntimeException;

import java.util.ArrayList;

public class BlocksCommand extends Command {
    private final ArrayList<Command> cmds;
    
    public BlocksCommand (int line) {
        super(line);
        this.cmds = new ArrayList<>();
    }
    
    public void addCommand (Command cmd) {
        this.cmds.add(cmd);
    }

    @Override
    public void execute () throws RuntimeException {
        for (Command cmd : this.cmds)
            cmd.execute();
    }
}
