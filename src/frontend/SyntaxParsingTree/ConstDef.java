package frontend.SyntaxParsingTree;

import frontend.State;
import frontend.Token;
import java.util.ArrayList;

public class ConstDef extends SyntaxParsingTree {
    private String ident;
    private final ArrayList<ConstExp> constExps;
    private ConstInitVal constInitVal;
    private int mode;

    public ConstDef() {
        label = State.ConstDef.toLabel();
        constExps = new ArrayList<>();
        ident = null;
        mode = 0;
        constInitVal = null;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(Token.IDENFR).append(" ").append(ident).append("\n");
        for (ConstExp constExp : constExps) {
            builder.append(Token.LBRACK).append(" ").append(Token.LBRACK.getName()).append("\n");
            builder.append(constExp.toString());
            builder.append(Token.RBRACK).append(" ").append(Token.RBRACK.getName()).append("\n");
        }
        builder.append(Token.ASSIGN).append(" ").append(Token.ASSIGN.getName()).append("\n");
        builder.append(constInitVal.toString());
        builder.append(label).append("\n");
        return builder.toString();
    }

    public void addConstExp(ConstExp constExp) {
        constExps.add(constExp);
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public void setConstInitVal(ConstInitVal constInitVal) {
        this.constInitVal = constInitVal;
    }

    public void setMode0() {
        mode = 0;
    }

    public void setMode1() {
        mode = 1;
    }

    public void setMode2() {
        mode = 2;
    }

    public ArrayList<ConstExp> getConstExps() {
        return constExps;
    }

    public ConstInitVal getConstInitVal() {
        return constInitVal;
    }

    public String getIdent() {
        return ident;
    }

    public int getMode() {
        return mode;
    }
}
