package frontend.SyntaxParsingTree;

import frontend.State;
import frontend.Token;

public class AddExp extends SyntaxParsingTree {
    private MulExp mulExp;
    private AddExp addExp;
    private String op;

    public AddExp() {
        label = State.AddExp.toLabel();
        mulExp = null;
        addExp = null;
        op = null;
    }

    public String print() {
        StringBuilder builder = new StringBuilder();
        builder.append(mulExp.print());
        builder.append(label).append("\n");     //左递归形式输出
        if (op != null) {
            Token tok = null;
            switch (op) {
                case "+":
                    tok = Token.PLUS;
                    break;
                case "-":
                    tok = Token.MINU;
                    break;
                default:
                    break;
            }
            builder.append(tok).append(" ").append(op).append("\n");
            builder.append(addExp.print());
        }
        //builder.append(label).append("\n");     //右递归形式输出
        return builder.toString();
    }

    public MulExp getMulExp() {
        return mulExp;
    }

    public void setMulExp(MulExp mulExp) {
        this.mulExp = mulExp;
    }

    public AddExp getAddExp() {
        return addExp;
    }

    public void setAddExp(AddExp addExp) {
        this.addExp = addExp;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }
}
