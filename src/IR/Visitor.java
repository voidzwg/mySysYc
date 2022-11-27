package IR;

import Error.CompileErrorException;
import IR.Types.*;
import IR.Values.*;
import IR.Values.Instructions.BinaryInstruction;
import IR.Values.Instructions.Cast.ZextInstruction;
import IR.Values.Instructions.IcmpInstruction;
import IR.Values.Instructions.Instruction;
import IR.Values.Instructions.Mem.AllocaInstruction;
import IR.Values.Instructions.Mem.GEPInstruction;
import IR.Values.Instructions.Mem.LoadInstruction;
import IR.Values.Instructions.Mem.StoreInstruction;
import IR.Values.Instructions.Operator;
import IR.Values.Instructions.Terminator.BrInstruction;
import IR.Values.Instructions.CallInstruction;
import IR.Values.Instructions.Terminator.RetInstruction;
import frontend.Parser;
import frontend.SyntaxParsingTree.Number;
import frontend.SyntaxParsingTree.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static Error.CompileErrorException.error;
import static Error.Error.*;
import static IR.Types.IntegerType.i1;
import static IR.Types.IntegerType.i32;
import static IR.Types.VoidType.Void;
import static IR.Values.ConstantArray.getValueAt;
import static IR.Values.ConstantArray.setValueAt;
import static IR.Values.ConstantInteger.constantZero;
import static IR.Values.Instructions.Operator.*;

public class Visitor {
    private SyntaxParsingTree syntaxParsingTree;
    private final Parser parser;
    private final Module module;
    private Value cv = null, tv = null;  // current value, temp value
    private Function cf = null;  // current function
    private BasicBlock cbb = null;  // current basic block
    private HashMap<String, Value> chm = null;  // current hashmap
    private ArrayList<Value> cfrp = null;  // current function real parameters
    private Integer ci;  // length of array
    private ConstantArray ca = null;  // current array
    private Stack<BasicBlock> whileEntry = new Stack<>();
    private Stack<BasicBlock> whileExit = new Stack<>();

    public static int LLVM_VERSION = 6;  // 线上评测机的llvm版本与我本地的llvm版本不同，语法规则也有不同

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

    private ConstantInteger calculateConstInteger(ConstantInteger a, Operator op, ConstantInteger b) {
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
            case GRE:
                return new ConstantInteger(a.getValue() > b.getValue());
            case GEQ:
                return new ConstantInteger(a.getValue() >= b.getValue());
            case LEQ:
                return new ConstantInteger(a.getValue() <= b.getValue());
            case LSS:
                return new ConstantInteger(a.getValue() < b.getValue());
            default:
                return null;
        }
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

