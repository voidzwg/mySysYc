package frontend.CompTree;

import frontend.State;
import java.util.ArrayList;

public class CompUnit extends CompTree {
    private final ArrayList<Decl> decls;
    private final ArrayList<FuncDef> funcDefs;
    private MainFuncDef mainFuncDef;

    public CompUnit() {
        label = State.CompUnit.toLabel();
        decls = new ArrayList<>();
        funcDefs = new ArrayList<>();
        mainFuncDef = null;
    }

    public String print() {
        StringBuilder builder = new StringBuilder();
        for (Decl decl : decls) {
            builder.append(decl.print());
        }
        for (FuncDef funcDef : funcDefs) {
            builder.append(funcDef.print());
        }
        builder.append(mainFuncDef.print());
        builder.append(label).append("\n");
        return builder.toString();
    }

    public void addDecl(Decl decl) {
        decls.add(decl);
    }

    public void addFuncDefs(FuncDef funcDef) {
        funcDefs.add(funcDef);
    }

    public void setMainFuncDef(MainFuncDef mainFuncDef) {
        this.mainFuncDef = mainFuncDef;
    }

    public Decl visitDecls(int i) {
        if (i >= decls.size()) {
            return null;
        }
        return decls.get(i);
    }

    public FuncDef visitFuncDefs(int i) {
        if (i >= funcDefs.size()) {
            return null;
        }
        return funcDefs.get(i);
    }

    public MainFuncDef getMainFuncDef() {
        return mainFuncDef;
    }
}
