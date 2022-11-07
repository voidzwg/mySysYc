package IR;

import Error.CompileErrorException;
import IR.Types.ArrayType;
import IR.Types.FunctionType;
import IR.Types.Type;
import IR.Values.*;
import IR.Values.Instructions.BinaryInstruction;
import IR.Values.Instructions.Instruction;
import IR.Values.Instructions.Mem.AllocaInstruction;
import IR.Values.Instructions.Mem.GEPInstruction;
import IR.Values.Instructions.Mem.LoadInstruction;
import IR.Values.Instructions.Mem.StoreInstruction;
import IR.Values.Instructions.Operator;
import IR.Values.Instructions.Terminator.CallInstruction;
import IR.Values.Instructions.Terminator.RetInstruction;
import frontend.Parser;
import frontend.SyntaxParsingTree.Number;
import frontend.SyntaxParsingTree.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static Error.CompileErrorException.error;
import static Error.Error.*;
import static IR.Types.IntegerType.i32;
import static IR.Types.VoidType.Void;
import static IR.Values.ConstantInteger.constantZero;
import static IR.Values.Instructions.Operator.*;

public class Visitor {
    private SyntaxParsingTree syntaxParsingTree;
    private final Parser parser;
    private final Module module;
    private Value cv = null, tv = null;  // current value, temp value
    private Function cf = null;  // current function
    private BasicBlock cbb = null;  // current basic block
    private Operator co = null;  // current operator
    private HashMap<String, Value> chm = null;  // current hashmap
    private ArrayList<Value> cfrp = null;  // current function real parameters
    private Integer ci;  // length of array
    private Queue<Integer> arrLength = new LinkedList<>();
    private ConstantArray ca = null;  // current array

    public Visitor(File fr) throws IOException {
        parser = new Parser(fr);
        module = new Module();
        syntaxParsingTree = parser.build();
    }

    public SyntaxParsingTree getSyntaxParsingTree() {
        return syntaxParsingTree;
    }

    public Module getModule() {
        return module;
    }

    public ConstantInteger calculateConstInteger(ConstantInteger a, Operator op, ConstantInteger b) {
        if (op == null) {
            return null;
        }
        switch (op) {
            case PLUS:
                return new ConstantInteger(a.getValue() + b.getValue());
            case MINU:
                return new ConstantInteger(a.getValue() - b.getValue());
            case MULT:
                return new ConstantInteger(a.getValue() * b.getValue());
            case DIV:
                return new ConstantInteger(a.getValue() / b.getValue());
            case MOD:
                return new ConstantInteger(a.getValue() % b.getValue());
        }
        return null;
    }

    private Value find(String s, int line, int col) {
        Value value = module.find(s);
        if (value == null) {
            error(Undeclared, line, col);
        }
        return value;
    }

    private Value find(String s, boolean isLib, int line, int col) {
        if (!isLib) {
            return find(s, line, col);
        }
        Value value = module.find(s, true);
        if (value == null) {
            error(Undeclared, line, col);
        }
        return value;
    }

//    private void initArray(ConstantArray array, Instruction ptr) {
//        for (int i = 0; i < array.getCapacity(); i++) {
//            Value v = array.getValues().get(i);
//            ArrayList<Value> initial = new ArrayList<>();
//            initial.add(constantZero);
//            initial.add(new ConstantInteger(i));
//            GEPInstruction gep = new GEPInstruction(cbb, ptr, initial);
//            if (v instanceof ConstantInteger) {
//                ConstantInteger integer = (ConstantInteger) v;
//                new StoreInstruction(cbb, gep, integer);
//            } else if (v instanceof AllocaInstruction) {
//                AllocaInstruction alloca = (AllocaInstruction) v;
//                cv = new LoadInstruction(cbb, alloca);
//                new StoreInstruction(cbb, gep, cv);
//            } else if (v instanceof ConstantArray) {
//                ConstantArray nextArray = (ConstantArray) v;
//                ArrayList<Object> contents = ((ConstantArray) v).getContents();
//
//                initArray(nextArray, gep);
//            }
//        }
//    }

