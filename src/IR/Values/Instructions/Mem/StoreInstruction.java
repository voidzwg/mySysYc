package IR.Values.Instructions.Mem;

import IR.Values.BasicBlock;
import IR.Values.ConstantInteger;
import IR.Values.GlobalVariable;
import IR.Values.Instructions.Operator;
import IR.Values.Value;

public class StoreInstruction extends MemBase {
    public StoreInstruction(BasicBlock basicBlock, Value ptr, Value value) {
        super(basicBlock, value.getType(), Operator.STR);
        this.addOperand(value);
        this.addOperand(ptr);
    }

    @Override
    public String toString() {
        String s1;
        Value o0 = getOperands().get(0);
        if (o0 instanceof ConstantInteger) {
            ConstantInteger integer = (ConstantInteger) o0;
            s1 = integer.getType() + " " + integer.getValue();
        } else if (o0 instanceof GlobalVariable && ((GlobalVariable) o0).isConstant()) {
            ConstantInteger integer = (ConstantInteger) ((GlobalVariable) o0).getValue();
            s1 = integer.getType() + " " + integer.getValue();
        } else {
            s1 = o0.getType() + " " + o0.getName();
        }
        return "store " + s1 + ", "
                + this.getOperands().get(1).getType()
                + " " + this.getOperands().get(1).getName();
    }
}
