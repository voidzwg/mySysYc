package IR.Values.Instructions.Terminator;

import IR.Values.BasicBlock;
import IR.Values.ConstantInteger;
import IR.Values.Value;

import static IR.Types.VoidType.Void;
import static IR.Values.Instructions.Operator.RET;

public class RetInstruction extends TerminatorBase {
    public RetInstruction(BasicBlock basicBlock) {
        super(basicBlock, Void, RET);
    }

    public RetInstruction(BasicBlock basicBlock, Value value) {
        super(basicBlock, value.getType(), RET);
        this.addOperand(value);
    }

    public Value getRetValue() {
        if (getOperands().size() > 0) {
            return getOperands().get(0);
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ret ").append(getType());
        if (getOperands().size() > 0) {
            Value ret = getOperands().get(0);
            String s;
            if (ret instanceof ConstantInteger) {
                s = String.valueOf(((ConstantInteger) ret).getValue());
            } else {
                s = ret.getName();
            }
            builder.append(" ").append(s);
        }
        return builder.toString();
    }
}
