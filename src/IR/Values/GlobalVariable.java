package IR.Values;

import IR.Types.*;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GlobalVariable that = (GlobalVariable) o;
        return isConstant == that.isConstant && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isConstant, value);
    }
}
