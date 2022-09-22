package frontend.CompTree;

import frontend.State;

public class Exp extends CompTree {
    private AddExp addExp;

    public Exp() {
        label = State.Exp.toLabel();
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
