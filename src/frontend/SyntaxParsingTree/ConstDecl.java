package frontend.SyntaxParsingTree;

import frontend.State;
import frontend.Token;
import java.util.ArrayList;

public class ConstDecl extends SyntaxParsingTree {
    private BType bType;
    private final ArrayList<ConstDef> constDefs;

    public ConstDecl() {
        label = State.ConstDecl.toLabel();
        bType = new BType();
        constDefs = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(Token.CONSTTK).append(" ").append(Token.CONSTTK.getName()).append("\n");
        builder.append(bType.toString());
        builder.append(constDefs.get(0).toString());
        for (int i = 1; i < constDefs.size(); i++) {
            builder.append(Token.COMMA).append(" ").append(Token.COMMA.getName()).append("\n");
            builder.append(constDefs.get(i).toString());
        }
        builder.append(Token.SEMICN).append(" ").append(Token.SEMICN.getName()).append("\n");
        builder.append(label).append("\n");
        return builder.toString();
    }

    public void setbType(BType bType) {
        this.bType = bType;
    }

    public BType getbType() {
        return bType;
    }

    public ArrayList<ConstDef> getConstDefs() {
        return constDefs;
    }

    public void addConstDefs(ConstDef constDef) {
        constDefs.add(constDef);
    }
}
