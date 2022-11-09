package backend.MachineCode.MCInstructions.MCMemory;

import backend.MachineCode.MCInstructions.mnemonic;
import backend.Registers.Registers;

public class sw extends MCMemBase {
    public sw(Registers base, String label, int offset, Registers rt) {
        super(mnemonic.sw, base, label, offset, rt);
        use.add(base);
        use.add(rt);
    }

    public sw(Registers base, int offset, Registers rt) {
        super(mnemonic.sw, base, offset, rt);
        use.add(base);
        use.add(rt);
    }

    public sw(String label, int offset, Registers rt) {
        super(mnemonic.sw, label, offset, rt);
        use.add(rt);
    }
}
