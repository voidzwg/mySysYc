package frontend.SyntaxParsingTree;

import frontend.State;
import frontend.Token;
import java.util.ArrayList;

public class ConstInitVal extends SyntaxParsingTree {
    private ConstExp constExp;
    private final ArrayList<ConstInitVal> constInitVals;

    public ConstInitVal() {
        label = State.ConstInitVal.toLabel();
        constExp = null;
        constInitVals = new ArrayList<>();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (constExp != null) {
            builder.append(constExp.toString());
        } else {
            builder.append(Token.LBRACE).append(" ").append(Token.LBRACE.getName()).append("\n");
            if (constInitVals.size() > 0) {
                builder.append(constInitVals.get(0).toString());
                for (int i = 1; i < constInitVals.size(); i++) {
                    builder.append(Token.COMMA).append(" ").append(Token.COMMA.getName()).append("\n");
                    builder.append(constInitVals.get(i).toString());
                }
            }
            builder.append(Token.RBRACE).append(" ").append(Token.RBRACE.getName()).append("\n");
        }
        builder.append(label).append("\n");
        return builder.toString();
    }

    public ConstExp getConstExp() {
        return constExp;
    }

    public void setConstExp(ConstExp constExp) {
        this.constExp = constExp;
    }

    public void addConstInitVal(ConstInitVal constInitVal) {
        constInitVals.add(constInitVal);
    }

    public ArrayList<ConstInitVal> getConstInitVal() {
        return constInitVals;
    }
}
