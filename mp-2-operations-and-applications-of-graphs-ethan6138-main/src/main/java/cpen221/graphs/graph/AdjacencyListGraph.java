package cpen221.graphs.graph;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;


public class AdjacencyListGraph<V extends Vertex, E extends Edge<V>>
    extends GeneralGraph<V, E> {

    /*
     * RI:
     *   - adj is non-null.
     *   - For every vertex v in adj.keySet():
     *       - adj.get(v) is non-null.
     *       - For every (u, e) in adj.get(v):
     *           - adj.containsKey(u) is true.
     *           - adj.get(u).get(v) == e (undirected symmetry).
     *           - e.v1() and e.v2() are in adj.keySet().
     *   - No self-loops: there is no edge with v == u.
     *
     * AF:
     *   - adj represents an undirected simple weighted graph where:
     *       - adj.keySet() is the vertex set.
     *       - For each pair (v, u) with edge e in adj.get(v):
     *           there is an undirected edge between v and u of length e.length().
     */


    private final Map<V, Map<V, E>> adj;

    //constructor
    public AdjacencyListGraph() {
        adj = new HashMap<>();
        checkRep();
    }

    /* ================================= TASK 1 =============================== */

    /**
     * Add a vertex if it's absent from the graph
     *
     * @param v vertex to add; may be {@code null}
     * @return {@code true} iff {@code v} is non-null and not already present
     */
    public boolean addVertex(V v) {
        if (v == null) {
            return false;
        }

        if (hasVertex(v)) {
            return false;
        }

        adj.put(v, new HashMap<>());
        checkRep();

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasVertex(V v) {
        return adj.containsKey(v);
    }

    /**
     * Add an undirected edge to this graph if valid and not already present
     * <p>
     * Endpoints must already be vertices in the graph, edges are undirected; dupes
     * are edges between teh same unordered pair (v1,v2); self loops are not permitted
     *
     * @param e the edge to add; may be {@code null}
     * @return {@code true} if the edge was added; {@code false} if the edge is {@code null},
     * an endpoint is not a vertex or if an edge pre-exists(duplicates)
     */
    public boolean addEdge(E e) {
        if (e == null) {
            return false;
        }

        V v1 = e.v1();
        V v2 = e.v2();

        Map<V, E> m1 = adj.get(v1), m2 = adj.get(v2);
        if (m1 == null || m2 == null) {
            return false;
        }
        if (m1.containsKey(v2)) {
            return false;
        }

        m1.put(v2, e);
        m2.put(v1, e);
        checkRep();

        return true;
    }

    //add in specs: return false if map is null for the key

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasEdge(V v1, V v2) {
        Map<V, E> maps = adj.get(v1);
        return maps != null && maps.containsKey(v2);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if either vertex is not in this graph
     *                                  or if no edges exists between them
     */
    @Override
    public double edgeLength(V v1, V v2) throws IllegalArgumentException {
        Map<V, E> map = adj.get(v1);
        if (map == null) {
            throw new IllegalArgumentException("No edge between given vertices");
        }
        E edge = map.get(v2);
        if (edge == null) {
            throw new IllegalArgumentException("No edge between given vertices");
        }
        return edge.length();
    }

    /* ========================= TASK 2 ============================ */

    //observers first

    /**
     * {@inheritDoc} - sums lengths of existing undirected edges, counting each of em once
     */
    @Override
    public double edgeLengthSum() {
        double sum = 0.0;

        for (var entry : adj.entrySet()) {
            V v = entry.getKey();
            for (var number : entry.getValue().entrySet()) {
                V u = number.getKey();

                if (v.id() < u.id()) {
                    sum += number.getValue().length();
                }
            }
        }

        return sum;
    }

    /**
     * {@inheritDoc} - returns {@code null} if missing
     */
    @Override
    public E getEdge(V v1, V v2) {
        Map<V, E> map = adj.get(v1);
        return (map == null) ? null : map.get(v2);
    }

    /**
     * {@inheritDoc} - returns a copy of the vertex set
     */
    @Override
    public Set<V> getVertices() {
        return new HashSet<>(adj.keySet());
    }

    /**
     * {@inheritDoc} - returns unique edges(no duplicates please!)
     */
    @Override
    public Set<E> getEdges() {
        Set<E> out = new HashSet<>();

        for (var temp : adj.values()) {
            out.addAll(temp.values());
        }

        return out;
    }

    /**
     * {@inheritDoc} - empty if {@code v} is absent
     */
    @Override
    public Set<E> getEdges(V v) {
        Map<V, E> out = adj.get(v);
        return (out == null) ? Collections.emptySet() : new HashSet<>(out.values());
    }

    /**
     * {@inheritDoc} - unmodifiable snapshot; empty if {@code v} absent
     */
    @Override
    public Map<V, E> getNeighbours(V v) {
        Map<V, E> map = adj.get(v);
        return (map == null) ? Collections.emptyMap() : Map.copyOf(map);
    }


    //mutators below

    /**
     * Removes an undirected edge (both directions) if present
     *
     * @param e the edge to remove may be {@code null}
     * @return {@code true} if at least one  direction was removed; otherwise {@code false}
     */
    public boolean removeEdge(E e) {
        return e != null && removeEdge(e.v1(), e.v2()); // no checkRep since it just delegates
    }

    /**
     * Removes the undirected edge between {@code v1} and {@code v2} if present
     *
     * @param v1 first endpoint
     * @param v2 second endpoint
     * @return {@code true} if an entry was removed from either side; otherwise {@code false}
     */
    public boolean removeEdge(V v1, V v2) {
        Map<V, E> map1 = adj.get(v1);
        Map<V, E> map2 = adj.get(v2);
        if (map1 == null || map2 == null) {
            return false;
        }
        E remove1 = map1.remove(v2);
        E remove2 = map2.remove(v1);

        checkRep();
        return (remove1 != null) || (remove2 != null);
    }

    /**
     * Removes a vertex and all incident edges if present
     *
     * @param v vertex to remove
     * @return {@code true} iff the vertex existed and was removed
     */
    public boolean removeVertex(V v) {
        Map<V, E> map = adj.remove(v);
        if (map == null) {
            return false;
        }

        for (V u : map.keySet()) {
            Map<V, E> map2 = adj.get(u);
            if (map2 != null) {
                map2.remove(v);
            }
        }

        checkRep();
        return true;
    }

    /**
     * Checks the representation invariant for this adjacency-list graph.
     */
    private void checkRep() {
        assert adj != null;

        for (Map.Entry<V, Map<V, E>> entry : adj.entrySet()) {
            V v = entry.getKey();
            Map<V, E> numbers = entry.getValue();

            assert v != null;
            assert numbers != null;

            for (Map.Entry<V, E> ne : numbers.entrySet()) {
                V u = ne.getKey();
                E e = ne.getValue();

                assert u != null;
                assert e != null;

                // Neighbour must also be a vertex in this graph.
                assert adj.containsKey(u);

                // Undirected symmetry: edge appears in both adjacency lists.
                Map<V, E> rev = adj.get(u);
                assert rev != null;
                assert rev.get(v) == e;

                // No self-loops.
                assert !v.equals(u);

                // Edge endpoints must match the vertices.
                assert e.incident(v) && e.incident(u);
            }
        }
    }

}
