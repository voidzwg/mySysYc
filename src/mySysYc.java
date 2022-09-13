import frontend.*;

import java.io.*;
import java.io.Reader;

public class mySysYc {
    char ch;
    public static void main(String[] args) {
        mySysYc test = new mySysYc();
        test.test();
        System.out.println("Hello, my compiler!");
    }

    void test() {
        File fr = new File("D:\\BUAA\\大三上\\编译技术\\mySysYc\\test\\1.文法解读\\files\\testfile1.txt");
        Lexer lexer = new Lexer(fr);
        while (!(lexer.isEmpty())) {
            System.out.print(lexer.next());
            System.out.print(' ');
            System.out.println(lexer.getToken());
        }
    }
}