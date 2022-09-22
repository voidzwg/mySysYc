package frontend.SyntaxParsingTree;

import frontend.State;
import frontend.Token;
import java.util.ArrayList;

public class FuncRParams extends SyntaxParsingTree {
    private final ArrayList<Exp> exps;

    public FuncRParams() {
        label = State.FuncRParams.toLabel();
        exps = new ArrayList<>();
    }

    public String print() {
        StringBuilder builder = new StringBuilder();
        builder.append(exps.get(0).print());
        for (int i = 1; i < exps.size(); i++) {
            builder.append(Token.COMMA).append(" ").append(Token.COMMA.getName()).append("\n");
            builder.append(exps.get(i).print());
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
}
