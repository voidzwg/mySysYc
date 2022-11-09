package backend.MachineCode.MCInstructions.MCMemory;

import backend.MachineCode.MCInstructions.mnemonic;
import backend.Registers.Registers;

public class lw extends MCMemBase {
    public lw(Registers base, String label, int offset, Registers rt) {
        super(mnemonic.lw, base, label, offset, rt);
        def.add(rt);
        use.add(base);
    }

    public lw(Registers base, int offset, Registers rt) {
        super(mnemonic.lw, base, offset, rt);
        def.add(rt);
        use.add(base);
    }

    public lw(String label, int offset, Registers rt) {
        super(mnemonic.lw, label, offset, rt);
        def.add(rt);
    }
}
