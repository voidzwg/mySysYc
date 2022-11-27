package IR.Types;

public class LabelType extends Type {
    private String name;

    public LabelType(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isLabelType() {
        return true;
    }

    @Override
    public Type getType() {
        return this;
    }

    @Override
    public String toString() {
        return name + ":\n";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof LabelType)) {
            return false;
        }
        LabelType label = (LabelType) o;
        return label.getName().equals(this.name);
    }
}
