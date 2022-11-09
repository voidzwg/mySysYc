package backend.MachineCode.MCInstructions.MCjump;

import backend.MachineCode.MCInstructions.mnemonic;
import backend.Registers.Registers;

public class jr extends MCJumpBase {
    public jr(Registers rs) {
        this.rs = rs;
        this.op = mnemonic.jr;
        use.add(rs);
    }
}