    private void initArray(ConstantArray array, Instruction ptr) {
        ArrayList<Value> initial = new ArrayList<>();
        initArray(array, ptr, initial, 0);
    }

    private void initArray(ConstantArray array, Instruction ptr, ArrayList<Value> initial, int index) {
        initial.add(new ConstantInteger(index));
        for (int i = 0; i < array.getCapacity(); i++) {
            Value v = array.getValues().get(i);
            if (v instanceof ConstantInteger) {
                ConstantInteger integer = (ConstantInteger) v;
                initial.add(new ConstantInteger(i));
                GEPInstruction gep = new GEPInstruction(cbb, ptr, initial);
                new StoreInstruction(cbb, gep, integer);
                initial.subList(initial.size() - 1, initial.size()).clear();
            } else if (v instanceof AllocaInstruction) {
                AllocaInstruction alloca = (AllocaInstruction) v;
                initial.add(new ConstantInteger(i));
                GEPInstruction gep = new GEPInstruction(cbb, ptr, initial);
                cv = new LoadInstruction(cbb, alloca);
                new StoreInstruction(cbb, gep, cv);
                initial.subList(initial.size() - 1, initial.size()).clear();
            } else if (v instanceof ConstantArray) {
                ConstantArray nextArray = (ConstantArray) v;
                int remain = initial.size();
                initArray(nextArray, ptr, initial, i);
                if (initial.size() > remain) {
                    initial.subList(remain, initial.size()).clear();
                }
            }
        }
    }

    private boolean isConstantValue() {
        return (cv instanceof ConstantInteger) ||
                (cv instanceof GlobalVariable && ((GlobalVariable) cv).isConstant()) ||
                (cv instanceof LoadInstruction && ((AllocaInstruction) ((LoadInstruction) cv).getOperands().get(0)).isConstant());
    }

    private boolean isConstantValue(Value v) {
        return (v instanceof ConstantInteger) ||
                ((v instanceof GlobalVariable) && ((GlobalVariable) v).isConstant()) ||
                (v instanceof LoadInstruction && ((AllocaInstruction) ((LoadInstruction) v).getOperands().get(0)).isConstant());
    }

    private void putCVIntoCHM(String name, int line, int col) {
        if (chm.putIfAbsent(name, cv) != null) {
            error(Redeclaration, line, col);
        }
    }

    private void changeCHM(String name, Value v) {
        if (chm.containsKey(name)) {
            chm.replace(name, v);
        } else {
            chm.put(name, v);
        }
    }

    public void visit() throws CompileErrorException {
        visitCompUnit((CompUnit) syntaxParsingTree);
    }

    public void visitCompUnit(CompUnit compUnitTree) throws CompileErrorException {
        chm = new HashMap<>();
        Value tmpValue;
        tmpValue = new Function(Void, "");
        tmpValue.setName("printf");
        ((FunctionType) tmpValue.getType()).addFuncFParam(Void); // TODO
        chm.put("printf", tmpValue);
        tmpValue = new Function(i32, "");
        tmpValue.setName("getint");
        chm.put("getint", tmpValue);
        module.pushSymbolTable(chm);
        for (Decl decl : compUnitTree.getDecls()) {
            visitDecl(decl);
        }
        for (FuncDef funcDef : compUnitTree.getFuncDefs()) {
            visitFuncDef(funcDef);
            module.addFunction(cf);
            chm.put(cf.getName(), cf);
        }
        visitMainFuncDef(compUnitTree.getMainFuncDef());
        module.addFunction(cf);
        chm.put(cf.getName(), cf);
        module.popSymbolTable();
        chm = module.topSymbolTable();
    }

