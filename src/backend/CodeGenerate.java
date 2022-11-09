package backend;

import IR.Module;
import IR.Types.ArrayType;
import IR.Types.IntegerType;
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
import backend.MachineCode.MCBlock;
import backend.MachineCode.MCData;
import backend.MachineCode.MCFunction;
import backend.MachineCode.MCInstructions.MCITypeInstructions.MCITypeInstruction;
import backend.MachineCode.MCInstructions.MCITypeInstructions.li;
import backend.MachineCode.MCInstructions.MCInstruction;
import backend.MachineCode.MCInstructions.MCMemory.la;
import backend.MachineCode.MCInstructions.MCMemory.lw;
import backend.MachineCode.MCInstructions.MCMemory.sw;
import backend.MachineCode.MCInstructions.MCRTypeInstructions.MCRTypeInstruction;
import backend.MachineCode.MCInstructions.MCRTypeInstructions.div;
import backend.MachineCode.MCInstructions.MCRTypeInstructions.move;
import backend.MachineCode.MCInstructions.MCRTypeInstructions.mult;
import backend.MachineCode.MCInstructions.MCTerminator.syscall;
import backend.MachineCode.MCInstructions.MCjump.jal;
import backend.MachineCode.MCInstructions.MCjump.jr;
import backend.MachineCode.MCInstructions.mnemonic;
import backend.MachineCode.MCInstructions.specialInstruction.mfhi;
import backend.MachineCode.MCInstructions.specialInstruction.mflo;
import backend.Registers.MCRegisterPool;
import backend.Registers.MCRegisters;
import backend.Registers.Registers;
import backend.Registers.VirtualRegisters;
import utils.Graph;
import utils.List;

import java.util.*;

import static IR.Types.IntegerType.i32;
import static backend.MachineCode.MCInstructions.mnemonic.*;
import static backend.Registers.MCRegisterPool.*;

