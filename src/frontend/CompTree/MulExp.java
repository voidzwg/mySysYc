package frontend.CompTree;

import frontend.State;
import frontend.Token;

public class MulExp extends CompTree {
    private UnaryExp unaryExp;
    private MulExp mulExp;
    private String op;

    public MulExp() {
        label = State.MulExp.toLabel();
        unaryExp = null;
        mulExp = null;
        op = null;
    }

    public String print() {
        StringBuilder builder = new StringBuilder();
        builder.append(unaryExp.print());
        builder.append(label).append("\n");     //左递归形式输出
        if (op != null) {
            Token tok = null;
            switch (op) {
                case "*":
                    tok = Token.MULT;
                    break;
                case "/":
                    tok = Token.DIV;
                    break;
                case "%":
                    tok = Token.MOD;
                    break;
                default:
                    break;
            }
            builder.append(tok).append(" ").append(op).append("\n");
            builder.append(mulExp.print());
        }
        //builder.append(label).append("\n");     //右递归形式输出
        return builder.toString();
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public UnaryExp getUnaryExp() {
        return unaryExp;
    }

    public void setUnaryExp(UnaryExp unaryExp) {
        this.unaryExp = unaryExp;
    }

    public MulExp getMulExp() {
        return mulExp;
    }

    public void setMulExp(MulExp mulExp) {
        this.mulExp = mulExp;
    }
}
