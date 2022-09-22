package frontend;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import frontend.CompTree.*;
import frontend.CompTree.Number;

public class Parser {
    private final Lexer lexer;
    private Token token;
    private String symbol;
    private int head = 0, tail = 0;
    private final ArrayList<Token> tokensQueue;
    private final ArrayList<String> symbolsQueue;

    public Parser(File fr){
        tokensQueue = new ArrayList<>();
        symbolsQueue = new ArrayList<>();
        lexer = new Lexer(fr);
    }

    public CompTree build() throws IOException, CompileErrorException {
        CompTree tree = new CompUnit();
        read();
        tree = parseCompUnit();
        return tree;
    }

    public void close() throws IOException {
        lexer.close();
    }

    private void push(Token tok, String sym) {
        tokensQueue.add(tok);
        symbolsQueue.add(sym);
        head++;
    }

    private void pop() {
        if (head > tail) {
            token = tokensQueue.get(tail);
            symbol = symbolsQueue.get(tail);
            tail++;
        } else {
            token = Token.EOF;
            symbol = Token.EOF.getName();
        }
    }

    private int readLexer() throws CompileErrorException {
        Token token;
        String symbol;
        while (true) {
            token = lexer.next();
            symbol = lexer.getToken();
            if (token == Token.EOF) {
                //error(Error.UndefinedError);
                System.out.println("EOF!");
                return -1;
            }
            if (token == Token.NOTE) {
                continue;
            }
            push(token, symbol);
            break;
        }
        return 0;
    }

    private Token readToken(int n) throws CompileErrorException {
        while (head <= tail + n) {
            if (readLexer() == -1) {
                break;
            }
        }
        if (head > tail + n) {
            return tokensQueue.get(tail + n);
        } else {
            return Token.EOF;
        }
    }

    private String readSymbol(int n) throws CompileErrorException {
        while (head <= tail + n) {
            readLexer();
        }
        if (head > tail + n) {
            return symbolsQueue.get(tail + n);
        } else {
            return Token.EOF.getName();
        }
    }

    private void read() throws CompileErrorException {
        if (head > tail) {
            pop();
        } else {
            readLexer();
            pop();
        }
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
        if (token == Token.CONSTTK) {
            return true;
        } else if (token == Token.INTTK) {
            if (readToken(0) == Token.IDENFR) {
                return readToken(1) == Token.COMMA || readToken(1) == Token.SEMICN || readToken(1) == Token.ASSIGN || readToken(1) == Token.LBRACK;
            }
        }
        return false;
    }

    private boolean isFuncDef() throws CompileErrorException {
        if (token == Token.INTTK || token == Token.VOIDTK) {
            if (readToken(0) == Token.IDENFR) {
                return readToken(1) == Token.LPARENT;
            }
        }
        return false;
    }

    public Decl parseDecl() throws CompileErrorException, IOException {
        Decl decl = new Decl();
        if (token == Token.CONSTTK) {
            decl.setConstDecl(parseConstDecl());
        } else if (token == Token.INTTK) {
            decl.setVarDecl(parseVarDecl());
        } else {
            error(Error.UnknownSymbol);
        }
        return decl;
    }

    public ConstDecl parseConstDecl() throws IOException, CompileErrorException {
        ConstDecl constDecl = new ConstDecl();
        read();
        if (token == Token.INTTK) {
            read();
            while (true) {
                constDecl.addConstDefs(parseConstDef());
                if (token == Token.COMMA) {
                    read();
                } else if (token == Token.SEMICN) {
                    read();
                    break;
                } else {
                    error(Error.MissedSemicolon);
                }
            }
        } else {
            error(Error.UndefinedError);
        }
        return constDecl;
    }

    public ConstDef parseConstDef() throws IOException, CompileErrorException {
        ConstDef constDef = new ConstDef();
        if (token == Token.IDENFR) {
            constDef.setIdent(symbol);
            read();
            if (token == Token.LBRACK) {
                read();
                constDef.addConstExp(parseConstExp());
                if (token != Token.RBRACK) {
                    error(Error.MissedRightBrackets);
                }
                read();
                if (token == Token.LBRACK) {
                    read();
                    constDef.addConstExp(parseConstExp());
                    if (token != Token.RBRACK) {
                        error(Error.MissedRightBrackets);
                    }
                    read();
                    constDef.setMode2();
                } else {
                    constDef.setMode1();
                }
            }
            if (token == Token.ASSIGN) {
                read();
                constDef.setConstInitVal(parseConstInitVal());
            } else {
                error(Error.UndefinedError);
            }
        } else {
            error(Error.UndefinedError);
        }
        return constDef;
    }