    private void initArray(ConstantArray array, Instruction ptr, ArrayList<Value> initial, int index) {
        initial.add(new ConstantInteger(index));
        for (int i = 0; i < array.getCapacity(); i++) {
            Value v = array.getValues().get(i);
            if (v.getType().isIntType()) {
                initial.add(new ConstantInteger(i));
                GEPInstruction gep = new GEPInstruction(cbb, ptr, initial);
                new StoreInstruction(cbb, gep, v);
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

    public static boolean isConstantValue(Value v) {
        return (v instanceof ConstantInteger) ||
                (v instanceof AllocaInstruction && ((AllocaInstruction) v).isConstant()) ||
                (v instanceof GlobalVariable && ((GlobalVariable) v).isConstant()) ||
                (v instanceof LoadInstruction && (((LoadInstruction) v).getOperands().get(0)) instanceof AllocaInstruction && ((AllocaInstruction) ((LoadInstruction) v).getOperands().get(0)).isConstant());
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

    private ArrayList<Value> getRealParams(ArrayList<Exp> expArrayList) {
        ArrayList<Value> frp = new ArrayList<>();
        // 排除函数参数列表以外加减乘除模号的影响
        for (Exp e : expArrayList) {
            visitExp(e, 1);
            frp.add(cv);
        }
        return frp;
    }

    public void visit() throws CompileErrorException {
        visitCompUnit((CompUnit) syntaxParsingTree);
    }

    private Value GlobalVariable2Load(Value v) {
        if (v instanceof GlobalVariable && !((GlobalVariable) v).isConstant()) {
            v = new LoadInstruction(cbb, v);
        }
        return v;
    }

    private void Exp2Cond() {
        if (!cv.getType().equals(i1)) {
            cv = GlobalVariable2Load(cv);
            cv = new IcmpInstruction(cbb, NEQ, cv, constantZero);
        }
    }

    private Value Cond2Exp(Value value) {
        if (!value.getType().equals(i32)) {
            value = GlobalVariable2Load(value);
            value = new ZextInstruction(cbb, value, i32);
        }
        return value;
    }

    private Value constValue2Int(Value value) {
        if (value instanceof ConstantArray) {
            // array
            ConstantArray array = (ConstantArray) value;
            for (int i = 0; i < array.getValues().size(); i++) {
                Value v = array.getValues().get(i);
                ArrayList<Value> index = new ArrayList<>();
                index.add(new ConstantInteger(i));
                setValueAt(array, index, constValue2Int(v));
            }
        } else if (value instanceof LoadInstruction) {
            // global array can only be initialized by other global var or array
            GEPInstruction gep = (GEPInstruction) ((LoadInstruction) value).getOperands().get(0);
            Value targetValue = gep.getTargetValue();
            if (targetValue instanceof GlobalVariable) {
                GlobalVariable global = (GlobalVariable) targetValue;
                // global var has already been cast to integer
                ConstantArray globalArr = (ConstantArray) global.getValue();
                ArrayList<Value> index = new ArrayList<>();
                for (int i = 2; i < gep.getOperands().size(); i++) {
                    Value v = constValue2Int(gep.getOperands().get(i));
                    index.add(v);
                }
                value = getValueAt(globalArr, index);
            } else if (targetValue instanceof AllocaInstruction) {
                for (BasicBlock bb : cf.getBasicBlocks()) {
                    for (Instruction instr : bb.getInstList()) {
                        if (instr instanceof StoreInstruction && instr.getOperands().get(1) == gep) {
                            value = constValue2Int(instr.getOperands().get(0));
                        }
                    }
                }
            }
        } else if (value instanceof BinaryInstruction) {
            BinaryInstruction binary = (BinaryInstruction) value;
            Value leftValue = constValue2Int(binary.getOperands().get(0));
            Value rightValue = constValue2Int(binary.getOperands().get(1));
            value = calculate(leftValue, binary.getOp(), rightValue);
        }
        return value;
    }

    private void visitCompUnit(CompUnit compUnitTree) throws CompileErrorException {
        chm = new HashMap<>();
        Value tmpValue;
        tmpValue = new Function(i32, "");
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
        }
        visitMainFuncDef(compUnitTree.getMainFuncDef());
        module.addFunction(cf);
        chm.put(cf.getName(), cf);
        module.popSymbolTable();
        chm = module.topSymbolTable();
    }

    private void visitMainFuncDef(MainFuncDef mainFuncDef) throws CompileErrorException {
        // 创建一个新的函数实体，向函数的子模块传递这个实体的引用
        cf = new Function(i32, "main");
        // 为这个函数实体创建一张符号表，向函数的子模块传递这个符号表的引用
        chm = new HashMap<>();
        // 为这个函数实体创建入口基本块，向基本块的子模块传递这个引用
        cbb = new BasicBlock(cf, true);
        // 将符号表插入模型的栈式符号表中
        module.pushSymbolTable(chm);
        cf.addBasicBlock(cbb);
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

    private void visitFuncDef(FuncDef funcDef) throws CompileErrorException {
        // 函数的返回类型，int32或者void的一个引用
        Type returnType = Objects.equals(funcDef.getFuncType().getType(), "int") ? i32 : Void;
        // 创建一个新的函数实体，向函数的子模块传递这个实体的引用
        cf = new Function(returnType, funcDef.getIdent());
        module.addFunction(cf);
        chm.put(cf.getName(), cf);
        // 为这个函数实体创建一张符号表，向函数的子模块传递这个符号表的引用
        chm = new HashMap<>();
        // 为这个函数实体创建入口基本块，向基本块的子模块传递这个引用
        cbb = new BasicBlock(cf, true);
        cf.addBasicBlock(cbb);
        // 处理函数的形参
        if (funcDef.getFuncFParams() != null) {
            // 函数的返回类型
            FunctionType functionType = (FunctionType) cf.getType();
            for (FuncFParam funcFParam : funcDef.getFuncFParams()) {
                // 分配一个形参实体
                Type type;
                int mode = funcFParam.getMode();
                if (mode == 0) {
                    type = i32;
                } else if (mode == 1) {
                    type = new PointerType(i32);
                } else {
                    visitConstExp(funcFParam.getConstExp());
                    int length = 0;
                    if (cv instanceof ConstantInteger) {
                        length = ((ConstantInteger) cv).getValue();
                    } else if (cv instanceof GlobalVariable) {
                        length = ((ConstantInteger) ((GlobalVariable) cv).getValue()).getValue();
                    }
                    type = new PointerType(new ArrayType(i32, length));
                }
                cv = new Parameter(type, funcFParam.getIdent(), cf);
                // 将形参类型的实体插入函数类型实体的形参类型表中
                functionType.addFuncFParam(type);
                // 将形参实体插入函数的形参表中
                cf.addParameter((Parameter) cv);
                // 将形参插入符号表中
                AllocaInstruction a = new AllocaInstruction(cbb, funcFParam.getIdent() + ".addr", type, false);
                new StoreInstruction(cbb, a, cv);
                cv = a;
                putCVIntoCHM("%" + funcFParam.getIdent(), funcFParam.getLine(), funcFParam.getCol());
            }
        }
        // 将符号表插入模型的栈式符号表中
        module.pushSymbolTable(chm);
        // 处理函数的函数体部分
        visitBlock(funcDef.getBlock());
        if (returnType == Void) {
            new RetInstruction(cbb);
        }
        // 重命名寄存器
        cf.reorder();
        // 将符号表从栈式符号表中弹出
        module.popSymbolTable();
        // 将当前符号表置为栈顶符号表
        chm = module.topSymbolTable();
        cbb = null;
    }

    private void visitBlock(Block block) throws CompileErrorException {
        if (block == null) {
            return;
        }
        for (BlockItem blockItem : block.getBlockItem()) {
            visitBlockItem(blockItem);
        }
    }

    private void visitBlockItem(BlockItem blockItem) throws CompileErrorException {
        if (blockItem.getDecl() != null) {
            visitDecl(blockItem.getDecl());
        } else if (blockItem.getStmt() != null) {
            visitStmt(blockItem.getStmt());
        }
    }

    private Value calculateLogical(Value leftValue, Operator op, Value rightValue) {
        if (isConstantValue(leftValue) && isConstantValue(rightValue)) {
            if (!(leftValue instanceof ConstantInteger)) {
                leftValue = ((GlobalVariable) leftValue).getValue();
            }
            if (!(rightValue instanceof ConstantInteger)) {
                rightValue = ((GlobalVariable) rightValue).getValue();
            }
        } else {
            leftValue = GlobalVariable2Load(leftValue);
            rightValue = GlobalVariable2Load(rightValue);
        }
        return new IcmpInstruction(cbb, op, Cond2Exp(leftValue), Cond2Exp(rightValue));
    }

    private void visitCond(Cond cond, BasicBlock trueBlock, BasicBlock falseBlock) {
        visitLOrExp(cond.getLOrExp(), trueBlock, falseBlock);
        // if Cond is not bool exp, change it to bool exp
        Exp2Cond();
    }

    private void visitLOrExp(LOrExp lOrExp, BasicBlock trueBlock, BasicBlock falseBlock) {
        BasicBlock next = falseBlock;
        if (lOrExp.getlOrExp() != null) {
            next = new BasicBlock(cf);
        }
        visitLAndExp(lOrExp.getlAndExp(), next);
        Exp2Cond();
        new BrInstruction(cbb, cv, trueBlock, next);
        if (lOrExp.getlOrExp() != null) {
            cbb = next;
            cf.addBasicBlock(cbb);
            visitLOrExp(lOrExp.getlOrExp(), trueBlock, falseBlock);
        }
    }

    private void visitLAndExp(LAndExp lAndExp, BasicBlock falseBlock) {
        visitEqExp(lAndExp.getEqExp());
        if (lAndExp.getlAndExp() != null) {
            BasicBlock next = new BasicBlock(cf);
            Exp2Cond();
            new BrInstruction(cbb, cv, next, falseBlock);
            cbb = next;
            cf.addBasicBlock(cbb);
            visitLAndExp(lAndExp.getlAndExp(), falseBlock);
        }
    }

    private void visitEqExp(EqExp eqExp) {
        visitRelExp(eqExp.getRelExp());
        if (eqExp.getEqExp() != null) {
            Value leftValue = cv;
            Operator op = Operator.OP(eqExp.getOp());
            if (visitEqExp(eqExp.getEqExp(), leftValue, op)) {
                cv = calculateLogical(leftValue, op, cv);
            }
        }
    }

    private boolean visitEqExp(EqExp eqExp, Value leftEqExp, Operator fop) {
        visitRelExp(eqExp.getRelExp());
        boolean usedFatherValue = false;
        if (eqExp.getEqExp() != null) {
            Value leftValue = calculateLogical(leftEqExp, fop, cv);
            Operator op = Operator.OP(eqExp.getOp());
            usedFatherValue = true;
            if (visitEqExp(eqExp.getEqExp(), leftValue, op)) {
                cv = calculateLogical(leftValue, op, cv);
            }
        }
        return !usedFatherValue;
    }

    private void visitRelExp(RelExp relExp) {
        visitAddExp(relExp.getAddExp(), 0);
        if (relExp.getRelExp() != null) {
            Value leftValue = cv;
            Operator op = Operator.OP(relExp.getOp());
            if (visitRelExp(relExp.getRelExp(), leftValue, op)) {
                cv = calculateLogical(leftValue, op, cv);
            }
        }
    }

    private boolean visitRelExp(RelExp relExp, Value leftRelExp, Operator fop) {
        visitAddExp(relExp.getAddExp(), 0);
        boolean usedFatherValue = false;
        if (relExp.getRelExp() != null) {
            Value leftValue = calculateLogical(leftRelExp, fop, cv);
            Operator op = Operator.OP(relExp.getOp());
            usedFatherValue = true;
            if (visitRelExp(relExp.getRelExp(), leftValue, op)) {
                cv = calculateLogical(leftValue, op, cv);
            }
        }
        return !usedFatherValue;
    }

    private void enterNewBlock(BasicBlock newBasicBlock) {
        // change current basic block
        cbb = newBasicBlock;
        // create new symbol table for new block
        chm = new HashMap<>();
        // push block into the stack
        module.pushSymbolTable(chm);
    }

    private void exitBasicBlock() {
        // pop the top symbol table from the stack (if.then symbol table)
        module.popSymbolTable();
        // change the current symbol table to the top of the stack
        chm = module.topSymbolTable();
    }

    private void visitStmt(Stmt stmt) {
        LVal lVal = stmt.getlVal();
        Exp exp = stmt.getExp();
        ArrayList<Stmt> stmts = stmt.getStmts();
        switch (stmt.getType()) {
            case 0:
                break;
            case 1:
                visitExp(stmt.getExp(), 0);
                break;
            case 2:
                // 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
                // 1. create if.then block, if.else block and exitBlock
                BasicBlock ifBasicBlock = new BasicBlock(cf);
                BasicBlock elseBasicBlock = new BasicBlock(cf);
                BasicBlock exitBasicBlock = new BasicBlock(cf);
                if (stmts.size() == 1) {
                    visitCond(stmt.getCond(), ifBasicBlock, exitBasicBlock);
                    enterNewBlock(ifBasicBlock);
                    cf.addBasicBlock(cbb);
                    visitStmt(stmts.get(0));
                } else {
                    visitCond(stmt.getCond(), ifBasicBlock, elseBasicBlock);
                    enterNewBlock(ifBasicBlock);
                    cf.addBasicBlock(cbb);
                    visitStmt(stmts.get(0));
                    new BrInstruction(cbb, exitBasicBlock);
                    exitBasicBlock();
                    enterNewBlock(elseBasicBlock);
                    cf.addBasicBlock(cbb);
                    visitStmt(stmts.get(1));
                }
                new BrInstruction(cbb, exitBasicBlock);
                exitBasicBlock();
                cbb = exitBasicBlock;
                cf.addBasicBlock(cbb);
                break;
            case 3:
                // 'while' '(' Cond ')' Stmt
                BasicBlock whileEntryBlock = new BasicBlock(cf);
                BasicBlock whileBasicBlock = new BasicBlock(cf);
                BasicBlock exitBasicBlock0 = new BasicBlock(cf);
                new BrInstruction(cbb, whileEntryBlock);
                whileEntry.push(whileEntryBlock);
                whileExit.push(exitBasicBlock0);
                enterNewBlock(whileEntryBlock);
                cf.addBasicBlock(cbb);
                visitCond(stmt.getCond(), whileBasicBlock, exitBasicBlock0);
                cbb = whileBasicBlock;
                cf.addBasicBlock(cbb);
                visitStmt(stmts.get(0));
                new BrInstruction(cbb, whileEntryBlock);
                exitBasicBlock();
                whileEntry.pop();
                whileExit.pop();
                cbb = exitBasicBlock0;
                cf.addBasicBlock(cbb);
                break;
            case 4:
                // 'break' ';'
                if (whileExit.isEmpty()) {
                    error(IllegalBreakOrContinue, stmt.getLine(), stmt.getCol());
                } else {
                    new BrInstruction(cbb, whileExit.peek());
                    cbb = new BasicBlock(cf);
                    cf.addBasicBlock(cbb);
                }
                break;
            case 5:
                // 'continue' ';'
                if (whileEntry.isEmpty()) {
                    error(IllegalBreakOrContinue, stmt.getLine(), stmt.getCol());
                } else {
                    new BrInstruction(cbb, whileEntry.peek());
                    cbb = new BasicBlock(cf);
                    cf.addBasicBlock(cbb);
                }
                break;
            case 6:
                // 'return' [ Exp ] ';'
                if (exp == null) {
                    new RetInstruction(cbb);
                } else {
                    visitExp(exp, 0);
                    new RetInstruction(cbb, cv);
                }
                break;
            case 7:
                // 'printf' '(' FormatString { ',' Exp } ')' ';'
                String fStr = stmt.getFormatString().replace("\"", "");
                int length = fStr.length();
                fStr = fStr.replace("\\n", "\\0A");
                length = 2 * length - fStr.length() + 1;
                fStr = fStr +  "\\00";
                String name = "void" + fStr.hashCode() + "void";
                ConstantString str = new ConstantString(length, name, fStr);
                GlobalVariable globalVariable = null;
                for (GlobalVariable gVal : module.getGlobalVariables()) {
                    Value v = gVal.getValue();
                    if (v instanceof ConstantString) {
                        if (v.equals(str)) {
                            globalVariable = gVal;
                        } else {
                            if (v.getName().equals(name)) {
                                name = "void" + name.hashCode() + "void";
                            }
                        }
                    }
                }
                if (globalVariable == null) {
                    globalVariable = new GlobalVariable(name, str, true);
                    module.addGlobalVariable(globalVariable);
                }
                ArrayList<Value> expList = new ArrayList<>();
                expList.add(globalVariable);
                expList.addAll(getRealParams(stmt.getExps()));
                cv = find("printf", true, stmt.getLine(), stmt.getCol());
                cv = new CallInstruction(cbb, (Function) cv, expList);
                break;
            case 8:
                // LVal '=' Exp ';'
                visitExp(exp, 0);
                Value expValue = cv;
                visitLVal(lVal);
                Value leftValue = cv;
                Value v = find(lVal.getIdent(), lVal.getLine(), lVal.getCol());
                if (isConstantValue(v)) {
                    error(ChangeConstValue, lVal.getLine(), lVal.getCol());
                }
                new StoreInstruction(cbb, leftValue, expValue);
                break;
            case 9:
                // LVal '=' 'getint' '(' ')' ';'
                visitLVal(lVal);
                v = find(lVal.getIdent(), lVal.getLine(), lVal.getCol());
                if (isConstantValue(v)) {
                    error(ChangeConstValue, lVal.getLine(), lVal.getCol());
                }
                tv = cv;
                cv = find("getint", true, lVal.getLine(), lVal.getCol());
                cv = new CallInstruction(cbb, (Function) cv, new ArrayList<>());
                new StoreInstruction(cbb, tv, cv);
                break;
            case 10:
                // Block
                BasicBlock basicBlock = new BasicBlock(cf);
                BasicBlock exitBasicBlock1 = new BasicBlock(cf);
                new BrInstruction(cbb, basicBlock);
                enterNewBlock(basicBlock);
                cf.addBasicBlock(cbb);
                visitBlock(stmt.getBlock());
                new BrInstruction(cbb, exitBasicBlock1);
                cbb = exitBasicBlock1;
                cf.addBasicBlock(cbb);
                exitBasicBlock();
                break;
            default:
                error(UndefinedError, stmt.getLine(), stmt.getCol());
                break;
        }
    }

    private void visitDecl(Decl decl) throws CompileErrorException {
        if (decl.getConstDecl() != null) {
            visitConstDecl(decl.getConstDecl());
        } else {
            visitVarDecl(decl.getVarDecl());
        }
    }

    private void visitConstDecl(ConstDecl constDecl) throws CompileErrorException {
        for (ConstDef constDef : constDecl.getConstDefs()) {
            visitConstDef(constDef);
            putCVIntoCHM(cv.getName(), constDef.getLine(), constDef.getCol());
        }
    }

    private void visitConstDef(ConstDef constDef) throws CompileErrorException {
        int mode = constDef.getMode();
        if (mode != 0) {
            visitConstExp(constDef.getConstExps().get(0));
            ca = new ConstantArray(i32, ((ConstantInteger) cv).getValue(), true);
            if (mode == 2) {
                visitConstExp(constDef.getConstExps().get(1));
                int len = ((ConstantInteger) cv).getValue();
                Value arr = new ConstantArray(i32, len, true);;
                for (int i = 0; i < ca.getCapacity(); i++) {
                    ca.addValue(arr);
                    arr = new ConstantArray(i32, len, true);
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
            cv = constValue2Int(cv);
            cv = new GlobalVariable(constDef.getIdent(), cv, true);
            module.addGlobalVariable((GlobalVariable) cv);
        } else {
            Value value = cv;
            cv = new AllocaInstruction(cbb, constDef.getIdent(), cv.getType(), true);
            if (((AllocaInstruction) cv).getAllocated().isArrayType()) {
                initArray((ConstantArray) value, (Instruction) cv, new ArrayList<>(), 0);
            } else {
                new StoreInstruction(cbb, cv, value);
            }
            cv.setName("%" + constDef.getIdent());
        }
    }

    private void visitConstInitVal(ConstInitVal constInitVal) throws CompileErrorException {
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
                if (!(cv instanceof ConstantArray)) {
                    ca.insert(cv, ci);
                }
            }
            cv = ca;
        }
    }

    private void visitVarDecl(VarDecl varDecl) throws CompileErrorException {
        for (VarDef varDef : varDecl.getVarDefs()) {
            visitVarDef(varDef);
            putCVIntoCHM(cv.getName(), varDecl.getLine(), varDef.getCol());
        }
    }

    private void visitVarDef(VarDef varDef) throws CompileErrorException {
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
            ca = new ConstantArray(i32, ((ConstantInteger) cv).getValue(), false);
            if (mode == 2) {
                visitConstExp(varDef.getConstExps().get(1));
                int len = ((ConstantInteger) cv).getValue();
                Value arr = new ConstantArray(i32, len, false);;
                for (int i = 0; i < ca.getCapacity(); i++) {
                    ca.addValue(arr);
                    arr = new ConstantArray(i32, len, false);
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
            cv = constValue2Int(cv);
            cv = new GlobalVariable(varDef.getIdent(), cv, false);
            module.addGlobalVariable((GlobalVariable) cv);
        } else {
            Value value = cv;
            cv = new AllocaInstruction(cbb, varDef.getIdent(), cv.getType(), false);
            if (flag) {
                if (((AllocaInstruction) cv).getAllocated().isArrayType()) {
                    ConstantArray array = (ConstantArray) value;
                    if (!array.isNeedInit()) {
                        initArray(array, (Instruction) cv, new ArrayList<>(), 0);
                    }
                } else {
                    new StoreInstruction(cbb, cv, value);
                }
            }
            cv.setName("%" + varDef.getIdent());
        }
    }

    private void visitInitVal(InitVal initVal) throws CompileErrorException {
        if (initVal.getExp() != null) {
            visitExp(initVal.getExp(), 0);
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

    private void visitExp(Exp exp, int mode) throws CompileErrorException {
        visitAddExp(exp.getAddExp(), mode);
    }

    private void visitNumber(Number number) {
        cv = new ConstantInteger(number.getNumber());
    }

    private void visitLVal(LVal lVal) throws CompileErrorException {
        // LVal may be: var array const constArray globalVar globalArray globalConst globalConstArray param paramArray
        // After visit, cv may be value if LVal is constant var, or pointer if LVal is var, all kinds of array
        Value v = find(lVal.getIdent(), lVal.getLine(), lVal.getCol());
        if (v instanceof AllocaInstruction) {
            // var array const constArray param paramArray
            Type allocated = ((AllocaInstruction) v).getAllocated();
            if (allocated.isIntType()) {
                // var const param
                if (((AllocaInstruction) v).isConstant()) {
                    // const
                    for (BasicBlock bb : cf.getBasicBlocks()) {
                        for (Instruction instr : bb.getInstList()) {
                            if (instr instanceof StoreInstruction) {
                                if (instr.getOperands().get(1) == v) {
                                    cv = instr.getOperands().get(0);
                                }
                            }
                        }
                    }
                } else {
                    // var param
                    cv = v;
                }
            } else if (allocated.isArrayType()) {
                // array constArray
                ArrayList<Value> index = new ArrayList<>();
                index.add(constantZero);
                for (Exp e : lVal.getExps()) {
                    visitExp(e, 0);
                    index.add(cv);
                }
                if (index.size() == 1) {
                    // has no exp
                    cv = v;
                } else {
                    cv = new GEPInstruction(cbb, v, index);
                }
            } else if (allocated.isPointerType()) {
                // paramArray
                v = new LoadInstruction(cbb, v);
                ArrayList<Value> index = new ArrayList<>();
                for (Exp e : lVal.getExps()) {
                    visitExp(e, 0);
                    index.add(cv);
                }
                if (index.size() == 0) {
                    // has no exp
                    cv = v;
                } else {
                    cv = new GEPInstruction(cbb, v, index);
                }
            }
        } else if (v instanceof GlobalVariable) {
            // globalVar globalArray globalConst globalConstArray
            Value global = ((GlobalVariable) v).getValue();
            if (global.getType().isIntType()) {
                // globalVar globalConst
                if (((GlobalVariable) v).isConstant()) {
                    // globalConst
                    cv = global;
                } else {
                    // globalVar
                    cv = v;
                }
            } else {
                // globalArray globalConstArray
                ArrayList<Value> index = new ArrayList<>();
                index.add(constantZero);
                for (Exp e : lVal.getExps()) {
                    visitExp(e, 0);
                    index.add(cv);
                }
                if (index.size() == 1) {
                    // has no exp
                    cv = v;
                } else {
                    cv = new GEPInstruction(cbb, v, index);
                }
            }
        }
    }

    private void visitPrimaryExp(PrimaryExp primaryExp, int mode) throws CompileErrorException {
        if (primaryExp.getNumber() != null) {
            visitNumber(primaryExp.getNumber());
        } else if (primaryExp.getExp() != null) {
            visitExp(primaryExp.getExp(), mode);
        } else {
            visitLVal(primaryExp.getlVal());
            if (mode == 0) {
                if (cv.getType().isPointerType()) {
                    cv = new LoadInstruction(cbb, cv);
                }
                // cv.getType().isIntType(): nothing need to do
            } else if (mode == 1) {
                // real param
                Type type = cv.getType();
                if (type.isPointerType()) {
                    Type targetType = ((PointerType) type).gettType();
                    if (cv instanceof LoadInstruction) {
                        // paramArray with no index
                        // nothing to do
                    } else if (targetType.isArrayType()) {
                        // paramArray with index
                        ArrayList<Value> initial = new ArrayList<>();
                        initial.add(constantZero);
                        initial.add(constantZero);
                        cv = new GEPInstruction(cbb, cv, initial);
                    } else if (targetType.isIntType()) {
                        // paramVar
                        cv = new LoadInstruction(cbb, cv);
                    }
                }
                // type.isIntType(): nothing need to do
            }
        }
    }

    private void visitUnaryExp(UnaryExp unaryExp, int mode) throws CompileErrorException {
        // unaryExp instanceof AllocaInstruction, GEPInstruction, ConstantInteger or CallInstruction
        if (unaryExp.getPrimaryExp() != null) {
            visitPrimaryExp(unaryExp.getPrimaryExp(), mode);
        } else if (unaryExp.getUnaryOp() != null) {
            Operator op = Operator.OP(unaryExp.getUnaryOp().getOp());
            visitUnaryExp(unaryExp.getUnaryExp(), mode);
            if (op == NOT) {
                cv = GlobalVariable2Load(cv);
                cv = new IcmpInstruction(cbb, EQL, cv, constantZero);
            } else if (op == MINU) {
                cv = calculate(constantZero, MINU, cv);
            }
        } else {
            Value value = find(unaryExp.getIdent(), unaryExp.getLine(), unaryExp.getCol());
            if (!(value instanceof Function)) {
                error(UndefinedError, unaryExp.getLine(), unaryExp.getCol());
            } else {
                Function f = (Function) value;
                if (unaryExp.getFuncRParams() != null) {
                    visitFuncRParams(unaryExp.getFuncRParams());
                } else {
                    cfrp = new ArrayList<>();
                }
                f.match(cfrp, unaryExp.getLine(), unaryExp.getCol());
                cv = new CallInstruction(cbb, f, cfrp);

            }
        }
    }

    private void visitFuncRParams(FuncRParams funcRParams) throws CompileErrorException {
        cfrp = getRealParams(funcRParams.getExps());
    }

    // visitAddExp(AddExp addExp) visitAddExp(AddExp addExp, Value leftAddExp, Operator fop) 调用此方法
    private void visitMulExp(MulExp mulExp, int mode) throws CompileErrorException {
        visitUnaryExp(mulExp.getUnaryExp(), mode);
        if (mulExp.getMulExp() != null) {
            Value leftValue = cv;
            Operator op = Operator.OP(mulExp.getOp());
            if (visitMulExp(mulExp.getMulExp(), leftValue, op, mode)) {
                cv = calculate(leftValue, op, cv);
            }
        }
    }

    // visitMulExp(MulExp mulExp)调用此方法
    private boolean visitMulExp(MulExp mulExp, Value leftMulExp, Operator fop, int mode) throws CompileErrorException {
        visitUnaryExp(mulExp.getUnaryExp(), mode);
        boolean usedFatherValue = false;
        if (mulExp.getMulExp() != null) {
            Value leftValue = calculate(leftMulExp, fop, cv);
            Operator op = Operator.OP(mulExp.getOp());
            usedFatherValue = true;
            if (visitMulExp(mulExp.getMulExp(), leftValue, op, mode)) {
                cv = calculate(leftValue, op, cv);
            }
        }
        return !usedFatherValue;
    }

    // visitExp(Exp exp)调用此方法
    private void visitAddExp(AddExp addExp, int mode) throws CompileErrorException {
        visitMulExp(addExp.getMulExp(), mode);
        if (addExp.getAddExp() != null) {
            Value leftValue = cv;
            Operator op = Operator.OP(addExp.getOp());
            if (visitAddExp(addExp.getAddExp(), leftValue, op, mode)) {
                cv = calculate(leftValue, op, cv);
            }
        }
    }

    // visitAddExp(AddExp addExp)调用此方法
    private boolean visitAddExp(AddExp addExp, Value leftAddExp, Operator fop, int mode) throws CompileErrorException {
        visitMulExp(addExp.getMulExp(), mode);
        boolean usedFatherValue = false;
        if (addExp.getAddExp() != null) {
            Value leftValue = calculate(leftAddExp, fop, cv);
            Operator op = Operator.OP(addExp.getOp());
            usedFatherValue = true;
            if (visitAddExp(addExp.getAddExp(), leftValue, op, mode)) {
                cv = calculate(leftValue, op, cv);
            }
        }
        return !usedFatherValue;
    }

    private Value calculate(Value leftValue, Operator op, Value rightValue) {
        if (isConstantValue(leftValue) && isConstantValue(rightValue)) {
            ConstantInteger left, right;
            if (leftValue instanceof ConstantInteger) {
                left = (ConstantInteger) leftValue;
            } else {
                left = (ConstantInteger) ((GlobalVariable) leftValue).getValue();
            }
            if (rightValue instanceof ConstantInteger) {
                right = (ConstantInteger) rightValue;
            } else {
                right = (ConstantInteger) ((GlobalVariable) rightValue).getValue();
            }
            return calculateConstInteger(left, op, right);
        } else {
            if (leftValue instanceof GlobalVariable) {
                leftValue = new LoadInstruction(cbb, leftValue);
            }
            if (rightValue instanceof GlobalVariable) {
                rightValue = new LoadInstruction(cbb, rightValue);
            }
            if (((IntegerType) leftValue.getType()).getLength() < ((IntegerType) rightValue.getType()).getLength()) {
                leftValue = new ZextInstruction(cbb, leftValue, rightValue.getType());
            } else if (((IntegerType) leftValue.getType()).getLength() > ((IntegerType) rightValue.getType()).getLength()) {
                rightValue = new ZextInstruction(cbb, rightValue, leftValue.getType());
            }
            return new BinaryInstruction(cbb, op, leftValue, rightValue);
        }
    }

    private void visitConstExp(ConstExp constExp) throws CompileErrorException {
        visitAddExp(constExp.getAddExp(), 0);
        cv = constValue2Int(cv);
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
