package IR.Values;

import IR.Types.Type;

public class Constant extends Value {
    public Constant(Type type, String name) {
        super(type, name);
    }

    public Constant() {
        super();
    }

    @Override
    public String toString() {
        return type.toString() + " ";
    }
}
