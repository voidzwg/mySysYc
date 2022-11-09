package frontend.SyntaxParsingTree;

import frontend.State;

public class Exp extends SyntaxParsingTree {
    private AddExp addExp;

    public Exp() {
        label = State.Exp.toLabel();
        addExp = null;
    }

    public String toString() {
        return addExp.toString() +
                label + "\n";
    }

    public AddExp getAddExp() {
        return addExp;
    }

    public void setAddExp(AddExp addExp) {
        this.addExp = addExp;
    }
}
