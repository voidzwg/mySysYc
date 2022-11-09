package backend.MachineCode;

import utils.List;

public class MCFunction {
    private String name;
    private final List<MCBlock, MCFunction> blocks;
    public int block_count = 0;

    public MCFunction(String name) {
        this.name = name;
        this.blocks = new List<>(this);
    }

    public List<MCBlock, MCFunction> getBlocks() {
        return blocks;
    }

    public void addBlock(MCBlock block) {
        blocks.addLast(block);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("# function ").append(name).append("()\n");
        for (MCBlock block : blocks) {
            builder.append(block);
        }
        builder.append("\n");
        return builder.toString();
    }
}
