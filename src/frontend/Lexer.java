package frontend;

import java.io.File;
import java.io.FileReader;

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

    public Token next() {
        token.clearToken();
        while (isBlank(token.getCh()))
            token.readChar();
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
            symbol = Token.INTCON;
        } else {
            symbol = Token.PLUS;
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
        return ch == ' ' || ch == '\n' || ch == '\t';
    }

    private boolean isIdent(char ch) {
        return Character.isLetterOrDigit(ch) || ch == '_';
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
