package backend;

import IR.Module;
import IR.Types.ArrayType;
import IR.Types.PointerType;
import IR.Types.Type;
import IR.Values.*;
import IR.Values.Instructions.*;
import IR.Values.Instructions.Cast.ZextInstruction;
import IR.Values.Instructions.Mem.AllocaInstruction;
import IR.Values.Instructions.Mem.GEPInstruction;
import IR.Values.Instructions.Mem.LoadInstruction;
import IR.Values.Instructions.Mem.StoreInstruction;
import IR.Values.Instructions.Terminator.BrInstruction;
import IR.Values.Instructions.Terminator.RetInstruction;
import backend.MachineCode.MCBlock;
import backend.MachineCode.MCData;
import backend.MachineCode.MCFunction;
import backend.MachineCode.MCInstructions.MCBranch.bnez;
import backend.MachineCode.MCInstructions.MCITypeInstructions.MCITypeInstruction;
import backend.MachineCode.MCInstructions.MCITypeInstructions.li;
import backend.MachineCode.MCInstructions.MCInstruction;
import backend.MachineCode.MCInstructions.MCMemory.la;
import backend.MachineCode.MCInstructions.MCMemory.lw;
import backend.MachineCode.MCInstructions.MCMemory.sw;
import backend.MachineCode.MCInstructions.MCRTypeInstructions.MCRTypeInstruction;
import backend.MachineCode.MCInstructions.MCRTypeInstructions.move;
import backend.MachineCode.MCInstructions.MCTerminator.syscall;
import backend.MachineCode.MCInstructions.MCjump.j;
import backend.MachineCode.MCInstructions.MCjump.jal;
import backend.MachineCode.MCInstructions.MCjump.jr;
import backend.MachineCode.MCInstructions.mnemonic;
import backend.Registers.MCRegisterPool;
import backend.Registers.MCRegisters;
import backend.Registers.Registers;
import backend.Registers.VirtualRegisters;
import utils.Graph;
import utils.List;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import static IR.Types.IntegerType.i32;
import static backend.MachineCode.MCInstructions.mnemonic.*;
import static backend.Registers.MCRegisterPool.*;

public class CodeGenerate {
    private final Module irModule;
    private final ArrayList<MCData> data;
    private final ArrayList<MCFunction> functions;
    private final HashMap<String, Boolean> isLeafFunction;
    private final HashMap<String, Integer> functionSize;
    private final HashMap<String, MCBlock> MCBlockNameMap;

    private MCFunction cf;  // current machine code function
    private MCBlock cb;  // current machine code basic block
    private Function cif;  // current IR function
    private int cst = 0;  // current stack top
    private int raTop = 0;
    private int expand = 0;

    private final HashMap<String, Registers> val2Reg = new HashMap<>();  // value name mapping to register
    private final HashMap<Registers, Integer> spillingIndex = new HashMap<>();  // color mapping to spilling stack index
    private int spilling = 0;
    private int fStringCount = 0;

    public CodeGenerate(Module irModule) {
        this.irModule = irModule;
        data = new ArrayList<>();
        functions = new ArrayList<>();
        isLeafFunction = new HashMap<>();
        functionSize = new HashMap<>();
        MCBlockNameMap = new HashMap<>();
    }

    public void generateMIPS() {
        ArrayList<GlobalVariable> globalVariables = irModule.getGlobalVariables();
        List<Function, Module> irFunctions = irModule.getFunctions();
        for (GlobalVariable globalVariable : globalVariables) {
            generateGlobalVariable(globalVariable);
        }
        int size = irFunctions.size();
        generateFunction(irFunctions.get(size - 1));
        for (int i = 0; i < size - 1; i ++) {
            generateFunction(irFunctions.get(i));
        }
    }

    public void generateGlobalVariable(GlobalVariable globalVariable) {
        String name = globalVariable.getName().replace("@", "");
        Type type = globalVariable.getValue().getType();
        if (type.isArrayType()) {
            ConstantArray array = (ConstantArray) globalVariable.getValue();
            ArrayList<Value> initialValues = array.getAllValues();
            ArrayList<Integer> initials = new ArrayList<>();
            MCData mcData = new MCData(name, initials);
            if (!array.isNeedInit()) {
                for (Value value : initialValues) {
                    if (value instanceof ConstantInteger) {
                        initials.add(((ConstantInteger) value).getValue());
                    }
                }
            }
            mcData.setSize(array.getCapacity() * array.getBlockLength() * 4);
            data.add(mcData);
        } else if (type.isIntType()) {
            ConstantInteger constInitial = (ConstantInteger) globalVariable.getValue();
            data.add(new MCData(name, constInitial.getValue()));
        }
    }

