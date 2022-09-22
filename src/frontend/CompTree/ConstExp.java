package frontend.CompTree;

import frontend.State;

public class ConstExp extends CompTree {
    private AddExp addExp;

    public ConstExp() {
        label = State.ConstExp.toLabel();
        addExp = null;
    }

    public String print() {
        return addExp.print() +
                label + "\n";
    }

    public AddExp getAddExp() {
        return addExp;
    }

    public void setAddExp(AddExp addExp) {
        this.addExp = addExp;
    }
}
