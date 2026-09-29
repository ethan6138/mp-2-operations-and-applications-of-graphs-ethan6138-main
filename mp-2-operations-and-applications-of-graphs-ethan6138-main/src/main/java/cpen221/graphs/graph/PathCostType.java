package cpen221.graphs.graph;

/**
 * Enumerates the path–cost models used by methods in {@link Graph}.
 *
 * <ul>
 *   <li>{@link #SUM_EDGES}: interpret the cost of a path as the sum of the
 *       lengths of all edges on that path.</li>
 *   <li>{@link #MAX_EDGE}: interpret the cost of a path as the maximum
 *       single-edge length along that path (the “bottleneck” edge).</li>
 * </ul>
 * <p>
 * These options are used by {@link Graph#pathCost(java.util.List, PathCostType)},
 * {@link Graph#minimumCostPath(Vertex, Vertex, PathCostType)},
 * {@link Graph#getDiameter(PathCostType)}, and {@link Graph#getCenter(PathCostType)}.
 */
public enum PathCostType {
    //Sum of all the edges in a path
    SUM_EDGES,

    //the max cost of an edge in a path
    MAX_EDGE
}