    public void generateFunction(Function irFunction) {
        String name = irFunction.getName().replace("@", "");
        List<BasicBlock, Function> blocks = irFunction.getBasicBlocks();
        cf = new MCFunction(name);
        cif = irFunction;

        boolean isEntry = true;
        val2Reg.clear();
        MCBlockNameMap.clear();

        for (BasicBlock bb : blocks) {
            MCBlock mcb = new MCBlock(cf, isEntry);
            if (isEntry) {
                isEntry = false;
            }
            MCBlockNameMap.put(mcb.getName(), mcb);
        }

        for (BasicBlock bb : blocks) {
            String bName = BasicBlock2MCBlockName(bb);
            cb = MCBlockNameMap.get(bName);
            generateBasicBlock(bb);
        }
        functions.add(cf);
        //reallocateRegisters();
        stupidReallocateRegisters();
        int nowSize = cst;
        for (MCBlock block : cf.getBlocks()) {
            for (MCInstruction instr : block.getInstructions()) {
                if (instr.def.contains(sp) && instr.op == addiu) {
                    assert instr instanceof MCITypeInstruction;
                    ((MCITypeInstruction) instr).setImmediate(-nowSize);
                    if (nowSize > 0) {
                        nowSize = -nowSize;
                    }
                }
            }
        }
        cf = null;
    }

    public void generateBasicBlock(BasicBlock bb) {
        List<Instruction, BasicBlock> instructions = bb.getInstList();
        if (bb.isEntry()) {
            prologue();
        }
        for (Instruction instruction : instructions) {
            generateInstruction(instruction);
        }
    }

