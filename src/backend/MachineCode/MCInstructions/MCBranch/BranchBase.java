package backend.MachineCode.MCInstructions.MCBranch;

import backend.MachineCode.MCInstructions.MCInstruction;
import backend.MachineCode.MCInstructions.mnemonic;
import backend.Registers.Registers;

public class BranchBase extends MCInstruction {
    protected String target;

    public BranchBase(mnemonic op, Registers rs, Registers rt, String target) {
        this.target = target;
        this.op = op;
        this.rs = rs;
        this.rt = rt;
        use.add(rs);
        use.add(rt);
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTarget() {
        return target;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(op).append("\t");
        if (!(op == mnemonic.bnez || op == mnemonic.beqz)) {
            builder.append("\t");
        }
        builder.append(rs).append(", ");
        if (rt != null) {
            builder.append(rt).append(", ");
        }
        builder.append(target);
        return builder.toString();
    }
}