    public ConstInitVal parseConstInitVal() throws IOException, CompileErrorException {
        ConstInitVal constInitVal = new ConstInitVal();
        if (token == Token.LBRACE) {
            read();
            if (token == Token.RBRACE) {
                read();
            } else {
                constInitVal.addConstInitVal(parseConstInitVal());
                while (true) {
                    if (token == Token.RBRACE) {
                        read();
                        break;
                    }
                    if (token == Token.COMMA) {
                        read();
                        constInitVal.addConstInitVal(parseConstInitVal());
                    } else {
                        error(Error.UnknownSymbol);
                    }
                }
            }
        } else {
            constInitVal.setConstExp(parseConstExp());
        }
        return constInitVal;
    }

    public VarDecl parseVarDecl() throws IOException, CompileErrorException {
        VarDecl varDecl = new VarDecl();
        read();
        varDecl.addVarDef(parseVarDef());
        if (token == Token.SEMICN) {
            read();
        } else if (token == Token.COMMA) {
            read();
            while (true) {
                varDecl.addVarDef(parseVarDef());
                if (token == Token.SEMICN) {
                    read();
                    break;
                } else if (token == Token.COMMA) {
                    read();
                } else {
                    error(Error.MissedSemicolon);
                }
            }
        } else {
            error(Error.MissedSemicolon);
        }
        return varDecl;
    }

    public VarDef parseVarDef() throws CompileErrorException, IOException {
        VarDef varDef = new VarDef();
        if (token == Token.IDENFR) {
            varDef.setIdent(symbol);
            read();
            if (token == Token.LBRACK) {
                read();
                varDef.addConstExp(parseConstExp());
                if (token != Token.RBRACK) {
                    error(Error.MissedRightBrackets);
                }
                read();
                if (token == Token.LBRACK) {
                    read();
                    varDef.addConstExp(parseConstExp());
                    if (token != Token.RBRACK) {
                        error(Error.MissedRightBrackets);
                    }
                    read();
                    varDef.setMode2();
                } else {
                    varDef.setMode1();
                }
            }
            if (token == Token.ASSIGN) {
                read();
                varDef.setInitVal(parseInitVal());
            }
        } else {
            error(Error.UndefinedError);
        }
        return varDef;
    }

    public InitVal parseInitVal() throws CompileErrorException, IOException {
        InitVal initVal = new InitVal();
        if (token == Token.LBRACE) {
            read();
            if (token == Token.RBRACE) {
                read();
            } else {
                initVal.addInitVal(parseInitVal());
                while (true) {
                    if (token == Token.RBRACE) {
                        read();
                        break;
                    }
                    if (token == Token.COMMA) {
                        read();
                        initVal.addInitVal(parseInitVal());
                    } else {
                        error(Error.UnknownSymbol);
                    }
                }
            }
        } else {
            initVal.setExp(parseExp());
        }
        return initVal;
    }

    public FuncDef parseFuncDef() throws IOException, CompileErrorException {
        FuncDef funcDef = new FuncDef();
        funcDef.setFuncType(parseFuncType());
        if (token == Token.IDENFR) {
            funcDef.setIdent(symbol);
            read();
            if (token == Token.LPARENT) {
                read();
                if (token == Token.RPARENT) {
                    read();
                } else {
                    funcDef.setFuncFParams(parseFuncFParams());
                    if (token == Token.RPARENT) {
                        read();
                    } else {
                        error(Error.MissedRightParentheses);
                    }
                }
            } else {
                error(Error.UndefinedError);
            }
        } else {
            error(Error.UnknownSymbol);
        }
        funcDef.setBlock(parseBlock());
        return funcDef;
    }

