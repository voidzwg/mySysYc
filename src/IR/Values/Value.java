package IR.Values;

import IR.Types.Type;
import IR.Use;

import java.util.ArrayList;
import java.util.Objects;

abstract public class Value {
    protected Type type;
    protected String name;
    protected final ArrayList<Use> uses;

    public Value() {
        uses = new ArrayList<>();
    }

    public Value(Type type, String name) {
        this.type = type;
        this.name = name;
        uses = new ArrayList<>();
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Use> getUses() {
        return uses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Value value = (Value) o;
        return Objects.equals(type, value.type) && Objects.equals(name, value.name) && Objects.equals(uses, value.uses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, uses);
    }
}
