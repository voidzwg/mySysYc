package frontend.SyntaxParsingTree;

import frontend.State;
import frontend.Token;

public class UnaryOp extends SyntaxParsingTree {
    private String op;

    public UnaryOp() {
        label = State.UnaryOp.toLabel();
        op = null;
    }

    public String toString() {
        Token tok;
        switch (op) {
            case "+":
                tok = Token.PLUS;
                break;
            case "-":
                tok = Token.MINU;
                break;
            case "!":
                tok = Token.NOT;
                break;
            default:
                tok = null;
                break;
        }
        return tok + " " + op + "\n" +
                label + "\n";
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getOp() {
        return op;
    }
}
