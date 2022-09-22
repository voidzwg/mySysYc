package frontend.SyntaxParsingTree;

import frontend.State;
import frontend.Token;

public class MainFuncDef extends SyntaxParsingTree {
    private Block block;

    public MainFuncDef() {
        label = State.MainFuncDef.toLabel();
        block = null;
    }

    public String print() {
        return Token.INTTK + " " + Token.INTTK.getName() + "\n" +
                Token.MAINTK + " " + Token.MAINTK.getName() + "\n" +
                Token.LPARENT + " " + Token.LPARENT.getName() + "\n" +
                Token.RPARENT + " " + Token.RPARENT.getName() + "\n" +
                block.print() +
                label + "\n";
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        if (block != null) {
            block.setMode2();
        }
        this.block = block;
    }
}
