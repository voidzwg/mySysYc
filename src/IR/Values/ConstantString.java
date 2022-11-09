package IR.Values;

import IR.Types.StringType;

import java.util.Objects;

public class ConstantString extends Constant {
    private String str;

    public ConstantString(int length, String name, String content) {
        super(new StringType(length), name);
        this.str = content;
    }

    public int getLength() {
        return ((StringType) this.getType()).getLength();
    }

    public String getStr() {
        return str;
    }

    @Override
    public String toString() {
        return this.getType() + " c\"" + str + "\"";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstantString that = (ConstantString) o;
        return Objects.equals(str, that.str) && ((StringType) getType()).getLength() == ((StringType) that.getType()).getLength();
    }

    @Override
    public int hashCode() {
        return Objects.hash(str);
    }
}
