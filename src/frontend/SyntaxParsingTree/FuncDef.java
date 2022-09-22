package frontend.SyntaxParsingTree;

import frontend.State;
import frontend.Token;

public class FuncDef extends SyntaxParsingTree {
    private FuncType funcType;
    private String ident;
    private FuncFParams funcFParams;
    private Block block;

    public FuncDef() {
        label = State.FuncDef.toLabel();
        funcType = null;
        ident = null;
        funcFParams = null;
        block = null;
    }

    public String print() {
        StringBuilder builder = new StringBuilder();
        builder.append(funcType.print());
        builder.append(Token.IDENFR).append(" ").append(ident).append("\n");
        builder.append(Token.LPARENT).append(" ").append(Token.LPARENT.getName()).append("\n");
        if (funcFParams != null) {
            builder.append(funcFParams.print());
        }
        builder.append(Token.RPARENT).append(" ").append(Token.RPARENT.getName()).append("\n");
        builder.append(block.print());
        builder.append(label).append("\n");
        return builder.toString();
    }

    public FuncType getFuncType() {
        return funcType;
    }

    public void setFuncType(FuncType funcType) {
        if (block != null) {
            if (funcType.getType().equals("int")) {
                block.setMode2();
            } else if (funcType.getType().equals("void")) {
                block.setMode1();
            }
        }
        this.funcType = funcType;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public FuncFParams getFuncFParams() {
        return funcFParams;
    }

    public void setFuncFParams(FuncFParams funcFParams) {
        this.funcFParams = funcFParams;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        if (block != null && funcType.getType() != null) {
            if (funcType.getType().equals("int")) {
                block.setMode2();
            } else if (funcType.getType().equals("void")) {
                block.setMode1();
            }
        }
        this.block = block;
    }
}
