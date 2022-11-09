package backend.MachineCode.MCInstructions.MCjump;

import backend.MachineCode.MCInstructions.mnemonic;

public class j extends MCJumpBase {
    public j(String target) {
        this.target = target;
        this.op = mnemonic.j;
    }
}
