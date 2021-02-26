package interpreter.command;

import interpreter.expr.Variable;
import interpreter.value.*;
import interpreter.RuntimeException;

import java.util.ArrayList;
import java.util.Scanner;

public class ReadCommand extends Command {
    private final ArrayList<Variable> vars;
    
    public ReadCommand (int line) {
        super(line);
        this.vars = new ArrayList<>();
    }
    
    public void addVariable (Variable var) {
        this.vars.add(var);
    }

    @Override
    public void execute () throws RuntimeException {
        String strValue;
        double numValue;
        
        Scanner sc = new Scanner(System.in);
        for (int i = 0; i < this.vars.size(); i++) {
            if (i == this.vars.size() - 1)
                strValue = sc.nextLine();
            else
                strValue = sc.next();

            Variable var = this.vars.get(i);
            if (var == null)
                throw new RuntimeException(String.format("%02d", this.getLine()) + ": Operação inválida. Variável não encontrada.");

            try {
                numValue = Double.parseDouble(strValue);
                if (numValue % 1 > 0)
                    var.setValue(new RealValue(numValue));
                else
                    var.setValue(new IntegerValue((int) numValue));
            } catch (NumberFormatException e) {
                var.setValue(new StringValue(strValue));
            }
        }
    }
}