    public void visitMainFuncDef(MainFuncDef mainFuncDef) throws CompileErrorException {
        // 创建一个新的函数实体，向函数的子模块传递这个实体的引用
        cf = new Function(i32, "main");
        // 为这个函数实体创建一张符号表，向函数的子模块传递这个符号表的引用
        chm = new HashMap<>();
        // 为这个函数实体创建入口基本块，向基本块的子模块传递这个引用
        cbb = new BasicBlock(cf, true);
        // 将符号表插入模型的栈式符号表中
        module.pushSymbolTable(chm);
        // 处理函数的函数体部分
        visitBlock(mainFuncDef.getBlock());
        // 重命名寄存器
        cf.reorder();
        // 将符号表从栈式符号表中弹出
        module.popSymbolTable();
        // 将当前符号表置为栈顶符号表
        chm = module.topSymbolTable();
        cbb = null;
    }

    public void visitFuncDef(FuncDef funcDef) throws CompileErrorException {
        // 函数的返回类型，int32或者void的一个引用
        Type returnType = Objects.equals(funcDef.getFuncType().getType(), "int") ? i32 : Void;
        // 创建一个新的函数实体，向函数的子模块传递这个实体的引用
        cf = new Function(returnType, funcDef.getIdent());
        // 为这个函数实体创建一张符号表，向函数的子模块传递这个符号表的引用
        chm = new HashMap<>();
        // 为这个函数实体创建入口基本块，向基本块的子模块传递这个引用
        cbb = new BasicBlock(cf, true);
        // 处理函数的形参
        if (funcDef.getFuncFParams() != null) {
            // 函数的返回类型
            FunctionType functionType = (FunctionType) cf.getType();
            for (FuncFParam funcFParam : funcDef.getFuncFParams()) {
                // 分配一个形参实体
                Type type = i32;
                cv = new Parameter(type, cf);
                // 将形参类型的实体插入函数类型实体的形参类型表中
                functionType.addFuncFParam(type);
                // 将形参实体插入函数的形参表中
                cf.addParameter((Parameter) cv);
                // 将形参插入符号表中
                AllocaInstruction a = new AllocaInstruction(cbb, type, false);
                new StoreInstruction(cbb, a, cv);
                cv = a;
                putCVIntoCHM("%" + funcFParam.getIdent(), funcFParam.getLine(), funcFParam.getCol());
            }
        }
        // 将符号表插入模型的栈式符号表中
        module.pushSymbolTable(chm);
        // 处理函数的函数体部分
        visitBlock(funcDef.getBlock());
        // 重命名寄存器
        cf.reorder();
        // 将符号表从栈式符号表中弹出
        module.popSymbolTable();
        // 将当前符号表置为栈顶符号表
        chm = module.topSymbolTable();
        cbb = null;
    }

    public void visitBlock(Block block) throws CompileErrorException {
        if (block == null) {
            return;
        }
        cf.addBasicBlock(cbb);
        for (BlockItem blockItem : block.getBlockItem()) {
            visitBlockItem(blockItem);
        }
    }

    public void visitBlockItem(BlockItem blockItem) throws CompileErrorException {
        if (blockItem.getDecl() != null) {
            visitDecl(blockItem.getDecl());
        } else if (blockItem.getStmt() != null) {
            visitStmt(blockItem.getStmt());
        }
    }

