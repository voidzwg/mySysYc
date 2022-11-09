package backend.Registers;

import java.util.*;

public class MCRegisterPool {
    private static final TreeSet<MCRegisters> canBeAllocated = new TreeSet<>(Comparator.comparingInt(Registers::getId));
    private static final TreeMap<MCRegisters, VirtualRegisters> usingMap = new TreeMap<>(Comparator.comparingInt(Registers::getId));
    private static final HashMap<VirtualRegisters, Integer> spillingMap = new HashMap<>();
    private static int top = 0;
    private static final MCRegisterPool pool = new MCRegisterPool();
    private static final TreeMap<MCRegisters, Boolean> toMemory = new TreeMap<>(Comparator.comparingInt(Registers::getId));

    private MCRegisterPool() {
    }

    public static MCRegisterPool getInstance(int stackTop) {
        canBeAllocated.add(t0);
        canBeAllocated.add(t1);
        canBeAllocated.add(t2);
        canBeAllocated.add(t3);
        canBeAllocated.add(t4);
        canBeAllocated.add(t5);
        canBeAllocated.add(t6);
        canBeAllocated.add(t7);
        canBeAllocated.add(t8);
        canBeAllocated.add(t9);
        canBeAllocated.add(s0);
        canBeAllocated.add(s1);
        canBeAllocated.add(s2);
        canBeAllocated.add(s3);
        canBeAllocated.add(s4);
        canBeAllocated.add(s5);
        canBeAllocated.add(s6);

        toMemory.put(s7, false);
        toMemory.put(fp, false);
        toMemory.replace(s7, false);
        toMemory.replace(fp, false);

        usingMap.clear();
        spillingMap.clear();
        top = stackTop;

        return pool;
    }

    public boolean canBeAllocated(MCRegisters color) {
        return canBeAllocated.contains(color);
    }

    public VirtualRegisters getUser(MCRegisters color) {
        return usingMap.get(color);
    }

    public void useColor(VirtualRegisters user, MCRegisters color) {
        canBeAllocated.remove(color);
        usingMap.put(color, user);
    }

    public int spilling(VirtualRegisters user, String fName, HashMap<String, Integer> map) {
        if (spillingMap.containsKey(user)) {
            return spillingMap.get(user);
        } else {
            spillingMap.put(user, top);
            map.replace(fName, top);
            top += 4;
            return top;
        }
    }

    public void newUser(VirtualRegisters user, MCRegisters color) {
        usingMap.replace(color, user);
    }

    public Integer getSpilling(VirtualRegisters user) {
        return spillingMap.get(user);
    }

    public MCRegisters getColor(VirtualRegisters user) {
        for (Map.Entry<MCRegisters, VirtualRegisters> entry : usingMap.entrySet()) {
            if (entry.getValue().equals(user)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public MCRegisters allocateFreeColor() {
        Random r = new Random();
        if (canBeAllocated.size() > 0) {
            int i = r.nextInt(canBeAllocated.size());
            Iterator<MCRegisters> it = canBeAllocated.iterator();
            while (i > 0) {
                it.next();
                i--;
            }
            return it.next();
        }
        return null;
    }

    public MCRegisters allocateBusyColor() {
        Random r = new Random();
        int i = r.nextInt(usingMap.size());
        for (MCRegisters color : usingMap.keySet()) {
            if (i == 0) {
                return color;
            }
            i--;
        }
        return null;
    }

    public int freeRegisters() {
        return canBeAllocated.size();
    }

    public MCRegisters getColor(HashSet<MCRegisters> notUse) {
        for (MCRegisters reg : canBeAllocated) {
            if (!notUse.contains(reg)) {
                return reg;
            }
        }
        return null;
    }



    public static MCRegisters zero = new MCRegisters("zero", false);

    public static MCRegisters at = new MCRegisters("at", false);

    public static MCRegisters v0 = new MCRegisters("v0", false);
    public static MCRegisters v1 = new MCRegisters("v1", false);

    public static MCRegisters a0 = new MCRegisters("a0", false);
    public static MCRegisters a1 = new MCRegisters("a1", false);
    public static MCRegisters a2 = new MCRegisters("a2", false);
    public static MCRegisters a3 = new MCRegisters("a3", false);

    public static MCRegisters t0 = new MCRegisters("t0", false);
    public static MCRegisters t1 = new MCRegisters("t1", false);
    public static MCRegisters t2 = new MCRegisters("t2", false);
    public static MCRegisters t3 = new MCRegisters("t3", false);
    public static MCRegisters t4 = new MCRegisters("t4", false);
    public static MCRegisters t5 = new MCRegisters("t5", false);
    public static MCRegisters t6 = new MCRegisters("t6", false);
    public static MCRegisters t7 = new MCRegisters("t7", false);

    public static MCRegisters s0 = new MCRegisters("s0", false);
    public static MCRegisters s1 = new MCRegisters("s1", false);
    public static MCRegisters s2 = new MCRegisters("s2", false);
    public static MCRegisters s3 = new MCRegisters("s3", false);
    public static MCRegisters s4 = new MCRegisters("s4", false);
    public static MCRegisters s5 = new MCRegisters("s5", false);
    public static MCRegisters s6 = new MCRegisters("s6", false);
    public static MCRegisters s7 = new MCRegisters("s7", false);

    public static MCRegisters t8 = new MCRegisters("t8", false);
    public static MCRegisters t9 = new MCRegisters("t9", false);

    public static MCRegisters k0 = new MCRegisters("k0", false);
    public static MCRegisters k1 = new MCRegisters("k1", false);

    public static MCRegisters gp = new MCRegisters("gp", false);
    public static MCRegisters sp = new MCRegisters("sp", false);

    public static MCRegisters fp = new MCRegisters("fp", false);  // also named "s8"

    public static MCRegisters ra = new MCRegisters("ra", false);
}
