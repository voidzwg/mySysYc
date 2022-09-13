package frontend;

import java.io.File;

public class Lexer {
    private Token symbol;
    private Reader token;

    public Lexer(File f) {
        token = new Reader(f);
    }

    public String getToken() {
        return token.getToken();
    }

    public int getNum() {
        return token.getNum();
    }

    public int getLine() {
        return token.getLine();
    }

    public Token next() {
        token.clearToken();
        if (token.readChar() == -1) {
            symbol = Token.ERROR;
            return symbol;
        }
        while (isBlank(token.getCh())) {
            if (isRet(token.getCh())) {
                token.nextLine();
            }
            if (token.readChar() == -1) {
                symbol = Token.ERROR;
                return symbol;
            }
        }
        if (Character.isLetter(token.getCh())) {
            while (isIdent(token.getCh())) {
                token.catToken();
                token.readChar();
            }
            token.retract();
            symbol = checkToken(token.getToken());
        } else if (Character.isDigit(token.getCh())) {
            while (Character.isDigit(token.getCh())) {
                token.catToken();
                token.readChar();
            }
            token.setNum();
            token.retract();
            symbol = Token.INTCON;
        } else if (isQuota(token.getCh())) {
            token.catToken();
            token.readChar();
            while (!isQuota(token.getCh())) {
                token.catToken();
                token.readChar();
            }
            token.catToken();
            symbol = Token.STRCON;
        } else if (isExclam(token.getCh())) {
            token.catToken();
            token.readChar();
            if (isEqual(token.getCh())) {
                token.catToken();
            } else {
                token.retract();
            }
            if (token.getToken().equals("!=")) {
                symbol = Token.NEQ;
            } else if (token.getToken().equals("!")) {
                symbol = Token.NOT;
            } else {
                symbol = Token.ERROR;
            }
        } else if (isRef(token.getCh())) {
            token.catToken();
            token.readChar();
            if (isRef(token.getCh())) {
                token.catToken();
            } else {
                token.retract();
            }
            if (token.getToken().equals("&&")) {
                symbol = Token.AND;
            } else {
                symbol = Token.ERROR;
            }
        } else if (isVer(token.getCh())) {
            token.catToken();
            token.readChar();
            if (isVer(token.getCh())) {
                token.catToken();
            } else {
                token.retract();
            }
            if (token.getToken().equals("||")) {
                symbol = Token.OR;
            } else {
                symbol = Token.ERROR;
            }
        } else if (isSingleSymbol(token.getCh())) {
            token.catToken();
            symbol = Token.getToken(String.valueOf(token.getCh()));
        } else if (isEqual(token.getCh())) {
            token.catToken();
            token.readChar();
            if (isEqual(token.getCh())) {
                token.catToken();
            } else {
                token.retract();
            }
            if (token.getToken().equals("==")) {
                symbol = Token.EQL;
            } else if (token.getToken().equals("=")) {
                symbol = Token.ASSIGN;
            } else {
                symbol = Token.ERROR;
            }
        } else if (isLss(token.getCh())) {
            token.catToken();
            token.readChar();
            if (isEqual(token.getCh())) {
                token.catToken();
            } else {
                token.retract();
            }
            if (token.getToken().equals("<=")) {
                symbol = Token.LEQ;
            } else if (token.getToken().equals("<")) {
                symbol = Token.LSS;
            } else {
                symbol = Token.ERROR;
            }
        } else if (isGre(token.getCh())) {
            token.catToken();
            token.readChar();
            if (isEqual(token.getCh())) {
                token.catToken();
            } else {
                token.retract();
            }
            if (token.getToken().equals(">=")) {
                symbol = Token.GEQ;
            } else if (token.getToken().equals(">")) {
                symbol = Token.GRE;
            } else {
                symbol = Token.ERROR;
            }
        } else if (isDiv(token.getCh())) {
            token.catToken();
            token.readChar();
            if (isMulti(token.getCh())) {
                int mode = 0;
                while (true) {
                    token.catToken();
                    token.readChar();
                    if (mode == 0 && isMulti(token.getCh())) {
                        mode = 1;
                    }
                    if (mode == 1) {
                        if (isDiv(token.getCh())) {
                            mode = 2;
                        } else if (!isMulti(token.getCh())) {
                            mode = 0;
                        }
                    }
                    if (mode == 2) {
                        token.catToken();
                        break;
                    }
                }
                symbol = Token.NOTE;
            } else if (isDiv(token.getCh())) {
                while (!isRet(token.getCh())) {
                    token.catToken();
                    token.readChar();
                }
                token.nextLine();
                symbol = Token.NOTE;
            } else {
                token.retract();
                symbol = Token.DIV;
            }
        } else {
            symbol = Token.ERROR;
        }
        return symbol;
    }

    public boolean isEmpty() {
        int ret = token.readChar();
        if (ret == -1) {
            return true;
        } else {
            token.retract();
            return false;
        }
    }

    private boolean isBlank(char ch) {
        return ch == ' ' || ch == '\n' || ch == '\t' || ch == '\r' || ch == '\f';
    }

    private boolean isRet(char ch) {
        return ch == '\n';
    }

    private boolean isIdent(char ch) {
        return Character.isLetterOrDigit(ch) || ch == '_';
    }

    private boolean isQuota(char ch) {
        return ch == '"';
    }

    private boolean isExclam(char ch) {
        return ch == '!';
    }

    private boolean isEqual(char ch) {
        return ch == '=';
    }

    private boolean isRef(char ch) {
        return ch == '&';
    }

    private boolean isVer(char ch) {
        return ch == '|';
    }

    private boolean isMulti(char ch) {
        return ch == '*';
    }

    private boolean isDiv(char ch) {
        return ch == '/';
    }

    private boolean isLss(char ch) {
        return ch == '<';
    }

    private boolean isGre(char ch) {
        return ch == '>';
    }

    private boolean isSingleSymbol(char c) {
        return c == '+' || c == '-' || c == '*' || c == '%'
                || c == ';' || c == ','
                || c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}';
    }

    private Token checkToken(String token) {
        if (token == null) {
            return null;
        }
        for (Token t : Token.values()) {
            int ordinal = t.ordinal();
            if ((ordinal >= 3 && ordinal <= 9) || (ordinal >= 13 && ordinal <= 16) || ordinal == 19) {
                if (t.getName().equals(token)) {
                    return t;
                }
            }
        }
        return Token.IDENFR;
    }
}
