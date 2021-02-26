package interpreter.command;

import interpreter.RuntimeException;

public abstract class Command {
    private final int line;

    protected Command (int line) {
        this.line = line;
    }

    public int getLine () {
        return line;
    }

    public abstract void execute () throws RuntimeException;
}
