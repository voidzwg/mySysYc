package IR.Values;

import IR.Types.ArrayType;
import IR.Types.IntegerType;
import IR.Types.Type;

import java.util.ArrayList;

public class ConstantArray extends Constant {
    private Type elementType;
    private final int capacity;
    private int blockLength = 1;
    private final ArrayList<Value> values;
    private boolean needInit = false;
    private boolean isConst;

    public ConstantArray(Type elementType, int capacity, boolean isConst) {
        super(new ArrayType(elementType, capacity), "");
        this.capacity = capacity;
        this.elementType = elementType;
        this.values = new ArrayList<>();
        this.isConst = isConst;
    }

    public boolean isNeedInit() {
        return needInit;
    }

    public Type getElementType() {
        return elementType;
    }

    public void setElementType(Type elementType) {
        this.elementType = elementType;
        ((ArrayType) type).setElementType(elementType);
    }

    public int getBlockLength() {
        return this.blockLength;
    }

    public void setNeedInit(boolean needInit) {
        this.needInit = needInit;
        if (this.elementType instanceof ArrayType) {
            for (Value v : values) {
                ConstantArray array = (ConstantArray) v;
                array.setNeedInit(needInit);
            }
        }
    }

    // 逐层计算blockLength
    public void newLevel() {
        if (values.size() > 0) {
            Value v = values.get(0);
            if (v instanceof ConstantArray) {
                ConstantArray arr = (ConstantArray) v;
                arr.newLevel();
                this.blockLength *= arr.getBlockLength() * arr.getCapacity();
            }
        }
    }

    // get value at index if this array is a constant array
    public ConstantInteger getValueAt(ArrayList<Value> index) {
        if (values.size() == 0) {
            return null;
        }
        for (int i = 0; i < index.size(); i++) {
            Value value = index.get(i);
            if (!(value instanceof ConstantInteger)) {
                return null;
            }
            Value nextValue = index.get(((ConstantInteger) value).getValue());
            if (nextValue instanceof ConstantInteger && i != index.size() - 1) {
                return null;
            } else if (nextValue instanceof ConstantInteger) {
                return (ConstantInteger) nextValue;
            } else if (nextValue instanceof ConstantArray){
                return getValueAt((ArrayList<Value>) index.subList(1, index.size()));
            }
        }
        return null;
    }

    public int getCapacity() {
        return capacity;
    }

    public ArrayList<Value> getValues() {
        return values;
    }

    public void addValue(Value value) {
        if (values.size() < capacity) {
            values.add(value);
        }
    }

    public void addValue(Value value, int index) {
        while (values.size() < index) {
            addValue(null);
        }
        if (values.size() == index) {
            addValue(value);
        } else {
            values.set(index, value);
        }
    }

    public void insert(Value a, int index) {
        int i = index / this.blockLength, nextIndex = index % this.blockLength;
        if (i < capacity) {
            if (elementType == IntegerType.i32) {
                addValue(a, index);
            } else if (elementType instanceof ArrayType) {
                ConstantArray array = (ConstantArray) values.get(i);
                array.insert(a, nextIndex);
            }
        }
    }

    public ArrayList<Value> getAllValues() {
        ArrayList<Value> values = new ArrayList<>();
        for (Value value : this.values) {
            if (value instanceof ConstantArray) {
                values.addAll(((ConstantArray) value).getAllValues());
            } else {
                values.add(value);
            }
        }
        return values;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getType()).append(" ");
        if (needInit) {
            builder.append("zeroinitializer");
        } else {
            builder.append("[");
            if (values.size() > 0) {
                builder.append(values.get(0));
            }
            for (int i = 1; i < values.size(); i++) {
                builder.append(", ").append(values.get(i));
            }
            builder.append("]");
        }
        return builder.toString();
    }
}