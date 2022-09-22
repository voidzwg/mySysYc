package frontend.SyntaxParsingTree;

import frontend.State;
import frontend.Token;

public class BType extends SyntaxParsingTree {
    private final String content;

    public BType() {
        label = State.BType.toLabel();
        content = "int";
    }

    public String getContent() {
        return content;
    }

    public String print() {
        //不要求输出标签
        return Token.INTTK + " " + Token.INTTK.getName() + "\n";
    }
}
