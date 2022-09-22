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

    public String print() {
        StringBuilder builder = new StringBuilder();
        builder.append(lAndExp.print());
        builder.append(label).append("\n");     //左递归形式输出
        if (lOrExp != null) {
            builder.append(Token.OR).append(" ").append(Token.OR.getName()).append("\n");
            builder.append(lOrExp.print());
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
