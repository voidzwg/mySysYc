package IR.Types;

import java.util.Objects;

public abstract class Type {
    public abstract Type getType();

    public boolean isArrayType() {
        return false;
    }

    public boolean isVoidType() {
        return false;
    }

    public boolean isFunctionType() {
        return false;
    }

    public boolean isIntType() {
        return false;
    }

    public boolean isPointerType() {
        return false;
    }

    public boolean isLabelType() {
        return false;
    }

    public boolean isString() {
        return false;
    }

    public int getSize() {
        return 0;
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
