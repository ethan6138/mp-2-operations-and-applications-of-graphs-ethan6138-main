package cpen221.graphs;

import cpen221.graphs.graph.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.Map;

public class Task2Tests {

    private static Vertex v(int id) { return new Vertex(id); }

    @Test
    void collections_and_sum_and_neighbours() {
        var g = new AdjacencyListGraph<>();
        Vertex a=v(1), b=v(2), c=v(3), d=v(4);

        g.addVertex(a); g.addVertex(b); g.addVertex(c); g.addVertex(d);
        Edge<Vertex> ab = new Edge<>(a,b,2.0);
        Edge<Vertex> bc = new Edge<>(b,c,0.0);
        Edge<Vertex> cd = new Edge<>(c,d,5.0);

        g.addEdge(ab); g.addEdge(bc); g.addEdge(cd);

        assertSame(ab, g.getEdge(a, b));     // present
        assertSame(ab, g.getEdge(b, a));     // symmetric presence
        assertNull(g.getEdge(a, d));         // non-adjacent pair
        assertNull(g.getEdge(v(999), a));    // absent vertex

        assertEquals(Set.of(a,b,c,d), g.getVertices());
        assertEquals(Set.of(ab, bc, cd), g.getEdges());

        assertEquals(Set.of(ab), g.getEdges(a));
        assertEquals(Set.of(ab, bc), g.getEdges(b));
        assertTrue(g.getEdges(v(999)).isEmpty());

        Map<Vertex, Edge<Vertex>> nbB = g.getNeighbours(b);
        assertEquals(2, nbB.size());
        assertEquals(ab, nbB.get(a));
        assertEquals(bc, nbB.get(c));
        assertTrue(g.getNeighbours(v(999)).isEmpty());

        assertEquals(2.0 + 0.0 + 5.0, g.edgeLengthSum(), 1e-9);
    }

    @Test
    void removals_edge_and_vertex() {
        var g = new AdjacencyListGraph<>();
        Vertex a=v(1), b=v(2), c=v(3);

        g.addVertex(a); g.addVertex(b); g.addVertex(c);
        Edge<Vertex> ab = new Edge<>(a,b,2.0);
        Edge<Vertex> bc = new Edge<>(b,c,3.0);
        g.addEdge(ab); g.addEdge(bc);

        assertFalse(g.removeEdge(null));
        assertTrue(g.hasEdge(a,b));
        assertTrue(g.removeEdge(ab));
        assertFalse(g.hasEdge(a,b));
        assertFalse(g.removeEdge(ab));
        assertNull(g.getEdge(a,b));

        assertTrue(g.hasEdge(b,c));
        assertTrue(g.removeEdge(b, c));
        assertNull(g.getEdge(b,c));
        assertFalse(g.hasEdge(b,c));

        assertTrue(g.removeVertex(a));
        assertFalse(g.removeVertex(a));

        g.addVertex(a);
        g.addEdge(new Edge<>(a,b,1.0));
        g.addEdge(new Edge<>(b,c,1.0));
        assertTrue(g.removeVertex(b));
        assertNull(g.getEdge(a,b));
        assertFalse(g.hasEdge(a,b));
        assertFalse(g.hasEdge(b,c));
        assertTrue(g.hasVertex(a));
        assertTrue(g.hasVertex(c));
    }

    @Test
    void observers_edges_vertices_neighbours_sum_getEdge() {
        Vertex a=v(1), b=v(2), c=v(3), d=v(4);
        Edge<Vertex> ab = new Edge<>(a,b,2.0);
        Edge<Vertex> bc = new Edge<>(b,c,0.0);
        Edge<Vertex> cd = new Edge<>(c,d,5.0);

        var g = new AdjacencyMatrixGraph<>(
            Set.of(a,b,c,d), Set.of(ab, bc, cd));

        assertEquals(Set.of(a,b,c,d), g.getVertices());
        assertEquals(Set.of(ab, bc, cd), g.getEdges());

        assertEquals(Set.of(ab), g.getEdges(a));
        assertEquals(Set.of(ab, bc), g.getEdges(b));
        assertTrue(g.getEdges(v(999)).isEmpty());

        Map<Vertex, Edge<Vertex>> nbC = g.getNeighbours(c);
        assertEquals(2, nbC.size());
        assertEquals(bc, nbC.get(b));
        assertEquals(cd, nbC.get(d));
        assertTrue(g.getNeighbours(v(999)).isEmpty());

        // Neighbours map is unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> nbC.put(v(5), ab));

        assertEquals(2.0 + 0.0 + 5.0, g.edgeLengthSum(), 1e-9);

        assertEquals(ab, g.getEdge(a,b));
        assertNull(g.getEdge(a,d));
        assertNull(g.getEdge(a, v(999)));
    }

}
