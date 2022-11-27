package IR.Values.Instructions.Terminator;

import IR.Values.BasicBlock;
import IR.Values.Value;

import static IR.Types.VoidType.Void;
import static IR.Values.Instructions.Operator.BR;

public class BrInstruction extends TerminatorBase {
    private Value result;
    private BasicBlock targetTrue, targetFalse;

    public BrInstruction(BasicBlock basicBlock, Value result, BasicBlock targetTrue, BasicBlock targetFalse) {
        super(basicBlock, Void, BR);
        this.result = result;
        this.targetTrue = targetTrue;
        this.targetFalse = targetFalse;
    }

    public BrInstruction(BasicBlock basicBlock, BasicBlock target) {
        super(basicBlock, Void, BR);
        this.result = null;
        this.targetTrue = target;
        this.targetFalse = null;
    }

    public void setResult(Value result) {
        this.result = result;
    }

    public void setTargetTrue(BasicBlock targetTrue) {
        this.targetTrue = targetTrue;
    }

    public void setTargetFalse(BasicBlock targetFalse) {
        this.targetFalse = targetFalse;
    }

    public Value getResult() {
        return result;
    }

    public BasicBlock getTargetTrue() {
        return targetTrue;
    }

    public BasicBlock getTargetFalse() {
        return targetFalse;
    }

    public boolean isUnconditional() {
        return result == null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("br ");
        if (result != null) {
            builder.append("i1 ").append(result.getName()).append(", label %").append(targetTrue.getName()).append(", label %").append(targetFalse.getName());
        } else {
            builder.append("label %").append(targetTrue.getName());
        }
        builder.append("\n");
        return builder.toString();
    }
}
