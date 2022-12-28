package IR.Values;

import Error.CompileErrorException;
import IR.Types.FunctionType;
import IR.Types.PointerType;
import IR.Types.Type;
import IR.Values.Instructions.CallInstruction;
import IR.Values.Instructions.Instruction;
import IR.Values.Instructions.Mem.AllocaInstruction;
import IR.Values.Instructions.Mem.GEPInstruction;
import IR.Values.Instructions.Mem.StoreInstruction;
import IR.Values.Instructions.Terminator.BrInstruction;
import utils.List;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import static Error.Error.ParamNumbersMismatched;
import static Error.Error.ParamTypeMismatched;
import static IR.Types.VoidType.Void;
import static IR.Visitor.LLVM_VERSION;

public class Function extends Value {
    private final List<BasicBlock, Function> basicBlocks;
    private final ArrayList<Parameter> parameters;
    private BasicBlock retBB;

    public Function(Type type, String name) {
        super(new FunctionType(type), "@" + name);
        basicBlocks = new List<>(this);
        parameters = new ArrayList<>();
    }

    public BasicBlock getRetBB() {
        return retBB;
    }

    public void setRetBB(BasicBlock retBB) {
        this.retBB = retBB;
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
        if (realParameters == null) {
            return;
        }
        if (realParameters.size() != parameters.size()) {
            CompileErrorException.error(ParamNumbersMismatched, line, col);
        }
        for (int i = 0; i < parameters.size(); i++) {
            Value parameter;
            if (i < realParameters.size()) {
                parameter = realParameters.get(i);
            } else {
                parameter = null;
            }
            if (parameter == null) {
                return;
            }
            Type type = parameter.getType();
            if (!type.equals(parameters.get(i).getType())) {
                CompileErrorException.error(ParamTypeMismatched, line, col);
            }
        }
    }

    private boolean needName(Instruction instr) {
        return !(instr instanceof StoreInstruction
                || (instr instanceof CallInstruction && instr.getType() == Void)
                || instr instanceof BrInstruction
        );
    }

    public void reorder() {
        int count;
        int blockCount;
        switch (LLVM_VERSION) {
            case 8:
                count = 1;
                blockCount = 0;
                for (Parameter param : parameters) {
                    param.setName(param.getName());
                }
                for (BasicBlock bb : basicBlocks) {
                    bb.setName("basicBlock" + blockCount++);
                    for (Instruction instr : bb.getInstList()) {
                        if (needName(instr)) {
                            instr.setName("%" + count++);
                        }
                    }
                }
                break;
            case 6:
                count = 0;
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
                break;
            default:
                break;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("define ").append(type).append(" ").append(name).append("(");
        for (Iterator<Parameter> it = parameters.iterator(); it.hasNext(); ) {
            Parameter parameter = it.next();
            builder.append(parameter.getType());
            if (LLVM_VERSION == 8) {
                builder.append(" ").append(parameter.getName());
            }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Function function = (Function) o;
        return Objects.equals(parameters, function.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameters);
    }
}
