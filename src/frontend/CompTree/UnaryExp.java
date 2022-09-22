package frontend.CompTree;

import frontend.State;
import frontend.Token;

public class UnaryExp extends CompTree {
    private PrimaryExp primaryExp;
    private String ident;
    private FuncRParams funcRParams;
    private UnaryOp unaryOp;
    private UnaryExp unaryExp;

    public UnaryExp() {
        label = State.UnaryExp.toLabel();
        primaryExp = null;
        ident = null;
        funcRParams = null;
        unaryOp = null;
        unaryExp = null;
    }

    public String print() {
        StringBuilder builder = new StringBuilder();
        if (primaryExp != null) {
            builder.append(primaryExp.print());
        } else if (unaryOp != null) {
            builder.append(unaryOp.print());
            builder.append(unaryExp.print());
        } else {
            builder.append(Token.IDENFR).append(" ").append(ident).append("\n");
            builder.append(Token.LPARENT).append(" ").append(Token.LPARENT.getName()).append("\n");
            if (funcRParams != null) {
                builder.append(funcRParams.print());
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
