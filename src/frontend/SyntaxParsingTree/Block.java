package frontend.SyntaxParsingTree;

import frontend.State;
import frontend.Token;
import java.util.ArrayList;

public class Block extends SyntaxParsingTree {
    private final ArrayList<BlockItem> blockItems;
    private int mode;
    /*
    mode = 0: 普通语句块、if else语句块，无return、无break continue
    mode = 1: void型函数语句块，有无返回值的return、无break continue
    mode = 2: int型函数语句块，有带返回值的return、无break continue
    mode = 3: while循环语句块，无return、有break continue
     */

    public Block() {
        label = State.Block.toLabel();
        blockItems = new ArrayList<>();
        mode = 0;
    }

    public String print() {
        StringBuilder builder = new StringBuilder();
        builder.append(Token.LBRACE).append(" ").append(Token.LBRACE.getName()).append("\n");
        for (BlockItem blockItem : blockItems) {
            builder.append(blockItem.print());
        }
        builder.append(Token.RBRACE).append(" ").append(Token.RBRACE.getName()).append("\n");
        builder.append(label).append("\n");
        return builder.toString();
    }

    public void addBlockItem(BlockItem blockItem) {
        blockItems.add(blockItem);
    }

    public BlockItem visitBlockItem(int i) {
        return blockItems.get(i);
    }

    public void setMode0() {
        mode = 0;
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

    public int getMode() {
        return mode;
    }
}
