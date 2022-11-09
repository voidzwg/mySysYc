package backend.MachineCode.MCInstructions.MCMemory;

import backend.MachineCode.MCInstructions.MCInstruction;
import backend.MachineCode.MCInstructions.mnemonic;
import backend.Registers.MCRegisters;
import backend.Registers.Registers;

public class MCMemBase extends MCInstruction {
    // memory operations
    protected Registers base;
    protected int offset;
    protected String label;

    public MCMemBase(mnemonic op, Registers base, String label, int offset, Registers rt) {
        this.op = op;
        this.base = base;
        this.label = label;
        this.offset = offset;
        this.rt = rt;
    }

    public MCMemBase(mnemonic op, Registers base, int offset, Registers rt) {
        this.op = op;
        this.base = base;
        this.label = null;
        this.offset = offset;
        this.rt = rt;
    }

    public MCMemBase(mnemonic op, String label, int offset, Registers rt) {
        this.op = op;
        this.base = null;
        this.label = label;
        this.offset = offset;
        this.rt = rt;
    }

    public Registers getBase() {
        return base;
    }

    public void setBase(Registers base) {
        this.base = base;
    }

    @Override
    public void set(Registers reg, Registers reg1) {
        super.set(reg, reg1);
        if (reg == base) {
            setBase(reg1);
        }
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(op).append("\t\t").append(rt).append(", ");
        if (label != null) {
            builder.append(label);
            if (offset != 0) {
                builder.append("+");
            }
        }
        if (offset != 0) {
            builder.append(offset);
        }
        if (base != null) {
            builder.append("(").append(base).append(")");
        }
        return builder.toString();
    }
}
