package backend.MachineCode.MCInstructions.MCjump;

import backend.MachineCode.MCInstructions.MCInstruction;

public class MCJumpBase extends MCInstruction {
    protected String target;

    public MCJumpBase() {
        this.op = null;
        this.rs = null;
        this.rt = null;
        this.rd = null;
        this.target = null;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(op).append("\t\t");
        if (target != null) {
            builder.append(target);
        } else if (rs != null) {
            builder.append(rs);
        }
        return builder.toString();
    }
}
