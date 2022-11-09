package frontend.SyntaxParsingTree;

import frontend.State;
import frontend.Token;

public class RelExp extends SyntaxParsingTree {
    private AddExp addExp;
    private RelExp relExp;
    private String op;

    public RelExp() {
        label = State.RelExp.toLabel();
        addExp = null;
        relExp = null;
        op = null;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(addExp.toString());
        builder.append(label).append("\n");     //左递归形式输出
        if (op != null) {
            Token tok = null;
            switch (op) {
                case "<":
                    tok = Token.LSS;
                    break;
                case ">":
                    tok = Token.GRE;
                    break;
                case "<=":
                    tok = Token.LEQ;
                    break;
                case ">=":
                    tok = Token.GEQ;
                    break;
                default:
                    break;
            }
            builder.append(tok).append(" ").append(op).append("\n");
            builder.append(relExp.toString());
        }
        //builder.append(label).append("\n");     //右递归形式输出
        return builder.toString();
    }

    public AddExp getAddExp() {
        return addExp;
    }

    public void setAddExp(AddExp addExp) {
        this.addExp = addExp;
    }

    public RelExp getRelExp() {
        return relExp;
    }

    public void setRelExp(RelExp relExp) {
        this.relExp = relExp;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }
}
