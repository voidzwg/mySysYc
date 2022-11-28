package backend.MachineCode;

import backend.MachineCode.MCInstructions.MCInstruction;
import utils.List;

import java.util.ListIterator;

public class MCBlock {
    private String name;
    private MCFunction function;
    private final List<MCInstruction, MCBlock> instructions;
    private final boolean isEntry;
    private MCBlock trueBlock;
    private MCBlock falseBlock;
    private boolean hasSuc = false;
    private boolean hasFalseSuc = false;

    public MCBlock(MCFunction function, boolean isEntry) {
        this.name = "_" + function.getName() + "_block_" + function.block_count++;
        this.function = function;
        this.isEntry = isEntry;
        this.instructions = new List<>(this);
        this.function.addBlock(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MCFunction getFunction() {
        return function;
    }

    public void setFunction(MCFunction function) {
        this.function = function;
    }

    public List<MCInstruction, MCBlock> getInstructions() {
        return instructions;
    }

    public void setTrueBlock(MCBlock trueBlock) {
        this.trueBlock = trueBlock;
        this.hasSuc = true;
    }

    public void setFalseBlock(MCBlock falseBlock) {
        this.falseBlock = falseBlock;
        this.hasSuc = true;
        this.hasFalseSuc = true;
    }

    public boolean hasSuc() {
        return hasSuc;
    }

    public boolean hasFalseSuc() {
        return hasFalseSuc;
    }

    public MCBlock getTrueBlock() {
        return trueBlock;
    }

    public MCBlock getFalseBlock() {
        return falseBlock;
    }

    public void addInstruction(MCInstruction instruction) {
        instructions.addLast(instruction);
        instruction.setBB(this);
    }

    // if function find afterThis in the list, insert instruction after afterThis
    public void insertInstructionAfter(MCInstruction afterThis, MCInstruction instruction) {
        ListIterator<MCInstruction> iterator = instructions.listIterator();
        while (iterator.hasNext()) {
            MCInstruction nextInstruction = iterator.next();
            if (afterThis.equals(nextInstruction)) {
                iterator.add(instruction);
                instruction.setBB(this);
                return;
            }
        }
    }

    // if function find beforeThis in the list, insert instruction before beforeThis
    public void insertInstructionBefore(MCInstruction beforeThis, MCInstruction instruction) {
        ListIterator<MCInstruction> iterator = instructions.listIterator();
        while (iterator.hasNext()) {
            MCInstruction nextInstruction = iterator.next();
            if (beforeThis.equals(nextInstruction)) {
                iterator.previous();
                iterator.add(instruction);
                instruction.setBB(this);
                return;
            }
        }
    }

    // if function find instruction in the list, delete it and return true, else return false
    public boolean deleteInstruction(MCInstruction instruction) {
        return instructions.remove(instruction);
    }

    public boolean isEntry() {
        return isEntry;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(name).append(":\n");
        for (MCInstruction instruction : instructions) {
            builder.append("\t").append(instruction).append("\n");
        }
        return builder.toString();
    }
}
