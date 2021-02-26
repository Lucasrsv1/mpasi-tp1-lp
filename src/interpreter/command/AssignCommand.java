package interpreter.command;

import interpreter.expr.Expr;
import interpreter.expr.Variable;
import interpreter.RuntimeException;

public class AssignCommand extends Command {
    private final Variable left;
    private final Expr right;
    
    public AssignCommand (int line, Variable left, Expr right) {
        super(line);
        this.left = left;
        this.right = right;
    }

    @Override
    public void execute () throws RuntimeException {
        if (this.left == null)
            throw new RuntimeException(String.format("%02d", this.getLine()) + ": Operação inválida. Variável não encontrada.");

        this.left.setValue(this.right.expr());
    }
}
