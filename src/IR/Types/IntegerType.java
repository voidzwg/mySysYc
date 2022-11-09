package IR.Types;

public class IntegerType extends Type {
    private int length;

    public IntegerType(int length) {
        this.length = length;
    }

    public static final IntegerType i32 = new IntegerType(32);
    public static final IntegerType i8 = new IntegerType(8);
    public static final IntegerType i1 = new IntegerType(1);

    @Override
    public boolean isIntType() {
        return true;
    }

    @Override
    public Type getType() {
        return i32;
    }

    @Override
    public int getSize() {
        return 4;
    }

    @Override
    public String toString() {
        return "i" + length;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        return o instanceof IntegerType;
    }
}
