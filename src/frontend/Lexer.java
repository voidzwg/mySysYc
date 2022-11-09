package frontend;

import java.io.File;
import java.io.IOException;

import static frontend.Token.*;

public class Lexer {
    private final Reader token;

    public Lexer(File f) {
        token = new Reader(f);
    }

    public void close() throws IOException {
        token.close();
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

    public int getCol() {
        return token.getCol();
    }

    public Token next() {
        Token symbol;
        token.clearToken();
        do {
            if (token.readChar() == -1) {
                symbol = EOF;
                return symbol;
            }
            if (isRet(token.getCh())) {
                token.nextLine();
            }
        } while (isBlank(token.getCh()));
        if (isLetterOr_(token.getCh())) {
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
            symbol = INTCON;
        } else if (isQuota(token.getCh())) {
            token.catToken();
            token.readChar();
            while (!isQuota(token.getCh())) {
                token.catToken();
                token.readChar();
            }
            token.catToken();
            symbol = STRCON;
        } else if (isExclam(token.getCh())) {
            token.catToken();
            token.readChar();
            if (isEqual(token.getCh())) {
                token.catToken();
            } else {
                token.retract();
            }
            if (token.getToken().equals("!=")) {
                symbol = NEQ;
            } else if (token.getToken().equals("!")) {
                symbol = NOT;
            } else {
                symbol = ERROR;
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
                symbol = AND;
            } else {
                symbol = ERROR;
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
                symbol = OR;
            } else {
                symbol = ERROR;
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
                symbol = EQL;
            } else if (token.getToken().equals("=")) {
                symbol = ASSIGN;
            } else {
                symbol = ERROR;
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
                symbol = LEQ;
            } else if (token.getToken().equals("<")) {
                symbol = LSS;
            } else {
                symbol = ERROR;
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
                symbol = GEQ;
            } else if (token.getToken().equals(">")) {
                symbol = GRE;
            } else {
                symbol = ERROR;
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
                symbol = NOTE;
            } else if (isDiv(token.getCh())) {
                while (!isRet(token.getCh())) {
                    token.catToken();
                    token.readChar();
                }
                token.nextLine();
                symbol = NOTE;
            } else {
                token.retract();
                symbol = DIV;
            }
        } else {
            symbol = ERROR;
        }
        return symbol;
    }

    private boolean isBlank(char ch) {
        return ch == ' ' || ch == '\n' || ch == '\t' || ch == '\r' || ch == '\f';
    }

    private boolean isRet(char ch) {
        return ch == '\n';
    }

    private boolean isLetterOr_(char ch) {
        return Character.isLetter(ch) || ch == '_';
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
        for (Token t : values()) {
            int ordinal = t.ordinal();
            if ((ordinal >= 3 && ordinal <= 9) || (ordinal >= 13 && ordinal <= 16) || ordinal == 19) {
                if (t.getName().equals(token)) {
                    return t;
                }
            }
        }
        return IDENFR;
    }
}
