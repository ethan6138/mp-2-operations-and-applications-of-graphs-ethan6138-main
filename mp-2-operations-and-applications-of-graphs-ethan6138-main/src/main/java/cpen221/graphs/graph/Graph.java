package cpen221.graphs.graph;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Graph<V extends Vertex, E extends Edge<V>> {

    /* === TASK 1 === */

    /**
     * Checks if the given vertex is in this graph
     *
     * @param v the vertex to check
     * @return {@code true} if {@code v} is a vertex in this graph; {@code false} otherwise
     */
    boolean hasVertex(V v);

    /**
     * Checks if the given undirected edge is in the current graph
     * the endpoints of {@code e} determine the edges getting tested
     *
     * @param e the edge to check; may be {@code null}
     * @return {@code true} if the edge exists, {@code false} otherwise
     */
    boolean hasEdge(E e);

    /**
     * Checks if an undirected edge exists between the two given vertices
     *
     * @param v1 the first endpoint
     * @param v2 the second endpoint
     * @return {@code true} if an edge between {@code v1} and {@code v2} exists; {@code false} otherwise
     */
    boolean hasEdge(V v1, V v2);

    /**
     * Returns the length of the edge between the given vertices.
     * <p>
     * both vertices must be in the graph and an edge between them must exist.
     *
     * @param v1 first endpoint
     * @param v2 second endpoint
     * @return the non-negative length of the edge between {@code v1} and {@code v2}
     * @throws IllegalArgumentException if either vertex is not in this graph
     *                                  or if no edge exists between the two vertices
     */
    double edgeLength(V v1, V v2);

    /* ===================== TASK 2 ===================== */

    /**
     * Returns the sum of lengths of all existing undirected edges,
     * counting each edge exactly one time(dupes exist in both implementations)
     *
     * @return Sum of edge lengths (finite; zero if no edges exists)
     */
    double edgeLengthSum();

    /**
     * Returns the edge instance between two vertices, if present
     *
     * @param v1 the first endpoint
     * @param v2 the second endpoint
     * @return the edge between {@code v1} and {@code v2}, or {@code null} if absent
     */
    E getEdge(V v1, V v2);

    /**
     * Return a snapshot of all vertices in this graph
     *
     * @return a set containing every single vertex, never {@code null}
     */
    Set<V> getVertices();

    /**
     * Return a snap of all undirected edges in this graph
     * (no duplicates for undirected pair)
     *
     * @return a set of edge; never {@code null}
     */
    Set<E> getEdges();

    /**
     * Returns a snapshot of edges incident to the given vertex
     *
     * @param v the vertex of interest
     * @return a set of incident edges; empty if {@code v} is not in this graph
     * or has no incident edges
     */
    Set<E> getEdges(V v);

    /**
     * Returns a snapshot map of neighbours for the given vertex
     * Keys are neighbour vertices values are the corresponding edges.
     *
     * @param v the vertex of interest
     * @return a (possibly empty) mapping of neighbour -> edge; never {@code null}
     */
    Map<V, E> getNeighbours(V v);

    /* === TASK 3 === */

    /**
     * Computes the cost of a vertex sequence under the specified path-cost modeling
     *
     * <p>The path is given as an ordered list of vertices {@code [v0, v1, ...., vk]}.
     * Every consecutive pair {@code (vi, vi+1)} must be joined by an edge in this graph.</p>
     *
     * <p>Cost models:
     * <ul>
     *   <li>{@link PathCostType#SUM_EDGES}: the sum of the lengths of all edges on the path.</li>
     *   <li>{@link PathCostType#MAX_EDGE}: the maximum single-edge length on the path (bottleneck).</li>
     * </ul>
     * All edge lengths are non-negative.</p>
     *
     * @param path     an ordered list of vertices with length at least 2
     * @param costType the cost of the model to use
     * @return a non-negative cost for the given path
     * @throws NullPointerException     if {@code path} or {@code costType} is {@code null}
     * @throws IllegalArgumentException if {@code path} has fewer than 2 vertices
     *                                  or if any vertex in {@code path} is not in this graph
     *                                  or if any consecutive pair in {@code path} is not an edge of this graph
     */
    double pathCost(List<V> path, PathCostType costType);

    /**
     * Returns a minimum-cost path from {@code source} to {@code sink} under the specified path–cost model.
     *
     * <p>The returned path is an ordered list of vertices from {@code source} to {@code sink}, inclusive.
     * If multiple minimum-cost paths exist, any one may be returned. If no path exists, the empty list
     * is returned. If {@code source.equals(sink)}, the singleton list {@code [source]} is returned.</p>
     *
     * <p>Cost models:
     *  <ul>
     *    <li>{@link PathCostType#SUM_EDGES}: minimize the sum of edge lengths.</li>
     *    <li>{@link PathCostType#MAX_EDGE}: minimize the maximum single-edge length along the path (bottleneck).</li>
     *  </ul>
     * All edge lengths are non-negative.</p>
     *
     * @param source   the starting vertex
     * @param sink     the target vertex
     * @param costType the cost model to use
     * @return a list of vertices encoding a minimum-cost path, or the empty list if {@code sink} is unreachable
     * @throws NullPointerException     if {@code costType} is {@code null}
     * @throws IllegalArgumentException if {@code source} or {@code sink} is not a vertex in this graph
     */
    List<V> minimumCostPath(V source, V sink, PathCostType costType);

    /* === TASK 4 === */

    /**
     * Returns the diameter of the graph under the given path cost metrics.
     *
     * <p>For vertices {@code u} and {@code v}, let {@code cost(u,v)}be the minimum
     * path cost between them measured using {@code costType}. The eccentricity of a
     * vertex {@code x} is {@code max_y cost(x,y)} taken over vertices {@code y} in the
     * same connected component as {@code x}. The diameter is the maximum eccentricity
     * among vertices in the <em>largest connected component</em> (by number of vertices).
     * If multiple components tie for largest, arbitrarily pick one.</p>
     *
     * <p>Two cost models:
     * <ul>
     *     <li> {@link PathCostType#SUM_EDGES}: sum of all the edge lengths along the path</li>
     *     <li> {@link PathCostType#MAX_EDGE}: maximum single-edge length along the path</li>
     * </ul>
     * <p>
     * All the edge lengths are non-negative.</p>
     * <p>
     * If graph is empty, we return 0.0.
     *
     * @param costType the path-cost model to use (non-null)
     * @return the diameter value(non-negative; {@code 0.0} if the graph has no vertices)
     * @throws NullPointerException if{@code costType} is {@code null}
     */
    double getDiameter(PathCostType costType);

    /**
     * Returns a center of the graph under the given path–cost metric.
     *
     * <p>The radius {@code r(G)} is the minimum eccentricity among vertices in the
     * <em>largest connected component</em> (by number of vertices). A center is any vertex
     * whose eccentricity equals {@code r(G)}. If multiple centers exist, any one may be
     * returned. If multiple components tie for largest, any one may be used.</p>
     *
     * <p>Two cost models:
     * <ul>
     *   <li>{@link PathCostType#SUM_EDGES}: sum of edge lengths along the path</li>
     *   <li>{@link PathCostType#MAX_EDGE}: maximum single-edge length along the path</li>
     * </ul>
     * <p>
     * All edge lengths are non-negative.</p>
     *
     * <p>If we have an empty graph, no center exists. If we have a singleton component:
     *   that lonely vertex is a center.</p>
     *
     * @param costType the path–cost model to use (non-null)
     * @return a vertex that is a center under {@code costType}
     * @throws NullPointerException  if {@code costType} is {@code null}
     * @throws IllegalStateException if the graph has no vertices
     */
    V getCenter(PathCostType costType);

    /* === TASK 5 === */

    /**
     * Returns the undirected connected components of this graph.
     *
     * <p>Each component is represented as a set of vertices. The components form
     * a partition of {@link #getVertices()}:
     * <ul>
     *   <li>they are pairwise disjoint, and</li>
     *   <li>their union is exactly the full vertex set.</li>
     * </ul>
     * If the graph has no vertices, the returned set is empty.</p>
     *
     * @return a set of vertex-sets representing the connected components;
     * never {@code null}
     */
    Set<Set<V>> getComponents();
}

