package backend.MachineCode.MCInstructions.MCBranch;

import backend.Registers.Registers;

import static backend.MachineCode.MCInstructions.mnemonic.bnez;

public class bnez extends BranchBase {
    public bnez(Registers rs, String target) {
        super(bnez, rs, null, target);
        use.remove(1);
    }
}