    public void visitStmt(Stmt stmt) {
        LVal lVal = stmt.getlVal();
        Exp exp = stmt.getExp();
        switch (stmt.getType()) {
            case 0:
                break;
            case 1:
                visitExp(stmt.getExp());
                break;
            case 2:
                // TODO: 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
                break;
            case 3:
                // TODO: 'while' '(' Cond ')' Stmt
                break;
            case 4:
                // TODO: 'break' ';'
                break;
            case 5:
                // TODO: 'continue' ';'
                break;
            case 6:
                if (exp == null) {
                    new RetInstruction(cbb);
                } else {
                    visitExp(exp);
                    new RetInstruction(cbb, cv);
                }
                break;
            case 7:
                // 'printf' '(' FormatString { ',' Exp } ')' ';'
                String fStr = stmt.getFormatString();
                ArrayList<Value> expList = new ArrayList<>();
                for (Exp e : stmt.getExps()) {
                    visitExp(e);
                    expList.add(cv);
                }
                cv = find("printf", true, stmt.getLine(), stmt.getCol());
                cv = new CallInstruction(cbb, (Function) cv, fStr, expList);
                break;
            case 8:
                // LVal '=' Exp ';'
                visitLVal(lVal);
                if (isConstantValue()) {
                    error(ChangeConstValue, lVal.getLine(), lVal.getCol());
                }
                Value leftValue = cv;
                //System.out.println("left Value: " + leftValue);
                visitExp(exp);
                new StoreInstruction(cbb, leftValue, cv);
                break;
            case 9:
                // LVal '=' 'getint' '(' ')' ';'
                visitLVal(lVal);
                if (isConstantValue()) {
                    error(ChangeConstValue, lVal.getLine(), lVal.getCol());
                }
                tv = cv;
                cv = find("getint", true, lVal.getLine(), lVal.getCol());
                cv = new CallInstruction(cbb, (Function) cv, new ArrayList<>());
                break;
            case 10:
                // TODO: Block
                break;
            default:
                error(UndefinedError, stmt.getLine(), stmt.getCol());
                break;
        }
    }

    public void visitDecl(Decl decl) throws CompileErrorException {
        if (decl.getConstDecl() != null) {
            visitConstDecl(decl.getConstDecl());
        } else {
            visitVarDecl(decl.getVarDecl());
        }
    }

    public void visitConstDecl(ConstDecl constDecl) throws CompileErrorException {
        for (ConstDef constDef : constDecl.getConstDefs()) {
            visitConstDef(constDef);
            putCVIntoCHM(cv.getName(), constDef.getLine(), constDef.getCol());
        }
    }

    public void visitConstDef(ConstDef constDef) throws CompileErrorException {
        int mode = constDef.getMode();
        if (mode != 0) {
            visitConstExp(constDef.getConstExps().get(0));
            ca = new ConstantArray(i32, ((ConstantInteger) cv).getValue());
            if (mode == 2) {
                visitConstExp(constDef.getConstExps().get(1));
                int len = ((ConstantInteger) cv).getValue();
                Value arr = new ConstantArray(i32, len);;
                for (int i = 0; i < ca.getCapacity(); i++) {
                    ca.addValue(arr);
                    arr = new ConstantArray(i32, len);
                }
                ca.setElementType(arr.getType());
                ca.newLevel();
            }
        } else {
            ca = null;
        }
        ci = -1;
        visitConstInitVal(constDef.getConstInitVal());
        if (cbb == null) {
            cv = new GlobalVariable(constDef.getIdent(), cv, true);
            module.addGlobalVariable((GlobalVariable) cv);
        } else {
            if (!(cv instanceof ConstantInteger)) {
                tv = cv;
                cv = new AllocaInstruction(cbb, cv.getType(), true);
                new StoreInstruction(cbb, cv, tv);
            }
            cv.setName("%" + constDef.getIdent());
        }
    }

    public void visitConstInitVal(ConstInitVal constInitVal) throws CompileErrorException {
        if (constInitVal == null) {
            cv = constantZero;
            return;
        }
        if (constInitVal.getConstExp() == null && constInitVal.getConstInitVal().size() == 0) {
            error(UninitializedConstant, constInitVal.getLine(), constInitVal.getCol());
        }
        if (constInitVal.getConstExp() != null) {
            visitConstExp(constInitVal.getConstExp());
            if (ca != null) {
                ci++;
            }
        } else {
            for (ConstInitVal constInitVal1 : constInitVal.getConstInitVal()) {
                visitConstInitVal(constInitVal1);
                if (cv instanceof ConstantInteger) {
                    ca.insert(cv, ci);
                }
            }
            cv = ca;
        }
    }

