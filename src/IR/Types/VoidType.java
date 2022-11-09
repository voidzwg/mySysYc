package IR.Types;

public class VoidType extends Type {
    public final static VoidType Void = new VoidType();

    @Override
    public boolean isVoidType() {
        return true;
    }

    @Override
    public Type getType() {
        return Void;
    }

    @Override
    public String toString() {
        return "void";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        return o instanceof VoidType;
    }
}
