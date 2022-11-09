package backend.MachineCode.MCInstructions.MCRTypeInstructions;

import backend.MachineCode.MCInstructions.mnemonic;
import backend.Registers.Registers;

import static backend.Registers.MCRegisterPool.zero;

public class move extends MCRTypeInstruction {
    // move contents of rs into rd
    public move(Registers rs, Registers rd) {
        super(mnemonic.addu, rs, zero, rd);
        use.remove(use.size() - 1);
    }

    @Override
    public String toString() {
        return "move\t" + rd + ", " + rs;
    }
}
