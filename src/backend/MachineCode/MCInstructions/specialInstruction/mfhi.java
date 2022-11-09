package backend.MachineCode.MCInstructions.specialInstruction;

import backend.MachineCode.MCInstructions.MCInstruction;
import backend.MachineCode.MCInstructions.mnemonic;
import backend.Registers.Registers;

public class mfhi extends MCInstruction {
    public mfhi(Registers rd) {
        this.op = mnemonic.mfhi;
        this.rs = null;
        this.rt = null;
        this.rd = rd;
        def.add(rd);
    }

    @Override
    public String toString() {
        return "mfhi\t" + rd;
    }
}
