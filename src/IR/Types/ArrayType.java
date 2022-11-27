package IR.Types;

public class ArrayType extends Type {
    private Type elementType;
    private final int maxLength;

    public ArrayType(Type elementType, int maxLength) {
        this.elementType = elementType;
        this.maxLength = maxLength;
    }

    public Type getElementType() {
        return elementType;
    }

    public void setElementType(Type type) {
        this.elementType = type;
    }

    public int getMaxLength() {
        return this.maxLength;
    }

    public int getDim() {
        int dim = 1;
        if (elementType.isArrayType()) {
            dim += ((ArrayType) elementType).getDim();
        }
        return dim;
    }

    // how much space will this array cost in bytes
    @Override
    public int getSize() {
        int size = maxLength;
        if (elementType instanceof ArrayType) {
            size *= ((ArrayType) elementType).getSize();
        } else {
            size *= 4;
        }
        return size;
    }

    @Override
    public boolean isArrayType() {
        return true;
    }

    @Override
    public Type getType() {
        return this;
    }

    @Override
    public String toString() {
        return "[" + this.maxLength + " x " + this.elementType + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof ArrayType)) {
            return false;
        }
        ArrayType arr = (ArrayType) o;
        if (!arr.getElementType().equals(this.elementType)) {
            return false;
        }
        return arr.getMaxLength() == this.getMaxLength();
    }
}
