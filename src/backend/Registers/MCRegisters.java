package backend.Registers;

public class MCRegisters extends Registers {
    private boolean allocated;
    RegistersNameMap map = RegistersNameMap.getInstance();

    public MCRegisters(String name, boolean allocated) {
        super(name);
        this.allocated = allocated;
        this.setId(map.getRegisterNum(this.getName()));
    }

    public MCRegisters(int id, boolean allocated) {
        super(id);
        this.allocated = allocated;
        this.setName(map.getRegisterName(this.getId()));
    }

    public void setAllocated(boolean allocated) {
        this.allocated = allocated;
    }

    public boolean isAllocated() {
        return allocated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MCRegisters registers = (MCRegisters) o;
        return getId() == registers.getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }
}
