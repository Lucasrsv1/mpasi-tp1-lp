package interpreter.util;

import interpreter.value.Value;
import interpreter.RuntimeException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Memory {
    private static final HashMap<String, Value> MEMORY = new HashMap<>();
    private static final Set<String> CONSTS = new HashSet<>();

    public static void registryVariable (String name) throws RuntimeException {
        registryVariable(name, null);
    }
    
    public static void registryVariable (String name, Value value) throws RuntimeException {
        if (MEMORY.containsKey(name))
            throw new RuntimeException("Operação inválida. Outra variável ou contante chamada " + name + " já foi declarada.");

        MEMORY.put(name, value);
    }
    
    public static void registryConstant (String name) throws RuntimeException {
        registryConstant(name, null);
    }
    
    public static void registryConstant (String name, Value value) throws RuntimeException {
        if (MEMORY.containsKey(name))
            throw new RuntimeException("Operação inválida. Outra variável ou contante chamada " + name + " já foi declarada.");

        MEMORY.put(name, value);
        CONSTS.add(name);
    }
    
    public static Value read (String name) throws RuntimeException {
        if (!MEMORY.containsKey(name))
            throw new RuntimeException("Operação inválida. Nenhuma variável ou constante chamada " + name + " foi encontrada.");

        return MEMORY.get(name);
    }
    
    public static void write (String name, Value value) throws RuntimeException {
        if (!MEMORY.containsKey(name))
            throw new RuntimeException("Operação inválida. Nenhuma variável ou constante chamada " + name + " foi encontrada.");

        MEMORY.put(name, value);
    }
}
