package IR.Types;

import java.util.Objects;

public class StringType extends Type {
    private int length;

    public StringType(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    @Override
    public Type getType() {
        return this;
    }

    @Override
    public String toString() {
        return "[" + length + " x i8]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        StringType that = (StringType) o;
        return length == that.length;
    }

    @Override
    public int hashCode() {
        return Objects.hash(length);
    }
}
