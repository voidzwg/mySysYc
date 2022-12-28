package frontend.SyntaxParsingTree;

import frontend.State;
import frontend.Token;

public class UnaryExp extends SyntaxParsingTree {
    private PrimaryExp primaryExp;
    private String ident;
    private FuncRParams funcRParams;
    private UnaryOp unaryOp;
    private UnaryExp unaryExp;
    private int identLine, identCol;

    public UnaryExp() {
        label = State.UnaryExp.toLabel();
        primaryExp = null;
        ident = null;
        funcRParams = null;
        unaryOp = null;
        unaryExp = null;
    }

    public int getIdentLine() {
        return identLine;
    }

    public void setIdentLine(int identLine) {
        this.identLine = identLine;
    }

    public int getIdentCol() {
        return identCol;
    }

    public void setIdentCol(int identCol) {
        this.identCol = identCol;
    }

    public void setIdentPos(int line, int col) {
        setIdentLine(line);
        setIdentCol(col);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (primaryExp != null) {
            builder.append(primaryExp.toString());
        } else if (unaryOp != null) {
            builder.append(unaryOp.toString());
            builder.append(unaryExp.toString());
        } else {
            builder.append(Token.IDENFR).append(" ").append(ident).append("\n");
            builder.append(Token.LPARENT).append(" ").append(Token.LPARENT.getName()).append("\n");
            if (funcRParams != null) {
                builder.append(funcRParams.toString());
            }
            builder.append(Token.RPARENT).append(" ").append(Token.RPARENT.getName()).append("\n");
        }
        builder.append(label).append("\n");
        return builder.toString();
    }

    public PrimaryExp getPrimaryExp() {
        return primaryExp;
    }

    public void setPrimaryExp(PrimaryExp primaryExp) {
        this.primaryExp = primaryExp;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public FuncRParams getFuncRParams() {
        return funcRParams;
    }

    public void setFuncRParams(FuncRParams funcRParams) {
        this.funcRParams = funcRParams;
    }

    public UnaryOp getUnaryOp() {
        return unaryOp;
    }

    public void setUnaryOp(UnaryOp unaryOp) {
        this.unaryOp = unaryOp;
    }

    public UnaryExp getUnaryExp() {
        return unaryExp;
    }

    public void setUnaryExp(UnaryExp unaryExp) {
        this.unaryExp = unaryExp;
    }
}
