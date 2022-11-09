package backend.Registers;

import java.util.HashMap;

public class RegistersNameMap {
    private static final RegistersNameMap map = new RegistersNameMap();

    private final HashMap<Integer, String> num2name = new HashMap<>();
    private final HashMap<String, Integer> name2num = new HashMap<>();

    public static RegistersNameMap getInstance() {
        return map;
    }

    private RegistersNameMap() {
        // name of 32 registers
        name2num.put("zero", 0);
        name2num.put("at", 1);
        name2num.put("v0", 2);
        name2num.put("v1", 3);
        name2num.put("a0", 4);
        name2num.put("a1", 5);
        name2num.put("a2", 6);
        name2num.put("a3", 7);
        name2num.put("t0", 8);
        name2num.put("t1", 9);
        name2num.put("t2", 10);
        name2num.put("t3", 11);
        name2num.put("t4", 12);
        name2num.put("t5", 13);
        name2num.put("t6", 14);
        name2num.put("t7", 15);
        name2num.put("s0", 16);
        name2num.put("s1", 17);
        name2num.put("s2", 18);
        name2num.put("s3", 19);
        name2num.put("s4", 20);
        name2num.put("s5", 21);
        name2num.put("s6", 22);
        name2num.put("s7", 23);
        name2num.put("t8", 24);
        name2num.put("t9", 25);
        name2num.put("k0", 26);
        name2num.put("k1", 27);
        name2num.put("gp", 28);
        name2num.put("sp", 29);
        name2num.put("fp", 30);
        name2num.put("ra", 31);

        // id of 32 registers
        num2name.put(0, "zero");
        num2name.put(1, "at");
        num2name.put(2, "v0");
        num2name.put(3, "v1");
        num2name.put(4, "a0");
        num2name.put(5, "a1");
        num2name.put(6, "a2");
        num2name.put(7, "a3");
        num2name.put(8, "t0");
        num2name.put(9, "t1");
        num2name.put(10, "t2");
        num2name.put(11, "t3");
        num2name.put(12, "t4");
        num2name.put(13, "t5");
        num2name.put(14, "t6");
        num2name.put(15, "t7");
        num2name.put(16, "s0");
        num2name.put(17, "s1");
        num2name.put(18, "s2");
        num2name.put(19, "s3");
        num2name.put(20, "s4");
        num2name.put(21, "s5");
        num2name.put(22, "s6");
        num2name.put(23, "s7");
        num2name.put(24, "t8");
        num2name.put(25, "t9");
        num2name.put(26, "k0");
        num2name.put(27, "k1");
        num2name.put(28, "gp");
        num2name.put(29, "sp");
        num2name.put(30, "fp");
        num2name.put(31, "ra");
    }

    public int getRegisterNum(String name) {
        return name2num.getOrDefault(name, -1);
    }

    public String getRegisterName(Integer num) {
        return num2name.getOrDefault(num, "");
    }
}
