package interpreter.util;

import interpreter.value.*;

public class Utils {
    public static double handleValue (Value v) {
        if (v instanceof StringValue) {
            try {
                return Double.valueOf(((StringValue) v).value());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        
        if (v instanceof IntegerValue)
            return Double.valueOf(((IntegerValue) v).value());
        
        return ((RealValue) v).value();
    }
}
