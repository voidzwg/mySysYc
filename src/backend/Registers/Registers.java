package backend.Registers;

import java.util.Objects;

public class Registers{
    private int id;
    private String name;
    private MCRegisters color = null;

    public Registers(int id) {
        this.id = id;
        this.name = "zero";
    }

    public Registers(String name) {
        this.name = name;
        this.id = 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public void color(MCRegisters color) {
        this.color = color;
    }

    public MCRegisters coloredBy() {
        return color;
    }

    @Override
    public String toString() {
        return "$" + this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Registers registers = (Registers) o;
        return id == registers.id && name.equals(registers.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
