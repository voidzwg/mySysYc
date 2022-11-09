package frontend.SyntaxParsingTree;

import frontend.State;
import frontend.Token;

public class FuncFParam extends SyntaxParsingTree {
    private final BType bType;
    private String ident;
    private ConstExp constExp;
    private int mode;

    public FuncFParam() {
        label = State.FuncFParam.toLabel();
        bType = new BType();
        ident = null;
        constExp = null;
        mode = 0;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(bType.toString());
        builder.append(Token.IDENFR).append(" ").append(ident).append("\n");
        if (mode > 0) {
            builder.append(Token.LBRACK).append(" ").append(Token.LBRACK.getName()).append("\n");
            builder.append(Token.RBRACK).append(" ").append(Token.RBRACK.getName()).append("\n");
            if (mode == 2) {
                builder.append(Token.LBRACK).append(" ").append(Token.LBRACK.getName()).append("\n");
                builder.append(constExp.toString());
                builder.append(Token.RBRACK).append(" ").append(Token.RBRACK.getName()).append("\n");
            }
        }
        builder.append(label).append("\n");
        return builder.toString();
    }

    public BType getbType() {
        return bType;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public ConstExp getConstExp() {
        return constExp;
    }

    public void setConstExp(ConstExp constExp) {
        this.constExp = constExp;
    }

    public int getMode() {
        return mode;
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
}
