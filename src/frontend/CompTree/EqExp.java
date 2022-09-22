package frontend.CompTree;

import frontend.State;
import frontend.Token;

public class EqExp extends CompTree {
    private RelExp relExp;
    private EqExp eqExp;
    private String op;

    public EqExp() {
        label = State.EqExp.toLabel();
        relExp = null;
        eqExp = null;
        op = null;
    }

    public String print() {
        StringBuilder builder = new StringBuilder();
        builder.append(relExp.print());
        builder.append(label).append("\n");     //左递归形式输出
        if (op != null) {
            Token tok = null;
            switch (op) {
                case "==":
                    tok = Token.EQL;
                    break;
                case "!=":
                    tok = Token.NEQ;
                    break;
                default:
                    break;
            }
            builder.append(tok).append(" ").append(op).append("\n");
            builder.append(eqExp.print());
        }
        //builder.append(label).append("\n");     //右递归形式输出
        return builder.toString();
    }

    public RelExp getRelExp() {
        return relExp;
    }

    public void setRelExp(RelExp relExp) {
        this.relExp = relExp;
    }

    public EqExp getEqExp() {
        return eqExp;
    }

    public void setEqExp(EqExp eqExp) {
        this.eqExp = eqExp;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }
}
