package cpen221.graphs;

import cpen221.graphs.graph.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.HashSet;

public class Task1Tests {
    private static Vertex v(int id) {
        return new Vertex(id);
    }

    @Test
    void addVertex_basicPaths() {
        AdjacencyListGraph<Vertex, Edge<Vertex>> g = new AdjacencyListGraph<>();

        assertFalse(g.addVertex(null));          // null -> false
        assertTrue(g.addVertex(v(1)));           // fresh -> true
        assertFalse(g.addVertex(v(1)));          // duplicate -> false

        assertTrue(g.hasVertex(v(1)));
        assertFalse(g.hasVertex(v(99)));
    }

    @Test
    void addEdge_null_missingEndpoints_duplicate_success() {
        AdjacencyListGraph<Vertex, Edge<Vertex>> g = new AdjacencyListGraph<>();
        Vertex a = v(1), b = v(2), c = v(3);
        Edge<Vertex> ab = new Edge<>(a, b, 2.5);

        //null edge => false
        assertFalse(g.addEdge(null));

        //endpoints not in graph => false
        assertFalse(g.addEdge(ab));
        assertFalse(g.hasEdge(a, b));

        //add endpoints then add edge
        assertTrue(g.addVertex(a));
        assertTrue(g.addVertex(b));
        assertTrue(g.addEdge(ab));

        //duplicate => false
        assertFalse(g.addEdge(ab));

        //sanity: unrelated vertex present but no edge
        assertTrue(g.addVertex(c));
        assertFalse(g.hasEdge(a, c));
    }

    @Test
    void hasEdge_byEdge_delegates_and_nullSafe() {
        AdjacencyListGraph<Vertex, Edge<Vertex>> g = new AdjacencyListGraph<>();
        Vertex a = v(1), b = v(2);
        Edge<Vertex> ab = new Edge<>(a, b, 1.0);

        assertFalse(g.hasEdge(null));

        g.addVertex(a);
        g.addVertex(b);

        assertFalse(g.hasEdge(ab)); // not added yet
        assertTrue(g.addEdge(ab));
        assertTrue(g.hasEdge(ab));  // now present
    }

    @Test
    void edgeLength_exceptionBranches_and_success_includingZeroWeight() {
        AdjacencyListGraph<Vertex, Edge<Vertex>> g = new AdjacencyListGraph<>();
        Vertex a = v(1), b = v(2), c = v(3);

        // map null (a not in graph)
        assertThrows(IllegalArgumentException.class, () -> g.edgeLength(a, b));

        // vertices present but edge missing
        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);
        assertThrows(IllegalArgumentException.class, () -> g.edgeLength(a, b));

        // success path with nonzero
        Edge<Vertex> ab = new Edge<>(a, b, 7.0);
        assertTrue(g.addEdge(ab));
        assertEquals(7.0, g.edgeLength(a, b), 1e-9);
        assertEquals(7.0, g.edgeLength(b, a), 1e-9);

        // zero-weight edge is valid; don’t confuse with “no edge”
        Edge<Vertex> bc = new Edge<>(b, c, 0.0);
        assertTrue(g.addEdge(bc));
        assertEquals(0.0, g.edgeLength(b, c), 1e-9);
    }

    @Test
    void constructor_preconditions_and_snapshot_ignoreOutsideVertices() {
        Vertex a = v(1), b = v(2), c = v(3), ghost = v(99);

        //null parameters -> NPE (from requireNonNull)
        assertThrows(NullPointerException.class,
            () -> new AdjacencyMatrixGraph<>(null, Set.of()));
        assertThrows(NullPointerException.class,
            () -> new AdjacencyMatrixGraph<>(Set.of(), null));

        //sets containing null -> IAE (from contains(null) checks)
        HashSet<Vertex> badV = new HashSet<>();
        badV.add(a);
        badV.add(null);
        assertThrows(IllegalArgumentException.class,
            () -> new AdjacencyMatrixGraph<>(badV, Set.of()));

        HashSet<Edge<Vertex>> badE = new HashSet<>();
        badE.add(null);
        assertThrows(IllegalArgumentException.class,
            () -> new AdjacencyMatrixGraph<>(Set.of(a, b), badE));

        //proper snapshot; ignore edges with endpoints not in vertices
        Set<Vertex> vs = Set.of(a, b, c);
        Set<Edge<Vertex>> es = Set.of(
            new Edge<>(a, b, 2.0),
            new Edge<>(b, c, 0.0),     // zero-weight valid
            new Edge<>(a, ghost, 5.0)      // ignored (ghost not in vs)
        );

        AdjacencyMatrixGraph<Vertex, Edge<Vertex>> g = new AdjacencyMatrixGraph<>(vs, es);

        assertTrue(g.hasVertex(a));
        assertTrue(g.hasVertex(b));
        assertTrue(g.hasVertex(c));
        assertFalse(g.hasVertex(ghost));

        assertTrue(g.hasEdge(a, b));
        assertTrue(g.hasEdge(b, c));
        assertFalse(g.hasEdge(a, c));
        assertFalse(g.hasEdge(a, ghost));
    }

    @Test
    void immutable_noops_and_edgeLength_paths() {
        Vertex a = v(1), b = v(2), c = v(3);

        AdjacencyMatrixGraph<Vertex, Edge<Vertex>> g =
            new AdjacencyMatrixGraph<>(Set.of(a, b, c), Set.of(new Edge<>(a, b, 6.0)));

        //immutable no-operations
        assertFalse(g.addVertex(v(4)));
        assertFalse(g.addEdge(new Edge<>(b, c, 1.0)));
        assertTrue(g.hasEdge(a, b)); // unchanged

        //edgeLength exception paths + success
        assertThrows(IllegalArgumentException.class, () -> g.edgeLength(a, v(999))); // vertex absent
        assertThrows(IllegalArgumentException.class, () -> g.edgeLength(a, c));      // edge absent
        assertEquals(6.0, g.edgeLength(a, b), 1e-9);
    }

    @Test
    void matrix_missingVertices_hit_idx_null_branches() {
        Vertex a = v(1), b = v(2), c = v(3);

        //Build a small graph with only (a,b) connected
        AdjacencyMatrixGraph<Vertex, Edge<Vertex>> g =
            new AdjacencyMatrixGraph<>(Set.of(a, b, c), Set.of(new Edge<>(a, b, 1.0)));

        //Vertices not present in the graph (idx.get(...) -> null)
        Vertex ghost1 = new Vertex(42);
        Vertex ghost2 = new Vertex(77);

        //hasEdge present+absent, absent+present, absent+absent
        assertFalse(g.hasEdge(a, ghost1));
        assertFalse(g.hasEdge(ghost1, c));
        assertFalse(g.hasEdge(ghost1, ghost2));

        //edgeLength either endpoint absent
        assertThrows(IllegalArgumentException.class, () -> g.edgeLength(a, ghost1));
        assertThrows(IllegalArgumentException.class, () -> g.edgeLength(ghost1, c));
    }

}
