package backend.MachineCode.MCInstructions.MCRTypeInstructions;

import backend.MachineCode.MCInstructions.mnemonic;
import backend.Registers.Registers;

public class mult extends MCRTypeInstruction {
    public mult(Registers rs, Registers rt) {
        super(mnemonic.mult, rs, rt, null);
        def.remove(def.size() - 1);
    }

    @Override
    public String toString() {
        return "mult\t" + rs + ", " + rt;
    }
}
