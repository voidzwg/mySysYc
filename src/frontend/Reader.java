package frontend;

import java.io.*;

public class Reader {
    private int num;
    private String token;
    private int head = -1;
    private char ch;
    private BufferedInputStream reader;

    Reader(File f) {
        clearToken();
        try {
            this.reader = new BufferedInputStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            this.reader = null;
        }
    }

    protected char getCh() {
        return ch;
    }

    protected String getToken() {
        return token;
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

    protected int readChar(){
        int ret = -1;
        try {
            ret = reader.read();
            reader.mark(10);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        if (ret == -1) {
            return ret;
        } else {
            ch = (char) ret;
            return 0;
        }
    }

    protected void catToken() {
        token += ch;
        head++;
    }

    protected void retract() {
        try {
            reader.reset();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    protected void clearToken() {
        token = "";
        head = -1;
    }
}