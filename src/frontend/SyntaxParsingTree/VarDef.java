package frontend.SyntaxParsingTree;

import frontend.State;
import frontend.Token;
import java.util.ArrayList;

public class VarDef extends SyntaxParsingTree {
    private String ident;
    private final ArrayList<ConstExp> constExps;
    private InitVal initVal;
    private int mode;

    public VarDef() {
        label = State.VarDef.toLabel();
        ident = null;
        constExps = new ArrayList<>();
        initVal = null;
        mode = 0;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(Token.IDENFR).append(" ").append(ident).append("\n");
        if (mode > 0) {
            builder.append(Token.LBRACK).append(" ").append(Token.LBRACK.getName()).append("\n");
            builder.append(constExps.get(0).toString());
            builder.append(Token.RBRACK).append(" ").append(Token.RBRACK.getName()).append("\n");
            if (mode == 2) {
                builder.append(Token.LBRACK).append(" ").append(Token.LBRACK.getName()).append("\n");
                builder.append(constExps.get(1).toString());
                builder.append(Token.RBRACK).append(" ").append(Token.RBRACK.getName()).append("\n");
            }
        }
        if (initVal != null) {
            builder.append(Token.ASSIGN).append(" ").append(Token.ASSIGN.getName()).append("\n");
            builder.append(initVal.toString());
        }
        builder.append(label).append("\n");
        return builder.toString();
    }

    public void addConstExp(ConstExp constExp) {
        constExps.add(constExp);
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public void setInitVal(InitVal initVal) {
        this.initVal = initVal;
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

    public InitVal getInitVal() {
        return initVal;
    }

    public String getIdent() {
        return ident;
    }

    public int getMode() {
        return mode;
    }
}
