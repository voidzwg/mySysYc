package Error;

public enum Error {
    //实验要求输出的错误
    IllegalSymbol("a", "Illegal Symbol in Format String"),
    Redeclaration("b", "Redeclaration of Identifier"),
    Undeclared("c", "Undeclared Identifier"),
    ParamNumbersMismatched("d", "Number of Parameters Mismatched"),
    ParamTypeMismatched("e", "Type of Parameters Mismatched"),
    ReturnValueExists("f", "Does Not Need Return Value"),
    ReturnValueNotExists("g", "Need Return Value"),
    ChangeConstValue("h", "Cannot Change Value of Constant"),
    MissedSemicolon("i", "Need ';'"),
    MissedRightParentheses("j", "Need ')'"),
    MissedRightBrackets("k", "Need ']'"),
    IllegalFormatStringInPrintf("l", "Number of Format String and Expressions Mismatched"),
    IllegalBreakOrContinue("m", "Use 'break' or 'continue' outside the 'while'"),

    //自定义的错误
    UninitializedConstant("o", "Uninitialized constant"),
    InitializationFailed("p", "Cannot initialize constant with non-constant value"),
    ArrayNeedLength("q", "The length of the array needs to be determined number"),
    InitialFormError("r", "The initialization has wrong dimension"),
    UnknownSymbol("z", "Unknown Symbol"),
    UndefinedError("z", "Undefined"),
    ;

    private final String code;
    private final String description;

    Error(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    String getDescription() {
        return description;
    }

}

