package frontend.SyntaxParsingTree;

import frontend.State;
import frontend.Token;
import java.util.ArrayList;

public class VarDecl extends SyntaxParsingTree {
    private final BType bType;
    private final ArrayList<VarDef> varDefs;

    public VarDecl() {
        label = State.VarDecl.toLabel();
        bType = new BType();
        varDefs = new ArrayList<>();
    }

    public String print() {
        StringBuilder builder = new StringBuilder();
        builder.append(bType.print());
        builder.append(varDefs.get(0).print());
        for (int i = 1; i < varDefs.size(); i++) {
            builder.append(Token.COMMA).append(" ").append(Token.COMMA.getName()).append("\n");
            builder.append(varDefs.get(i).print());
        }
        builder.append(Token.SEMICN).append(" ").append(Token.SEMICN.getName()).append("\n");
        builder.append(label).append("\n");
        return builder.toString();
    }

    public void addVarDef(VarDef varDef) {
        varDefs.add(varDef);
    }

    public VarDef visitVarDef(int i) {
        return varDefs.get(i);
    }

    public BType getbType() {
        return bType;
    }
}
