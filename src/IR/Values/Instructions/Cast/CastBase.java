package IR.Values.Instructions.Cast;

import IR.Types.Type;
import IR.Values.BasicBlock;
import IR.Values.Instructions.Instruction;
import IR.Values.Value;

import java.util.Objects;

import static IR.Values.Instructions.Operator.ZEXT;

public class CastBase extends Instruction {
    protected Type oldType, newType;
    protected Value value;

    public CastBase(BasicBlock basicBlock, Value value, Type newType) {
        super(basicBlock, newType, ZEXT);
        this.newType = newType;
        this.oldType = value.getType();
        this.value = value;
        addOperand(value);
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public Type getOldType() {
        return oldType;
    }

    public void setOldType(Type oldType) {
        this.oldType = oldType;
    }

    public Type getNewType() {
        return newType;
    }

    public void setNewType(Type newType) {
        this.newType = newType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CastBase castBase = (CastBase) o;
        return Objects.equals(oldType, castBase.oldType) && Objects.equals(newType, castBase.newType) && Objects.equals(value, castBase.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), oldType, newType, value);
    }
}
