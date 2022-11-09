package frontend.SyntaxParsingTree;

public abstract class SyntaxParsingTree {
    protected String label;
    protected int line = 0, col = 0;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    @Override
    abstract public String toString();
}
