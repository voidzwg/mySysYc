package IR.Values.Instructions;

import IR.Values.BasicBlock;
import IR.Values.ConstantInteger;
import IR.Values.Value;

import static IR.Types.IntegerType.i32;

public class BinaryInstruction extends Instruction {
    public BinaryInstruction(BasicBlock basicBlock, Operator op, Value leftValue, Value rightValue) {
        super(basicBlock, i32, op);
        addOperands(leftValue, rightValue);
        setName("");
    }

    public void addOperands(Value leftValue, Value rightValue) {
        this.addOperand(leftValue);
        this.addOperand(rightValue);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Value o0 = getOperands().get(0), o1 = getOperands().get(1);
        builder.append(getName()).append(" = ").append(op.symbolOf()).append(o0.getType()).append(" ");
        if (o0 instanceof ConstantInteger) {
            builder.append(((ConstantInteger) o0).getValue());
        } else {
            builder.append(o0.getName());
        }
        builder.append(", ");
        if (o1 instanceof ConstantInteger) {
            builder.append(((ConstantInteger) o1).getValue());
        } else {
            builder.append(o1.getName());
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
