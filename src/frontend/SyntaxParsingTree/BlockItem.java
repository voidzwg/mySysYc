package frontend.SyntaxParsingTree;

import frontend.State;

public class BlockItem extends SyntaxParsingTree {
    private Decl decl;
    private Stmt stmt;

    public BlockItem() {
        label = State.BlockItem.toLabel();
        decl = null;
        stmt = null;
    }

    public String toString() {
        //不要求输出标签
        StringBuilder builder = new StringBuilder();
        if (decl != null) {
            builder.append(decl.toString());
        } else if (stmt != null) {
            builder.append(stmt.toString());
        }
        return builder.toString();
    }

    public Decl getDecl() {
        return decl;
    }

    public void setDecl(Decl decl) {
        this.decl = decl;
    }

    public Stmt getStmt() {
        return stmt;
    }

    public void setStmt(Stmt stmt) {
        this.stmt = stmt;
    }
}
