package backend.MachineCode.MCInstructions;

// some basic instructions' mnemonic of MIPS-32
public enum mnemonic {
    // R-R calculation
    addu,
    sub,
    mult,
    div,

    slt,
    sle,
    sgt,
    sge,
    seq,
    sne,

    and,
    or,
    xor,

    // R-I calculation
    addiu,

    slti,

    andi,
    ori,
    xori,

    // load and store
    li,
    la,
    lw,
    sw,

    // branch
    bnez,
    beqz,
    bne,
    beq,

    // jump
    j,
    jr,
    jal,

    // special
    mfhi,
    mflo,

    // trap
    syscall,

    // all zero
    nop,
    ;
}
