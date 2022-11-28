package backend.MachineCode.MCInstructions;

import backend.MachineCode.MCBlock;
import backend.Registers.Registers;

import java.util.ArrayList;

public class MCInstruction {
    // the first, second and third 8-bit register code in MIPS-32 instructions
    protected Registers rs = null, rt = null, rd = null;
    protected MCBlock bb;

    public MCInstruction() {
    }

    public void setBB(MCBlock bb) {
        this.bb = bb;
    }

    public MCBlock getBB() {
        return bb;
    }

    public mnemonic op = mnemonic.nop;
    public ArrayList<Registers> use = new ArrayList<>();
    public ArrayList<Registers> def = new ArrayList<>();

    public void setRs(Registers rs) {
        this.rs = rs;
    }

    public void setRt(Registers rt) {
        this.rt = rt;
    }

    public void setRd(Registers rd) {
        this.rd = rd;
    }

    public void set(Registers reg, Registers reg1) {
        if (reg == rs) {
            setRs(reg1);
        } else if (reg == rt) {
            setRt(reg1);
        } else if (reg == rd) {
            setRd(reg1);
        }
    }

    public Registers getRs() {
        return rs;
    }

    public Registers getRt() {
        return rt;
    }

    public Registers getRd() {
        return rd;
    }

    @Override
    public String toString() {
        return op.toString();
    }
}
