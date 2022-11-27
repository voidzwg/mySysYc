package IR.Values;

import IR.Types.Type;

public class Parameter extends Value {
    private final Function function;

    public Parameter(Type type, String name, Function function) {
        super(type, "%" + name);
        this.function = function;
    }

    public Function getFunction() {
        return function;
    }

    @Override
    public String toString() {
        return type + " " + name;
    }
}
