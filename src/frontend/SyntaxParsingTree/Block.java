package frontend.SyntaxParsingTree;

import frontend.State;
import frontend.Token;
import java.util.ArrayList;

public class Block extends SyntaxParsingTree {
    private final ArrayList<BlockItem> blockItems;
    private int mode;
    /*
    mode = 0: 无return、无break continue
    mode = 1: 有无返回值的return、无break continue
    mode = 2: 有带返回值的return、无break continue
    mode = 3: 无return、有break continue
    mode = 4: 有无返回值的return、有break continue
    mode = 5: 有带返回值的return、有break continue
    mode = 6: 有两种return
     */

    public Block() {
        label = State.Block.toLabel();
        blockItems = new ArrayList<>();
        mode = 0;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(Token.LBRACE).append(" ").append(Token.LBRACE.getName()).append("\n");
        for (BlockItem blockItem : blockItems) {
            builder.append(blockItem.toString());
        }
        builder.append(Token.RBRACE).append(" ").append(Token.RBRACE.getName()).append("\n");
        builder.append(label).append("\n");
        return builder.toString();
    }

    public void addBlockItem(BlockItem blockItem) {
        blockItems.add(blockItem);
    }

    public ArrayList<BlockItem> getBlockItem() {
        return blockItems;
    }

    public void setMode(int mode) {
        if (0 <= mode && mode <= 6) {
            this.mode = mode;
        }
    }

    public void setMode1() {
        mode = 1;
    }

    public void setMode2() {
        mode = 2;
    }

    public void setMode3() {
        mode = 3;
    }

    public void setMode4() {
        mode = 4;
    }

    public void setMode5() {
        mode = 5;
    }

    public int getMode() {
        return mode;
    }
}