    public void visitVarDecl(VarDecl varDecl) throws CompileErrorException {
        for (VarDef varDef : varDecl.getVarDefs()) {
            visitVarDef(varDef);
            putCVIntoCHM(cv.getName(), varDecl.getLine(), varDef.getCol());
        }
    }

    public void visitVarDef(VarDef varDef) throws CompileErrorException {
        boolean flag = true;
        int mode = varDef.getMode();
        if (mode == 0) {
            ca = null;
            if (varDef.getInitVal() != null) {
                visitInitVal(varDef.getInitVal());
            } else {
                flag = false;
                cv = constantZero;
            }
        } else {
            visitConstExp(varDef.getConstExps().get(0));
            ca = new ConstantArray(i32, ((ConstantInteger) cv).getValue());
            if (mode == 2) {
                visitConstExp(varDef.getConstExps().get(1));
                int len = ((ConstantInteger) cv).getValue();
                Value arr = new ConstantArray(i32, len);;
                for (int i = 0; i < ca.getCapacity(); i++) {
                    ca.addValue(arr);
                    arr = new ConstantArray(i32, len);
                }
                ca.setElementType(arr.getType());
                ca.newLevel();
            }
            if (varDef.getInitVal() != null) {
                ci = -1;
                visitInitVal(varDef.getInitVal());
            } else {
                ca.setNeedInit(true);
                cv = ca;
            }
        }
        if (cbb == null) {
            cv = new GlobalVariable(varDef.getIdent(), cv, false);
            module.addGlobalVariable((GlobalVariable) cv);
        } else {
            tv = cv;
            cv = new AllocaInstruction(cbb, cv.getType(), false);
            if (flag) {
                if (((AllocaInstruction) cv).getAllocated() instanceof ArrayType) {
                    ConstantArray array = (ConstantArray) tv;
                    if (!array.isNeedInit()) {
                        initArray(array, (Instruction) cv);
                    }
                } else {
                    new StoreInstruction(cbb, cv, tv);
                }
            }
            cv.setName("%" + varDef.getIdent());
        }
    }

    public void visitInitVal(InitVal initVal) throws CompileErrorException {
        if (initVal.getExp() != null) {
            visitExp(initVal.getExp());
            if (ca != null) {
                ci++;
            }
        } else {
            for (InitVal initVal1 : initVal.getInitVal()) {
                visitInitVal(initVal1);
                if (!(cv instanceof ConstantArray)) {
                    ca.insert(cv, ci);
                }
            }
            cv = ca;
        }
    }

    public void visitExp(Exp exp) throws CompileErrorException {
        visitAddExp(exp.getAddExp());
        if ((cv instanceof GlobalVariable) && ((GlobalVariable) cv).isConstant()) {
            cv = (ConstantInteger) ((GlobalVariable) cv).getValue();
        }
    }

    public void visitNumber(Number number) {
        cv = new ConstantInteger(number.getNumber());
    }

