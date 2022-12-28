package IR.Values.Instructions.Mem;

import IR.Types.ArrayType;
import IR.Types.PointerType;
import IR.Types.Type;
import IR.Values.BasicBlock;
import IR.Values.ConstantInteger;
import IR.Values.GlobalVariable;
import IR.Values.Value;

import java.util.ArrayList;
import java.util.Objects;

import static IR.Values.Instructions.Operator.GEP;

public class GEPInstruction extends MemBase {
    private Type elementType;
    private Value targetValue;

    public GEPInstruction(BasicBlock bb, Value ptr, ArrayList<Value> initial) {
        super(bb, new PointerType(getElementType(ptr, initial)), GEP);
        this.elementType = getElementType(ptr, initial);
        this.targetValue = ptr;
        this.addOperand(ptr);
        for (Value value : initial) {
            this.addOperand(value);
        }
    }

    public Type getElementType() {
        return elementType;
    }

    public void setElementType(Type elementType) {
        this.elementType = elementType;
    }

    public Value getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(Value targetValue) {
        this.targetValue = targetValue;
    }

    private static Type getElementType(Value ptr, ArrayList<Value> initial) {
        Type type = ptr.getType();
        if (type.isArrayType() || type.isPointerType()) {
            for (int i = 0; i < initial.size(); i++) {
                if (type.isArrayType()) {
                    type = ((ArrayType) type).getElementType();
                } else if (type.isPointerType()) {
                    type = ((PointerType) type).gettType();
                } else {
                    break;
                }
            }
        }
        return type;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.name).append(" = getelementptr ");
        builder.append(((PointerType) getOperands().get(0).getType()).gettType()).append(", ");
        for (int i = 0; i < getOperands().size(); i++) {
            if (i != 0) {
                builder.append(", ");
            }
            Value v = this.getOperands().get(i);
            String s = "";
            if (v instanceof ConstantInteger) {
                ConstantInteger integer = (ConstantInteger) v;
                s = integer.getType() + " " + integer.getValue();
            } else {
                s = v.getType() + " " + v.getName();
            }
            builder.append(s);
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GEPInstruction that = (GEPInstruction) o;
        return elementType.equals(that.elementType) && targetValue.equals(that.targetValue) && getOperands().equals(that.getOperands());
    }

    @Override
    public int hashCode() {
        return Objects.hash(elementType, targetValue);
    }
}
