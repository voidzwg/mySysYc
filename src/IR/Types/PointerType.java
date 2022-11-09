package IR.Types;

public class PointerType extends Type {
    private Type tType;

    public PointerType(Type tType) {
        this.tType = tType;
    }

    public Type gettType() {
        return tType;
    }

    @Override
    public boolean isPointerType() {
        return true;
    }

    @Override
    public Type getType() {
        return this;
    }

    @Override
    public String toString() {
        return tType.toString() + "*";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof PointerType)) {
            return false;
        }
        PointerType ptr = (PointerType) o;
        return ptr.gettType().equals(this.tType);
    }
}
