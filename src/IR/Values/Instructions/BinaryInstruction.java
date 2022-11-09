package IR.Values.Instructions;

import IR.Types.IntegerType;
import IR.Types.VoidType;
import IR.Values.BasicBlock;
import IR.Values.ConstantInteger;
import IR.Values.Value;

public class BinaryInstruction extends Instruction {
    public BinaryInstruction(BasicBlock basicBlock, Operator op, Value leftValue, Value rightValue) {
        super(basicBlock, IntegerType.i32, op);
        addOperands(leftValue, rightValue);
        setName("");
    }

    public void addOperands(Value leftValue, Value rightValue) {
        this.addOperand(leftValue);
        this.addOperand(rightValue);
    }

    @Override
    public String toString() {
        String s1, s2;
        Value o0 = getOperands().get(0), o1 = getOperands().get(1);
        if (o0 instanceof ConstantInteger) {
            s1 = String.valueOf(((ConstantInteger) o0).getValue());
        } else {
            s1 = o0.getName();
        }
        if (o1 instanceof ConstantInteger) {
            s2 = String.valueOf(((ConstantInteger) o1).getValue());
        } else {
            s2 = o1.getName();
        }
        return getName() + " = " + op.symbolOf() + s1 + ", " + s2;
    }
}
