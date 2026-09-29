package cpen221.graphs.applications.sealevels;

public final class GridLocation {
    public final int row;
    public final int col;

    public GridLocation(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    @Override public boolean equals(Object rhs) {
        if (!(rhs instanceof GridLocation)) return false;
        
        var other = (GridLocation) rhs;
        return row == other.row && col == other.col;
    }
    
    @Override public int hashCode() {
        return 31 * row + col;
    }
    
    @Override public String toString() {
        var b = new StringBuilder();
        b.append("{ ");
        b.append(row);
        b.append(", ");
        b.append(col);
        b.append(" }");
        return b.toString();
    }
}
