package frontend.CompTree;

import frontend.State;
import frontend.Token;
import java.util.ArrayList;

public class LVal extends CompTree {
    private String ident;
    private final ArrayList<Exp> exps;
    private int mode;

    public LVal() {
        label = State.LVal.toLabel();
        ident = null;
        exps = new ArrayList<>();
        mode = 0;
    }

    public String print() {
        StringBuilder builder = new StringBuilder();
        builder.append(Token.IDENFR).append(" ").append(ident).append("\n");
        if (mode > 0) {
            builder.append(Token.LBRACK).append(" ").append(Token.LBRACK.getName()).append("\n");
            builder.append(exps.get(0).print());
            builder.append(Token.RBRACK).append(" ").append(Token.RBRACK.getName()).append("\n");
            if (mode == 2) {
                builder.append(Token.LBRACK).append(" ").append(Token.LBRACK.getName()).append("\n");
                builder.append(exps.get(1).print());
                builder.append(Token.RBRACK).append(" ").append(Token.RBRACK.getName()).append("\n");
            }
        }
        builder.append(label).append("\n");
        return builder.toString();
    }

    public void addExp(Exp exp) {
        exps.add(exp);
    }

    public Exp visitExp(int i) {
        return exps.get(i);
    }

    public String getIdent() {
        return ident;
    }

    public int getMode() {
        return mode;
    }

    public void setIdent(String ident) {
        this.ident = ident;
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
