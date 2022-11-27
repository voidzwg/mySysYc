package IR.Values;

import IR.Types.IntegerType;

import java.util.Objects;

import static IR.Types.IntegerType.*;

public class ConstantInteger extends Constant {
    private Integer value;
    public static final ConstantInteger constantZero = new ConstantInteger();
    public static final ConstantInteger True = new ConstantInteger(true);
    public static final ConstantInteger False = new ConstantInteger(false);

    public ConstantInteger() {
        super(i32, "");
        value = 0;
    }

    public ConstantInteger(int value) {
        super(i32, "");
        this.value = value;
    }

    public ConstantInteger(boolean value) {
        super(i1, "");
        this.value = value ? 1 : 0;
    }

    public ConstantInteger(int value, int mode) {
        super(i32, "");
        this.value = value;
        switch (mode) {
            case 1:
                setType(i1);
                if (value != 0) {
                    this.value = 1;
                } else {
                    this.value = 0;
                }
                break;
            case 8:
                setType(i8);
                break;
            case 32:
                break;
            default:
                setType(new IntegerType(mode));
                break;
        }
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
        return Objects.equals(value, integer.value) && this.type.equals(integer.type);
    }
}
