package IR.Values;

import Error.CompileErrorException;
import IR.Types.FunctionType;
import IR.Types.PointerType;
import IR.Types.Type;
import IR.Values.Instructions.Instruction;
import IR.Values.Instructions.Mem.AllocaInstruction;
import IR.Values.Instructions.Mem.GEPInstruction;
import IR.Values.Instructions.Mem.StoreInstruction;
import IR.Values.Instructions.Terminator.CallInstruction;
import utils.List;

import java.util.ArrayList;
import java.util.Iterator;

import static Error.Error.ParamNumbersMismatched;
import static Error.Error.ParamTypeMismatched;
import static IR.Types.VoidType.Void;

public class Function extends Value {
    private final List<BasicBlock, Function> basicBlocks;
    private final ArrayList<Parameter> parameters;

    public Function(Type type, String name) {
        super(new FunctionType(type), "@" + name);
        basicBlocks = new List<>(this);
        parameters = new ArrayList<>();
    }

    public List<BasicBlock, Function> getBasicBlocks() {
        return basicBlocks;
    }

    public ArrayList<Parameter> getParameters() {
        return parameters;
    }

    public void addBasicBlock(BasicBlock basicBlock) {
        basicBlocks.addLast(basicBlock);
    }

    public void addParameter(Parameter parameter) {
        parameters.add(parameter);
    }

    public void match(ArrayList<Value> realParameters, int line, int col) throws CompileErrorException {
        if (realParameters.size() != parameters.size()) {
            CompileErrorException.error(ParamNumbersMismatched, line, col);
        }
        for (int i = 0; i < parameters.size(); i++) {
            Value parameter = realParameters.get(i);
            Type type = parameter.getType();
            if (parameter instanceof GlobalVariable) {
                type = ((GlobalVariable) parameter).getValue().getType();
            } else if (parameter instanceof AllocaInstruction) {
                type = ((PointerType) parameter.getType()).gettType();
            } else if (parameter instanceof GEPInstruction) {
                type = ((PointerType) parameter.getType()).gettType();
            }
            if (!type.equals(parameters.get(i).getType())) {
                CompileErrorException.error(ParamTypeMismatched, line, col);
            }
        }
    }

    private boolean needName(Instruction instr) {
        return !(instr instanceof StoreInstruction
                || (instr instanceof CallInstruction && instr.getType() == Void)
        );
    }

    public void reorder() {
        int count = 0;
        for (Parameter param : parameters) {
            param.setName("%" + count++);
        }
        for (BasicBlock bb : basicBlocks) {
            bb.setName(String.valueOf(count++));
            for (Instruction instr : bb.getInstList()) {
                if (needName(instr)) {
                    instr.setName("%" + count++);
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("define ").append(type).append(" ").append(name).append("(");
        for (Iterator<Parameter> it = parameters.iterator(); it.hasNext(); ) {
            Parameter parameter = it.next();
            builder.append(parameter.getType());
            if (it.hasNext()) {
                builder.append(", ");
            }
        }
        builder.append(") #0 {\n");
        for (BasicBlock basicBlock : basicBlocks) {
            builder.append(basicBlock);
        }
        builder.append("}\n");
        return builder.toString();
    }
}
