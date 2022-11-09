package IR.Values.Instructions.Mem;

import IR.Types.ArrayType;
import IR.Types.PointerType;
import IR.Types.Type;
import IR.Values.BasicBlock;
import IR.Values.Instructions.Operator;

public class AllocaInstruction extends MemBase {
    private boolean isConstant;
    private Type allocated;
    private Type arrayAllocated;

    public AllocaInstruction(BasicBlock basicBlock, Type allocated, boolean isConstant) {
        super(basicBlock, new PointerType(allocated), Operator.ALC);
        this.allocated = allocated;
        this.setName("");
        this.isConstant = isConstant;
        if (allocated.isArrayType()) {
            this.arrayAllocated = new PointerType(((ArrayType) allocated).getElementType());
        } else {
            this.arrayAllocated = null;
        }
    }

    public boolean isConstant() {
        return this.isConstant;
    }

    public int getAllocatedSize() {
        if (allocated instanceof ArrayType) {
            return ((ArrayType) allocated).getSize();
        } else {
            return 4;
        }
    }

    public Type getAllocated() {
        return this.allocated;
    }

    public Type getArrayAllocated() {
        return this.arrayAllocated;
    }

    @Override
    public String toString() {
        return this.getName() + " = alloca " + allocated;
    }
}
