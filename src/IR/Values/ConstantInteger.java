package IR.Values;

import IR.Types.IntegerType;

import java.util.Objects;

public class ConstantInteger extends Constant {
    private Integer value;
    public static ConstantInteger constantZero = new ConstantInteger();

    public ConstantInteger() {
        super(IntegerType.i32, "");
        value = 0;
    }

    public ConstantInteger(int value) {
        super(IntegerType.i32, "");
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return type.toString() + " " + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConstantInteger integer = (ConstantInteger) o;
        return Objects.equals(value, integer.value);
    }
}