    public MainFuncDef parseMainFuncDef() throws IOException, CompileErrorException {
        MainFuncDef mainFuncDef = new MainFuncDef();
        read();
        read();
        if (token == Token.LPARENT) {
            read();
            if (token == Token.RPARENT) {
                read();
                mainFuncDef.setBlock(parseBlock());
            } else {
                error(Error.MissedRightParentheses);
            }
        } else {
            error(Error.UndefinedError);
        }
        return mainFuncDef;
    }

    public FuncType parseFuncType() throws CompileErrorException, IOException {
        FuncType funcType = new FuncType();
        if (token == Token.INTTK) {
            funcType.setTypeInt();
        } else if (token == Token.VOIDTK) {
            funcType.setTypeVoid();
        } else {
            error(Error.UndefinedError);
        }
        read();
        return funcType;
    }

    public FuncFParams parseFuncFParams() throws IOException, CompileErrorException {
        FuncFParams funcFParams = new FuncFParams();
        funcFParams.addFuncFParam(parseFuncFParam());
        while (true) {
            if (token == Token.COMMA) {
                read();
                funcFParams.addFuncFParam(parseFuncFParam());
                continue;
            }
            break;
        }
        return funcFParams;
    }

    public FuncFParam parseFuncFParam() throws IOException, CompileErrorException {
        FuncFParam funcFParam = new FuncFParam();
        if (token == Token.INTTK) {
            read();
            if (token == Token.IDENFR) {
                funcFParam.setIdent(symbol);
                read();
                if (token == Token.LBRACK) {
                    read();
                    if (token == Token.RBRACK) {
                        read();
                        if (token == Token.LBRACK) {
                            read();
                            funcFParam.setConstExp(parseConstExp());
                            if (token == Token.RBRACK) {
                                funcFParam.setMode2();
                                read();
                            } else {
                                error(Error.MissedRightBrackets);
                            }
                        } else {
                            funcFParam.setMode1();
                        }
                    } else {
                        error(Error.MissedRightBrackets);
                    }
                } else {
                    funcFParam.setMode0();
                }
            } else {
                error(Error.UnknownSymbol);
            }
        } else {
            error(Error.UndefinedError);
        }
        return funcFParam;
    }

    public Block parseBlock() throws IOException, CompileErrorException {
        Block block = new Block();
        if (token == Token.LBRACE) {
            read();
            while (true) {
                if (token == Token.RBRACE) {
                    read();
                    break;
                }
                block.addBlockItem(parseBlockItem());
            }
        } else {
            error(Error.UnknownSymbol);
        }
        return block;
    }

    public BlockItem parseBlockItem() throws IOException, CompileErrorException {
        BlockItem blockItem = new BlockItem();
        if (token == Token.INTTK || token == Token.CONSTTK) {
            blockItem.setDecl(parseDecl());
        } else {
            blockItem.setStmt(parseStmt());
        }
        return blockItem;
    }

