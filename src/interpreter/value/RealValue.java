package interpreter.value;

public class RealValue extends Value<Double> {
    private Double value;

    public RealValue (Double value) {
        this.value = value;
    }

    public Double value () {
        return value;
    }
}
