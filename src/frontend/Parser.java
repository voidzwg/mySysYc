package frontend;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import frontend.SyntaxParsingTree.*;
import frontend.SyntaxParsingTree.Number;
import Error.CompileErrorException;

import static Error.Error.*;
import static frontend.Token.*;
import static Error.CompileErrorException.*;

public class Parser {
    private final Lexer lexer;
    private Token token;
    private String symbol;
    private int head = 0, tail = 0;
    private int beforeWordLine, beforeWordCol;
    private int thisWordLine, thisWordCol;
    private Token cft;  // current func type
    private final ArrayList<Token> tokensQueue;
    private final ArrayList<String> symbolsQueue;
    private final ArrayList<Integer> beforeWordLines;
    private final ArrayList<Integer> beforeWordCols;
    private final ArrayList<Integer> thisWordLines;
    private final ArrayList<Integer> thisWordCols;

    public Parser(File fr) {
        tokensQueue = new ArrayList<>();
        symbolsQueue = new ArrayList<>();
        beforeWordLines = new ArrayList<>();
        beforeWordCols = new ArrayList<>();
        thisWordLines = new ArrayList<>();
        thisWordCols = new ArrayList<>();
        lexer = new Lexer(fr);
    }

    public SyntaxParsingTree build() throws IOException, CompileErrorException {
        SyntaxParsingTree tree;
        read();
        tree = parseCompUnit();
        return tree;
    }

    public void close() throws IOException {
        lexer.close();
    }

    private boolean hasAssignSymbol() throws CompileErrorException {
        int i = 0;
        while (true) {
            if (readToken(i) == EOF || readToken(i) == SEMICN) {
                return false;
            }
            if (readToken(i) == ASSIGN) {
                return true;
            }
            i++;
        }
    }

