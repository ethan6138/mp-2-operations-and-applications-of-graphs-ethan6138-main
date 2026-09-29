package cpen221.graphs.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;


public class AdjacencyMatrixGraph<V extends Vertex, E extends Edge<V>>
    extends GeneralGraph<V, E> {

    /*
     * RI:
     *   - 0 <= n == verticesByIdx.size()
     *   - idx maps each vertex in verticesByIdx to a unique integer in [0, n).
     *   - edgeMatrix is an n-by-n array.
     *   - For all i: edgeMatrix[i][i] == null (no self-loops).
     *   - For all i != j: edgeMatrix[i][j] == edgeMatrix[j][i]
     *       (undirected symmetry).
     *   - For any non-null edge e in edgeMatrix[i][j]:
     *       - e.v1() and e.v2() are in idx.keySet().
     *
     * AF:
     *   - verticesByIdx holds all vertices of the graph.
     *   - edgeMatrix[i][j] is the (possibly null) undirected edge between
     *     vertices verticesByIdx.get(i) and verticesByIdx.get(j).
     *     If edgeMatrix[i][j] is null, there is no edge between those vertices.
     */


    private final int n;
    private final Map<V, Integer> idx;
    private final List<V> verticesByIdx;
    private final E[][] edgeMatrix;
    private static final int MIN_INDEX_MAP_CAPACITY = 16;

    /*===================================Task 1========================================*/

    /**
     * Creates an immutable snapshot from the provided sets
     * edges whose endpoints are not both in {@code vertices} are ignored
     *
     * @param vertices set of vertices, cannot have null elements
     * @param edges    set of edges, also cannot contain null elements
     * @throws NullPointerException     if {@code vertices} or {@code edges} is null
     * @throws IllegalArgumentException if{@code edges} contains null elements
     *                                  or{@code vertices} contains null elements
     */
    @SuppressWarnings("unchecked")
    public AdjacencyMatrixGraph(Set<V> vertices, Set<E> edges)
        throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(vertices, "vertices");
        Objects.requireNonNull(edges, "edges");
        for (V v : vertices) {
            if (v == null) {
                throw new IllegalArgumentException("vertices contains null");
            }
        }
        for (E e : edges) {
            if (e == null) {
                throw new IllegalArgumentException("edges contains null");
            }
        }

        this.n = vertices.size();
        this.idx = new HashMap<>(Math.max(MIN_INDEX_MAP_CAPACITY, n * 2));
        this.verticesByIdx = new ArrayList<>(n);

        int k = 0;
        for (V v : vertices) {
            idx.put(v, k++);
            verticesByIdx.add(v);
        }

        edgeMatrix = (E[][]) new Edge[n][n];

        for (E e : edges) {
            V v1 = e.v1(), v2 = e.v2();
            Integer i = idx.get(v1), j = idx.get(v2);
            if (i == null || j == null) {
                continue;
            }
            if (i.equals(j)) {
                continue;
            }
            edgeMatrix[i][j] = e;
            edgeMatrix[j][i] = e;
        }

        checkRep();
    }

    /**
     * Does not add a vertex, immutable. Always returns {@code false}
     */
    public boolean addVertex(V v) {
        return false;
    }

    /**
     * Does not add an edge, immutable. Always returns {@code false}
     */
    public boolean addEdge(E e) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasVertex(V v) {
        return idx.containsKey(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasEdge(V v1, V v2) {
        Integer i = idx.get(v1), j = idx.get(v2);
        if (i == null || j == null) {
            return false;
        }
        return edgeMatrix[i][j] != null;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if either vertex is not in this graph
     *                                  or if no edge exists between them
     */
    @Override
    public double edgeLength(V v1, V v2) {
        Integer i = idx.get(v1), j = idx.get(v2);
        if (i == null || j == null || edgeMatrix[i][j] == null) {
            throw new IllegalArgumentException("No edge between given vertices");
        }
        return edgeMatrix[i][j].length();
    }

    /*===================================== Task 2 ====================================*/

    //observers

    /**
     * {@inheritDoc} - sums lengths of existing undirected edges, counting each once
     */
    @Override
    public double edgeLengthSum() {
        double sum = 0.0;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                E e = edgeMatrix[i][j];
                if (e != null) {
                    sum += e.length();
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
        Integer i = idx.get(v1);
        Integer j = idx.get(v2);
        return (i == null || j == null) ? null : edgeMatrix[i][j];
    }

    /**
     * {@inheritDoc} - returns a copy of the vertex set
     */
    @Override
    public Set<V> getVertices() {
        return new HashSet<>(idx.keySet());
    }

    /**
     * {@inheritDoc} - unique edges from upper triangle
     */
    @Override
    public Set<E> getEdges() {
        Set<E> out = new HashSet<>();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                E e = edgeMatrix[i][j];
                if (e != null) {
                    out.add(e);
                }
            }
        }

        return out;
    }

    /**
     * {@inheritDoc} - empty if {@code v} absent
     */
    @Override
    public Set<E> getEdges(V v) {
        Integer i = idx.get(v);
        if (i == null) {
            return Set.of();
        }
        Set<E> out = new HashSet<>();
        for (int j = 0; j < n; j++) {
            E e = edgeMatrix[i][j];
            if (e != null) {
                out.add(e);
            }
        }

        return out;
    }

    /**
     * {@inheritDoc} - unmodifiable snapshot; empty if {@code v} absent
     */
    @Override
    public Map<V, E> getNeighbours(V v) {
        Integer i = idx.get(v);
        if (i == null) {
            return Map.of();
        }

        Map<V, E> out = new HashMap<>();
        for (int j = 0; j < n; j++) {
            E e = edgeMatrix[i][j];
            if (e != null) {
                out.put(verticesByIdx.get(j), e);
            }
        }

        return Map.copyOf(out);
    }

    /**
     * Checks the representation invariant for this adjacency-matrix graph.
     */
    private void checkRep() {
        assert n >= 0;
        assert verticesByIdx.size() == n;
        assert idx.size() == n;
        assert edgeMatrix.length == n;

        for (int i = 0; i < n; i++) {
            assert verticesByIdx.get(i) != null;
            V v = verticesByIdx.get(i);
            Integer pos = idx.get(v);
            assert pos != null;
            assert pos == i;
            assert edgeMatrix[i].length == n;
        }

        for (int i = 0; i < n; i++) {
            // No self-loops.
            assert edgeMatrix[i][i] == null;

            for (int j = i + 1; j < n; j++) {
                E e = edgeMatrix[i][j];
                // Symmetry.
                assert edgeMatrix[j][i] == e;

                if (e != null) {
                    V vi = verticesByIdx.get(i);
                    V vj = verticesByIdx.get(j);
                    assert e.incident(vi);
                    assert e.incident(vj);
                }
            }
        }
    }

}
