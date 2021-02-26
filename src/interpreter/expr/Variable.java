package interpreter.expr;

import interpreter.value.Value;
import interpreter.util.Memory;
import interpreter.RuntimeException;
import java.util.Map;
import java.util.HashMap;

public class Variable extends Expr {
    private static final Map<String, Variable> VARIABLES = new HashMap<>();

    private final String name;

    public Variable (int line, String name) {
        super(line);
        this.name = name;

        VARIABLES.put(this.name, this);
    }
    
    public void setValue (Value value) throws RuntimeException {
        Memory.write(this.name, value);
    }
    
    public String getName () {
        return this.name;
    }
    
    @Override
    public Value expr () throws RuntimeException {
        return Memory.read(this.name);
    }
    
    public static Variable getInstance (String id) {
        return VARIABLES.get(id);
    }
}
