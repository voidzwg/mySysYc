package IR.Values;

import IR.Types.LabelType;
import IR.Values.Instructions.Instruction;
import utils.List;

public class BasicBlock extends Value {
    private final Function function;
    private final List<Instruction, BasicBlock> instructions;
    private final boolean isEntry;

    public BasicBlock(Function function, boolean isEntry) {
        super(new LabelType(""), "");
        instructions = new List<>(this);
        this.function = function;
        this.isEntry = isEntry;
    }

    public boolean isEntry() {
        return isEntry;
    }

    public Function getFunction() {
        return function;
    }

    public void addInstruction(Instruction instruction) {
        instructions.addLast(instruction);
    }

    public void addAllocaInstruction(Instruction alloca) {
        instructions.addFirst(alloca);
    }

    public List<Instruction, BasicBlock> getInstList() {
        return instructions;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (!isEntry) {
            builder.append(type);
        }
        for (Instruction instruction : instructions) {
            builder.append("  ").append(instruction).append("\n");
        }
        return builder.toString();
    }
}
