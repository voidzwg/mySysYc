package backend.MachineCode;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class MCData {
    // .data instructions
    private String name;
    /*
    mode = 0 : String
    mode = 1 : Integer
    mode = 2 : Array
     */
    private final int mode;
    private Integer num = null;
    private String str = null;
    private ArrayList<Integer> initials = null;
    private int size;

    public MCData(String name, String str) {
        this.name = name;
        this.str = str;
        this.mode = 0;
    }

    public MCData(String name, int num) {
        this.name = name;
        this.num = num;
        this.mode = 1;
    }

    public MCData(String name, ArrayList<Integer> initials) {
        this.name = name;
        this.initials = initials;
        this.mode = 2;
    }

    public String getName() {
        return name;
    }

    public int getMode() {
        return mode;
    }

    public Integer getNum() {
        return num;
    }

    public String getStr() {
        return str;
    }

    public ArrayList<Integer> getInitials() {
        return initials;
    }

    public void setSize(int needInitialize) {
        if (needInitialize != -1) {
            this.size = needInitialize;
        } else {
            this.size = initials.size() * 32;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\t");
        builder.append(name).append(":");
        if (num != null) {
            builder.append(" .word ").append(num).append("\n");
        } else if (str != null) {
            builder.append(" .asciiz ").append("\"").append(str).append("\"").append("\n");
        } else if (initials != null) {
            if (initials.size() > 0) {
                builder.append("\n");
                for (Integer integer : initials) {
                    builder.append("\t\t").append(".word ").append(integer).append("\n");
                }
            } else {
                builder.append(" .word 0:").append(size / 4).append("\n");
            }
        }
        return builder.toString();
    }
}
