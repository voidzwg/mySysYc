package frontend.SyntaxParsingTree;

import frontend.State;

public class Cond extends SyntaxParsingTree {
    private LOrExp lOrExp;

    public Cond() {
        label = State.Cond.toLabel();
        lOrExp = null;
    }

    public String print() {
        return lOrExp.print() +
                label + "\n";
    }

    public LOrExp getLOrExp() {
        return lOrExp;
    }

    public void setLOrExp(LOrExp lOrExp) {
        this.lOrExp = lOrExp;
    }
}
