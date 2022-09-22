package frontend;

public enum State {
    CompUnit("<CompUnit>"),
    Decl("<Decl>"),                     //不要求输出
    ConstDecl("<ConstDecl>"),
    BType("<BType>"),                   //不要求输出
    ConstDef("<ConstDef>"),
    ConstInitVal("<ConstInitVal>"),
    VarDecl("<VarDecl>"),
    VarDef("<VarDef>"),
    InitVal("<InitVal>"),
    FuncDef("<FuncDef>"),
    MainFuncDef("<MainFuncDef>"),
    FuncType("<FuncType>"),
    FuncFParams("<FuncFParams>"),
    FuncFParam("<FuncFParam>"),
    Block("<Block>"),
    BlockItem("<BlockItem>"),           //不要求输出
    Stmt("<Stmt>"),
    Exp("<Exp>"),
    Cond("<Cond>"),
    LVal("<LVal>"),
    PrimaryExp("<PrimaryExp>"),
    Number("<Number>"),
    UnaryExp("<UnaryExp>"),
    UnaryOp("<UnaryOp>"),
    FuncRParams("<FuncRParams>"),
    MulExp("<MulExp>"),
    AddExp("<AddExp>"),
    RelExp("<RelExp>"),
    EqExp("<EqExp>"),
    LAndExp("<LAndExp>"),
    LOrExp("<LOrExp>"),
    ConstExp("<ConstExp>"),
    ;

    private final String label;

    State(String label) {
        this.label = label;
    }

    public String toLabel() {
        return this.label;
    }
}
