package IR.Values.Instructions;

import IR.Types.Type;
import IR.Values.BasicBlock;
import IR.Values.Instructions.Mem.AllocaInstruction;
import IR.Values.User;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instruction that = (Instruction) o;
        return op == that.op && getOperands().equals(that.getOperands());
    }

    @Override
    public int hashCode() {
        return Objects.hash(op, basicBlock);
    }
}