    public Stmt parseStmt() throws IOException, CompileErrorException {
        Stmt stmt = new Stmt();
        if (token == Token.IFTK) {
            read();
            if (token == Token.LPARENT) {
                read();
                stmt.setCond(parseCond());
                if (token == Token.RPARENT) {
                    read();
                    stmt.addStmt(parseStmt());
                    if (token == Token.ELSETK) {
                        read();
                        stmt.addStmt(parseStmt());
                    }
                } else {
                    error(Error.MissedRightParentheses);
                }
            } else {
                error(Error.UnknownSymbol);
            }
            stmt.setType2();
        } else if (token == Token.WHILETK) {
            read();
            if (token == Token.LPARENT) {
                read();
                stmt.setCond(parseCond());
                if (token == Token.RPARENT) {
                    read();
                    stmt.addStmt(parseStmt());
                } else {
                    error(Error.MissedRightParentheses);
                }
            } else {
                error(Error.UnknownSymbol);
            }
            stmt.setType3();
        } else if (token == Token.BREAKTK) {
            read();
            if (token != Token.SEMICN) {
                error(Error.MissedSemicolon);
            }
            read();
            stmt.setType4();
        } else if (token == Token.CONTINUETK) {
            read();
            if (token != Token.SEMICN) {
                error(Error.MissedSemicolon);
            }
            read();
            stmt.setType5();
        } else if (token == Token.RETURNTK) {
            read();
            if (token == Token.SEMICN) {
                read();
            } else {
                stmt.setExp(parseExp());
                if (token == Token.SEMICN) {
                    read();
                } else {
                    error(Error.MissedSemicolon);
                }
            }
            stmt.setType6();
        } else if (token == Token.PRINTFTK) {
            read();
            if (token == Token.LPARENT) {
                read();
                if (token == Token.STRCON) {
                    stmt.setFormatString(symbol);
                    read();
                    while (true) {
                        if (token == Token.COMMA) {
                            read();
                            stmt.addExp(parseExp());
                            continue;
                        }
                        break;
                    }
                    if (token == Token.RPARENT) {
                        read();
                        if (token == Token.SEMICN) {
                            read();
                        } else {
                            error(Error.MissedSemicolon);
                        }
                    } else {
                        error(Error.MissedRightParentheses);
                    }
                } else {
                    error(Error.UnknownSymbol);
                }
            } else {
                error(Error.UnknownSymbol);
            }
            stmt.setType7();
        } else if (token == Token.SEMICN) {
            read();
        } else if (token == Token.LBRACE) {
            stmt.setBlock(parseBlock());
            stmt.setType10();
        } else if (hasAssignSymbol()) {
            stmt.setlVal(parseLVal());
            if (token == Token.ASSIGN) {
                read();
                if (token == Token.GETINTTK) {
                    read();
                    if (token == Token.LPARENT) {
                        read();
                        if (token == Token.RPARENT) {
                            read();
                            if (token == Token.SEMICN) {
                                read();
                                stmt.setType9();
                            } else {
                                error(Error.MissedSemicolon);
                            }
                        } else {
                            error(Error.MissedRightParentheses);
                        }
                    } else {
                        error(Error.UnknownSymbol);
                    }
                } else {
                    stmt.setExp(parseExp());
                    if (token == Token.SEMICN) {
                        read();
                        stmt.setType8();
                    } else {
                        error(Error.MissedSemicolon);
                    }
                }
            } else {
                error(Error.UndefinedError);
            }
        } else {
            stmt.setExp(parseExp());
            if (token == Token.SEMICN) {
                read();
                stmt.setType1();
            } else {
                error(Error.MissedSemicolon);
            }
        }
        return stmt;
    }

    private boolean hasAssignSymbol() throws CompileErrorException {
        int i = 0;
        while (true) {
            if (readToken(i) == Token.EOF || readToken(i) == Token.SEMICN) {
                return false;
            }
            if (readToken(i) == Token.ASSIGN) {
                return true;
            }
            i++;
        }
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
        if (token == Token.IDENFR) {
            lVal.setIdent(symbol);
            read();
            if (token == Token.LBRACK) {
                read();
                lVal.addExp(parseExp());
                if (token == Token.RBRACK) {
                    read();
                    if (token == Token.LBRACK) {
                        read();
                        lVal.addExp(parseExp());
                        if (token == Token.RBRACK) {
                            read();
                        } else {
                            error(Error.MissedRightBrackets);
                        }
                        lVal.setMode2();
                    } else {
                        lVal.setMode1();
                    }
                } else {
                    error(Error.MissedRightBrackets);
                }
            }
        } else {
            error(Error.UnknownSymbol);
        }
        return lVal;
    }

    public PrimaryExp parsePrimaryExp() throws IOException, CompileErrorException {
        PrimaryExp primaryExp = new PrimaryExp();
        if (token == Token.LPARENT) {
            read();
            primaryExp.setExp(parseExp());
            if (token == Token.RPARENT) {
                read();
            } else {
                error(Error.MissedRightParentheses);
            }
        } else if (token == Token.INTCON) {
            primaryExp.setNumber(parseNumber());
        } else {
            primaryExp.setlVal(parseLVal());
        }
        return primaryExp;
    }

    public Number parseNumber() throws IOException, CompileErrorException {
        Number number = new Number();
        if (token == Token.INTCON) {
            number.setNumber(symbol);
            read();
        } else {
            error(Error.UnknownSymbol);
        }
        return number;
    }

