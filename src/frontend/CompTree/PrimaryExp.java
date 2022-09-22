package frontend.CompTree;

import frontend.State;
import frontend.Token;

public class PrimaryExp extends CompTree {
    private Exp exp;
    private LVal lVal;
    private Number number;

    public PrimaryExp() {
        label = State.PrimaryExp.toLabel();
        exp = null;
        number = null;
    }

    public String print() {
        StringBuilder builder = new StringBuilder();
        if (exp != null) {
            builder.append(Token.LPARENT).append(" ").append(Token.LPARENT.getName()).append("\n");
            builder.append(exp.print());
            builder.append(Token.RPARENT).append(" ").append(Token.RPARENT.getName()).append("\n");
        } else if (lVal != null) {
            builder.append(lVal.print());
        } else {
            builder.append(number.print());
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

    public LVal getlVal() {
        return lVal;
    }

    public void setlVal(LVal lVal) {
        this.lVal = lVal;
    }

    public Number getNumber() {
        return number;
    }

    public void setNumber(Number number) {
        this.number = number;
    }
}
