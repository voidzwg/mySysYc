package IR.Values.Instructions;

import IR.Values.BasicBlock;
import IR.Values.ConstantInteger;
import IR.Values.Value;

import static IR.Types.IntegerType.i1;

public class IcmpInstruction extends BinaryInstruction {
    public IcmpInstruction(BasicBlock basicBlock, Operator scheme, Value leftValue, Value rightValue) {
        super(basicBlock, scheme, leftValue, rightValue);
        this.setType(i1);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Value o0 = getOperands().get(0), o1 = getOperands().get(1);
        builder.append(getName()).append(" = icmp ").append(op.symbolOf()).append(o0.getType()).append(" ");
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
}
