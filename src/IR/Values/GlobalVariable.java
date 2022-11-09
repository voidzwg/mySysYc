package IR.Values;

import IR.Types.*;

public class GlobalVariable extends Value {
    private final boolean isConstant;
    private Value value;

    public GlobalVariable(String name, Value value, boolean isConstant) {
        super(new PointerType(value.getType()), "@" + name);
        this.isConstant = isConstant;
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public boolean isConstant() {
        return isConstant;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getName()).append(" = ");
        if (isConstant) {
            builder.append("constant ");
        } else {
            builder.append("global ");
        }
        if (value != null) {
            builder.append(value);
        }
        builder.append("\n");
        return builder.toString();
    }

}
