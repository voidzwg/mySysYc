package frontend;

public enum Error {
    IllegalSymbol("a", "Illegal Symbol in Format String"),
    Redeclaration("b", "Redeclaration of Identifier"),
    Undeclared("c", "Undeclared Identifier"),
    ParamNumbersMismatched("d", "Number of Parameters Mismatched"),
    ParamTypeMismatched("e", "Type of Parameters Mismatched"),
    ReturnValueExists("f", "Does Not Need Return Value"),
    ReturnValueNotExists("g", "Need Return Value"),
    ChangeConstValue("h", "Cannot Change Value of Constant"),
    MissedSemicolon("i", "Need ;"),
    MissedRightParentheses("j", "Need )"),
    MissedRightBrackets("k", "Need ]"),
    IllegalFormatStringInPrintf("l", "Number of Format String and Expressions Mismatched"),
    IllegalBreakOrContinue("m", "Use 'break' or 'continue' outside the 'while'"),
    UnknownSymbol("o", "Unknown Symbol"),
    ;

    final String code;
    final String description;

    Error(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
