package IR.Values.Instructions.Cast;

import IR.Types.Type;
import IR.Values.BasicBlock;
import IR.Values.Instructions.Instruction;
import IR.Values.Value;

import static IR.Values.Instructions.Operator.ZEXT;

public class CastBase extends Instruction {
    protected Type oldType, newType;
    protected Value value;

    public CastBase(BasicBlock basicBlock, Value value, Type newType) {
        super(basicBlock, newType, ZEXT);
        this.newType = newType;
        this.oldType = value.getType();
        this.value = value;
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
}
