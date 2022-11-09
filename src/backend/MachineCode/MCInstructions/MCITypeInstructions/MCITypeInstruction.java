package backend.MachineCode.MCInstructions.MCITypeInstructions;

import backend.MachineCode.MCInstructions.MCInstruction;
import backend.MachineCode.MCInstructions.mnemonic;
import backend.Registers.Registers;

public class MCITypeInstruction extends MCInstruction {
    // I Type Instructions
    protected int immediate;

    public MCITypeInstruction(mnemonic op, Registers rs, Registers rt, int immediate) {
        this.op = op;
        this.rs = rs;
        this.rt = rt;
        this.rd = null;
        this.immediate = immediate;
        def.add(rt);
        use.add(rs);
    }

    public void setImmediate(int imm) {
        this.immediate = imm;
    }

    @Override
    public String toString() {
        if (op == mnemonic.addiu) {
            return op + "\t" + rt + ", " + rs + ", " + immediate;
        } else {
            return op + "\t\t" + rt + ", " + rs + ", " + immediate;
        }
    }
}
