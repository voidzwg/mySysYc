package backend.MachineCode.MCInstructions.MCTerminator;

import backend.MachineCode.MCInstructions.MCInstruction;
import backend.MachineCode.MCInstructions.mnemonic;

public class syscall extends MCInstruction {
    public syscall() {
        this.op = mnemonic.syscall;
        this.rs = null;
        this.rt = null;
        this.rd = null;
    }

    @Override
    public String toString() {
        return "syscall";
    }
}
