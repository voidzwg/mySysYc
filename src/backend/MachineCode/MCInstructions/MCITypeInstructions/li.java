package backend.MachineCode.MCInstructions.MCITypeInstructions;

import backend.MachineCode.MCInstructions.mnemonic;
import backend.Registers.Registers;

import static backend.Registers.MCRegisterPool.zero;

public class li extends MCITypeInstruction {
    // load immediate
    public li(Registers rt, int immediate) {
        super(mnemonic.addiu, zero, rt, immediate);
        use.remove(use.size() - 1);
    }

    @Override
    public String toString() {
        return "li\t\t" + rt + ", " + immediate;
    }
}
