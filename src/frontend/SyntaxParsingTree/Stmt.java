package frontend.SyntaxParsingTree;

import frontend.State;
import frontend.Token;
import java.util.ArrayList;

public class Stmt extends SyntaxParsingTree {
    private LVal lVal;
    private Exp exp;
    private Block block;
    private Cond cond;
    private final ArrayList<Stmt> stmts;
    private final ArrayList<Exp> exps;
    private String formatString;
    int breakContinueLine, breakContinueCol;
    private int type;
    /*
    type = 0: ';'
    type = 1: Exp ';'
    type = 2: 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
    type = 3: 'while' '(' Cond ')' Stmt
    type = 4: 'break' ';'
    type = 5: 'continue' ';'
    type = 6: 'return' [Exp] ';'
    type = 7: 'printf' '(' FormatString { ',' Exp } ')' ';'
    type = 8: LVal '=' Exp ';'
    type = 9: LVal '=' 'getint' '(' ')' ';'
    type = 10: Block
     */

    public Stmt() {
        label = State.Stmt.toLabel();
        lVal = null;
        exp = null;
        block = null;
        cond = null;
        stmts = new ArrayList<>();
        exps = new ArrayList<>();
        formatString = null;
        type = 0;
    }

    public int getBreakContinueLine() {
        return breakContinueLine;
    }

    public void setBreakContinueLine(int breakContinueLine) {
        this.breakContinueLine = breakContinueLine;
    }

    public int getBreakContinueCol() {
        return breakContinueCol;
    }

    public void setBreakContinueCol(int breakContinueCol) {
        this.breakContinueCol = breakContinueCol;
    }

    public void setBreakContinuePos(int line, int col) {
        setBreakContinueLine(line);
        setBreakContinueCol(col);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        switch (type) {
            case 0:
                builder.append(Token.SEMICN).append(" ").append(Token.SEMICN.getName()).append("\n");
                break;
            case 1:
                builder.append(exp.toString());
                builder.append(Token.SEMICN).append(" ").append(Token.SEMICN.getName()).append("\n");
                break;
            case 2:
                builder.append(Token.IFTK).append(" ").append(Token.IFTK.getName()).append("\n");
                builder.append(Token.LPARENT).append(" ").append(Token.LPARENT.getName()).append("\n");
                builder.append(cond.toString());
                builder.append(Token.RPARENT).append(" ").append(Token.RPARENT.getName()).append("\n");
                builder.append(stmts.get(0).toString());
                if (stmts.size() == 2) {
                    builder.append(Token.ELSETK).append(" ").append(Token.ELSETK.getName()).append("\n");
                    builder.append(stmts.get(1).toString());
                }
                break;
            case 3:
                builder.append(Token.WHILETK).append(" ").append(Token.WHILETK.getName()).append("\n");
                builder.append(Token.LPARENT).append(" ").append(Token.LPARENT.getName()).append("\n");
                builder.append(cond.toString());
                builder.append(Token.RPARENT).append(" ").append(Token.RPARENT.getName()).append("\n");
                builder.append(stmts.get(0).toString());
                break;
            case 4:
                builder.append(Token.BREAKTK).append(" ").append(Token.BREAKTK.getName()).append("\n");
                builder.append(Token.SEMICN).append(" ").append(Token.SEMICN.getName()).append("\n");
                break;
            case 5:
                builder.append(Token.CONTINUETK).append(" ").append(Token.CONTINUETK.getName()).append("\n");
                builder.append(Token.SEMICN).append(" ").append(Token.SEMICN.getName()).append("\n");
                break;
            case 6:
                builder.append(Token.RETURNTK).append(" ").append(Token.RETURNTK.getName()).append("\n");
                if (exp != null) {
                    builder.append(exp.toString());
                }
                builder.append(Token.SEMICN).append(" ").append(Token.SEMICN.getName()).append("\n");
                break;
            case 7:
                builder.append(Token.PRINTFTK).append(" ").append(Token.PRINTFTK.getName()).append("\n");
                builder.append(Token.LPARENT).append(" ").append(Token.LPARENT.getName()).append("\n");
                builder.append(Token.STRCON).append(" ").append(formatString).append("\n");
                for (Exp exp : exps) {
                    builder.append(Token.COMMA).append(" ").append(Token.COMMA.getName()).append("\n");
                    builder.append(exp.toString());
                }
                builder.append(Token.RPARENT).append(" ").append(Token.RPARENT.getName()).append("\n");
                builder.append(Token.SEMICN).append(" ").append(Token.SEMICN.getName()).append("\n");
                break;
            case 8:
                builder.append(lVal.toString());
                builder.append(Token.ASSIGN).append(" ").append(Token.ASSIGN.getName()).append("\n");
                builder.append(exp.toString());
                builder.append(Token.SEMICN).append(" ").append(Token.SEMICN.getName()).append("\n");
                break;
            case 9:
                builder.append(lVal.toString());
                builder.append(Token.ASSIGN).append(" ").append(Token.ASSIGN.getName()).append("\n");
                builder.append(Token.GETINTTK).append(" ").append(Token.GETINTTK.getName()).append("\n");
                builder.append(Token.LPARENT).append(" ").append(Token.LPARENT.getName()).append("\n");
                builder.append(Token.RPARENT).append(" ").append(Token.RPARENT.getName()).append("\n");
                builder.append(Token.SEMICN).append(" ").append(Token.SEMICN.getName()).append("\n");
                break;
            case 10:
                builder.append(block.toString());
                break;
            default:
                break;
        }
        builder.append(label).append("\n");
        return builder.toString();
    }

    public void addStmt(Stmt stmt) {
        stmts.add(stmt);
    }

    public ArrayList<Stmt> getStmts() {
        return stmts;
    }

    public void addExp(Exp exp) {
        exps.add(exp);
    }

    public ArrayList<Exp> getExps() {
        return exps;
    }

    public LVal getlVal() {
        return lVal;
    }

    public void setlVal(LVal lVal) {
        this.lVal = lVal;
    }

    public Exp getExp() {
        return exp;
    }

    public void setExp(Exp exp) {
        this.exp = exp;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public Cond getCond() {
        return cond;
    }

    public void setCond(Cond cond) {
        this.cond = cond;
    }

    public String getFormatString() {
        return formatString;
    }

    public void setFormatString(String formatString) {
        this.formatString = formatString;
    }

    public int getType() {
        return type;
    }

    public void setType0() {
        type = 0;
    }

    public void setType1() {
        type = 1;
    }

    public void setType2() {
        type = 2;
    }

    public void setType3() {
        type = 3;
    }

    public void setType4() {
        type = 4;
    }

    public void setType5() {
        type = 5;
    }

    public void setType6() {
        type = 6;
    }

    public void setType7() {
        type = 7;
    }

    public void setType8() {
        type = 8;
    }

    public void setType9() {
        type = 9;
    }

    public void setType10() {
        type = 10;
    }
}
