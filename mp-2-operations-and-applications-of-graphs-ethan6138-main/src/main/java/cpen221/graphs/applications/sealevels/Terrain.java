package cpen221.graphs.applications.sealevels;

import java.util.*;

public class Terrain {
    public double[][] heights;
    public GridLocation[] sources;
    
    public Terrain(double[][] heights, GridLocation[] sources) {
        this.heights = heights;
        this.sources = sources;
    }
    
    @Override public String toString() {
        return Arrays.deepToString(heights) + Arrays.deepToString(sources);
    }
}
