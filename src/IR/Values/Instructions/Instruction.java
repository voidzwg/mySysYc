package IR.Values.Instructions;

import IR.Types.Type;
import IR.Values.BasicBlock;
import IR.Values.Instructions.Mem.AllocaInstruction;
import IR.Values.User;

public class Instruction extends User {
    protected Operator op;
    protected BasicBlock basicBlock;

    public Instruction(BasicBlock basicBlock, Type type, Operator op) {
        super(type, "");
        this.op = op;
        this.basicBlock = basicBlock;
        if (basicBlock != null) {
            if (this instanceof AllocaInstruction) {
                basicBlock.addAllocaInstruction(this);
            } else {
                basicBlock.addInstruction(this);
            }
        }
    }

    public Operator getOp() {
        return op;
    }

    public BasicBlock getBasicBlock() {
        return basicBlock;
    }

    @Override
    public String toString() {
        return "";
    }
}
