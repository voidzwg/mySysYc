package IR.Values.Instructions.Terminator;

import IR.Types.FunctionType;
import IR.Types.Type;
import IR.Values.*;
import IR.Values.Instructions.Mem.GEPInstruction;

import java.util.ArrayList;
import java.util.List;

import static IR.Types.IntegerType.i32;
import static IR.Values.ConstantInteger.constantZero;
import static IR.Values.Instructions.Operator.CALL;

public class CallInstruction extends TerminatorBase {
    private final Function function;
    private final ArrayList<Value> realParameters;
    private String fString;
    private final boolean isPrintf;

    public CallInstruction(BasicBlock basicBlock, Function function, ArrayList<Value> funcRParams) {
        super(basicBlock, ((FunctionType) function.getType()).getReturnType(), CALL);
        this.function = function;
        realParameters = new ArrayList<>(funcRParams);
        if (function.getName().equals("printf")) {
            isPrintf = true;
            this.fString = ((ConstantString) ((GlobalVariable) funcRParams.get(0)).getValue()).getStr();
        } else {
            isPrintf = false;
            this.fString = "";
        }
    }

    // get memory space of the call instruction need in bytes
    public int getCallSize() {
        if (realParameters.size() <= 4) {
            // 4 registers a0~3 to store the value, do not need memory
            return 0;
        } else {
            return (realParameters.size() - 4) * 4;
        }
    }

    public String getfString() {
        return fString;
    }

    public List<Value> getRealParameters() {
        if (isPrintf) {
            return realParameters.subList(1, realParameters.size());
        } else {
            return realParameters;
        }
    }

    public Type getReturnType() {
        return ((FunctionType) function.getType()).getReturnType();
    }

    public String getFunctionName() {
        return function.getName();
    }

    private String getParamString(Value param) {
        String s;
        if (param instanceof ConstantInteger) {
            s = param.toString();
        } else if (param instanceof GlobalVariable && ((GlobalVariable) param).isConstant()) {
            s = String.valueOf(((GlobalVariable) param).getValue().toString());
        } else {
            s = param.getType() + " " + param.getName();
        }
        return s;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Type type = this.getReturnType();
        if (type == i32) {
            builder.append(this.getName()).append(" = ");
        }
        builder.append("call ").append(type).append(" ");
        String name = function.getName();
        if (isPrintf) {
            GlobalVariable fString = (GlobalVariable) realParameters.get(0);
            builder.append("(i8*, ...) @printf(i8* getelementptr(").append(fString.getValue().getType());
            builder.append(", ").append(fString.getType()).append(" ").append(fString.getName()).append(", i32 0, i32 0)");
            for (int i = 1; i < realParameters.size(); i++) {
                Value param = realParameters.get(i);
                String s = getParamString(param);
                builder.append(", ").append(s);
            }
            builder.append(")");
        } else {
            if (name.equals("getint")) {
                name = "@" + name;
            }
            builder.append(name).append("(");
            if (realParameters.size() > 0) {
                Value param = realParameters.get(0);
                String s = getParamString(param);
                builder.append(s);
                for (int i = 1; i < realParameters.size(); i++) {
                    param = realParameters.get(i);
                    s = getParamString(param);
                    builder.append(", ").append(s);
                }
            }
            builder.append(")");
        }
        return builder.toString();
    }
}
