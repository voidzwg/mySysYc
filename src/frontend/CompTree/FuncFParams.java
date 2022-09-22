package frontend.CompTree;

import frontend.State;
import frontend.Token;
import java.util.ArrayList;

public class FuncFParams extends CompTree {
    private final ArrayList<FuncFParam> funcFParams;

    public FuncFParams() {
        label = State.FuncFParams.toLabel();
        funcFParams = new ArrayList<>();
    }

    public String print() {
        StringBuilder builder = new StringBuilder();
        builder.append(funcFParams.get(0).print());
        for (int i = 1; i < funcFParams.size(); i++) {
            builder.append(Token.COMMA).append(" ").append(Token.COMMA.getName()).append("\n");
            builder.append(funcFParams.get(i).print());
        }
        builder.append(label).append("\n");
        return builder.toString();
    }

    public void addFuncFParam(FuncFParam funcFParam) {
        funcFParams.add(funcFParam);
    }

    public FuncFParam visitFuncFParam(int i) {
        return funcFParams.get(i);
    }
}
