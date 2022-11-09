package backend.MachineCode.MCInstructions.MCRTypeInstructions;

import backend.MachineCode.MCInstructions.MCInstruction;
import backend.MachineCode.MCInstructions.mnemonic;
import backend.Registers.Registers;

public class MCRTypeInstruction extends MCInstruction {
    // R type Instructions
    public MCRTypeInstruction(mnemonic op, Registers rs, Registers rt, Registers rd) {
        this.op = op;
        this.rs = rs;
        this.rt = rt;
        this.rd = rd;
        use.add(rs);
        use.add(rt);
        def.add(rd);
    }

    @Override
    public String toString() {
        if (op == mnemonic.addu) {
            return op + "\t" + rd + ", " + rs + ", " + rt;
        } else {

            return op + "\t\t" + rd + ", " + rs + ", " + rt;
        }
    }
}
