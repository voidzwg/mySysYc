package frontend;

import java.io.*;

public class Reader {
    private int num, line, col;
    private int markedLine, markedCol;
    private String token;
    private char ch;
    private BufferedInputStream reader;

    Reader(File f) {
        clearToken();
        line = 1;
        col = 1;
        try {
            this.reader = new BufferedInputStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            this.reader = null;
        }
    }

    protected void close() throws IOException {
        reader.close();
    }

    protected char getCh() {
        return ch;
    }

    protected String getToken() {
        return token;
    }

    protected int getLine() {
        return line;
    }

    protected int getCol() {
        return col;
    }

    protected void setNum() {
        try {
            this.num = Integer.parseInt(token);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    protected int getNum() {
        return num;
    }

    private void markPos() {
        markedLine = line;
        markedCol = col;
    }

    private void resetPos() {
        line = markedLine;
        col = markedCol;
    }

    protected int readChar(){
        int ret = -1;
        try {
            reader.mark(10);
            markPos();
            ret = reader.read();
        } catch (java.io.IOException e) {
            System.out.println("Caught IOException in readChar()");
            System.out.println(ret);
            e.printStackTrace();
        }
        if (ret == -1) {
            return ret;
        } else {
            ch = (char) ret;
            if (ch == '\n') {
                line++;
                col = 1;
            } else {
                col++;
            }
            //System.out.println("Already read " + ch);
            return 0;
        }
    }

    protected void catToken() {
        token += ch;
    }

    protected void retract() {
        try {
            reader.reset();
            resetPos();
            //System.out.println("Already reset");
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    protected void clearToken() {
        token = "";
    }
}
