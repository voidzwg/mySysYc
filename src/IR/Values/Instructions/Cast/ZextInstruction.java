package IR.Values.Instructions.Cast;

import IR.Types.Type;
import IR.Values.BasicBlock;
import IR.Values.Value;

public class ZextInstruction extends CastBase {
    public ZextInstruction(BasicBlock basicBlock, Value value, Type newType) {
        super(basicBlock, value, newType);
    }

    @Override
    public String toString() {
        return name + " = zext " + value.getType() + " " + value.getName() + " to " + newType;
    }
}