    public void visitLVal(LVal lVal) throws CompileErrorException {
        // lVal instanceof ConstantInteger, GEPInstruction or AllocaInstruction
        int mode = lVal.getMode();
        cv = find(lVal.getIdent(), lVal.getLine(), lVal.getCol());
        if (cv instanceof AllocaInstruction) {
            // lVal instanceof AllocaInstruction or GEPInstruction
            if (mode != 0) {
                AllocaInstruction alloca = (AllocaInstruction) cv;
                ArrayList<Value> initial = new ArrayList<>();
                initial.add(constantZero);
                for (Exp exp : lVal.getExps()) {
                    visitExp(exp);
                    initial.add(cv);
                }
                cv = new GEPInstruction(cbb, alloca, initial);
            }
        } else if (cv instanceof GlobalVariable) {
            // lVal instanceof ConstantInteger or GEPInstruction
            GlobalVariable gVal = (GlobalVariable) cv;
            if (!gVal.isConstant()) {
                // lVal instanceof ConstantInteger or GEPInstruction
                if (mode != 0) {
                    ArrayList<Value> initial = new ArrayList<>();
                    initial.add(constantZero);
                    for (Exp exp : lVal.getExps()) {
                        visitExp(exp);
                        initial.add(cv);
                    }
                    cv = new GEPInstruction(cbb, gVal, initial);
                } else {
                    cv = ((GlobalVariable) cv).getValue();
                }
            } else {
                // lVal instanceof ConstantInteger or GEPInstruction
                Value v = gVal.getValue();
                if (v instanceof ConstantInteger) {
                    cv = v;
                } else if (v instanceof ConstantArray) {
                    ArrayList<Value> initial = new ArrayList<>();
                    initial.add(constantZero);
                    for (Exp exp : lVal.getExps()) {
                        visitExp(exp);
                        initial.add(cv);
                    }
                    ConstantInteger integer = ((ConstantArray) v).getValueAt((ConstantArray) v, (ArrayList<Value>) initial.subList(1, initial.size()));
                    cv = integer != null ? integer : new GEPInstruction(cbb, gVal, initial);
                }
            }
        }
    }

    public void visitPrimaryExp(PrimaryExp primaryExp) throws CompileErrorException {
        // primaryExp instanceof AllocaInstruction, GEPInstruction, ConstantInteger or CallInstruction
        if (primaryExp.getNumber() != null) {
            visitNumber(primaryExp.getNumber());
        } else if (primaryExp.getExp() != null) {
            visitExp(primaryExp.getExp());
        } else {
            visitLVal(primaryExp.getlVal());
            if (cv instanceof GEPInstruction || cv instanceof AllocaInstruction) {
                cv = new LoadInstruction(cbb, cv);
            }
        }
    }

    public void visitUnaryExp(UnaryExp unaryExp) throws CompileErrorException {
        // unaryExp instanceof AllocaInstruction, GEPInstruction, ConstantInteger or CallInstruction
        if (unaryExp.getPrimaryExp() != null) {
            visitPrimaryExp(unaryExp.getPrimaryExp());
        } else if (unaryExp.getUnaryOp() != null) {
            co = getOp(unaryExp.getUnaryOp().getOp());
            visitUnaryExp(unaryExp.getUnaryExp());
            if (co == NOT) {
                cv = new BinaryInstruction(cbb, EQL, cv, constantZero);
            } else if (co == MINU) {
                if (cv instanceof ConstantInteger) {
                    ((ConstantInteger) cv).setValue(-((ConstantInteger) cv).getValue());
                } else {
                    cv = new BinaryInstruction(cbb, co, constantZero, cv);
                }
            }
            co = null;
        } else {
            if (unaryExp.getFuncRParams() != null) {
                visitFuncRParams(unaryExp.getFuncRParams());
            } else {
                cfrp = new ArrayList<>();
            }
            Value value = find(unaryExp.getIdent(), unaryExp.getLine(), unaryExp.getCol());
            if (!(value instanceof Function)) {
                error(UndefinedError, unaryExp.getLine(), unaryExp.getCol());
            } else {
                Function f = (Function) value;
                f.match(cfrp, unaryExp.getLine(), unaryExp.getCol());
                cv = new CallInstruction(cbb, f, cfrp);
            }
        }
    }

    public void visitFuncRParams(FuncRParams funcRParams) throws CompileErrorException {
        cfrp = new ArrayList<>();
        for (Exp e : funcRParams.getExps()) {
            visitExp(e);
            cfrp.add(cv);
        }
    }

