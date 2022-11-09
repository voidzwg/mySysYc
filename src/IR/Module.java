package IR;

import IR.Values.Function;
import IR.Values.GlobalVariable;
import IR.Values.Value;
import utils.List;

import java.util.ArrayList;
import java.util.HashMap;

public class Module {
    private final ArrayList<GlobalVariable> globalVariables;
    private final List<Function, Module> functions;
    private final ArrayList<HashMap<String, Value>> symbolTable;
    private final ArrayList<HashMap<String, Value>> poppedSymbolTable;

    public Module() {
        globalVariables = new ArrayList<>();
        functions = new List<>(this);
        symbolTable = new ArrayList<>();
        poppedSymbolTable = new ArrayList<>();
    }

    public ArrayList<HashMap<String, Value>> getSymbolTable() {
        return symbolTable;
    }

    public ArrayList<HashMap<String, Value>> getPoppedSymbolTable() {
        return poppedSymbolTable;
    }

    public void pushSymbolTable(HashMap<String, Value> hashMap) {
        symbolTable.add(hashMap);
    }

    public HashMap<String, Value> popSymbolTable() {
        if (symbolTable.size() == 0) {
            return null;
        }
        poppedSymbolTable.add(symbolTable.get(symbolTable.size() - 1));
        return symbolTable.remove(symbolTable.size() - 1);
    }

    public HashMap<String, Value> topSymbolTable() {
        if (symbolTable.size() == 0) {
            return null;
        }
        return symbolTable.get(symbolTable.size() - 1);
    }

    public Value find(String s, boolean isLib) {
        if (!isLib) {
            return find(s);
        }
        Value v = null;
        for (int i = symbolTable.size() - 1; i >= 0 && v == null; i--) {
            v = symbolTable.get(i).get(s);
        }
        return v;
    }

    public Value find(String s) {
        Value v = null;
        String t = "%" + s;
        for (int i = symbolTable.size() - 1; i >= 0 && v == null; i--) {
            if (i == 0) {
                t = "@" + s;
            }
            v = symbolTable.get(i).get(t);
        }
        return v;
    }

    public ArrayList<GlobalVariable> getGlobalVariables() {
        return globalVariables;
    }

    public void addGlobalVariable(GlobalVariable globalVariable) {
        globalVariables.add(globalVariable);
    }

    public List<Function, Module> getFunctions() {
        return functions;
    }

    public void addFunction(Function function) {
        functions.add(function);
    }

    public String printSymbolTable() {
        StringBuilder builder = new StringBuilder();
        for (int i = poppedSymbolTable.size() - 1; i >= 0; i--) {
            HashMap<String, Value> hashMap = poppedSymbolTable.get(i);
            for (String key : hashMap.keySet()) {
                builder.append(key).append(" ").append(hashMap.get(key).getClass()).append("\n");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("declare void @memset(i32*, i32, i32)\n");
        builder.append("declare i32 @printf(i8*, ...)\n");
        builder.append("declare i32 @getint()\n\n");
        for (GlobalVariable globalVariable : globalVariables) {
            builder.append(globalVariable.toString());
        }
        builder.append("\n");
        for (Function function : functions) {
            builder.append(function.toString()).append("\n");
        }
        return builder.toString();
    }
}
