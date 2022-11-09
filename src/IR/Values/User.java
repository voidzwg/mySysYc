package IR.Values;

import IR.Types.Type;
import IR.Use;
import java.util.ArrayList;

public class User extends Value {
    private final ArrayList<Value> operands;

    public User(Type type, String name) {
        super(type, name);
        operands = new ArrayList<>();
    }

    public ArrayList<Value> getOperands() {
        return operands;
    }

    public void addOperand(Value value) {
        operands.add(value);
    }
}
