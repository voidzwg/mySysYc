import frontend.*;

import java.io.*;
import java.io.Reader;

public class mySysYc {
    char ch;
    public static void main(String[] args) {
        mySysYc test = new mySysYc();
        try {
            test.test();
        } catch (IOException e) {
            System.out.println("Caught IOException in mySysYc");
            e.printStackTrace();
        }
        //System.out.println("Hello, my compiler!");
    }

    void test() throws IOException{
        File fr = new File("./test/2.词法分析/testfile.txt");
        File fw = new File("./test/2.词法分析/output.txt");
        if (!fw.exists()) {
            boolean ret = fw.createNewFile();
        }
        FileWriter out= new FileWriter(fw);
        Lexer lexer = new Lexer(fr);
        while (true) {
            Token token = lexer.next();
            if (token.equals(Token.ERROR)) {
                break;
            }
            String result = token + " " + lexer.getToken() + "\n";
            System.out.print(result);
            if (token.equals(Token.NOTE)) {
                continue;
            }
            out.append(result);
        }
        out.close();
    }
}