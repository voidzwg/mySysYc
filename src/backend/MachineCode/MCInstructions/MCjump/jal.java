package backend.MachineCode.MCInstructions.MCjump;

import backend.MachineCode.MCInstructions.mnemonic;

import static backend.Registers.MCRegisterPool.ra;

public class jal extends MCJumpBase {
    public jal(String target) {
        this.target = target;
        this.op = mnemonic.jal;
        use.add(ra);
    }
}
