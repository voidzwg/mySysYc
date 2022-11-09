package IR.Values.Instructions.Terminator;

import IR.Types.Type;
import IR.Values.BasicBlock;
import IR.Values.Instructions.Instruction;
import IR.Values.Instructions.Operator;

public class TerminatorBase extends Instruction {
    public TerminatorBase(BasicBlock basicBlock, Type type, Operator op) {
        super(basicBlock, type, op);
    }
}