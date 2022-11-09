package IR.Values.Instructions.Mem;

import IR.Types.Type;
import IR.Values.BasicBlock;
import IR.Values.Instructions.Instruction;
import IR.Values.Instructions.Operator;

public class MemBase extends Instruction {
    public MemBase(BasicBlock basicBlock, Type type, Operator op) {
        super(basicBlock, type, op);
    }

    @Override
    public String toString() {
        return "";
    }
}
