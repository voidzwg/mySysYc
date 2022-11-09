package backend.Registers;

import java.util.Objects;

public class VirtualRegisters extends Registers {
    private static int count = 0;
    private int vid;

    public VirtualRegisters() {
        super(String.valueOf(count++));
        this.vid = count;
    }

    public int getVid() {
        return vid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        VirtualRegisters that = (VirtualRegisters) o;
        return vid == that.vid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), vid);
    }
}
