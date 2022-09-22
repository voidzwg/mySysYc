package frontend.SyntaxParsingTree;

import frontend.State;

public class Decl extends SyntaxParsingTree {
    private ConstDecl constDecl;
    private VarDecl varDecl;

    public Decl() {
        label = State.Decl.toLabel();
        constDecl = null;
        varDecl = null;
    }

    public String print() {
        //不要求输出标签
        StringBuilder builder = new StringBuilder();
        if (constDecl != null) {
            builder.append(constDecl.print());
        } else {
            builder.append(varDecl.print());
        }
        return builder.toString();
    }

    public ConstDecl getConstDecl() {
        return constDecl;
    }

    public void setConstDecl(ConstDecl constDecl) {
        this.constDecl = constDecl;
    }

    public VarDecl getVarDecl() {
        return varDecl;
    }

    public void setVarDecl(VarDecl varDecl) {
        this.varDecl = varDecl;
    }
}