    public UnaryExp parseUnaryExp() throws IOException, CompileErrorException {
        UnaryExp unaryExp = new UnaryExp();
        if (token == Token.IDENFR) {
            unaryExp.setIdent(symbol);
            //read();
            if (readToken(0) == Token.LPARENT) {
                read();
                read();
                if (token == Token.RPARENT) {
                    read();
                } else {
                    unaryExp.setFuncRParams(parseFuncRParams());
                    if (token == Token.RPARENT) {
                        read();
                    } else {
                        error(Error.MissedRightParentheses);
                    }
                }
            } else {
                unaryExp.setPrimaryExp(parsePrimaryExp());
            }
        } else if (token == Token.PLUS || token == Token.MINU || token == Token.NOT) {
            unaryExp.setUnaryOp(parseUnaryOp());
            unaryExp.setUnaryExp(parseUnaryExp());
        } else if (token == Token.LPARENT || token == Token.INTCON){
            unaryExp.setPrimaryExp(parsePrimaryExp());
        } else {
            error(Error.UnknownSymbol);
        }
        return unaryExp;
    }

    public UnaryOp parseUnaryOp() throws CompileErrorException {
        UnaryOp unaryOp = new UnaryOp();
        if (token == Token.PLUS || token == Token.MINU || token == Token.NOT) {
            unaryOp.setOp(symbol);
            read();
        } else {
            error(Error.UnknownSymbol);
        }
        return unaryOp;
    }

    public FuncRParams parseFuncRParams() throws IOException, CompileErrorException {
        FuncRParams funcRParams = new FuncRParams();
        funcRParams.addExp(parseExp());
        while (true) {
            if (token == Token.COMMA) {
                read();
                funcRParams.addExp(parseExp());
                continue;
            }
            break;
        }
        return funcRParams;
    }

    public MulExp parseMulExp() throws IOException, CompileErrorException {
        MulExp mulExp = new MulExp();
        mulExp.setUnaryExp(parseUnaryExp());
        if (token == Token.MULT || token == Token.DIV || token == Token.MOD) {
            mulExp.setOp(symbol);
            read();
            mulExp.setMulExp(parseMulExp());
        }
        return mulExp;
    }

    public AddExp parseAddExp() throws IOException, CompileErrorException {
        AddExp addExp = new AddExp();
        addExp.setMulExp(parseMulExp());
        if (token == Token.PLUS || token == Token.MINU) {
            addExp.setOp(symbol);
            read();
            addExp.setAddExp(parseAddExp());
        }
        return addExp;
    }

    public RelExp parseRelExp() throws IOException, CompileErrorException {
        RelExp relExp = new RelExp();
        relExp.setAddExp(parseAddExp());
        if (token == Token.LSS || token == Token.GRE || token == Token.LEQ || token == Token.GEQ) {
            relExp.setOp(symbol);
            read();
            relExp.setRelExp(parseRelExp());
        }
        return relExp;
    }

    public EqExp parseEqExp() throws IOException, CompileErrorException {
        EqExp eqExp = new EqExp();
        eqExp.setRelExp(parseRelExp());
        if (token == Token.EQL || token == Token.NEQ) {
            eqExp.setOp(symbol);
            read();
            eqExp.setEqExp(parseEqExp());
        }
        return eqExp;
    }

    public LAndExp parseLAndExp() throws IOException, CompileErrorException {
        LAndExp lAndExp = new LAndExp();
        lAndExp.setEqExp(parseEqExp());
        if (token == Token.AND) {
            read();
            lAndExp.setlAndExp(parseLAndExp());
        }
        return lAndExp;
    }

    public LOrExp parseLOrExp() throws IOException, CompileErrorException {
        LOrExp lOrExp = new LOrExp();
        lOrExp.setlAndExp(parseLAndExp());
        if (token == Token.OR) {
            read();
            lOrExp.setlOrExp(parseLOrExp());
        }
        return lOrExp;
    }

    public ConstExp parseConstExp() throws IOException, CompileErrorException {
        ConstExp constExp = new ConstExp();
        constExp.setAddExp(parseAddExp());
        return constExp;
    }

    public void error(Error e) throws CompileErrorException {
        throw new CompileErrorException(e);
    }
}
