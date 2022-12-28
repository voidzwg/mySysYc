package frontend.SyntaxParsingTree;

import frontend.State;
import frontend.Token;

public class MainFuncDef extends SyntaxParsingTree {
    private Block block;
    private int identLine, identCol;

    public MainFuncDef() {
        label = State.MainFuncDef.toLabel();
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
        return Token.INTTK + " " + Token.INTTK.getName() + "\n" +
                Token.MAINTK + " " + Token.MAINTK.getName() + "\n" +
                Token.LPARENT + " " + Token.LPARENT.getName() + "\n" +
                Token.RPARENT + " " + Token.RPARENT.getName() + "\n" +
                block.toString() +
                label + "\n";
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }
}
