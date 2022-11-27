package IR.Types;

import java.util.Objects;

public class IntegerType extends Type {
    private final int length;

    public IntegerType(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        IntegerType that = (IntegerType) o;
        return length == that.length;
    }

    @Override
    public int hashCode() {
        return Objects.hash(length);
    }
}
