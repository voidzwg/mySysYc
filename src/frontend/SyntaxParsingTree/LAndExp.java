package frontend.SyntaxParsingTree;

import frontend.State;
import frontend.Token;

public class LAndExp extends SyntaxParsingTree {
    private EqExp eqExp;
    private LAndExp lAndExp;

    public LAndExp() {
        label = State.LAndExp.toLabel();
        eqExp = null;
        lAndExp = null;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(eqExp.toString());
        builder.append(label).append("\n");     //左递归形式输出
        if (lAndExp != null) {
            builder.append(Token.AND).append(" ").append(Token.AND.getName()).append("\n");
            builder.append(lAndExp.toString());
        }
        //builder.append(label).append("\n");     //右递归形式输出
        return builder.toString();
    }

    public EqExp getEqExp() {
        return eqExp;
    }

    public void setEqExp(EqExp eqExp) {
        this.eqExp = eqExp;
    }

    public LAndExp getlAndExp() {
        return lAndExp;
    }

    public void setlAndExp(LAndExp lAndExp) {
        this.lAndExp = lAndExp;
    }
}
