package cpen221.graphs.graph;

import java.util.NoSuchElementException;

/**
 * Parametrized, immutable implementation of a graph edge.
 *
 * <p>Implements some basic operations for graph edges,
 * such as obtaining the vertices that are end-points
 * of the edge, the length of the edge, and to determine
 * if an edge is incident on a vertex or if two edges
 * intersect.</p>
 *
 * <p>In this implementation, the first vertex of an edge,
 * returned by {@code v1()} is the vertex with smaller
 * identifier relative to the vertex returned by {@code v2()}.</p>
 *
 * @param <V> the vertex type
 * @author CPEN 221 Staff
 */
public class Edge<V extends Vertex> {
    /*
     * RI:
     *   - v1 != null, v2 != null
     *   - !v1.equals(v2)
     *   - length >= 0
     *   - v1.id() < v2.id()
     *
     * AF:
     *   - This object represents an undirected edge connecting vertices v1 and v2
     *     with non-negative length 'length'. Since v1.id() < v2.id(), (v1, v2)
     *     and (v2, v1) represent the same undirected edge.
     */

    private final V v1;
    private final V v2;
    private final double length;

    /**
     * Constructs and edge between two distinct vertices with default length 1
     *
     * @param v1 first endpoint(non-null)
     * @param v2 second endpoint (non-null, distinct form {@code v1})
     * @throws IllegalArgumentException if either endpoint is {@code null},
     *                                  or if {@code v1.equals(v2)}
     */
    public Edge(V v1, V v2) {
        this(v1, v2, 1);
        checkRep();
    }

    /**
     * Constructs an edge between two distinct vertices with the given length.
     * The length must be non-negative
     *
     * <p>The endpoints are stores in canonical order so that {@code v1.id() < v2.id()}.</p>
     *
     * @param v1     first endpoint(non null)
     * @param v2     second endpoint (non null- distinct from {@code v1)}
     * @param length non-negative edge length
     * @throws IllegalArgumentException if either endpoint is {@code null},
     *                                  or if {@code v1.equals(v2)},
     *                                  or if {@code length < 0}
     */
    public Edge(V v1, V v2, double length) {
        if (v1 == null || v2 == null) {
            throw new IllegalArgumentException("Vertices cannot be null");
        }
        if (v1.equals(v2)) {
            throw new IllegalArgumentException("The same vertex cannot be at both ends of an edge");
        }
        if (length < 0) {
            throw new IllegalArgumentException("Edge weight cannot be negative");
        }

        if (v1.id() < v2.id()) {
            this.v1 = v1;
            this.v2 = v2;
        } else {
            this.v1 = v2;
            this.v2 = v1;
        }

        this.length = length;

        checkRep();
    }

    /**
     * Returns the endpoint with smaller id
     *
     * @return the first endpoint, with {@code v1.id() < v2.id()}
     */
    public V v1() {
        return v1;
    }

    /**
     * Returns the endpoint with larger id
     *
     * @return the second endpoint, with {@code v1.id() < v2.id()}
     */
    public V v2() {
        return v2;
    }

    /**
     * Returns the (non-negative) length of this edge
     *
     * @return the edge length
     */
    public double length() {
        return length;
    }

    /**
     * Two edges are equal iff they connect the same unordered pair rof vertices
     * <p>The edge length is ignored for equality</p>
     *
     * @param o other object
     * @return {@code true} if {@code o} is an {@code Edge} with the same endpoints; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Edge<?> other) {
            return other.v1.equals(this.v1) && other.v2.equals(this.v2);
        }
        return false;
    }

    /**
     * Hash code consistent with {@link #equals(Object)}, derived from the endpoints only
     *
     * @return hash of this edge
     */
    @Override
    public int hashCode() {
        return v1.hashCode() + v2.hashCode();
    }

    /**
     * Tests whether the given vertex is incident to this edge
     *
     * @param v vertex to test; may be {@code null}
     * @return {@code true} if {@code v} equals {@code v1} or {@code v2}; otherwise return {@code false}
     */
    public boolean incident(V v) {
        if (v == null) {
            return false;
        }
        return (v.equals(v1) || v.equals(v2));
    }

    /**
     * Tests regardless of whether this edge and {@code e} share at least one end point
     *
     * @param e another edge; may be {@code null}
     * @return {@code true} if the edges share a vertex; {@code false} if {@code e} is {@code null} or disjoint
     */
    public boolean intersects(Edge<V> e) {
        if (e == null) {
            return false;
        }
        return this.incident(e.v1) || this.incident(e.v2);
    }

    /**
     * Returns the common endpoint of this edge and {@code e}
     *
     * @param e another edge
     * @return the shared vertex
     * @throws NoSuchElementException if {@code e} if {@code null} or if the edge have no common vertex
     */
    public V intersection(Edge<V> e) throws NoSuchElementException {
        if (e == null) {
            throw new NoSuchElementException("No common vertex");
        }
        if (this.v1.equals(e.v1) || this.v1.equals(e.v2)) {
            return this.v1;
        }
        if (this.v2.equals(e.v1) || this.v2.equals(e.v2)) {
            return this.v2;
        }
        throw new NoSuchElementException("No common vertex");
    }

    /**
     * Returns the endpoint of this edge that is distinct from {@code v}
     *
     * <p>If {@code v} equals {@code v1}, returns {@code v2}. If {@code v} equals {@code v2},
     * returns {@code v1}. If {@code v} is not incident to this edge, this method returns {@code v1}</p>
     *
     * @param v a vertex
     * @return the other endpoint if {@code v} is incident; otherwise {@code v1}
     */
    public V distinctVertex(V v) {
        if (this.v1.equals(v)) {
            return this.v2;
        } else {
            return this.v1;
        }
    }

    /**
     * Returns the endpoint of this edge that is distinct from the shared endpoint with {@code e}
     *
     * <ul>
     *     <li>If this edge and {@code e} are equal (same endpoints), throws{@link NoSuchElementException}.</li>
     *     <li>If the edges are disjoint (no common vertex), returns {@code v1} deterministically.</li>
     *     <li>If the edges share a vertex {@code s}, returns the endpoint of <em>this</em>
     *     edge that is not {@code s}.</li>
     * </ul>
     *
     * @param e another edge
     * @return the endpoint of this edge that is distinct from the shared endpoint (or {@code v1} if disjoint)
     * @throws NoSuchElementException if the two edges are equal (no "distinct" endpoint exists)
     */
    public V distinctVertex(Edge<V> e) {
        if (this.equals(e)) {
            throw new NoSuchElementException("No distinct vertex");
        }
        V sv;
        try {
            sv = this.intersection(e);
        } catch (NoSuchElementException nse) {
            // when there is no common vertex,
            // return any vertex (deterministic choice of v1 is okay).
            return v1;
        }
        if (v1.equals(sv)) {
            return v2;
        } else {
            return v1;
        }
    }

    private void checkRep() {
        assert v1 != null;
        assert v2 != null;
        assert !v1.equals(v2);
        assert length >= 0;
        assert v1.id() < v2.id();
    }
}
