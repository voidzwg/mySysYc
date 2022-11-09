package frontend.SyntaxParsingTree;

import frontend.State;
import frontend.Token;
import java.util.ArrayList;

public class InitVal extends SyntaxParsingTree {
    private Exp exp;
    private final ArrayList<InitVal> initVals;

    public InitVal() {
        label = State.InitVal.toLabel();
        exp = null;
        initVals = new ArrayList<>();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (exp != null) {
            builder.append(exp.toString());
        } else {
            builder.append(Token.LBRACE).append(" ").append(Token.LBRACE.getName()).append("\n");
            if (initVals.size() > 0) {
                builder.append(initVals.get(0).toString());
                for (int i = 1; i < initVals.size(); i++) {
                    builder.append(Token.COMMA).append(" ").append(Token.COMMA.getName()).append("\n");
                    builder.append(initVals.get(i).toString());
                }
            }
            builder.append(Token.RBRACE).append(" ").append(Token.RBRACE.getName()).append("\n");
        }
        builder.append(label).append("\n");
        return builder.toString();
    }

    public Exp getExp() {
        return exp;
    }

    public void setExp(Exp exp) {
        this.exp = exp;
    }

    public void addInitVal(InitVal initVal) {
        initVals.add(initVal);
    }

    public ArrayList<InitVal> getInitVal() {
        return initVals;
    }
}
