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
    BR("br"),
    ZEXT("zext"),
    ;

    private String op;

    Operator(String op) {
        this.op = op;
    }

    public String symbolOf() {
        switch (this) {
            case PLUS:
                return "add ";
            case MINU:
                return "sub ";
            case MULT:
                return "mul ";
            case DIV:
                return "sdiv ";
            case MOD:
                return "srem ";
            case EQL:
                return "eq ";
            case NEQ:
                return "ne ";
            case GEQ:
                return "sge ";
            case GRE:
                return "sgt ";
            case LEQ:
                return "sle ";
            case LSS:
                return "slt ";
            default:
                return "";
        }
    }

    public void setOp(String op) {
        this.op = op;
    }

    public static Operator OP(String op) {
        for (Operator operator : Operator.values()) {
            if (operator.op.equals(op)) {
                return operator;
            }
        }
        return null;
    }
}
