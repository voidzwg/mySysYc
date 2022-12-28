package frontend.SyntaxParsingTree;

import frontend.State;
import frontend.Token;

import java.util.ArrayList;

public class FuncDef extends SyntaxParsingTree {
    private FuncType funcType;
    private String ident;
    private FuncFParams funcFParams;
    private Block block;
    private int identLine, identCol;

    public FuncDef() {
        label = State.FuncDef.toLabel();
        funcType = null;
        ident = null;
        funcFParams = null;
        block = null;
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
        builder.append(funcType.toString());
        builder.append(Token.IDENFR).append(" ").append(ident).append("\n");
        builder.append(Token.LPARENT).append(" ").append(Token.LPARENT.getName()).append("\n");
        if (funcFParams != null) {
            builder.append(funcFParams.toString());
        }
        builder.append(Token.RPARENT).append(" ").append(Token.RPARENT.getName()).append("\n");
        builder.append(block.toString());
        builder.append(label).append("\n");
        return builder.toString();
    }

    public FuncType getFuncType() {
        return funcType;
    }

    public void setFuncType(FuncType funcType) {
        this.funcType = funcType;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public ArrayList<FuncFParam> getFuncFParams() {
        if (funcFParams == null) {
            return null;
        } else {
            return funcFParams.getFuncFParams();
        }
    }

    public void setFuncFParams(FuncFParams funcFParams) {
        this.funcFParams = funcFParams;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }
}
