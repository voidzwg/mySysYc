package IR.Types;

import java.util.ArrayList;
import java.util.Objects;

public class FunctionType extends Type {
    private ArrayList<Type> funcFParams;
    private final Type returnType;

    public FunctionType(Type returnType) {
        this.funcFParams = new ArrayList<>();
        this.returnType = returnType;
    }

    public void addFuncFParam(Type funcFParam) {
        funcFParams.add(funcFParam);
    }

    public void setFuncFParams(ArrayList<Type> funcFParams) {
        this.funcFParams = funcFParams;
    }

    public ArrayList<Type> getFuncFParams() {
        return funcFParams;
    }

    public Type getReturnType() {
        return returnType;
    }

    @Override
    public boolean isFunctionType() {
        return true;
    }

    @Override
    public Type getType() {
        return this;
    }

    @Override
    public String toString() {
        return returnType.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        FunctionType functionType = (FunctionType) o;
        if (!Objects.equals(this.returnType, functionType.returnType)) {
            return false;
        }
        if (this.funcFParams.size() != functionType.funcFParams.size()) {
            return false;
        }
        return Objects.equals(this.funcFParams, functionType.funcFParams);
    }
}
