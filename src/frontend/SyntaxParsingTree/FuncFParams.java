package frontend.SyntaxParsingTree;

import frontend.State;
import frontend.Token;
import java.util.ArrayList;

public class FuncFParams extends SyntaxParsingTree {
    private final ArrayList<FuncFParam> funcFParams;

    public FuncFParams() {
        label = State.FuncFParams.toLabel();
        funcFParams = new ArrayList<>();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(funcFParams.get(0).toString());
        for (int i = 1; i < funcFParams.size(); i++) {
            builder.append(Token.COMMA).append(" ").append(Token.COMMA.getName()).append("\n");
            builder.append(funcFParams.get(i).toString());
        }
        builder.append(label).append("\n");
        return builder.toString();
    }

    public void addFuncFParam(FuncFParam funcFParam) {
        if (funcFParam == null) {
            return;
        }
        funcFParams.add(funcFParam);
    }

    public ArrayList<FuncFParam> getFuncFParams() {
        return funcFParams;
    }
}
