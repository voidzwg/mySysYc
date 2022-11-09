package backend.MachineCode.MCInstructions.MCRTypeInstructions;

import backend.MachineCode.MCInstructions.mnemonic;
import backend.Registers.Registers;

public class div extends MCRTypeInstruction {
    public div(Registers rs, Registers rt) {
        super(mnemonic.div, rs, rt, null);
        def.remove(def.size() - 1);
    }

    @Override
    public String toString() {
        return "div\t\t" + rs + ", " + rt;
    }
}