    public void generateInstruction(Instruction instr) {
        if (instr instanceof StoreInstruction) {
            StoreInstruction storeInstr = (StoreInstruction) instr;
            ArrayList<Value> operands = storeInstr.getOperands();
            Value value = operands.get(0), ptr = operands.get(1);
            int offset = 0;
            Registers rt = value2Register(value);
            Registers base = value2Register(ptr);
            cb.addInstruction(new sw(base, offset, rt));
        } else if (instr instanceof LoadInstruction) {
            LoadInstruction loadInstr = (LoadInstruction) instr;
            Value ptr = loadInstr.getOperands().get(0);
            Registers rt = value2Register(loadInstr);
            Registers base = value2Register(ptr);
            cb.addInstruction(new lw(base, 0, rt));
        } else if (instr instanceof AllocaInstruction) {
            AllocaInstruction allocaInstr = (AllocaInstruction) instr;
            Type type = allocaInstr.getAllocated();
            Registers reg = value2Register(allocaInstr);
            if (type instanceof ArrayType) {
                raTop -= type.getSize();
            } else {
                raTop -= 4;
            }
            cb.addInstruction(new MCITypeInstruction(mnemonic.addiu, sp, reg, raTop));
        } else if (instr instanceof GEPInstruction) {
            GEPInstruction gepInstr = (GEPInstruction) instr;
            Registers gepReg = value2Register(gepInstr);
            ArrayList<Value> operands = gepInstr.getOperands();
            Value target = gepInstr.getTargetValue();
            Type ptr = target.getType();

            // calculate size of each dim
            ArrayList<Integer> dimSize = new ArrayList<>();
            int size;
            if (ptr.isPointerType()) {
                // ptr must be pointer type
                Type type = ((PointerType) ptr).gettType();
                size = type.getSize();
                while (type.isArrayType()) {
                    dimSize.add(size);
                    type = ((ArrayType) type).getElementType();
                    size = type.getSize();
                }
                dimSize.add(size);
            }

            // calculate offset of this gep instr
            Registers offsetReg = buildVirtualRegister();
            int offset = 0;
            // operands.size() must be dimSize.size() + 1
            // operands.get(0) is target
            for (int i = 1; i < operands.size(); i++) {
                // v.getType() must be i32
                Value v = operands.get(i);
                int dim = dimSize.get(i - 1);
                if (v instanceof ConstantInteger) {
                    offset += dim * ((ConstantInteger) v).getValue();
                }
            }
            cb.addInstruction(new li(offsetReg, offset));
            for (int i = 1; i < operands.size(); i++) {
                Value v = operands.get(i);
                int dim = dimSize.get(i - 1);
                if (!(v instanceof ConstantInteger)) {
                    Registers valReg = value2Register(v);
                    Registers dimOffset = buildVirtualRegister();
                    cb.addInstruction(new MCITypeInstruction(mul, valReg, dimOffset, dim));
                    cb.addInstruction(new MCRTypeInstruction(addu, offsetReg, dimOffset, offsetReg));
                }
            }

            // target address + offset = result address
            Registers base;
            String name = target.getName().replace("@", "");
            if (target instanceof GlobalVariable) {
                base = buildVirtualRegister();
                cb.addInstruction(new la(name, 0, base));
                cb.addInstruction(new MCRTypeInstruction(addu, base, offsetReg, gepReg));
            } else {
                base = value2Register(target);
                cb.addInstruction(new MCRTypeInstruction(addu, base, offsetReg, gepReg));
            }
        } else if (instr instanceof IcmpInstruction) {
            IcmpInstruction icmp = (IcmpInstruction) instr;
            Registers result = value2Register(icmp);
            Value leftValue = icmp.getOperands().get(0);
            Value rightValue = icmp.getOperands().get(1);
            mnemonic scheme = op2mnemonic(instr.getOp());
            if (leftValue instanceof ConstantInteger && rightValue instanceof ConstantInteger) {
                boolean to = compare(instr.getOp(), ((ConstantInteger) leftValue).getValue(), ((ConstantInteger) rightValue).getValue());
                int imm = to ? 1 : 0;
                cb.addInstruction(new li(result, imm));
            } else {
                Registers rs = value2Register(leftValue);
                Registers rt = value2Register(rightValue);
                cb.addInstruction(new MCRTypeInstruction(scheme, rs, rt, result));
            }
        } else if (instr instanceof BinaryInstruction) {
            BinaryInstruction bInstr = (BinaryInstruction) instr;
            Registers result = value2Register(bInstr);
            Value leftValue = bInstr.getOperands().get(0);
            Value rightValue = bInstr.getOperands().get(1);
            mnemonic op = op2mnemonic(instr.getOp());
            if (leftValue instanceof ConstantInteger && rightValue instanceof ConstantInteger) {
                cb.addInstruction(new li(result, calculate(instr.getOp(), ((ConstantInteger) leftValue).getValue(), ((ConstantInteger) rightValue).getValue())));
            } else {
                Registers rs = value2Register(leftValue);
                Registers rt = value2Register(rightValue);
                cb.addInstruction(new MCRTypeInstruction(op, rs, rt, result));
            }
        } else if (instr instanceof CallInstruction) {
            CallInstruction callInstr = (CallInstruction) instr;
            String funcName = callInstr.getFunctionName();
            if (funcName.equals("printf")) {
                callPrintf(callInstr);
            } else if (funcName.equals("getint")) {
                callGetInt(callInstr);
            } else {
                saveEnvironment();
                java.util.List<Value> args = callInstr.getRealParameters();
                for (int i = 0; i < args.size(); i++) {
                    Value v = args.get(i);
                    if (i < 4) {
                        if (v instanceof ConstantInteger) {
                            cb.addInstruction(new li(new MCRegisters("a" + i, false), ((ConstantInteger) v).getValue()));
                        } else {
                            cb.addInstruction(new move(value2Register(v), new MCRegisters("a" + i, false)));
                        }
                    } else {
                        cb.addInstruction(new sw(sp, (i - 3) * 4, value2Register(v)));
                    }
                }
                String addr = "_" + callInstr.getFunctionName().replace("@", "") + "_block_0";
                cb.addInstruction(new jal(addr));
                cb.addInstruction(new MCInstruction());
                if (callInstr.getReturnType() == i32) {
                    Registers reg = value2Register(callInstr);
                    cb.addInstruction(new move(v0, reg));
                }
                loadEnvironment();
            }
        } else if (instr instanceof RetInstruction) {
            RetInstruction retInstr = (RetInstruction) instr;
            Value retVal = retInstr.getRetValue();
            if (retVal instanceof ConstantInteger) {
                ConstantInteger integer = (ConstantInteger) retVal;
                cb.addInstruction(new li(v0, integer.getValue()));
            } else if (retVal != null) {
                Registers reg = value2Register(retVal);
                cb.addInstruction(new move(reg, v0));
            }
            epilogue();
        } else if (instr instanceof BrInstruction) {
            BrInstruction br = (BrInstruction) instr;
            if (br.isUnconditional()) {
                BasicBlock target = br.getUnconditionalJumpTarget();
                String name = BasicBlock2MCBlockName(target);
                cb.addInstruction(new j(name));
                cb.setTrueBlock(MCBlockNameMap.get(name));
                cb.addInstruction(new MCInstruction());
            } else {
                BasicBlock targetTrue = br.getTargetTrue();
                BasicBlock targetFalse = br.getTargetFalse();
                String labelTrue = BasicBlock2MCBlockName(targetTrue);
                String labelFalse = BasicBlock2MCBlockName(targetFalse);
                Value result = br.getResult();
                Registers rs = value2Register(result);
                cb.addInstruction(new bnez(rs, labelTrue));
                cb.setTrueBlock(MCBlockNameMap.get(labelTrue));
                cb.addInstruction(new MCInstruction());
                cb.addInstruction(new j(labelFalse));
                cb.setFalseBlock(MCBlockNameMap.get(labelFalse));
                cb.addInstruction(new MCInstruction());
            }
        } else if (instr instanceof ZextInstruction) {
            ZextInstruction zext = (ZextInstruction) instr;
            Registers rs = value2Register(zext.getValue());
            Registers rt = value2Register(zext);
            cb.addInstruction(new MCITypeInstruction(ori, rs, rt, 0));
        }
    }

