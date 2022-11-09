package backend.MachineCode.MCInstructions.MCMemory;

import backend.MachineCode.MCInstructions.mnemonic;
import backend.Registers.Registers;

public class la extends MCMemBase {
    public la(Registers base, String label, int offset, Registers rt) {
        super(mnemonic.la, base, label, offset, rt);
        def.add(rt);
        use.add(base);
    }

    public la(Registers base, int offset, Registers rt) {
        super(mnemonic.la, base, offset, rt);
        def.add(rt);
        use.add(base);
    }

    public la(String label, int offset, Registers rt) {
        super(mnemonic.la, label, offset, rt);
        def.add(rt);
    }
}
