package IR.Values.Instructions.Mem;

import IR.Types.PointerType;
import IR.Values.BasicBlock;
import IR.Values.Instructions.Operator;
import IR.Values.Value;

public class LoadInstruction extends MemBase {
    public LoadInstruction(BasicBlock basicBlock, Value ptr) {
        super(basicBlock, ((PointerType) ptr.getType()).gettType(), Operator.LD);
        this.addOperand(ptr);
        setName("");
    }

    public Value getPointer() {
        return getOperands().get(0);
    }

    @Override
    public String toString() {
        return this.getName() + " = " + "load " + this.getType() + ", "
                + this.getOperands().get(0).getType()
                +  " " + this.getOperands().get(0).getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoadInstruction that = (LoadInstruction) o;
        return getPointer().equals(that.getPointer());
    }
}
