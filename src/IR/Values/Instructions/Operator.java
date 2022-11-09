package IR.Values.Instructions;

public enum Operator {
    PLUS("+"),
    MINU("-"),
    MULT("*"),
    DIV("/"),
    MOD("%"),
    AND("&&"),
    OR("||"),
    NOT("!"),
    LSS("<"),
    LEQ("<="),
    GRE(">"),
    GEQ(">="),
    EQL("=="),
    NEQ("!="),

    CALL("call"),
    ALC("alloca"),
    LD("load"),
    STR("store"),
    RET("ret"),
    GEP("getelementptr"),
    ;

    private String op;

    Operator(String op) {
        this.op = op;
    }

    public String getOp() {
        return op;
    }

    public String symbolOf() {
        String s;
        switch (this) {
            case PLUS:
                s = "add i32 ";
                break;
            case MINU:
                s = "sub i32 ";
                break;
            case MULT:
                s = "mul i32 ";
                break;
            case DIV:
                s = "sdiv i32 ";
                break;
            case MOD:
                s = "srem i32 ";
                break;
            default:
                s = this.getOp();
                break;
        }
        return s;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public static Operator getOp(String op) {
        switch (op) {
            case "+":
                return PLUS;
            case "-":
                return MINU;
            case "*":
                return MULT;
            case "/":
                return DIV;
            case "%":
                return MOD;
            default:
                return null;
        }
    }
}
