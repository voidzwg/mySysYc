package backend.MachineCode.MCInstructions.specialInstruction;

import backend.MachineCode.MCInstructions.MCInstruction;
import backend.MachineCode.MCInstructions.mnemonic;
import backend.Registers.Registers;

public class mflo extends MCInstruction {
    public mflo(Registers rd) {
        this.op = mnemonic.mflo;
        this.rs = null;
        this.rt = null;
        this.rd = rd;
        def.add(rd);
    }

    @Override
    public String toString() {
        return "mflo\t" + rd;
    }
}
