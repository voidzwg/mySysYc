import Error.CompileErrorException;
import IR.Values.ConstantInteger;
import IR.Values.Instructions.Operator;
import IR.Visitor;
import backend.CodeGenerate;
import frontend.Lexer;
import frontend.Parser;
import frontend.Token;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static java.util.Collections.sort;
import static IR.Visitor.LLVM_VERSION;

public class Compiler {
    char ch;
    String frURL = "./test/5.代码生成/testfile.c";
    String fwURL = "./test/5.代码生成/mips.txt";
    String feURL = "./test/5.代码生成/error.txt";
    public static void main(String[] args) {
        Compiler test = new Compiler();
        try {
            //test.test2();
            //test.submit2();
            //test.test3();
            //test.submit3();
            //test.test4();
            //test.submit4();
            //test.test5();
            test.submit5();
            //test.tete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void tete() throws IOException {
        ConstantInteger a = new ConstantInteger(22);
        ConstantInteger b = new ConstantInteger(22);
        ConstantInteger c = new ConstantInteger(33);
        System.out.println(a.equals(b));
        System.out.println(b.equals(c));
        System.out.println(c.equals(a));
        ConstantInteger d = new ConstantInteger(22, 8);
        System.out.println(d.equals(a));
        ConstantInteger e = new ConstantInteger(-1, 1);
        System.out.println(e.equals(ConstantInteger.True));
    }

    void submit5() throws IOException {
        File fr = new File("testfile.txt");
        File fw = new File("mips.txt");
        LLVM_VERSION = 8;
        Visitor visitor;
        CodeGenerate generator;
        while (true) {
            try (FileWriter out = new FileWriter(fw)) {
                visitor =  new Visitor(fr);
                generator = new CodeGenerate(visitor.getModule());
                //System.out.println("Syntax Tree:\n" + visitor.printSyntaxTree());
                visitor.visit();
                generator.generateMIPS();
                String str = generator.toString();
                //System.out.println(str);
                out.append(str);
                break;
            } catch (CompileErrorException e) {
                System.out.println("Caught a compile Error at " + e.getPos() + ": " + e.getMessage());
            }
        }
        visitor.close();
    }

    void test5() throws IOException {
        File fr = new File(frURL);
        File fw = new File(fwURL);
        File fw2 = new File("./test/5.代码生成/llvm_ir.ll");
        File fe = new File(feURL);
        LLVM_VERSION = 8;
        FileWriter fwe = new FileWriter(fe);
        Visitor visitor;
        CodeGenerate generator;
        ArrayList<CompileErrorException> eList = new ArrayList<>();
        while (true) {
            try (FileWriter out = new FileWriter(fw)) {
                FileWriter out2 = new FileWriter(fw2);
                visitor =  new Visitor(fr);
                generator = new CodeGenerate(visitor.getModule());
                //System.out.println("Syntax Tree:\n" + visitor.printSyntaxTree());
                visitor.visit();
                generator.generateMIPS();
                String str = generator.toString();
                //System.out.println(str);
                out.append(str);
                out2.append(visitor.toString());
                out2.close();
                break;
            } catch (CompileErrorException e) {
                System.out.println("Caught a compile Error at " + e.getPos() + ": " + e.getMessage());
                eList.add(e);
            }
        }
        sort(eList);
        for (CompileErrorException e : eList) {
            fwe.append(String.valueOf(e.getLine())).append(" ").append(e.getECode()).append("\n");
        }
        visitor.close();
        fwe.close();
    }

    void test4() throws IOException {
        File fr = new File(frURL);
        File fw = new File(fwURL);
        File fe = new File(feURL);
        LLVM_VERSION = 8;
        FileWriter fwe = new FileWriter(fe);
        Visitor visitor;
        ArrayList<CompileErrorException> eList = new ArrayList<>();
        while (true) {
            try (FileWriter out = new FileWriter(fw)) {
                visitor =  new Visitor(fr);
                //System.out.println("Syntax Tree:\n" + visitor.printSyntaxTree());
                visitor.visit();
                String str = visitor.toString();
                //System.out.println(str);
                out.append(str);
                //System.out.println("\nSymbol Table:\n" + visitor.printSymTbl());
                break;
            } catch (CompileErrorException e) {
                System.out.println("Caught a compile Error at " + e.getPos() + ": " + e.getMessage());
                eList.add(e);
            }
        }
        sort(eList);
        for (CompileErrorException e : eList) {
            fwe.append(String.valueOf(e.getLine())).append(" ").append(e.getECode()).append("\n");
        }
        visitor.close();
        fwe.close();
    }

    void submit4() throws IOException {
        File fr = new File("testfile.txt");
        File fw = new File("llvm_ir.txt");
        LLVM_VERSION = 8;
        Visitor visitor;
        while (true) {
            try (FileWriter out = new FileWriter(fw)) {
                visitor =  new Visitor(fr);
                visitor.visit();
                String str = visitor.toString();
                out.append(str);
                //System.out.println("\nSymbol Table:\n" + visitor.printSymTbl());
                break;
            } catch (CompileErrorException e) {
                System.out.println("Caught a compile Error at " + e.getPos() + ": " + e.getMessage());
            }
        }
        visitor.close();
    }

    void test3() throws IOException {
        File fr = new File(frURL);
        File fw = new File(fwURL);
        LLVM_VERSION = 8;
        Parser parser = new Parser(fr);
        try (FileWriter out = new FileWriter(fw)) {
            String str;
            str = parser.build().toString();
            out.append(str);
        } catch (CompileErrorException e) {
            System.out.println("Caught a Compile Error");
        } finally {
            parser.close();
        }
    }

    void submit3() throws IOException {
        File fr = new File("testfile.txt");
        File fw = new File("output.txt");
        LLVM_VERSION = 6;
        Parser parser = new Parser(fr);
        FileWriter out = new FileWriter(fw);
        String str = "";
        try {
            str = new Parser(fr).build().toString();
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
        LLVM_VERSION = 8;
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
        LLVM_VERSION = 6;
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