public class CodeGenerate {
    private final Module irModule;
    private final ArrayList<MCData> data;
    private final ArrayList<MCFunction> functions;
    private final HashMap<String, Boolean> isLeafFunction;
    private final HashMap<String, Integer> functionSize;

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
        if (type instanceof ArrayType) {
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
        } else if (type instanceof IntegerType) {
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

        for (BasicBlock bb : blocks) {
            cb = new MCBlock(cf, isEntry);
            if (isEntry) {
                isEntry = false;
            }
            generateBasicBlock(bb);
        }
        functions.add(cf);
        //reallocateRegisters();
        stupidReallocateRegisters();
        int nowSize = cst;
        for (MCInstruction instr : cf.getBlocks().get(0).getInstructions()) {
            if (instr.def.contains(sp) && instr.op == addiu) {
                assert instr instanceof MCITypeInstruction;
                ((MCITypeInstruction) instr).setImmediate(-nowSize);
                nowSize = -nowSize;
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
            Registers reg = value2Register(gepInstr);
            ArrayList<Value> operands = gepInstr.getOperands();
            Value ptr = gepInstr.getTargetValue();
            Type type;
            int offset = 0;
            if (ptr instanceof AllocaInstruction) {
                type = ((AllocaInstruction) ptr).getAllocated();
            } else {
                assert ptr instanceof GlobalVariable;
                type = ((GlobalVariable) ptr).getValue().getType();
            }
            Registers base = value2Register(ptr);
            String name = ptr.getName().replace("@", "");
            for (int i = 1; i < operands.size(); i++) {
                Value v = operands.get(i);
                if (v instanceof ConstantInteger) {
                    ConstantInteger integer = (ConstantInteger) v;
                    int position = integer.getValue();  // position in this level
                    offset += position * type.getSize();  // offset += position * level size
                } else {
                    generateInstruction((Instruction) v);  // calculate the offset
                    Registers result = value2Register(v);  // get the result of calculation
                    cb.addInstruction(new MCRTypeInstruction(mnemonic.addu, base, result, base));  // calculate new base
                }
                if (type instanceof ArrayType) {
                    type = ((ArrayType) type).getElementType();
                }
            }
            if (ptr instanceof GlobalVariable) {
                cb.addInstruction(new la(name, offset, reg));
            } else {
                cb.addInstruction(new MCITypeInstruction(mnemonic.addiu, base, reg, offset));
            }
        } else if (instr instanceof BinaryInstruction) {
            BinaryInstruction bInstr = (BinaryInstruction) instr;
            Registers result = value2Register(bInstr);
            Value leftValue = bInstr.getOperands().get(0);
            Value rightValue = bInstr.getOperands().get(1);
            mnemonic op = operator2RType(instr.getOp());
            if (leftValue instanceof ConstantInteger && rightValue instanceof ConstantInteger) {
                cb.addInstruction(new li(result, calculate(instr.getOp(), ((ConstantInteger) leftValue).getValue(), ((ConstantInteger) rightValue).getValue())));
            } else if (leftValue instanceof LoadInstruction && rightValue instanceof ConstantInteger) {
                switch (instr.getOp()) {
                    case PLUS:
                        cb.addInstruction(new MCITypeInstruction(addiu, value2Register(leftValue), result, ((ConstantInteger) rightValue).getValue()));
                        break;
                    case MINU:
                        cb.addInstruction(new MCITypeInstruction(addiu, value2Register(leftValue), result, -((ConstantInteger) rightValue).getValue()));
                        break;
                    case MULT:
                        cb.addInstruction(new mult(value2Register(leftValue), value2Register(rightValue)));
                        cb.addInstruction(new mflo(result));
                        break;
                    case DIV:
                        cb.addInstruction(new div(value2Register(leftValue), value2Register(rightValue)));
                        cb.addInstruction(new mflo(result));
                        break;
                    case MOD:
                        cb.addInstruction(new div(value2Register(leftValue), value2Register(rightValue)));
                        cb.addInstruction(new mfhi(result));
                        break;
                    default:
                        break;
                }
            } else if (leftValue instanceof ConstantInteger && rightValue instanceof LoadInstruction) {
                switch (instr.getOp()) {
                    case PLUS:
                        cb.addInstruction(new MCITypeInstruction(addu, value2Register(rightValue), result, ((ConstantInteger) leftValue).getValue()));
                        break;
                    case MINU:
                        cb.addInstruction(new MCRTypeInstruction(sub, value2Register(leftValue), value2Register(rightValue), result));
                        break;
                    case MULT:
                        cb.addInstruction(new mult(value2Register(leftValue), value2Register(rightValue)));
                        cb.addInstruction(new mflo(result));
                        break;
                    case DIV:
                        cb.addInstruction(new div(value2Register(leftValue), value2Register(rightValue)));
                        cb.addInstruction(new mflo(result));
                        break;
                    case MOD:
                        cb.addInstruction(new div(value2Register(leftValue), value2Register(rightValue)));
                        cb.addInstruction(new mfhi(result));
                        break;
                    default:
                        break;
                }
            } else {
                switch (instr.getOp()) {
                    case PLUS:
                    case MINU:
                        cb.addInstruction(new MCRTypeInstruction(op, value2Register(leftValue), value2Register(rightValue), result));
                        break;
                    case MULT:
                        cb.addInstruction(new mult(value2Register(leftValue), value2Register(rightValue)));
                        cb.addInstruction(new mflo(result));
                        break;
                    case DIV:
                        cb.addInstruction(new div(value2Register(leftValue), value2Register(rightValue)));
                        cb.addInstruction(new mflo(result));
                        break;
                    case MOD:
                        cb.addInstruction(new div(value2Register(leftValue), value2Register(rightValue)));
                        cb.addInstruction(new mfhi(result));
                        break;
                    default:
                        break;
                }
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
            if (c != '%') {
                builder.append(c);
            } else {
                String format = builder.toString();
                builder = new StringBuilder();
                printString(format);
                char cj = fString.charAt(i + 1);
                if (cj == 'd') {
                    printNumber(exps.get(order++));
                }
                i++;
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

    public int calculate(Operator op, int left, int right) {
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

    // binary instruction operator to RType instruction
    public mnemonic operator2RType(Operator op) {
        switch (op) {
            case PLUS:
                return addu;
            case MINU:
                return sub;
            case MULT:
                return mult;
            case DIV:
                return div;
            case AND:
                return and;
            case OR:
                return or;
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
//        for (BasicBlock bb : cif.getBasicBlocks()) {
//            for (Instruction instr : bb.getInstList()) {
//                for (Value v : instr.getOperands()) {
//                    if (v instanceof GlobalVariable) {
//                        value2Register(v);
//                    }
//                }
//            }
//        }
    }

    private void epilogue(){
        String name = cf.getName();
        // reset $ra
        if(!isLeafFunction.get(name)) {
            cb.addInstruction(new lw(sp, functionSize.get(name) - 4, ra));
        }
        // reset stack pointer
        cb.addInstruction(new MCITypeInstruction(addiu, sp, sp, functionSize.get(name)));

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
        int size = S + R + A;
        isLeafFunction.put(name, isLeaf);
        functionSize.put(name, size);
        return size;
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
                cb.addInstruction(new lw(name, 0, reg));
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
