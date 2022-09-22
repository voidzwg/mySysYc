package frontend.SyntaxParsingTree;

import frontend.State;
import frontend.Token;

public class Number extends SyntaxParsingTree {
    private int number;

    public Number() {
        label = State.Number.toLabel();
        number = 0;
    }

    public String print() {
        return Token.INTCON + " " + number + "\n" +
                label + "\n";
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = Integer.parseInt(number);
    }
}
