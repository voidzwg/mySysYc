package utils;

import java.util.ArrayList;

public class Matrix<ElementType> {
    private final ArrayList<ArrayList<ElementType>> matrix;
    private int row = 0, col = 0;

    public Matrix() {
        this.matrix = new ArrayList<>();
    }

    public Matrix(int row, int col) {
        if (row >= 0 && col >= 0 && row + col > 0) {
            this.row = row;
            this.col = col;
            this.matrix = new ArrayList<>(row);
        } else {
            if (row < 0 || col < 0) {
                throw new IllegalArgumentException("Illegal Capacity: " + row + ", " + col);
            }
            this.matrix = new ArrayList<>();
        }
    }

    public Matrix(int row, int col, ElementType initial) {
        this(row, col);
        for (int i = 1; i <= row; i++) {
            ArrayList<ElementType> newRow = new ArrayList<>(col);
            newRow.add(initial);
            this.matrix.add(newRow);
        }
    }

    public Matrix(Matrix<ElementType> matrix) {
        this.row = matrix.row;
        this.col = matrix.col;
        this.matrix = new ArrayList<>(this.row);
        for (int i = 0; i < matrix.getMatrix().size(); i++) {
            ArrayList<ElementType> eachRow = new ArrayList<>(matrix.getMatrix().get(i));
            this.matrix.add(eachRow);
        }
    }

    public ElementType get(int i, int j) {
        boolean r = rangeCheck(i, j);
        if (!r) {
            return null;
        }
        return matrix(i, j);
    }

    public void replace(int i, int j, ElementType e) {
        boolean r = rangeCheck(i, j);
        if (!r) {
            return;
        }
        this.matrix.get(i - 1).set(j - 1, e);
    }

    public void setAll(ElementType initial) {
        for (int i = 1; i <= row; i++) {
            if (matrix.size() < i) {
                matrix.add(new ArrayList<>(col));
            }
            ArrayList<ElementType> thisRow = matrix.get(i - 1);
            for (int j = 1; j <= col; j++) {
                if (thisRow.size() < j) {
                    thisRow.add(initial);
                } else {
                    thisRow.set(j - 1, initial);
                }
            }
        }
    }

    public void expand(int size, ElementType e) {
        if (size <= 0) {
            return;
        }
        ArrayList<ElementType> expand = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            expand.add(e);
        }
        for (ArrayList<ElementType> eachRow : matrix) {
            eachRow.addAll(expand);
        }
        this.row += size;
        this.col += size;
        ArrayList<ElementType> newRow = new ArrayList<>(col);
        for (int j = 0; j < col; j++) {
            newRow.add(e);
        }
        for (int i = 0; i < size; i++) {
            ArrayList<ElementType> innerRow = new ArrayList<>(newRow);
            matrix.add(innerRow);
        }
    }

    public void removeRow(int i) {
        if (matrix.size() == 0 || i > matrix.size()) {
            return;
        }
        matrix.remove(i - 1);
        row--;
    }

    public void removeCol(int j) {
        if (matrix.size() == 0 || j > matrix.get(0).size()) {
            return;
        }
        for (ArrayList<ElementType> eachRow : matrix) {
            eachRow.remove(j - 1);
        }
        col--;
    }

    public int width() {
        return col;
    }

    public int height() {
        return row;
    }

    public ArrayList<ArrayList<ElementType>> getMatrix() {
        return matrix;
    }

    private ElementType matrix(int i, int j) {
        return this.matrix.get(i - 1).get(j - 1);
    }

    private boolean rangeCheck(int i, int j) {
        if (i > row || j > col) {
            throw new IndexOutOfBoundsException("index: " + i + ", " + j + "; " + "size: " + row + ", " + col);
        } else {
            return j <= matrix.size() && i <= matrix.get(j - 1).size();
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (ArrayList<ElementType> eachRow : matrix) {
            builder.append("| ");
            for (ElementType eachOne : eachRow) {
                builder.append(eachOne).append(" ");
            }
            builder.append("|\n");
        }
        return builder.toString();
    }
}