    public void visitMulExp(MulExp mulExp) throws CompileErrorException {
        visitUnaryExp(mulExp.getUnaryExp());
        Value leftValue = cv;
        if (mulExp.getMulExp() != null) {
            if (co == MULT || co == DIV || co == MOD) {
                // 表明该乘法式分析模块的父节点也是乘法式分析模块
                // 为遵守乘法式的运算顺序
                // 应该先将该节点的左兄弟节点与左操作数进行计算
                // 结果作为该节点的左操作数
                // 此时，tv等于左兄弟节点，cv是刚刚计算出的左操作数
                if (tv instanceof ConstantInteger && cv instanceof ConstantInteger) {
                    cv = calculateConstInteger((ConstantInteger) tv, co, (ConstantInteger) cv);
                } else {
                    cv = new BinaryInstruction(cbb, co, tv, cv);
                }
            }
            tv = cv;
            visitMulExp(mulExp.getMulExp());
            co = getOp(mulExp.getOp());
            if (isConstantValue(leftValue) && isConstantValue()) {
                ConstantInteger left, right;
                if (leftValue instanceof ConstantInteger) {
                    left = (ConstantInteger) leftValue;
                } else {
                    left = (ConstantInteger) ((GlobalVariable) leftValue).getValue();
                }
                if (cv instanceof ConstantInteger) {
                    right = (ConstantInteger) cv;
                } else {
                    right = (ConstantInteger) ((GlobalVariable) cv).getValue();
                }
                cv = calculateConstInteger(left, co, right);
            } else {
                cv = new BinaryInstruction(cbb, co, leftValue, cv);
            }
            co = null;
        }
    }

    public void visitAddExp(AddExp addExp) throws CompileErrorException {
        visitMulExp(addExp.getMulExp());
        Value leftValue = cv;
        if (addExp.getAddExp() != null) {
            if (co == PLUS || co == MINU) {
                // 表明该加法式分析模块的父节点也是加法式分析模块
                // 为遵守加法式的运算顺序
                // 应该先将该节点的左兄弟节点与左操作数进行计算
                // 结果作为该节点的左操作数
                // 此时，tv等于左兄弟节点，cv是刚刚计算出的左操作数
                if (tv instanceof ConstantInteger && cv instanceof ConstantInteger) {
                    cv = calculateConstInteger((ConstantInteger) tv, co, (ConstantInteger) cv);
                } else {
                    cv = new BinaryInstruction(cbb, co, tv, cv);
                }
            }
            tv = cv;
            visitAddExp(addExp.getAddExp());
            co = getOp(addExp.getOp());
            if (isConstantValue(leftValue) && isConstantValue()) {
                ConstantInteger left, right;
                if (leftValue instanceof ConstantInteger) {
                    left = (ConstantInteger) leftValue;
                } else {
                    left = (ConstantInteger) ((GlobalVariable) leftValue).getValue();
                }
                if (cv instanceof ConstantInteger) {
                    right = (ConstantInteger) cv;
                } else {
                    right = (ConstantInteger) ((GlobalVariable) cv).getValue();
                }
                cv = calculateConstInteger(left, co, right);
            } else {
                cv = new BinaryInstruction(cbb, co, leftValue, cv);
            }
            co = null;
        }
    }

    public void visitConstExp(ConstExp constExp) throws CompileErrorException {
        visitAddExp(constExp.getAddExp());
        if (!isConstantValue()) {
            error(InitializationFailed, constExp.getLine(), constExp.getCol());
        }
        if ((cv instanceof GlobalVariable) && ((GlobalVariable) cv).isConstant()) {
            cv = ((GlobalVariable) cv).getValue();
        }
    }

    public void close() throws IOException {
        parser.close();
    }

    @Override
    public String toString() {
        return module.toString();
    }

    public String printSymTbl() {
        return module.printSymbolTable();
    }

    public String printSyntaxTree() {
        return syntaxParsingTree.toString();
    }
}
