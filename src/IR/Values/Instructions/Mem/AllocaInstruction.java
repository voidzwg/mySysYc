package IR.Values.Instructions.Mem;

import IR.Types.ArrayType;
import IR.Types.PointerType;
import IR.Types.Type;
import IR.Values.BasicBlock;
import IR.Values.Instructions.Operator;

import java.util.Objects;

public class AllocaInstruction extends MemBase {
    private boolean isConstant;
    private Type allocated;
    private Type arrayAllocated;

    public AllocaInstruction(BasicBlock basicBlock, String name, Type allocated, boolean isConstant) {
        super(basicBlock, new PointerType(allocated), Operator.ALC);
        this.allocated = allocated;
        this.setName("%" + name);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AllocaInstruction that = (AllocaInstruction) o;
        return isConstant == that.isConstant && allocated.equals(that.allocated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isConstant, allocated);
    }
}