    public void saveEnvironment() {
    }

    public void loadEnvironment() {

    }

    public void callPrintf(CallInstruction callInstr) {
        String fString = callInstr.getfString().replace("\"", "");
        java.util.List<Value> exps = callInstr.getRealParameters();

        int order = 0;

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < fString.length(); i++) {
            char c = fString.charAt(i);
            if (c == '%') {
                String format = builder.toString();
                builder = new StringBuilder();
                printString(format);
                char cj = fString.charAt(i + 1);
                if (cj == 'd') {
                    printNumber(exps.get(order++));
                }
                i++;
            } else if (c == '\\') {
                String xy = "0x" + fString.substring(i + 1, i + 3);
                int num = Integer.decode(xy);
                if (num > 0) {
                    char t = (char) num;
                    if (t == '\n') {
                        builder.append("\\n");
                    }
                }
                i += 2;
            } else {
                builder.append(c);
            }
        }
        printString(builder.toString());
    }

    public void callGetInt(CallInstruction callInstr) {
        cb.addInstruction(new li(v0, 5));
        cb.addInstruction(new syscall());
        Registers reg = value2Register(callInstr);
        cb.addInstruction(new move(v0, reg));
    }

    private void printString(String s) {
        if (s.equals("")) {
            return;
        }
        String sname = "__fString_" + fStringCount++;
        MCData mcData = new MCData(sname, s);
        data.add(mcData);
        cb.addInstruction(new la(sname, 0, a0));
        cb.addInstruction(new li(v0, 4));
        cb.addInstruction(new syscall());
    }

    private void printNumber(Value v) {
        Registers reg = value2Register(v);
        cb.addInstruction(new move(reg, a0));
        cb.addInstruction(new li(v0, 1));
        cb.addInstruction(new syscall());
    }

    private int calculate(Operator op, int left, int right) {
        switch (op) {
            case PLUS:
                return left + right;
            case MINU:
                return left - right;
            case MULT:
                return left * right;
            case DIV:
                return left / right;
            case MOD:
                return left % right;
            default:
                return 0;
        }
    }

    private boolean compare(Operator op, int left, int right) {
        switch (op) {
            case EQL:
                return left == right;
            case NEQ:
                return left != right;
            case LSS:
                return left < right;
            case LEQ:
                return left <= right;
            case GRE:
                return left > right;
            case GEQ:
                return left >= right;
            default:
                return true;
        }
    }



    public Registers value2Register(Value value) {
        String name = value.getName().replaceAll("[@%]", "");
        if (val2Reg.containsKey(name)) {
            return val2Reg.get(name);
        }
        if (value instanceof ConstantInteger) {
            int num = ((ConstantInteger) value).getValue();
            if (num == 0) {
                return zero;
            } else {
                Registers reg = buildVirtualRegister();
                cb.addInstruction(new li(reg, num));
                return reg;
            }
        } else if (value instanceof GlobalVariable) {
            Value gVal = ((GlobalVariable) value).getValue();
            if (!(gVal instanceof ConstantArray)) {
                Registers reg = buildVirtualRegister();
                cb.addInstruction(new la(name, 0, reg));
                val2Reg.put(name, reg);
                return reg;
            }
            return null;
        } else {
            Registers reg = buildVirtualRegister();
            val2Reg.put(name, reg);
            return reg;
        }
    }

    private String BasicBlock2MCBlockName(BasicBlock bb) {
        // BasicBlockXXX
        String bbName = bb.getName();
        int index = Integer.parseInt(bbName.substring(10));
        return  "_" + cf.getName() + "_block_" + index;
    }

    private mnemonic op2mnemonic(Operator op) {
        switch (op) {
            case PLUS:
                return addu;
            case MINU:
                return sub;
            case MULT:
                return mul;
            case DIV:
                return div;
            case MOD:
                return rem;
            case EQL:
                return seq;
            case NEQ:
                return sne;
            case LSS:
                return slt;
            case LEQ:
                return sle;
            case GRE:
                return sgt;
            case GEQ:
                return sge;
            default:
                return nop;
        }
    }

    public void prologue() {
        // save the frame pointer
        cb.addInstruction(new move(sp, fp));

        // set the top of the stack
        cst = calculateSizeOfIRFunction(cif);
        raTop = cst;
        cb.addInstruction(new MCITypeInstruction(mnemonic.addiu, sp, sp, -cst));

        // read the arguments
        ArrayList<Parameter> args = cif.getParameters();
        for (int i = 0; i < args.size(); i++) {
            Parameter arg = args.get(i);
            Registers reg = value2Register(arg);
            if (i < 4) {
                // arguments are in the stack
                cb.addInstruction(new move(new MCRegisters("a" + i, false), reg));
            } else {
                // arguments are in the memory
                cb.addInstruction(new lw(fp, (i - 3) * 4, reg));
            }
        }

        // store frame pointer
        cb.addInstruction(new sw(sp, 0, fp));

        // set register ra ($31)
        String name = cif.getName().replace("@", "");
        if (!isLeafFunction.get(name)) {
            raTop -= 4;
            cb.addInstruction(new sw(sp, raTop, ra));
        }

        // load global variable which will be used in this function
        for (BasicBlock bb : cif.getBasicBlocks()) {
            for (Instruction instr : bb.getInstList()) {
                for (Value v : instr.getOperands()) {
                    if (v instanceof GlobalVariable) {
                        value2Register(v);
                    }
                }
            }
        }
    }

    private void epilogue(){
        String name = cf.getName();
        // reset $ra
        if(!isLeafFunction.get(name)) {
            cb.addInstruction(new lw(sp, functionSize.get(name) - 4, ra));
        }
        // reset stack pointer
        cb.addInstruction(new MCITypeInstruction(addiu, sp, sp, cst));

        // reset frame pointer
        cb.addInstruction(new lw(sp, 0, fp));

        // jump back or terminate
        if(!name.equals("main")) {
            cb.addInstruction(new jr(ra));
            cb.addInstruction(new MCInstruction());
        }
        else {
            cb.addInstruction(new li(v0, 10));
            cb.addInstruction(new syscall());
        }
    }

    public void stupidReallocateRegisters() {
        ArrayList<MCInstruction> instructionList = new ArrayList<>();
        HashMap<VirtualRegisters, Integer> indexMap = new HashMap<>();
        expand = 0;
        // 获取函数的指列令表
        for (MCBlock bb : cf.getBlocks()) {
            instructionList.addAll(bb.getInstructions());
        }
        for (MCInstruction instr : instructionList) {
            int i = 1;
            for (Registers reg : instr.use) {
                if (reg instanceof VirtualRegisters) {
                    Integer pos = indexMap.get(reg);
                    if (pos == null) {
                        pos = cst;
                        cst += 4;
                        indexMap.put((VirtualRegisters) reg, pos);
                    }
                    MCRegisters mcReg = (i == 1) ? t1 : t2;
                    instr.getBB().insertInstructionBefore(instr, new lw(sp, pos, mcReg));
                    instr.set(reg, mcReg);
                }
                i++;
            }
            for (Registers reg : instr.def) {
                if (reg instanceof VirtualRegisters) {
                    Integer pos = indexMap.get(reg);
                    if (pos == null) {
                        pos = cst;
                        cst += 4;
                        indexMap.put((VirtualRegisters) reg, pos);
                    }
                    instr.getBB().insertInstructionAfter(instr, new sw(sp, pos, t0));
                    instr.set(reg, t0);
                }
            }
        }
    }

    public void reallocateRegisters() {
        ArrayList<MCInstruction> instructionList = new ArrayList<>();
        Graph<Registers> interfereGraph = new Graph<>();
        MCRegisterPool pool = MCRegisterPool.getInstance(functionSize.get(cf.getName()));
        // 获取函数的指列令表
        for (MCBlock bb : cf.getBlocks()) {
            instructionList.addAll(bb.getInstructions());
        }
        // 获取指令相关的虚拟寄存器，在冲突图中初始化结点
        for (MCInstruction instr : instructionList) {
            for (Registers defReg : instr.def) {
                if (defReg instanceof VirtualRegisters) {
                    interfereGraph.addNode(defReg);
                }
            }
            for (Registers useReg : instr.use) {
                if (useReg instanceof VirtualRegisters) {
                    interfereGraph.addNode(useReg);
                }
            }
        }
        // 构建冲突图
        buildInterfereGraph(instructionList, interfereGraph);
        // 图着色算法分配空闲寄存器
        HashMap<Registers, MCRegisters> vir2mc = colorGraph(interfereGraph, pool);
        //HashMap<Registers, MCRegisters> vir2mc = new HashMap<>();
        // 将指令的寄存器替换为分配的寄存器
        resetRegister(instructionList, vir2mc, pool);
    }

    private void buildInterfereGraph(ArrayList<MCInstruction> instructionList, Graph<Registers> interfereGraph) {
        int instrListSize = instructionList.size();
        for (int i = 0; i < instrListSize; i++) {
            MCInstruction instr = instructionList.get(i);
            for (Registers defReg : instr.def) {
                if (defReg instanceof VirtualRegisters) {
                    int nextDef = instrListSize - 1;
                    // 正向寻找最近的重定义
                    for (int j = i + 1; j < instrListSize; j++) {
                        MCInstruction innerInstr = instructionList.get(j);
                        for (Registers innerReg : innerInstr.def) {
                            if (innerReg == defReg) {
                                nextDef = j;
                                break;
                            }
                        }
                        if (nextDef != instrListSize - 1) {
                            break;
                        }
                    }
                    // 从最近的重定义开始，逆向寻找最后的引用
                    int lastUse = i;
                    for (int k = nextDef; k > i; k--) {
                        MCInstruction innerInstr = instructionList.get(k);
                        for (Registers innerReg : innerInstr.use) {
                            if (innerReg == defReg) {
                                lastUse = k;
                                break;
                            }
                        }
                        if (lastUse != i) {
                            break;
                        }
                    }
                    // 从最后的引用开始，到该指令的位置，添加冲突链
                    for (int l = lastUse; l > i; l--) {
                        MCInstruction innerInstr = instructionList.get(l);
                        for (Registers innerReg : innerInstr.def) {
                            if (l == lastUse) {
                                // 最后一次引用和该次的定义不冲突
                                continue;
                            }
                            if (innerReg instanceof VirtualRegisters) {
                                interfereGraph.addDoubleWayEdge(defReg, innerReg);
                            }
                        }
                        for (Registers innerReg : innerInstr.use) {
                            if (innerReg == defReg) {
                                // 自身和自身不冲突
                                continue;
                            }
                            if (innerReg instanceof VirtualRegisters) {
                                interfereGraph.addDoubleWayEdge(defReg, innerReg);
                            }
                        }
                    }
                }
            }
        }
    }

    private HashMap<Registers, MCRegisters> colorGraph(Graph<Registers> graph, MCRegisterPool pool) {
        HashMap<Registers, MCRegisters> vir2mc = new HashMap<>();
        // 创建一个原图的拷贝
        Graph<Registers> tmp = new Graph<>(graph);
        Stack<Graph.Node<Registers>> nodeStack = new Stack<>();
        HashSet<MCRegisters> notUse;
        // 将度小于可分配的颜色数的任一结点从图上删除，放入栈中；
        // 若没有任意结点的度小于可分配的颜色数，将任一结点标记为不分配颜色，并将其从图上删除；
        // 重复此操作直至图为空。
        while (!tmp.isEmpty()) {
            Graph.Node<Registers> node = tmp.getSmallestDigitNode();
            if (node.getDigit() < pool.freeRegisters()) {
                nodeStack.push(node);
                tmp.removeNode(node);
            } else {
                tmp.removeNode(node);
            }
        }
        // 将栈中的结点依次弹出并涂色
        while (!nodeStack.isEmpty()) {
            Graph.Node<Registers> node = nodeStack.pop();
            MCRegisters color;
            // 获取相关结点的颜色
            notUse = new HashSet<>();
            for (Graph.Node<Registers> otherNode : graph.getRelatedNodes(node)) {
                MCRegisters nodeColor = otherNode.getContext().coloredBy();
                if (nodeColor != null) {
                    notUse.add(nodeColor);
                }
            }
            // 选择一个与相关结点不同色的颜色
            color = pool.getColor(notUse);
            if (color != null) {
                // 不存在冲突，给结点涂色
                node.getContext().color(color);
            } else {
                // 存在冲突，相邻结点重新选择颜色
                for (Graph.Node<Registers> otherNode : graph.getRelatedNodes(node)) {
                    HashSet<MCRegisters> otherNotUse = new HashSet<>();
                    MCRegisters nodeColor = otherNode.getContext().coloredBy();
                    // 若该邻居未涂色，换一个邻居
                    if (nodeColor == null) {
                        continue;
                    }
                    // 将邻居结点的颜色加入邻居结点的不可用色集合
                    otherNotUse.add(nodeColor);
                    // 将邻居结点的邻居结点的颜色加入不可用色集合
                    for (Graph.Node<Registers> anotherNode : graph.getRelatedNodes(otherNode)) {
                        MCRegisters anotherNodeColor = anotherNode.getContext().coloredBy();
                        if (anotherNodeColor != null) {
                            otherNotUse.add(anotherNodeColor);
                        }
                    }
                    // 基于扩展的不可用色集合分配一个新颜色
                    MCRegisters newNodeColor = pool.getColor(otherNotUse);
                    // 如果分配成功，就把邻居结点涂上新颜色，该结点涂上邻居结点的颜色
                    if (newNodeColor != null) {
                        otherNode.getContext().color(newNodeColor);
                        node.getContext().color(nodeColor);
                        break;
                    }
                }
            }
        }
        for (Graph.Node<Registers> node : graph.getNodeSet()) {
            vir2mc.putIfAbsent(node.getContext(), node.getContext().coloredBy());
        }
        return vir2mc;
    }

    private void resetRegister(ArrayList<MCInstruction> instructionList, HashMap<Registers, MCRegisters> vir2mc, MCRegisterPool pool) {
        for (MCInstruction instr : instructionList) {
            for (Registers useReg: instr.use) {
                if (useReg instanceof VirtualRegisters) {
                    colored(vir2mc, instr, (VirtualRegisters) useReg, pool);
                }
            }
            for (Registers defReg : instr.def) {
                if (defReg instanceof VirtualRegisters) {
                    colored(vir2mc, instr, (VirtualRegisters) defReg, pool);
                }
            }
        }
    }

    private void colored(HashMap<Registers, MCRegisters> vir2mc, MCInstruction instr, VirtualRegisters vReg, MCRegisterPool pool) {
        // 获取寄存器池
        // 获取该虚拟寄存器的颜色
        MCRegisters color = vir2mc.get(vReg);
        if (color != null) {
            // 如果被涂色，查看这个颜色的占用者
            VirtualRegisters user = pool.getUser(color);
            if (user == null) {
                // 该颜色未被占用，占用该颜色
                pool.useColor(vReg, color);
            } else if (!user.equals(vReg)) {
                // 该颜色被其他虚拟寄存器占用，抢占，将占用者的内容存入内存
                instr.getBB().insertInstructionBefore(instr, new sw(sp, pool.spilling(user, cf.getName(), functionSize), color));
                // 给这个颜色标上自己的名字
                pool.newUser(vReg, color);
                if (instr.use.contains(vReg)) {
                    // 如果该虚拟寄存器的值将被指令使用，查看该虚拟寄存器之前是否被抢占过
                    Integer index = pool.getSpilling(vReg);
                    if (index != null) {
                        // 被抢占过，将内容从内存中恢复
                        instr.getBB().insertInstructionBefore(instr, new lw(sp, index, color));
                    }
                } else if (instr.def.contains(vReg)) {
                    Integer index = pool.getSpilling(vReg);
                    if (index != null) {
                        instr.getBB().insertInstructionAfter(instr, new sw(sp, index, color));
                    }
                }
            }
        } else {
            // 如果未被涂色，查看是否已经占用颜色
            color = pool.getColor(vReg);
            if (color == null) {
                // 未占用颜色，尝试分配未占用颜色
                color = pool.allocateFreeColor();
                if (color != null) {
                    // 有未占用颜色，占用该颜色
                    pool.useColor(vReg, color);
                } else {
                    // 无未占用颜色，随机抢占一个颜色
                    color = pool.allocateBusyColor();
                    // 获取被抢占的冤大头
                    VirtualRegisters user = pool.getUser(color);
                    // 将冤大头的内容存入内存
                    instr.getBB().insertInstructionBefore(instr, new sw(sp, pool.spilling(user, cf.getName(), functionSize), color));
                    // 给冤大头的颜色标上自己的名字
                    pool.newUser(vReg, color);
                    if (instr.use.contains(vReg)) {
                        // 如果该虚拟寄存器的值将被指令使用，查看该虚拟寄存器之前是否被抢占过
                        Integer index = pool.getSpilling(vReg);
                        if (index != null) {
                            // 被抢占过，将内容从内存中恢复
                            instr.getBB().insertInstructionBefore(instr, new lw(sp, index, color));
                        }
                    } else if (instr.def.contains(vReg)) {
                        Integer index = pool.getSpilling(vReg);
                        if (index != null) {
                            instr.getBB().insertInstructionAfter(instr, new sw(sp, index, color));
                        }
                    }
                }
            }
            // 已占用颜色，不需进行任何操作
        }
        // 使用颜色
        instr.set(vReg, color);
    }

    public int calculateSizeOfIRFunction(Function function) {
        int S = 0, R = 0, A = 0;
        boolean isLeaf = true;
        String name = function.getName().replace("@", "");
        for (BasicBlock bb : function.getBasicBlocks()) {
            for (Instruction instr : bb.getInstList()) {
                if (instr instanceof AllocaInstruction) {
                    S += ((AllocaInstruction) instr).getAllocatedSize();
                } else if (instr instanceof CallInstruction) {
                    int callSize = ((CallInstruction) instr).getCallSize();
                    if (isLeaf && !((CallInstruction) instr).getFunctionName().equals("printf") && !((CallInstruction) instr).getFunctionName().equals("getint")) {
                        isLeaf = false;
                        R = 4;
                    }
                    if (callSize > A) {
                        A = callSize;
                    }
                }
            }
        }
        int size = S + R + A + 4; // 4 is fp
        isLeafFunction.put(name, isLeaf);
        functionSize.put(name, size);
        return size;
    }

    public VirtualRegisters buildVirtualRegister() {
        return new VirtualRegisters();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(".data\n");
        for (MCData line : data) {
            builder.append(line);
        }
        builder.append(".text\n");
        for (MCFunction function : functions) {
            builder.append(function);
        }
        return builder.toString();
    }
}
