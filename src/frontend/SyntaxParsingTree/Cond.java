package frontend.SyntaxParsingTree;

import frontend.State;

public class Cond extends SyntaxParsingTree {
    private LOrExp lOrExp;

    public Cond() {
        label = State.Cond.toLabel();
        lOrExp = null;
    }

    public String toString() {
        return lOrExp.toString() +
                label + "\n";
    }

    public LOrExp getLOrExp() {
        return lOrExp;
    }

    public void setLOrExp(LOrExp lOrExp) {
        this.lOrExp = lOrExp;
    }
}