    private int checkFormatString(String s, int line, int col) {
        int count = 0;
        s = s.substring(1, s.length() - 1);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == 32 || c == 33 || (c <= 126 && c >= 40)) {
                // 除 " # $ % & ' 以外的可见字符
                if (c == '\\') {
                    i++;
                    if (i >= s.length() || s.charAt(i) != 'n') {
                        error(IllegalSymbol, line, col);
                        break;
                    }
                }
            } else if (c == '%') {
                i++;
                if (i >= s.length() || s.charAt(i) != 'd') {
                    error(IllegalSymbol, line, col);
                    break;
                } else {
                    count++;
                }
            } else {
                error(IllegalSymbol, line, col);
                break;
            }
        }
        return count;
    }

    private void push(Token tok, String sym, int beforeWordLine, int beforeWordCol, int thisWordLine, int thisWOrdCol) {
        tokensQueue.add(tok);
        symbolsQueue.add(sym);
        beforeWordLines.add(beforeWordLine);
        beforeWordCols.add(beforeWordCol);
        thisWordLines.add(thisWordLine);
        thisWordCols.add(thisWOrdCol);
        head++;
    }

    // 将队尾token赋值给全局变量token，并将队尾移出队列
    private void pop() {
        if (head > tail) {
            token = tokensQueue.get(tail);
            symbol = symbolsQueue.get(tail);
            beforeWordLine = beforeWordLines.get(tail);
            beforeWordCol = beforeWordCols.get(tail);
            thisWordLine = thisWordLines.get(tail);
            thisWordCol = thisWordCols.get(tail);
            tail++;
        } else {
            token = EOF;
            symbol = EOF.getName();
        }
    }

    // 从lexer中读取一个token，并放入token队列
    private int readLexer() {
        Token token;
        String symbol;
        int beforeWordLine, beforeWordCol, thisWordLine, thisWordCol;
        while (true) {
            beforeWordLine = lexer.getLine();
            beforeWordCol = lexer.getCol();
            token = lexer.next();
            thisWordLine = lexer.getLine();
            thisWordCol = lexer.getCol();
            symbol = lexer.getToken();
            if (token == EOF) {
                return -1;
            }
            if (token == NOTE) {
                continue;
            }
            push(token, symbol, beforeWordLine, beforeWordCol, thisWordLine, thisWordCol);
            break;
        }
        return 0;
    }

    private void setPosAsBeforeWordPos(SyntaxParsingTree node) {
        if (node == null) {
            return;
        }
        node.setLine(beforeWordLine);
        node.setCol(beforeWordCol);
    }

    private void setPosAsThisWordPos(SyntaxParsingTree node) {
        if (node == null) {
            return;
        }
        node.setLine(thisWordLine);
        node.setCol(thisWordCol);
    }

    // 读取token队列队尾开始序号为n的元素，序号从0开始
    private Token readToken(int n) {
        while (head <= tail + n) {
            if (readLexer() == -1) {
                break;
            }
        }
        if (head > tail + n) {
            return tokensQueue.get(tail + n);
        } else {
            return EOF;
        }
    }

    // 读取下一个token
    private void read() {
        if (head > tail) {
            pop();
        } else {
            readLexer();
            pop();
        }
        //System.out.println(token.getName() + " " + symbol);
    }

    public CompUnit parseCompUnit() throws IOException, CompileErrorException {
        CompUnit compUnit = new CompUnit();
        while (isDecl()) {
            compUnit.addDecl(parseDecl());
        }
        while (isFuncDef()) {
            compUnit.addFuncDefs(parseFuncDef());
        }
        compUnit.setMainFuncDef(parseMainFuncDef());
        return compUnit;
    }

    private boolean isDecl() throws CompileErrorException {
        if (token == CONSTTK) {
            return true;
        } else if (token == INTTK) {
            if (readToken(0) == IDENFR) {
                return readToken(1) == COMMA ||
                        readToken(1) == SEMICN ||
                        readToken(1) == ASSIGN ||
                        readToken(1) == LBRACK;
            }
        }
        return false;
    }

    private boolean isFuncDef() {
        if (token == INTTK || token == VOIDTK) {
            if (readToken(0) == IDENFR) {
                return readToken(1) == LPARENT;
            }
        }
        return false;
    }

    public Decl parseDecl() throws CompileErrorException, IOException {
        Decl decl = new Decl();
        if (token == CONSTTK) {
            decl.setConstDecl(parseConstDecl());
        } else if (token == INTTK) {
            decl.setVarDecl(parseVarDecl());
        }
        return decl;
    }

    public ConstDecl parseConstDecl() throws IOException, CompileErrorException {
        ConstDecl constDecl = new ConstDecl();
        read();
        if (token == INTTK) {
            read();
            while (true) {
                constDecl.addConstDefs(parseConstDef());
                if (token == COMMA) {
                    read();
                } else if (token == SEMICN) {
                    read();
                    break;
                } else {
                    error(MissedSemicolon, beforeWordLine, beforeWordCol);
                }
            }
        }
        return constDecl;
    }

    public ConstDef parseConstDef() throws IOException, CompileErrorException {
        ConstDef constDef = new ConstDef();
        if (token == IDENFR) {
            constDef.setIdent(symbol);
            read();
            if (token == LBRACK) {
                read();
                constDef.addConstExp(parseConstExp());
                constDef.setMode1();
                if (token != RBRACK) {
                    error(MissedRightBrackets, beforeWordLine, beforeWordCol);
                } else {
                    read();
                    if (token == LBRACK) {
                        read();
                        constDef.addConstExp(parseConstExp());
                        if (token != RBRACK) {
                            error(MissedRightBrackets, beforeWordLine, beforeWordCol);
                        }
                        read();
                        constDef.setMode2();
                    }
                }
            }
            while (token != ASSIGN) {
                read();
            }
            read();
            constDef.setConstInitVal(parseConstInitVal());
        }
        setPosAsBeforeWordPos(constDef);
        return constDef;
    }

    public ConstInitVal parseConstInitVal() throws IOException, CompileErrorException {
        ConstInitVal constInitVal = new ConstInitVal();
        if (token == LBRACE) {
            read();
            if (token == RBRACE) {
                read();
            } else {
                constInitVal.addConstInitVal(parseConstInitVal());
                while (true) {
                    if (token == RBRACE) {
                        read();
                        break;
                    }
                    if (token == COMMA) {
                        read();
                        constInitVal.addConstInitVal(parseConstInitVal());
                    } else {
                        read();
                    }
                }
            }
        } else {
            constInitVal.setConstExp(parseConstExp());
        }
        setPosAsBeforeWordPos(constInitVal);
        return constInitVal;
    }

    public VarDecl parseVarDecl() throws IOException, CompileErrorException {
        VarDecl varDecl = new VarDecl();
        read();
        varDecl.addVarDef(parseVarDef());
        if (token == SEMICN) {
            read();
        } else if (token == COMMA) {
            read();
            while (true) {
                varDecl.addVarDef(parseVarDef());
                if (token == SEMICN) {
                    read();
                    break;
                } else if (token == COMMA) {
                    read();
                } else {
                    error(MissedSemicolon, beforeWordLine, beforeWordCol);
                }
            }
        } else {
            error(MissedSemicolon, beforeWordLine, beforeWordCol);
        }
        setPosAsBeforeWordPos(varDecl);
        return varDecl;
    }

    public VarDef parseVarDef() throws CompileErrorException, IOException {
        VarDef varDef = new VarDef();
        if (token == IDENFR) {
            varDef.setIdent(symbol);
            read();
            if (token == LBRACK) {
                read();
                varDef.addConstExp(parseConstExp());
                varDef.setMode1();
                if (token != RBRACK) {
                    error(MissedRightBrackets, beforeWordLine, beforeWordCol);
                } else {
                    read();
                    if (token == LBRACK) {
                        read();
                        varDef.addConstExp(parseConstExp());
                        if (token != RBRACK) {
                            error(MissedRightBrackets, beforeWordLine, beforeWordCol);
                        }
                        read();
                        varDef.setMode2();
                    }
                }
            }
            while (token != ASSIGN && token != COMMA && token != SEMICN) {
                read();
            }
            if (token == ASSIGN) {
                read();
                varDef.setInitVal(parseInitVal());
            }
        }
        setPosAsBeforeWordPos(varDef);
        return varDef;
    }

    public InitVal parseInitVal() throws CompileErrorException, IOException {
        InitVal initVal = new InitVal();
        if (token == LBRACE) {
            read();
            if (token == RBRACE) {
                read();
            } else {
                initVal.addInitVal(parseInitVal());
                while (true) {
                    if (token == RBRACE) {
                        read();
                        break;
                    }
                    if (token == COMMA) {
                        read();
                        initVal.addInitVal(parseInitVal());
                    } else {
                        read();
                    }
                }
            }
        } else {
            initVal.setExp(parseExp());
        }
        setPosAsBeforeWordPos(initVal);
        return initVal;
    }

    public FuncDef parseFuncDef() throws IOException, CompileErrorException {
        FuncDef funcDef = new FuncDef();
        funcDef.setFuncType(parseFuncType());
        if (token == IDENFR) {
            funcDef.setIdent(symbol);
            funcDef.setIdentPos(thisWordLine, thisWordCol);
            read();
            if (token == LPARENT) {
                read();
                if (token == RPARENT) {
                    read();
                } else if (token == LBRACE) {
                    error(MissedRightParentheses, beforeWordLine, beforeWordCol);
                } else {
                    funcDef.setFuncFParams(parseFuncFParams());
                    if (token == RPARENT) {
                        read();
                    } else {
                        error(MissedRightParentheses, beforeWordLine, beforeWordCol);
                    }
                }
            } else {
                error(MissedSemicolon, beforeWordLine, beforeWordCol);
            }
        }
        Block tempBlock = parseBlock();
        int mode = tempBlock.getMode();
        funcDef.setBlock(tempBlock);
        setPosAsBeforeWordPos(funcDef);
        cft = null;
        return funcDef;
    }

    public MainFuncDef parseMainFuncDef() throws IOException, CompileErrorException {
        MainFuncDef mainFuncDef = new MainFuncDef();
        cft = INTTK;
        read();
        mainFuncDef.setIdentPos(thisWordLine, thisWordCol);
        read();
        if (token == LPARENT) {
            read();
            if (token == RPARENT) {
                read();
                mainFuncDef.setBlock(parseBlock());
            } else {
                error(MissedRightParentheses, beforeWordLine, beforeWordCol);
            }
        }
        setPosAsBeforeWordPos(mainFuncDef);
        cft = null;
        return mainFuncDef;
    }

    public FuncType parseFuncType() throws CompileErrorException, IOException {
        FuncType funcType = new FuncType();
        if (token == INTTK) {
            funcType.setTypeInt();
            cft = INTTK;
        } else if (token == VOIDTK) {
            funcType.setTypeVoid();
            cft = VOIDTK;
        }
        read();
        return funcType;
    }

    public FuncFParams parseFuncFParams() throws IOException, CompileErrorException {
        FuncFParams funcFParams = new FuncFParams();
        funcFParams.addFuncFParam(parseFuncFParam());
        while (true) {
            if (token != RPARENT) {
                if (token == COMMA) {
                    read();
                    funcFParams.addFuncFParam(parseFuncFParam());
                } else {
                    break;
                }
                continue;
            }
            break;
        }
        return funcFParams;
    }

    public FuncFParam parseFuncFParam() throws IOException, CompileErrorException {
        FuncFParam funcFParam = new FuncFParam();
        if (token == INTTK) {
            read();
        }
        if (token == IDENFR) {
            funcFParam.setIdent(symbol);
            read();
        }
        funcFParam.setMode0();
        if (token == LBRACK) {
            read();
            if (token == RBRACK) {
                read();
                funcFParam.setMode1();
                if (token == LBRACK) {
                    read();
                    funcFParam.setMode2();
                    funcFParam.setConstExp(parseConstExp());
                    if (token == RBRACK) {
                        read();
                    } else {
                        error(MissedRightBrackets, beforeWordLine, beforeWordCol);
                    }
                }
            } else {
                error(MissedRightBrackets, beforeWordLine, beforeWordCol);
            }
            while (token != COMMA && token != RPARENT && token != LBRACE) {
                read();
            }
        }
//        if (token == INTTK) {
//            read();
//            if (token == IDENFR) {
//                funcFParam.setIdent(symbol);
//                read();
//                if (token == LBRACK) {
//                    read();
//                    if (token == RBRACK) {
//                        read();
//                        if (token == LBRACK) {
//                            read();
//                            funcFParam.setConstExp(parseConstExp());
//                            if (token == RBRACK) {
//                                funcFParam.setMode2();
//                                read();
//                            } else {
//                                error(MissedRightBrackets, line, col);
//                            }
//                        } else {
//                            funcFParam.setMode1();
//                        }
//                    } else {
//                        error(MissedRightBrackets, line, col);
//                    }
//                } else {
//                    funcFParam.setMode0();
//                }
//            } else {
//                error(UnknownSymbol, line, col);
//            }
//        } else {
//            error(UnknownSymbol, line, col);
//            funcFParam = null;
//        }
        setPosAsBeforeWordPos(funcFParam);
        return funcFParam;
    }

    public Block parseBlock() throws IOException, CompileErrorException {
        Block block = new Block();
        int mode = 0;
        if (token == LBRACE) {
            read();
            while (true) {
                if (token == RBRACE) {
                    block.setEndPos(thisWordLine, thisWordCol);
                    read();
                    break;
                }
                BlockItem tempBlockItem = parseBlockItem();
                Stmt stmt = tempBlockItem.getStmt();
                if (stmt != null) {
                    int type = stmt.getType();
                    if (type == 6) {
                        if (stmt.getExp() == null) {
                            if (mode == 0) {
                                mode = 1;
                            } else if (mode == 2 || mode == 5) {
                                mode = 6;
                            } else if (mode == 3) {
                                mode = 4;
                            }
                        } else {
                            if (mode == 0) {
                                mode = 2;
                            } else if (mode == 1 || mode == 4) {
                                mode = 6;
                            } else if (mode == 3) {
                                mode = 5;
                            }
                        }
                    } else if (type == 4 || type == 5) {
                        if (mode == 0) {
                            mode = 3;
                        } else if (mode == 1) {
                            mode = 4;
                        } else if (mode == 2) {
                            mode = 5;
                        }
                    } else if (type == 10) {
                        int innerMode = stmt.getBlock().getMode();
                        if (innerMode == 6) {
                            mode = 6;
                        } else if (innerMode == 5 || innerMode == 2) {
                            if (mode == 0 || mode == 3) {
                                mode += 2;
                            } else if (mode == 1 || mode == 4) {
                                mode = 6;
                            }
                        } else if (innerMode == 4 || innerMode == 1) {
                            if (mode == 0 || mode == 3) {
                                mode += 1;
                            } else if (mode == 2 || mode == 5) {
                                mode = 6;
                            }
                        }
                    }
                    block.setMode(mode);
                }
                block.addBlockItem(tempBlockItem);
            }
        }
        setPosAsBeforeWordPos(block);
        return block;
    }

    public BlockItem parseBlockItem() throws IOException, CompileErrorException {
        BlockItem blockItem = new BlockItem();
        if (token == INTTK || token == CONSTTK) {
            blockItem.setDecl(parseDecl());
        } else {
            blockItem.setStmt(parseStmt());
        }
        return blockItem;
    }

    public Stmt parseStmt() throws IOException, CompileErrorException {
        Stmt stmt = new Stmt();
        if (token == IFTK) {
            read();
            if (token == LPARENT) {
                read();
                stmt.setCond(parseCond());
                if (token == RPARENT) {
                    read();
                    stmt.addStmt(parseStmt());
                    if (token == ELSETK) {
                        read();
                        stmt.addStmt(parseStmt());
                    }
                } else {
                    error(MissedRightParentheses, beforeWordLine, beforeWordCol);
                }
            }
            stmt.setType2();
        } else if (token == WHILETK) {
            read();
            if (token == LPARENT) {
                read();
                stmt.setCond(parseCond());
                if (token == RPARENT) {
                    read();
                    Stmt tempStmt = parseStmt();
                    if (tempStmt.getBlock() != null) {
                        tempStmt.getBlock().setMode3();
                    }
                    stmt.addStmt(tempStmt);
                } else {
                    error(MissedRightParentheses, beforeWordLine, beforeWordCol);
                }
            }
            stmt.setType3();
        } else if (token == BREAKTK) {
            stmt.setBreakContinuePos(thisWordLine, thisWordCol);
            read();
            if (token != SEMICN) {
                error(MissedSemicolon, beforeWordLine, beforeWordCol);
            }
            read();
            stmt.setType4();
        } else if (token == CONTINUETK) {
            stmt.setBreakContinuePos(thisWordLine, thisWordCol);
            read();
            if (token != SEMICN) {
                error(MissedSemicolon, beforeWordLine, beforeWordCol);
            }
            read();
            stmt.setType5();
        } else if (token == RETURNTK) {
            read();
            if (token == SEMICN) {
                if (cft == INTTK) {
                    error(ReturnValueNotExists, beforeWordLine, beforeWordCol);
                }
                read();
            } else {
                if (cft == VOIDTK) {
                    error(ReturnValueExists, beforeWordLine, beforeWordCol);
                }
                stmt.setExp(parseExp());
                if (token == SEMICN) {
                    read();
                } else {
                    error(MissedSemicolon, beforeWordLine, beforeWordCol);
                }
            }
            stmt.setType6();
        } else if (token == PRINTFTK) {
            int line = thisWordLine, col = thisWordCol;
            read();
            if (token == LPARENT) {
                read();
                if (token == STRCON) {
                    int count = checkFormatString(symbol, thisWordLine, thisWordCol);
                    int num = 0;
                    stmt.setFormatString(symbol);
                    read();
                    while (true) {
                        if (token == COMMA) {
                            read();
                            stmt.addExp(parseExp());
                            num++;
                            continue;
                        }
                        break;
                    }
                    if (count != num) {
                        error(IllegalFormatStringInPrintf, line, col);
                    }
                    if (token == RPARENT) {
                        read();
                        if (token == SEMICN) {
                            read();
                        } else {
                            error(MissedSemicolon, beforeWordLine, beforeWordCol);
                        }
                    } else {
                        error(MissedRightParentheses, beforeWordLine, beforeWordCol);
                    }
                } else {
                    error(ParamTypeMismatched, beforeWordLine, beforeWordCol);
                }
            }
            stmt.setType7();
        } else if (token == SEMICN) {
            read();
        } else if (token == LBRACE) {
            stmt.setBlock(parseBlock());
            stmt.setType10();
        } else if (hasAssignSymbol()) {
            stmt.setlVal(parseLVal());
            if (token == ASSIGN) {
                read();
                if (token == GETINTTK) {
                    read();
                    if (token == LPARENT) {
                        read();
                        if (token == RPARENT) {
                            read();
                            if (token == SEMICN) {
                                read();
                                stmt.setType9();
                            } else {
                                error(MissedSemicolon, beforeWordLine, beforeWordCol);
                            }
                        } else {
                            error(MissedRightParentheses, beforeWordLine, beforeWordCol);
                        }
                    }
                } else {
                    stmt.setExp(parseExp());
                    if (token == SEMICN) {
                        read();
                    } else {
                        error(MissedSemicolon, beforeWordLine, beforeWordCol);
                    }
                    stmt.setType8();
                }
            }
        } else {
            stmt.setExp(parseExp());
            if (token == SEMICN) {
                read();
                stmt.setType1();
            } else {
                error(MissedSemicolon, beforeWordLine, beforeWordCol);
            }
        }
        setPosAsBeforeWordPos(stmt);
        return stmt;
    }

    public Exp parseExp() throws IOException, CompileErrorException {
        Exp exp = new Exp();
        exp.setAddExp(parseAddExp());
        return exp;
    }

    public Cond parseCond() throws IOException, CompileErrorException {
        Cond cond = new Cond();
        cond.setLOrExp(parseLOrExp());
        return cond;
    }

    public LVal parseLVal() throws IOException, CompileErrorException {
        LVal lVal = new LVal();
        if (token == IDENFR) {
            lVal.setIdent(symbol);
            read();
            if (token == LBRACK) {
                read();
                lVal.addExp(parseExp());
                if (token == RBRACK) {
                    read();
                    if (token == LBRACK) {
                        read();
                        lVal.addExp(parseExp());
                        if (token == RBRACK) {
                            read();
                        } else {
                            error(MissedRightBrackets, beforeWordLine, beforeWordCol);
                        }
                        lVal.setMode2();
                    } else {
                        lVal.setMode1();
                    }
                } else {
                    error(MissedRightBrackets, beforeWordLine, beforeWordCol);
                }
            }
        }
        setPosAsBeforeWordPos(lVal);
        return lVal;
    }

    public PrimaryExp parsePrimaryExp() throws IOException, CompileErrorException {
        PrimaryExp primaryExp = new PrimaryExp();
        if (token == LPARENT) {
            read();
            primaryExp.setExp(parseExp());
            if (token == RPARENT) {
                read();
            } else {
                error(MissedRightParentheses, beforeWordLine, beforeWordCol);
            }
        } else if (token == INTCON) {
            primaryExp.setNumber(parseNumber());
        } else {
            primaryExp.setlVal(parseLVal());
        }
        setPosAsBeforeWordPos(primaryExp);
        return primaryExp;
    }

    public Number parseNumber() throws CompileErrorException {
        Number number = new Number();
        if (token == INTCON) {
            number.setNumber(symbol);
            read();
        }
        setPosAsBeforeWordPos(number);
        return number;
    }

    public UnaryExp parseUnaryExp() throws IOException, CompileErrorException {
        UnaryExp unaryExp = new UnaryExp();
        if (token == IDENFR) {
            unaryExp.setIdent(symbol);
            unaryExp.setIdentPos(thisWordLine, thisWordCol);
            if (readToken(0) == LPARENT) {
                read();
                read();
                if (token == RPARENT) {
                    read();
                } else {
                    unaryExp.setFuncRParams(parseFuncRParams());
                    if (token == RPARENT) {
                        read();
                    } else {
                        error(MissedRightParentheses, beforeWordLine, beforeWordCol);
                    }
                }
            } else {
                unaryExp.setPrimaryExp(parsePrimaryExp());
            }
        } else if (token == PLUS || token == MINU || token == NOT) {
            unaryExp.setUnaryOp(parseUnaryOp());
            unaryExp.setUnaryExp(parseUnaryExp());
        } else if (token == LPARENT || token == INTCON){
            unaryExp.setPrimaryExp(parsePrimaryExp());
        }
        setPosAsBeforeWordPos(unaryExp);
        return unaryExp;
    }

    public UnaryOp parseUnaryOp() throws CompileErrorException {
        UnaryOp unaryOp = new UnaryOp();
        if (token == PLUS || token == MINU || token == NOT) {
            unaryOp.setOp(symbol);
            read();
        }
        setPosAsBeforeWordPos(unaryOp);
        return unaryOp;
    }

    public FuncRParams parseFuncRParams() throws IOException, CompileErrorException {
        FuncRParams funcRParams = new FuncRParams();
        funcRParams.addExp(parseExp());
        while (token != COMMA && token != RPARENT && token != SEMICN) {
            read();
        }
        while (true) {
            if (token == COMMA) {
                read();
                funcRParams.addExp(parseExp());
                while (token != COMMA && token != RPARENT && token != SEMICN) {
                    read();
                }
                continue;
            }
            break;
        }
        setPosAsBeforeWordPos(funcRParams);
        return funcRParams;
    }

    public MulExp parseMulExp() throws IOException, CompileErrorException {
        MulExp mulExp = new MulExp();
        mulExp.setUnaryExp(parseUnaryExp());
        if (token == MULT || token == DIV || token == MOD) {
            mulExp.setOp(symbol);
            read();
            mulExp.setMulExp(parseMulExp());
        }
        setPosAsBeforeWordPos(mulExp);
        return mulExp;
    }

    public AddExp parseAddExp() throws IOException, CompileErrorException {
        AddExp addExp = new AddExp();
        addExp.setMulExp(parseMulExp());
        if (token == PLUS || token == MINU) {
            addExp.setOp(symbol);
            read();
            addExp.setAddExp(parseAddExp());
        }
        addExp.setLine(beforeWordLine);
        addExp.setCol(beforeWordCol);
        setPosAsBeforeWordPos(addExp);
        return addExp;
    }

    public RelExp parseRelExp() throws IOException, CompileErrorException {
        RelExp relExp = new RelExp();
        relExp.setAddExp(parseAddExp());
        if (token == LSS || token == GRE || token == LEQ || token == GEQ) {
            relExp.setOp(symbol);
            read();
            relExp.setRelExp(parseRelExp());
        }
        setPosAsBeforeWordPos(relExp);
        return relExp;
    }

    public EqExp parseEqExp() throws IOException, CompileErrorException {
        EqExp eqExp = new EqExp();
        eqExp.setRelExp(parseRelExp());
        if (token == EQL || token == NEQ) {
            eqExp.setOp(symbol);
            read();
            eqExp.setEqExp(parseEqExp());
        }
        setPosAsBeforeWordPos(eqExp);
        return eqExp;
    }

    public LAndExp parseLAndExp() throws IOException, CompileErrorException {
        LAndExp lAndExp = new LAndExp();
        lAndExp.setEqExp(parseEqExp());
        if (token == AND) {
            read();
            lAndExp.setlAndExp(parseLAndExp());
        }
        setPosAsBeforeWordPos(lAndExp);
        return lAndExp;
    }

    public LOrExp parseLOrExp() throws IOException, CompileErrorException {
        LOrExp lOrExp = new LOrExp();
        lOrExp.setlAndExp(parseLAndExp());
        if (token == OR) {
            read();
            lOrExp.setlOrExp(parseLOrExp());
        }
        setPosAsBeforeWordPos(lOrExp);
        return lOrExp;
    }

    public ConstExp parseConstExp() throws IOException, CompileErrorException {
        ConstExp constExp = new ConstExp();
        constExp.setAddExp(parseAddExp());
        return constExp;
    }
}
