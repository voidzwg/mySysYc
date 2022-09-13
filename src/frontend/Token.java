package frontend;

public enum Token {
    IDENFR(0, "Ident"),
    INTCON(1, "IntConst"),
    STRCON(2, "FormatString"),
    MAINTK(3, "main"),
    CONSTTK(4, "const"),
    INTTK(5, "int"),
    BREAKTK(6, "break"),
    CONTINUETK(7, "continue"),
    IFTK(8, "if"),
    ELSETK(9, "else"),
    NOT(10, "!"),
    AND(11, "&&"),
    OR(12, "||"),
    WHILETK(13, "while"),
    GETINTTK(14, "getint"),
    PRINTFTK(15, "printf"),
    RETURNTK(16, "return"),
    PLUS(17, "+"),
    MINU(18, "-"),
    VOIDTK(19, "void"),
    MULT(20, "*"),
    DIV(21, "/"),
    MOD(22, "%"),
    LSS(23, "<"),
    LEQ(24, "<="),
    GRE(25, ">"),
    GEQ(26, ">="),
    EQL(27, "=="),
    NEQ(28, "!="),
    ASSIGN(29, "="),
    SEMICN(30, ";"),
    COMMA(31, ","),
    LPARENT(32, "("),
    RPARENT(33, ")"),
    LBRACK(34, "["),
    RBRACK(35, "]"),
    LBRACE(36, "{"),
    RBRACE(37, "}"),
    ERROR(38, "o"),
    NOTE(39, "//"),
    EOF(40, "\0"),
    ;

    private final int index;
    private final String name;

    Token(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String valueOf(int index) {
        for (Token t : values()) {
            if (t.index == index) {
                return t.name;
            }
        }
        return null;
    }

    public static Token getToken(String name) {
        for (Token t : values()) {
            if (t.name.equals(name)) {
                return t;
            }
        }
        return ERROR;
    }
}
