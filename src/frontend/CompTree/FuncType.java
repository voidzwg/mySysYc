package frontend.CompTree;

import frontend.State;
import frontend.Token;

public class FuncType extends CompTree {
    private String type;

    public FuncType() {
        label = State.FuncType.toLabel();
        type = null;
    }

    public String print() {
        StringBuilder builder = new StringBuilder();
        if ("int".equals(type)) {
            builder.append(Token.INTTK).append(" ").append(type).append("\n");
        } else if ("void".equals(type)) {
            builder.append(Token.VOIDTK).append(" ").append(type).append("\n");
        }
        builder.append(label).append("\n");
        return builder.toString();
    }

    public void setTypeInt() {
        type = "int";
    }

    public void setTypeVoid() {
        type = "void";
    }

    public String getType() {
        return type;
    }
}
