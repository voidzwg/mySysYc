package backend.MachineCode.MCInstructions.MCBranch;

import backend.Registers.Registers;

import static backend.MachineCode.MCInstructions.mnemonic.beqz;

public class beqz extends BranchBase {
    public beqz(Registers rs, String target) {
        super(beqz, rs, null, target);
        use.remove(1);
    }
}
