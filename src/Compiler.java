import frontend.*;
import java.io.*;

public class Compiler {
    char ch;
    String frURL = "./test/3.语法分析/testfile.txt";
    String fwURL = "./test/3.语法分析/output.txt";
    public static void main(String[] args) {
        Compiler test = new Compiler();
        try {
            //test.test2();
            //test.submit2();
            //test.test3();
            test.submit3();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("Hello, my compiler!");
    }

    void test3() throws IOException{
        File fr = new File(frURL);
        File fw = new File(fwURL);
        Parser parser = new Parser(fr);
        FileWriter out = new FileWriter(fw);
        String str = "";
        try {
            str = new Parser(fr).build().print();
            out.append(str);
            out.close();
            parser.close();
        } catch (CompileErrorException e) {
            System.out.print(str);
            out.close();
            parser.close();
            e.printStackTrace();
        }
    }

    void submit3() throws IOException{
        File fr = new File("testfile.txt");
        File fw = new File("output.txt");
        Parser parser = new Parser(fr);
        FileWriter out = new FileWriter(fw);
        String str = "";
        try {
            str = new Parser(fr).build().print();
            out.append(str);
            out.close();
            parser.close();
        } catch (CompileErrorException e) {
            System.out.print(str);
            out.close();
            parser.close();
            e.printStackTrace();
        }
    }

    void test2() throws IOException {
        File fr = new File(frURL);
        File fw = new File(fwURL);
        if (!fw.exists()) {
            boolean ret = fw.createNewFile();
        }
        FileWriter out= new FileWriter(fw);
        Lexer lexer = new Lexer(fr);
        while (true) {
            Token token = lexer.next();
            if (token.equals(Token.EOF)) {
                break;
            }
            String result = token + " " + lexer.getToken() + " " + lexer.getLine() + "\n";
            System.out.print(result);
            if (token.equals(Token.NOTE)) {
                continue;
            }
            out.append(result);
        }
        out.close();
        lexer.close();
    }

    void submit2() throws IOException {
        File fr = new File("testfile.txt");
        File fw = new File("output.txt");
        if (!fw.exists()) {
            boolean ret = fw.createNewFile();
        }
        FileWriter out= new FileWriter(fw);
        Lexer lexer = new Lexer(fr);
        while (true) {
            Token token = lexer.next();
            if (token.equals(Token.EOF)) {
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
        lexer.close();
    }
}