package frontend.SyntaxParsingTree;

import frontend.State;
import frontend.Token;

public class LOrExp extends SyntaxParsingTree {
    private LAndExp lAndExp;
    private LOrExp lOrExp;

    public LOrExp() {
        label = State.LOrExp.toLabel();
        lAndExp = null;
        lOrExp = null;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(lAndExp.toString());
        builder.append(label).append("\n");     //左递归形式输出
        if (lOrExp != null) {
            builder.append(Token.OR).append(" ").append(Token.OR.getName()).append("\n");
            builder.append(lOrExp.toString());
        }
        //builder.append(label).append("\n");     //右递归形式输出
        return builder.toString();
    }

    public LAndExp getlAndExp() {
        return lAndExp;
    }

    public void setlAndExp(LAndExp lAndExp) {
        this.lAndExp = lAndExp;
    }

    public LOrExp getlOrExp() {
        return lOrExp;
    }

    public void setlOrExp(LOrExp lOrExp) {
        this.lOrExp = lOrExp;
    }
}
