package Error;

import java.util.ArrayList;
import java.util.HashMap;

public class CompileErrorException extends RuntimeException implements Comparable<CompileErrorException> {
    private final int line;
    private final int col;
    private final String eCode;
    public static final HashMap<Integer, ArrayList<Integer>> eList = new HashMap<>();

    public CompileErrorException(Error e, int line, int col) {
        super(e.getDescription());
        this.eCode = e.getCode();
        this.line = line;
        this.col = col;
    }

    public String getECode() {
        return eCode;
    }

    public int getLine() {
        return line;
    }

    public int getCol() {
        return col;
    }

    public ArrayList<Integer> getPos() {
        ArrayList<Integer> pos = new ArrayList<>();
        pos.add(line);
        pos.add(col);
        return pos;
    }

    public static void error(Error e, int line, int col) throws CompileErrorException {
        ArrayList<Integer> arrayList;
        CompileErrorException compileErrorException = new CompileErrorException(e, line, col);
        if (eList.containsKey(line)) {
            arrayList = eList.get(line);
            if (arrayList.contains(col)){
                return;
            }
            arrayList.add(col);
        } else {
            arrayList = new ArrayList<>();
            arrayList.add(col);
            eList.put(line, arrayList);
        }
        throw compileErrorException;
    }

    @Override
    public int compareTo(CompileErrorException e) {
        if (this.line == e.line) {
            if (this.col - e.col <= 0) {
                return -1;
            } else {
                return 1;
            }
        }
        return this.line - e.line;
    }
}
