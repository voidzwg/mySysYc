package frontend.SyntaxParsingTree;

import frontend.State;
import java.util.ArrayList;

public class CompUnit extends SyntaxParsingTree {
    private final ArrayList<Decl> decls;
    private final ArrayList<FuncDef> funcDefs;
    private MainFuncDef mainFuncDef;

    public CompUnit() {
        label = State.CompUnit.toLabel();
        decls = new ArrayList<>();
        funcDefs = new ArrayList<>();
        mainFuncDef = null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Decl decl : decls) {
            builder.append(decl.toString());
        }
        for (FuncDef funcDef : funcDefs) {
            builder.append(funcDef.toString());
        }
        builder.append(mainFuncDef.toString());
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

    public ArrayList<Decl> getDecls() {
        return decls;
    }

    public ArrayList<FuncDef> getFuncDefs() {
        return funcDefs;
    }

    public MainFuncDef getMainFuncDef() {
        return mainFuncDef;
    }
}
