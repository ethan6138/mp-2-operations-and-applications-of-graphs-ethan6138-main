package cpen221.graphs;

import cpen221.graphs.graph.AdjacencyListGraph;
import cpen221.graphs.graph.AdjacencyMatrixGraph;
import cpen221.graphs.graph.Algorithms;
import cpen221.graphs.graph.Edge;
import cpen221.graphs.graph.Graph;
import cpen221.graphs.graph.PathCostType;
import cpen221.graphs.graph.Vertex;
import org.junit.jupiter.api.Test;
import cpen221.graphs.applications.textsimilarity.Document;
import cpen221.graphs.applications.textsimilarity.DocumentVertex;
import cpen221.graphs.applications.textsimilarity.Similarity;
import cpen221.graphs.applications.textsimilarity.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class AllTestCases {

    //EVERYTHING WILL BE ADDED HERE!

    private static Vertex v(int id) {
        return new Vertex(id);
    }

    private AdjacencyListGraph<Vertex, Edge<Vertex>> g() {
        return new AdjacencyListGraph<>();
    }

    private void addVertices(AdjacencyListGraph<Vertex, Edge<Vertex>> g, int... ids) {
        for (int id : ids) {
            g.addVertex(v(id));
        }
    }

    private void addUEdge(AdjacencyListGraph<Vertex, Edge<Vertex>> g, int a, int b, double w) {
        g.addEdge(new Edge<>(v(a), v(b), w));
    }

    // Invoke AdjacencyListGraph.checkRep() via reflection and rethrow AssertionError.
    private void invokeListCheckRep(AdjacencyListGraph<Vertex, Edge<Vertex>> g) {
        try {
            Method m = AdjacencyListGraph.class.getDeclaredMethod("checkRep");
            m.setAccessible(true);
            m.invoke(g);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof AssertionError ae) {
                throw ae;
            } else {
                fail("Unexpected exception from AdjacencyListGraph.checkRep", cause);
            }
        } catch (Exception e) {
            fail("Reflection error invoking AdjacencyListGraph.checkRep", e);
        }
    }

    // Invoke AdjacencyMatrixGraph.checkRep() via reflection and rethrow AssertionError.
    private void invokeMatrixCheckRep(AdjacencyMatrixGraph<Vertex, Edge<Vertex>> g) {
        try {
            Method m = AdjacencyMatrixGraph.class.getDeclaredMethod("checkRep");
            m.setAccessible(true);
            m.invoke(g);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof AssertionError ae) {
                throw ae;
            } else {
                fail("Unexpected exception from AdjacencyMatrixGraph.checkRep", cause);
            }
        } catch (Exception e) {
            fail("Reflection error invoking AdjacencyMatrixGraph.checkRep", e);
        }
    }

    // Construct an Algorithms.DisjointSet<Vertex> via reflection.
    private Object makeDisjointSet(Set<Vertex> elements) {
        try {
            Class<?> dsuClass =
                Class.forName("cpen221.graphs.graph.Algorithms$DisjointSet");
            Constructor<?> ctor = dsuClass.getDeclaredConstructor(Set.class);
            ctor.setAccessible(true);
            return ctor.newInstance(elements);
        } catch (Exception e) {
            fail("Reflection error creating DisjointSet", e);
            return null; // unreachable
        }
    }

    // Invoke the private checkRep() on a DisjointSet instance and rethrow AssertionError.
    private void invokeDisjointSetCheckRep(Object dsu) {
        try {
            Method m = dsu.getClass().getDeclaredMethod("checkRep");
            m.setAccessible(true);
            m.invoke(dsu);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof AssertionError ae) {
                throw ae;
            } else {
                fail("Unexpected exception from DisjointSet.checkRep", cause);
            }
        } catch (Exception e) {
            fail("Reflection error invoking DisjointSet.checkRep", e);
        }
    }

    /** Create a temp text file with the given content. */
    private File createTempFileWithContent(String prefix, String content) throws IOException {
        File temp = File.createTempFile(prefix, ".txt");
        try (FileWriter writer = new FileWriter(temp)) {
            writer.write(content);
        }
        temp.deleteOnExit();
        return temp;
    }

    /** Convenience helper: build a Document from in-memory text. */
    private Document makeDocFromContent(String id, String content) throws IOException {
        File f = createTempFileWithContent("doc-" + id, content);
        return new Document(id, f.getAbsolutePath());
    }

    // Invoke Document.checkRep() via reflection and rethrow AssertionError.
    private void invokeDocumentCheckRep(Document d) {
        try {
            Method m = Document.class.getDeclaredMethod("checkRep");
            m.setAccessible(true);
            m.invoke(d);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof AssertionError ae) {
                throw ae;
            } else {
                fail("Unexpected exception from Document.checkRep", cause);
            }
        } catch (Exception e) {
            fail("Reflection error invoking Document.checkRep", e);
        }
    }

    /** Create a Document from raw text by writing to a temporary file. */
    private Document makeDocumentFromText(String id, String text) throws IOException {
        Path temp = Files.createTempFile("doc_" + id, ".txt");
        Files.writeString(temp, text, StandardCharsets.UTF_8);
        Document doc = new Document(id, temp.toString());
        Files.deleteIfExists(temp); // Document has already read all content
        return doc;
    }

    //========================Task 1=======================


    @Test
    public void testCreateGraph() {
        Vertex v1 = new Vertex(1);
        Vertex v2 = new Vertex(2);
        Vertex v3 = new Vertex(3);
        Vertex v4 = new Vertex(4);

        Edge<Vertex> e1 = new Edge<>(v1, v2, 5);
        Edge<Vertex> e2 = new Edge<>(v2, v3, 7);
        Edge<Vertex> e3 = new Edge<>(v1, v4, 9);

        AdjacencyListGraph<Vertex, Edge<Vertex>> g = new AdjacencyListGraph<>();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addEdge(e1);
        g.addEdge(e2);
        g.addEdge(e3);

        assertEquals(e2, g.getEdge(v2, v3));
        assertEquals(21,
            g.pathCost(
                g.minimumCostPath(v3, v4, PathCostType.SUM_EDGES),
                PathCostType.SUM_EDGES
            )
        );
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
        assertThrows(IllegalArgumentException.class,
            () -> g.edgeLength(a, v(999))); // vertex absent
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

    // =====================task 2================================
    @Test
    void collections_and_sum_and_neighbours() {
        var g = new AdjacencyListGraph<>();
        Vertex a = v(1), b = v(2), c = v(3), d = v(4);

        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);
        g.addVertex(d);
        Edge<Vertex> ab = new Edge<>(a, b, 2.0);
        Edge<Vertex> bc = new Edge<>(b, c, 0.0);
        Edge<Vertex> cd = new Edge<>(c, d, 5.0);

        g.addEdge(ab);
        g.addEdge(bc);
        g.addEdge(cd);

        assertSame(ab, g.getEdge(a, b));     // present
        assertSame(ab, g.getEdge(b, a));     // symmetric presence
        assertNull(g.getEdge(a, d));         // non-adjacent pair
        assertNull(g.getEdge(v(999), a));    // absent vertex

        assertEquals(Set.of(a, b, c, d), g.getVertices());
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
        Vertex a = v(1), b = v(2), c = v(3);

        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);
        Edge<Vertex> ab = new Edge<>(a, b, 2.0);
        Edge<Vertex> bc = new Edge<>(b, c, 3.0);
        g.addEdge(ab);
        g.addEdge(bc);

        assertFalse(g.removeEdge(null));
        assertTrue(g.hasEdge(a, b));
        assertTrue(g.removeEdge(ab));
        assertFalse(g.hasEdge(a, b));
        assertFalse(g.removeEdge(ab));
        assertNull(g.getEdge(a, b));

        assertTrue(g.hasEdge(b, c));
        assertTrue(g.removeEdge(b, c));
        assertNull(g.getEdge(b, c));
        assertFalse(g.hasEdge(b, c));

        assertTrue(g.removeVertex(a));
        assertFalse(g.removeVertex(a));

        g.addVertex(a);
        g.addEdge(new Edge<>(a, b, 1.0));
        g.addEdge(new Edge<>(b, c, 1.0));
        assertTrue(g.removeVertex(b));
        assertNull(g.getEdge(a, b));
        assertFalse(g.hasEdge(a, b));
        assertFalse(g.hasEdge(b, c));
        assertTrue(g.hasVertex(a));
        assertTrue(g.hasVertex(c));
    }

    @Test
    void observers_edges_vertices_neighbours_sum_getEdge() {
        Vertex a = v(1), b = v(2), c = v(3), d = v(4);
        Edge<Vertex> ab = new Edge<>(a, b, 2.0);
        Edge<Vertex> bc = new Edge<>(b, c, 0.0);
        Edge<Vertex> cd = new Edge<>(c, d, 5.0);

        var g = new AdjacencyMatrixGraph<>(
            Set.of(a, b, c, d), Set.of(ab, bc, cd));

        assertEquals(Set.of(a, b, c, d), g.getVertices());
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

        assertEquals(ab, g.getEdge(a, b));
        assertNull(g.getEdge(a, d));
        assertNull(g.getEdge(a, v(999)));
    }

    //============================task 3=================================

    @Test
    void pathCost_valid_and_invalid() {
        var g = new AdjacencyListGraph<>();
        Vertex a = v(1), b = v(2), c = v(3);
        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);
        g.addEdge(new Edge<>(a, b, 2.0));
        g.addEdge(new Edge<>(b, c, 5.0));

        assertEquals(7.0, g.pathCost(List.of(a, b, c), PathCostType.SUM_EDGES), 1e-9);
        assertEquals(5.0, g.pathCost(List.of(a, b, c), PathCostType.MAX_EDGE), 1e-9);

        assertThrows(IllegalArgumentException.class,
            () -> g.pathCost(List.of(a), PathCostType.SUM_EDGES));
        assertThrows(NullPointerException.class, () -> g.pathCost(null, PathCostType.SUM_EDGES));
        assertThrows(IllegalArgumentException.class,
            () -> g.pathCost(List.of(a, v(999)), PathCostType.SUM_EDGES)); // non-adjacent pair
    }

    @Test
    void dijkstra_sum_improves_tentative_path() {
        var g = new AdjacencyListGraph<>();
        Vertex n1 = v(1), n2 = v(2), n3 = v(3), n4 = v(4);

        g.addVertex(n1);
        g.addVertex(n2);
        g.addVertex(n3);
        g.addVertex(n4);
        g.addEdge(new Edge<>(n1, n2, 5.0));
        g.addEdge(new Edge<>(n1, n3, 3.0));
        g.addEdge(new Edge<>(n3, n4, 4.0)); // first seen path to 4 costs 7
        g.addEdge(new Edge<>(n2, n4, 1.0)); // later we discover 1->2->4 costs 6

        var path = g.minimumCostPath(n1, n4, PathCostType.SUM_EDGES);
        assertEquals(List.of(n1, n2, n4), path);
        assertEquals(6.0, g.pathCost(path, PathCostType.SUM_EDGES), 1e-9);
    }

    @Test
    void dijkstra_bottleneck_picks_minimax_path() {
        var g = new AdjacencyListGraph<>();
        Vertex n1 = v(1), n2 = v(2), n3 = v(3), n4 = v(4);

        g.addVertex(n1);
        g.addVertex(n2);
        g.addVertex(n3);
        g.addVertex(n4);
        g.addEdge(new Edge<>(n1, n2, 5.0));
        g.addEdge(new Edge<>(n1, n3, 3.0));
        g.addEdge(new Edge<>(n3, n4, 4.0)); // path 1-3-4 bottleneck 4
        g.addEdge(new Edge<>(n2, n4, 1.0)); // path 1-2-4 bottleneck 5

        var path = g.minimumCostPath(n1, n4, PathCostType.MAX_EDGE);
        assertEquals(List.of(n1, n3, n4), path);
        assertEquals(4.0, g.pathCost(path, PathCostType.MAX_EDGE), 1e-9);
    }

    @Test
    void minimumCostPath_unreachable_and_trivial_and_errors() {
        var g = new AdjacencyListGraph<>();
        Vertex a = v(1), b = v(2), c = v(3), x = v(99);
        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);
        g.addEdge(new Edge<>(a, b, 1.0));
        // c is isolated, x not in graph

        assertEquals(List.of(a), g.minimumCostPath(a, a, PathCostType.SUM_EDGES)); // source==sink
        assertTrue(g.minimumCostPath(a, c, PathCostType.SUM_EDGES).isEmpty());     // unreachable

        assertThrows(IllegalArgumentException.class,
            () -> g.minimumCostPath(x, b, PathCostType.SUM_EDGES));
        assertThrows(IllegalArgumentException.class,
            () -> g.minimumCostPath(a, x, PathCostType.MAX_EDGE));
    }

    //for the matrix version
    @Test
    void dijkstra_sum_and_bottleneck_on_matrix() {
        Vertex a = v(1), b = v(2), c = v(3), d = v(4), x = v(99);

        var g = new AdjacencyMatrixGraph<>(
            Set.of(a, b, c, d, x),
            Set.of(
                new Edge<>(a, b, 2.0),
                new Edge<>(b, c, 10.0),
                new Edge<>(a, c, 5.0),
                new Edge<>(c, d, 1.0),
                new Edge<>(b, d, 100.0)
            )
        );

        // sum: a->c->d (5+1=6) beats a->b->d (2+100)
        assertEquals(List.of(a, c, d), g.minimumCostPath(a, d, PathCostType.SUM_EDGES));
        assertEquals(6.0, g.pathCost(List.of(a, c, d), PathCostType.SUM_EDGES), 1e-9);

        // bottleneck: a->c->d has max edge 5; a->b->d has 100 -> pick a-c-d
        assertEquals(List.of(a, c, d), g.minimumCostPath(a, d, PathCostType.MAX_EDGE));
        assertEquals(5.0, g.pathCost(List.of(a, c, d), PathCostType.MAX_EDGE), 1e-9);

        // unreachable from A to isolated? here x is isolated: empty
        assertTrue(g.minimumCostPath(a, x, PathCostType.SUM_EDGES).isEmpty());
    }

    @Test
    void pathCost_matrix_valid_and_invalid() {
        Vertex a = v(1), b = v(2), c = v(3);

        var g = new AdjacencyMatrixGraph<>(
            Set.of(a, b, c),
            Set.of(new Edge<>(a, b, 2.0), new Edge<>(b, c, 0.0))
        );

        assertEquals(2.0, g.pathCost(List.of(a, b), PathCostType.SUM_EDGES), 1e-9);
        assertEquals(2.0, g.pathCost(List.of(a, b), PathCostType.MAX_EDGE), 1e-9);
        assertEquals(2.0, g.pathCost(List.of(a, b, c), PathCostType.MAX_EDGE), 1e-9); // max{2,0}=2

        assertThrows(IllegalArgumentException.class,
            () -> g.pathCost(List.of(a), PathCostType.SUM_EDGES));
        assertThrows(IllegalArgumentException.class,
            () -> g.pathCost(List.of(a, v(999)), PathCostType.SUM_EDGES));
    }

    // Path of 5 vertices with unit weights: basic sanity for SUM_EDGES vs MAX_EDGE
    @Test
    void path5_sum_vs_max() {
        var g = g();
        addVertices(g, 1, 2, 3, 4, 5);
        addUEdge(g, 1, 2, 1);
        addUEdge(g, 2, 3, 1);
        addUEdge(g, 3, 4, 1);
        addUEdge(g, 4, 5, 1);

        assertEquals(4.0, g.getDiameter(PathCostType.SUM_EDGES), 1e-9);
        assertEquals(3, g.getCenter(PathCostType.SUM_EDGES).id());

        assertEquals(1.0, g.getDiameter(PathCostType.MAX_EDGE), 1e-9);
        assertEquals(1, g.getCenter(PathCostType.MAX_EDGE).id());
    }

    // Weighted chain 1–2–3–4 shows different SUM vs MAX behavior and center tie-break
    @Test
    void weighted_chain_tie_breaks() {
        var g = g();
        addVertices(g, 1, 2, 3, 4);
        addUEdge(g, 1, 2, 10);
        addUEdge(g, 2, 3, 1);
        addUEdge(g, 3, 4, 10);

        assertEquals(21.0, g.getDiameter(PathCostType.SUM_EDGES), 1e-9);
        assertEquals(2, g.getCenter(PathCostType.SUM_EDGES).id());

        assertEquals(10.0, g.getDiameter(PathCostType.MAX_EDGE), 1e-9);
        assertEquals(1, g.getCenter(PathCostType.MAX_EDGE).id());
    }

    // Star graph: unique center at the hub for both metrics
    @Test
    void star_graph() {
        var g = g();
        addVertices(g, 1, 2, 3, 4, 5);
        addUEdge(g, 1, 2, 1);
        addUEdge(g, 1, 3, 1);
        addUEdge(g, 1, 4, 1);
        addUEdge(g, 1, 5, 1);

        assertEquals(2.0, g.getDiameter(PathCostType.SUM_EDGES), 1e-9);
        assertEquals(1, g.getCenter(PathCostType.SUM_EDGES).id());

        assertEquals(1.0, g.getDiameter(PathCostType.MAX_EDGE), 1e-9);
        assertEquals(1, g.getCenter(PathCostType.MAX_EDGE).id());
    }

    // Cycle of 6 vertices: multiple centers exist; smallest-id center chosen
    @Test
    void cycle_graph() {
        var g = g();
        addVertices(g, 1, 2, 3, 4, 5, 6);
        addUEdge(g, 1, 2, 1);
        addUEdge(g, 2, 3, 1);
        addUEdge(g, 3, 4, 1);
        addUEdge(g, 4, 5, 1);
        addUEdge(g, 5, 6, 1);
        addUEdge(g, 6, 1, 1);

        assertEquals(3.0, g.getDiameter(PathCostType.SUM_EDGES), 1e-9);
        assertEquals(1, g.getCenter(PathCostType.SUM_EDGES).id());

        assertEquals(1.0, g.getDiameter(PathCostType.MAX_EDGE), 1e-9);
        assertEquals(1, g.getCenter(PathCostType.MAX_EDGE).id());
    }

    // Zero-weight edges: all distances collapse to zero for both cost models
    @Test
    void zero_weight_edges() {
        var g = g();
        addVertices(g, 1, 2, 3, 4);
        addUEdge(g, 1, 2, 0);
        addUEdge(g, 2, 3, 0);
        addUEdge(g, 3, 4, 0);

        assertEquals(0.0, g.getDiameter(PathCostType.SUM_EDGES), 1e-9);
        assertEquals(1, g.getCenter(PathCostType.SUM_EDGES).id());

        assertEquals(0.0, g.getDiameter(PathCostType.MAX_EDGE), 1e-9);
        assertEquals(1, g.getCenter(PathCostType.MAX_EDGE).id());
    }

    // Empty graph and singleton graph: edge-case behavior for diameter and center
    @Test
    void empty_and_singleton() {
        var gEmpty = g();
        assertEquals(0.0, gEmpty.getDiameter(PathCostType.SUM_EDGES), 1e-9);
        assertThrows(IllegalStateException.class,
            () -> gEmpty.getCenter(PathCostType.SUM_EDGES));

        var g1 = g();
        addVertices(g1, 42);

        assertEquals(0.0, g1.getDiameter(PathCostType.SUM_EDGES), 1e-9);
        assertEquals(42, g1.getCenter(PathCostType.SUM_EDGES).id());

        assertEquals(0.0, g1.getDiameter(PathCostType.MAX_EDGE), 1e-9);
        assertEquals(42, g1.getCenter(PathCostType.MAX_EDGE).id());
    }

    //Tie for largest component size: component with the smallest minimum id is chosen
    @Test
    void tie_largest_component_different_diameters() {
        var g = g();
        addVertices(g, 1, 2, 3, 10, 11, 12);
        addUEdge(g, 1, 2, 1);
        addUEdge(g, 2, 3, 1);
        addUEdge(g, 10, 11, 100);
        addUEdge(g, 11, 12, 100);

        assertEquals(2.0, g.getDiameter(PathCostType.SUM_EDGES), 1e-9);
        assertEquals(2, g.getCenter(PathCostType.SUM_EDGES).id());
    }

    // Stale priority-queue entries exercised for both SUM_EDGES and MAX_EDGE Dijkstra variants
    @Test
    void stale_pq_entries_sum_and_max() {
        var g1 = g();
        addVertices(g1, 1, 2, 3);
        addUEdge(g1, 1, 2, 100);
        addUEdge(g1, 1, 3, 1);
        addUEdge(g1, 3, 2, 1);

        assertEquals(2.0, g1.getDiameter(PathCostType.SUM_EDGES), 1e-9);
        assertEquals(3, g1.getCenter(PathCostType.SUM_EDGES).id());

        var g2 = g();
        addVertices(g2, 1, 2, 3);
        addUEdge(g2, 1, 2, 10);
        addUEdge(g2, 1, 3, 5);
        addUEdge(g2, 3, 2, 5);

        assertEquals(5.0, g2.getDiameter(PathCostType.MAX_EDGE), 1e-9);
        assertEquals(1, g2.getCenter(PathCostType.MAX_EDGE).id());
    }

    // getComponents on an empty graph should return an empty set.
    @Test
    public void componentsEmptyGraph() {
        AdjacencyListGraph<Vertex, Edge<Vertex>> graph = g();
        assertTrue(graph.getComponents().isEmpty(),
            "Empty graph should have no components");
    }

    // getComponents on a single vertex with no edges -> one singleton component.
    @Test
    public void componentsSingleVertexNoEdges() {
        AdjacencyListGraph<Vertex, Edge<Vertex>> graph = g();
        addVertices(graph, 1);

        Set<Set<Vertex>> comps = graph.getComponents();
        assertEquals(1, comps.size());

        Set<Set<Vertex>> expected = new HashSet<>();
        expected.add(Set.of(v(1)));

        assertEquals(expected, comps);
    }

    // getComponents on a simple line 1-2-3-4 -> one connected component {1,2,3,4}.
    @Test
    public void componentsSingleConnectedComponentLine() {
        AdjacencyListGraph<Vertex, Edge<Vertex>> graph = g();
        addVertices(graph, 1, 2, 3, 4);

        addUEdge(graph, 1, 2, 1.0);
        addUEdge(graph, 2, 3, 2.0);
        addUEdge(graph, 3, 4, 3.0);

        Set<Set<Vertex>> comps = graph.getComponents();

        Set<Set<Vertex>> expected = new HashSet<>();
        expected.add(Set.of(v(1), v(2), v(3), v(4)));

        assertEquals(expected, comps);
    }

    // getComponents on multiple components, including an isolated vertex.
    @Test
    public void componentsMultipleComponentsIncludingIsolated() {
        AdjacencyListGraph<Vertex, Edge<Vertex>> graph = g();
        addVertices(graph, 1, 2, 3, 4, 5, 6);

        // Component 1: 1 - 2
        addUEdge(graph, 1, 2, 1.0);
        // Component 2: isolated vertex 3
        // Component 3: 4 - 5 - 6
        addUEdge(graph, 4, 5, 1.0);
        addUEdge(graph, 5, 6, 1.0);

        Set<Set<Vertex>> comps = graph.getComponents();

        Set<Set<Vertex>> expected = new HashSet<>();
        expected.add(Set.of(v(1), v(2)));
        expected.add(Set.of(v(3)));
        expected.add(Set.of(v(4), v(5), v(6)));

        assertEquals(expected, comps);
    }

    /* ==================================== partitionGraph error-handling tests ================ */

    // partitionGraph should throw NullPointerException when graph is null.
    @Test
    public void partitionNullGraphThrows() {
        assertThrows(NullPointerException.class,
            () -> Algorithms.partitionGraph(null, 1));
    }

    // Empty graph: requesting 0 partitions is allowed and returns an empty graph.
    @Test
    public void partitionEmptyGraphZeroPartitionsOk() {
        AdjacencyListGraph<Vertex, Edge<Vertex>> graph = g();

        Graph<Vertex, Edge<Vertex>> result = Algorithms.partitionGraph(graph, 0);

        assertTrue(result.getVertices().isEmpty());
        assertTrue(result.getEdges().isEmpty());
        assertTrue(result.getComponents().isEmpty());
    }

    // Empty graph: requesting any non-zero number of partitions should throw.
    @Test
    public void partitionEmptyGraphNonZeroThrows() {
        AdjacencyListGraph<Vertex, Edge<Vertex>> graph = g();

        assertThrows(IllegalArgumentException.class,
            () -> Algorithms.partitionGraph(graph, 1));
    }

    // Graph with two isolated vertices: asking for 1 partition (less than #components) should throw.
    @Test
    public void partitionTooFewPartitionsForComponentsThrows() {
        AdjacencyListGraph<Vertex, Edge<Vertex>> graph = g();
        // Two isolated vertices => 2 connected components
        addVertices(graph, 1, 2);

        assertThrows(IllegalArgumentException.class,
            () -> Algorithms.partitionGraph(graph, 1));
    }

    // Graph with 2 vertices: asking for more partitions than vertices should throw.
    @Test
    public void partitionTooManyPartitionsThrows() {
        AdjacencyListGraph<Vertex, Edge<Vertex>> graph = g();
        addVertices(graph, 1, 2);
        addUEdge(graph, 1, 2, 1.0);

        // |V| = 2 => k = 3 is invalid
        assertThrows(IllegalArgumentException.class,
            () -> Algorithms.partitionGraph(graph, 3));
    }

    /* ============================= partitionGraph: A–B–C–D example ================= */

    // Build the A–B–C–D example graph from the handout:
    // vertices 1(A),2(B),3(C),4(D) with edges:
    // A-B(5), A-C(10), B-C(3), C-D(1).
    private AdjacencyListGraph<Vertex, Edge<Vertex>> abcGraph() {
        AdjacencyListGraph<Vertex, Edge<Vertex>> graph = g();
        addVertices(graph, 1, 2, 3, 4);

        addUEdge(graph, 1, 2, 5.0);   // A-B
        addUEdge(graph, 1, 3, 10.0);  // A-C
        addUEdge(graph, 2, 3, 3.0);   // B-C
        addUEdge(graph, 3, 4, 1.0);   // C-D (shortest)
        return graph;
    }

    // A–B–C–D graph: k = 4 => all vertices isolated, no edges.
    @Test
    public void partitionABCDFourPartitions() {
        AdjacencyListGraph<Vertex, Edge<Vertex>> graph = abcGraph();

        Graph<Vertex, Edge<Vertex>> result =
            Algorithms.partitionGraph(graph, 4);

        Set<Set<Vertex>> comps = result.getComponents();
        Set<Set<Vertex>> expected = new HashSet<>();
        expected.add(Set.of(v(1)));
        expected.add(Set.of(v(2)));
        expected.add(Set.of(v(3)));
        expected.add(Set.of(v(4)));

        assertEquals(expected, comps);
        assertTrue(result.getEdges().isEmpty());
    }

    // A–B–C–D graph: k = 3 => C and D should merge via the shortest edge (C-D).
    @Test
    public void partitionABCDThreePartitions() {
        AdjacencyListGraph<Vertex, Edge<Vertex>> graph = abcGraph();

        Graph<Vertex, Edge<Vertex>> result =
            Algorithms.partitionGraph(graph, 3);

        Set<Set<Vertex>> comps = result.getComponents();
        Set<Set<Vertex>> expected = new HashSet<>();
        expected.add(Set.of(v(1)));
        expected.add(Set.of(v(2)));
        expected.add(Set.of(v(3), v(4))); // C-D join via the shortest edge

        assertEquals(expected, comps);
        assertEquals(1, result.getEdges().size());
        assertTrue(result.hasEdge(v(3), v(4)));
    }

    // A–B–C–D graph: k = 2 => {1} and {2,3,4} according to the shortest-edge merges.
    @Test
    public void partitionABCDTwoPartitions() {
        AdjacencyListGraph<Vertex, Edge<Vertex>> graph = abcGraph();

        Graph<Vertex, Edge<Vertex>> result =
            Algorithms.partitionGraph(graph, 2);

        Set<Set<Vertex>> comps = result.getComponents();
        Set<Set<Vertex>> expected = new HashSet<>();
        expected.add(Set.of(v(1)));
        expected.add(Set.of(v(2), v(3), v(4)));

        assertEquals(expected, comps);
        assertTrue(result.hasEdge(v(3), v(4)));
        assertTrue(result.hasEdge(v(2), v(3)));
        assertEquals(2, result.getEdges().size());
    }

    // A–B–C–D graph: k = 1 => original graph, all edges kept.
    @Test
    public void partitionABCDOnePartition() {
        AdjacencyListGraph<Vertex, Edge<Vertex>> graph = abcGraph();

        Graph<Vertex, Edge<Vertex>> result =
            Algorithms.partitionGraph(graph, 1);

        Set<Set<Vertex>> comps = result.getComponents();
        Set<Set<Vertex>> expected = new HashSet<>();
        expected.add(Set.of(v(1), v(2), v(3), v(4)));

        assertEquals(expected, comps);
        assertEquals(4, result.getEdges().size());
        assertTrue(result.hasEdge(v(1), v(2)));
        assertTrue(result.hasEdge(v(2), v(3)));
        assertTrue(result.hasEdge(v(3), v(4)));
        assertTrue(result.hasEdge(v(1), v(3)));
    }

    /* =================================== partitionGraph: disconnected graph ======================= */

    /**
     * Builds a disconnected graph with:
     * Component 1: 1 - 2 - 3  (weights 1, 2)
     * Component 2: 4 - 5      (weight 3)
     */
    private AdjacencyListGraph<Vertex, Edge<Vertex>> disconnectedGraph() {
        AdjacencyListGraph<Vertex, Edge<Vertex>> graph = g();
        addVertices(graph, 1, 2, 3, 4, 5);

        addUEdge(graph, 1, 2, 1.0);
        addUEdge(graph, 2, 3, 2.0);
        addUEdge(graph, 4, 5, 3.0);

        return graph;
    }

    // Disconnected graph: k = minComponents = 2 => keep each connected component intact.
    @Test
    public void partitionDisconnectedGraphMinPartitions() {
        AdjacencyListGraph<Vertex, Edge<Vertex>> graph = disconnectedGraph();

        // minNumPartitions = number of connected components = 2
        Graph<Vertex, Edge<Vertex>> result =
            Algorithms.partitionGraph(graph, 2);

        Set<Set<Vertex>> comps = result.getComponents();
        Set<Set<Vertex>> expected = new HashSet<>();
        expected.add(Set.of(v(1), v(2), v(3)));
        expected.add(Set.of(v(4), v(5)));

        assertEquals(expected, comps);
        assertEquals(3, result.getEdges().size());
        assertTrue(result.hasEdge(v(1), v(2)));
        assertTrue(result.hasEdge(v(2), v(3)));
        assertTrue(result.hasEdge(v(4), v(5)));
    }

    // Disconnected graph: k = 3 => only the shortest edges are used until we hit 3 components.
    @Test
    public void partitionDisconnectedGraphIntermediatePartitions() {
        AdjacencyListGraph<Vertex, Edge<Vertex>> graph = disconnectedGraph();

        // Request 3 partitions: we expect {1,2,3}, {4}, {5}
        Graph<Vertex, Edge<Vertex>> result =
            Algorithms.partitionGraph(graph, 3);

        Set<Set<Vertex>> comps = result.getComponents();

        // Edges sorted by weight: (1-2)[1], (2-3)[2], (4-5)[3]
        // Merges:
        //   start: 5 sets
        //   (1-2) -> 4 sets
        //   (2-3) -> 3 sets, then stop (no union for 4-5).
        Set<Set<Vertex>> expected = new HashSet<>();
        expected.add(Set.of(v(1), v(2), v(3)));
        expected.add(Set.of(v(4)));
        expected.add(Set.of(v(5)));

        assertEquals(expected, comps);
        assertTrue(result.hasEdge(v(1), v(2)));
        assertTrue(result.hasEdge(v(2), v(3)));
        assertFalse(result.hasEdge(v(4), v(5)));
    }

    // Disconnected graph: k = |V| = 5 => no unions, everything becomes isolated vertices.
    @Test
    public void partitionDisconnectedGraphMaxPartitions() {
        AdjacencyListGraph<Vertex, Edge<Vertex>> graph = disconnectedGraph();

        // Request |V| partitions = 5: no unions should occur.
        Graph<Vertex, Edge<Vertex>> result =
            Algorithms.partitionGraph(graph, 5);

        Set<Set<Vertex>> comps = result.getComponents();
        Set<Set<Vertex>> expected = new HashSet<>();
        expected.add(Set.of(v(1)));
        expected.add(Set.of(v(2)));
        expected.add(Set.of(v(3)));
        expected.add(Set.of(v(4)));
        expected.add(Set.of(v(5)));

        assertEquals(expected, comps);
        assertTrue(result.getEdges().isEmpty());
    }

    /**
     * Breaks the invariant that every neighbour vertex is a key in adj:
     * - Build a small graph 1-2-3
     * - Then remove vertex 2 from adj (but leave entries pointing to it)
     * - checkRep() should fail at assert adj.containsKey(u).
     */
    @Test
    public void listCheckRepMissingNeighbourVertex() throws Exception {
        AdjacencyListGraph<Vertex, Edge<Vertex>> g = g();
        g.addVertex(v(1));
        g.addVertex(v(2));
        g.addVertex(v(3));

        g.addEdge(new Edge<>(v(1), v(2), 1.0));
        g.addEdge(new Edge<>(v(2), v(3), 2.0));

        // Access private field "adj".
        Field adjField = AdjacencyListGraph.class.getDeclaredField("adj");
        adjField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Vertex, Map<Vertex, Edge<Vertex>>> adj =
            (Map<Vertex, Map<Vertex, Edge<Vertex>>>) adjField.get(g);

        // Remove one vertex entirely from the map, leaving neighbour references pointing to it.
        // We want a vertex that has neighbours; pick the one with id==2.
        Vertex v2 = adj.keySet().stream().filter(v -> v.id() == 2).findFirst().orElseThrow();
        adj.remove(
            v2);  // Now other vertices still have edges to v2, but adj.containsKey(v2) is false.

        assertThrows(AssertionError.class, () -> invokeListCheckRep(g));
    }

    /**
     * Breaks the invariant that adjacency is symmetric:
     * - Build graph 1-2
     * - Delete the reverse edge entry from adj.get(2) while keeping adj.get(1) pointing to 2.
     * - checkRep() should fail at assert rev.get(v) == e.
     */
    @Test
    public void listCheckRepAsymmetricEdge() throws Exception {
        AdjacencyListGraph<Vertex, Edge<Vertex>> g = g();
        g.addVertex(v(1));
        g.addVertex(v(2));
        g.addEdge(new Edge<>(v(1), v(2), 5.0));

        Field adjField = AdjacencyListGraph.class.getDeclaredField("adj");
        adjField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Vertex, Map<Vertex, Edge<Vertex>>> adj =
            (Map<Vertex, Map<Vertex, Edge<Vertex>>>) adjField.get(g);

        // Find the two vertex objects inside the map.
        Vertex v1Key = adj.keySet().stream().filter(v -> v.id() == 1).findFirst().orElseThrow();
        Vertex v2Key = adj.keySet().stream().filter(v -> v.id() == 2).findFirst().orElseThrow();

        // Remove the back edge 2 -> 1, but keep 1 -> 2.
        Map<Vertex, Edge<Vertex>> numbersOf2 = adj.get(v2Key);
        numbersOf2.remove(v1Key);

        // Now for v = 1, u = 2:
        //   adj.containsKey(2) is true,
        //   rev = adj.get(2) is not null,
        //   but rev.get(1) is null while e is non-null => assertion fails.
        assertThrows(AssertionError.class, () -> invokeListCheckRep(g));
    }

    /**
     * Breaks the invariant that each stored edge is incident on both endpoints:
     * - Build graph 1-2
     * - Grab the edge object and mutate its v1 field via reflection to point
     * to a new vertex not in the graph (id 99).
     * - Then for the entry (1,2), e.incident(1) becomes false and checkRep()
     * should fail at assert e.incident(v) && e.incident(u).
     */
    @Test
    public void listCheckRepEdgeNotIncidentOnEndpoint() throws Exception {
        AdjacencyListGraph<Vertex, Edge<Vertex>> g = g();
        g.addVertex(v(1));
        g.addVertex(v(2));
        g.addEdge(new Edge<>(v(1), v(2), 7.0));

        // Access adj to fetch the stored edge.
        Field adjField = AdjacencyListGraph.class.getDeclaredField("adj");
        adjField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Vertex, Map<Vertex, Edge<Vertex>>> adj =
            (Map<Vertex, Map<Vertex, Edge<Vertex>>>) adjField.get(g);

        Vertex v1Key = adj.keySet().stream().filter(v -> v.id() == 1).findFirst().orElseThrow();
        Vertex v2Key = adj.keySet().stream().filter(v -> v.id() == 2).findFirst().orElseThrow();

        Edge<Vertex> e = adj.get(v1Key).get(v2Key);
        assertNotNull(e);

        // Mutate the edge so that it is no longer incident on v1.
        Field v1Field = Edge.class.getDeclaredField("v1");
        v1Field.setAccessible(true);
        v1Field.set(e, v(99));  // v(99) is not in the graph.

        // Now e.incident(v1Key) is false, while e.incident(v2Key) is still true.
        assertThrows(AssertionError.class, () -> invokeListCheckRep(g));
    }

    /* ================== AdjacencyMatrixGraph: break RIs ================== */

    /**
     * Breaks the invariant that there are no self-loops in the matrix:
     * - Build a 2-vertex graph with one edge between them.
     * - Use reflection to stuff a non-null edge into edgeMatrix[i][i].
     * - checkRep() should fail at assert edgeMatrix[i][i] == null.
     */
    @Test
    public void matrixCheckRepDiagonalNonNull() throws Exception {
        // Build vertices {1,2} and one edge(1-2).
        Set<Vertex> vertices = new HashSet<>();
        vertices.add(v(1));
        vertices.add(v(2));

        Set<Edge<Vertex>> edges = new HashSet<>();
        edges.add(new Edge<>(v(1), v(2), 1.0));

        AdjacencyMatrixGraph<Vertex, Edge<Vertex>> g =
            new AdjacencyMatrixGraph<>(vertices, edges);

        // Access idx to map vertex -> index.
        Field idxField = AdjacencyMatrixGraph.class.getDeclaredField("idx");
        idxField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Vertex, Integer> idx =
            (Map<Vertex, Integer>) idxField.get(g);

        int i = idx.get(v(1)); // index of vertex 1

        // Access edgeMatrix and put a non-null edge on the diagonal.
        Field matrixField = AdjacencyMatrixGraph.class.getDeclaredField("edgeMatrix");
        matrixField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Edge<Vertex>[][] matrix = (Edge<Vertex>[][]) matrixField.get(g);

        // Reuse the existing edge for convenience.
        Edge<Vertex> someEdge = edges.iterator().next();
        matrix[i][i] = someEdge;  // violate "no self-loop" invariant

        assertThrows(AssertionError.class, () -> invokeMatrixCheckRep(g));
    }

    /**
     * Breaks the invariant that the adjacency matrix is symmetric:
     * - Build a 2-vertex graph with one edge between them.
     * - Manually set edgeMatrix[i][j] != edgeMatrix[j][i].
     * - checkRep() should fail at assert edgeMatrix[j][i] == e.
     */
    @Test
    public void matrixCheckRepAsymmetricMatrix() throws Exception {
        // Build vertices {1,2} and one edge(1-2).
        Set<Vertex> vertices = new HashSet<>();
        vertices.add(v(1));
        vertices.add(v(2));

        Set<Edge<Vertex>> edges = new HashSet<>();
        edges.add(new Edge<>(v(1), v(2), 2.0));

        AdjacencyMatrixGraph<Vertex, Edge<Vertex>> g =
            new AdjacencyMatrixGraph<>(vertices, edges);

        // Access idx to map vertex -> index.
        Field idxField = AdjacencyMatrixGraph.class.getDeclaredField("idx");
        idxField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Vertex, Integer> idx =
            (Map<Vertex, Integer>) idxField.get(g);

        int i = idx.get(v(1));
        int j = idx.get(v(2));

        // Access edgeMatrix and break symmetry.
        Field matrixField = AdjacencyMatrixGraph.class.getDeclaredField("edgeMatrix");
        matrixField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Edge<Vertex>[][] matrix = (Edge<Vertex>[][]) matrixField.get(g);

        // Original constructor set matrix[i][j] == matrix[j][i] == e.
        Edge<Vertex> e = matrix[i][j];
        assertNotNull(e);

        // Break symmetry: set the opposite entry to null.
        matrix[j][i] = null;

        // Now checkRep should see e at [i][j] but null at [j][i] and assert.
        assertThrows(AssertionError.class, () -> invokeMatrixCheckRep(g));
    }

    /**
     * Breaks adj != null by setting the field to null.
     */
    @Test
    public void listCheckRepAdjNull() throws Exception {
        AdjacencyListGraph<Vertex, Edge<Vertex>> g = g();
        // Make sure we actually have a non-empty adj first.
        g.addVertex(v(1));

        Field adjField = AdjacencyListGraph.class.getDeclaredField("adj");
        adjField.setAccessible(true);
        adjField.set(g, null);  // violate adj != null

        assertThrows(AssertionError.class, () -> invokeListCheckRep(g));
    }

    /**
     * Adds a null key to adj so v becomes null in the outer loop.
     */
    @Test
    public void listCheckRepNullVertexKey() throws Exception {
        AdjacencyListGraph<Vertex, Edge<Vertex>> g = g();
        g.addVertex(v(1));

        Field adjField = AdjacencyListGraph.class.getDeclaredField("adj");
        adjField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Vertex, Map<Vertex, Edge<Vertex>>> adj =
            (Map<Vertex, Map<Vertex, Edge<Vertex>>>) adjField.get(g);

        // Insert an entry with a null key but non-null map value.
        adj.put(null, new java.util.HashMap<>());

        assertThrows(AssertionError.class, () -> invokeListCheckRep(g));
    }

    /**
     * Uses a null neighbour-map value so numbers becomes null.
     */
    @Test
    public void listCheckRepNullNeighbourMap() throws Exception {
        AdjacencyListGraph<Vertex, Edge<Vertex>> g = g();
        g.addVertex(v(1));

        Field adjField = AdjacencyListGraph.class.getDeclaredField("adj");
        adjField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Vertex, Map<Vertex, Edge<Vertex>>> adj =
            (Map<Vertex, Map<Vertex, Edge<Vertex>>>) adjField.get(g);

        // Pick the existing vertex key and make its map null.
        Vertex existing = adj.keySet().iterator().next();
        adj.put(existing, null);

        assertThrows(AssertionError.class, () -> invokeListCheckRep(g));
    }

    /**
     * Adds a null neighbour key so u becomes null in the inner loop.
     */
    @Test
    public void listCheckRepNullNeighbourKey() throws Exception {
        AdjacencyListGraph<Vertex, Edge<Vertex>> g = g();
        g.addVertex(v(1));
        g.addVertex(v(2));
        g.addEdge(new Edge<>(v(1), v(2), 1.0));

        Field adjField = AdjacencyListGraph.class.getDeclaredField("adj");
        adjField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Vertex, Map<Vertex, Edge<Vertex>>> adj =
            (Map<Vertex, Map<Vertex, Edge<Vertex>>>) adjField.get(g);

        Vertex v1Key = adj.keySet().stream().filter(x -> x.id() == 1).findFirst().orElseThrow();
        Map<Vertex, Edge<Vertex>> numbers = adj.get(v1Key);
        // Add an entry with null neighbour but non-null edge.
        numbers.put(null, new Edge<>(v(1), v(2), 2.0));

        assertThrows(AssertionError.class, () -> invokeListCheckRep(g));
    }

    /**
     * Adds a neighbour mapping to a null edge so e becomes null.
     */
    @Test
    public void listCheckRepNullEdgeValue() throws Exception {
        AdjacencyListGraph<Vertex, Edge<Vertex>> g = g();
        g.addVertex(v(1));
        g.addVertex(v(2));
        g.addEdge(new Edge<>(v(1), v(2), 1.0));

        Field adjField = AdjacencyListGraph.class.getDeclaredField("adj");
        adjField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Vertex, Map<Vertex, Edge<Vertex>>> adj =
            (Map<Vertex, Map<Vertex, Edge<Vertex>>>) adjField.get(g);

        Vertex v1Key = adj.keySet().stream().filter(x -> x.id() == 1).findFirst().orElseThrow();
        Vertex v2Key = adj.keySet().stream().filter(x -> x.id() == 2).findFirst().orElseThrow();

        Map<Vertex, Edge<Vertex>> numbers = adj.get(v1Key);
        // Overwrite the mapping to v2 with a null edge.
        numbers.put(v2Key, null);

        assertThrows(AssertionError.class, () -> invokeListCheckRep(g));
    }

    /**
     * Makes the reverse adjacency map null so rev becomes null.
     */
    @Test
    public void listCheckRepNullReverseMap() throws Exception {
        AdjacencyListGraph<Vertex, Edge<Vertex>> g = g();
        g.addVertex(v(1));
        g.addVertex(v(2));
        g.addEdge(new Edge<>(v(1), v(2), 1.0));

        Field adjField = AdjacencyListGraph.class.getDeclaredField("adj");
        adjField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Vertex, Map<Vertex, Edge<Vertex>>> adj =
            (Map<Vertex, Map<Vertex, Edge<Vertex>>>) adjField.get(g);

        Vertex v2Key = adj.keySet().stream().filter(x -> x.id() == 2).findFirst().orElseThrow();

        // Set the map for v2 to null, but keep v1 -> v2 mapping.
        adj.put(v2Key, null);

        assertThrows(AssertionError.class, () -> invokeListCheckRep(g));
    }

    /**
     * Introduces a fake self-loop entry so v.equals(u) becomes true.
     */
    @Test
    public void listCheckRepSelfLoopEntry() throws Exception {
        AdjacencyListGraph<Vertex, Edge<Vertex>> g = g();
        g.addVertex(v(1));
        g.addVertex(v(2));
        g.addEdge(new Edge<>(v(1), v(2), 1.0));

        Field adjField = AdjacencyListGraph.class.getDeclaredField("adj");
        adjField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Vertex, Map<Vertex, Edge<Vertex>>> adj =
            (Map<Vertex, Map<Vertex, Edge<Vertex>>>) adjField.get(g);

        Vertex v1Key = adj.keySet().stream().filter(x -> x.id() == 1).findFirst().orElseThrow();
        Map<Vertex, Edge<Vertex>> numbers = adj.get(v1Key);

        // Take some existing edge (1-2) and pretend it's a self-loop 1-1 in the adjacency map.
        Edge<Vertex> e = numbers.values().iterator().next();
        numbers.clear();
        numbers.put(v1Key, e);  // now u == v in the first (and only) inner-loop entry

        assertThrows(AssertionError.class, () -> invokeListCheckRep(g));
    }

    // matrix: break n >= 0
    @Test
    public void matrixCheckRepNegativeN() throws Exception {
        // Build a small, valid matrix graph first
        Set<Vertex> vertices = new HashSet<>();
        vertices.add(v(1));
        vertices.add(v(2));

        Set<Edge<Vertex>> edges = new HashSet<>();
        edges.add(new Edge<>(v(1), v(2), 1.0));

        AdjacencyMatrixGraph<Vertex, Edge<Vertex>> g =
            new AdjacencyMatrixGraph<>(vertices, edges);

        // Set n < 0
        Field nField = AdjacencyMatrixGraph.class.getDeclaredField("n");
        nField.setAccessible(true);
        nField.setInt(g, -1);

        assertThrows(AssertionError.class, () -> invokeMatrixCheckRep(g));
    }

    // matrix: break verticesByIdx.size() == n
    @Test
    public void matrixCheckRepVerticesByIdxSizeMismatch() throws Exception {
        Set<Vertex> vertices = new HashSet<>();
        vertices.add(v(1));
        vertices.add(v(2));
        vertices.add(v(3));

        Set<Edge<Vertex>> edges = new HashSet<>();
        edges.add(new Edge<>(v(1), v(2), 1.0));
        edges.add(new Edge<>(v(2), v(3), 2.0));

        AdjacencyMatrixGraph<Vertex, Edge<Vertex>> g =
            new AdjacencyMatrixGraph<>(vertices, edges);

        // Get current n (keep it valid)
        Field nField = AdjacencyMatrixGraph.class.getDeclaredField("n");
        nField.setAccessible(true);
        int n = nField.getInt(g);

        // Shrink verticesByIdx by one element
        Field vbiField = AdjacencyMatrixGraph.class.getDeclaredField("verticesByIdx");
        vbiField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.List<Vertex> verticesByIdx =
            (java.util.List<Vertex>) vbiField.get(g);

        verticesByIdx.remove(verticesByIdx.size() - 1);
        assertEquals(n - 1, verticesByIdx.size());

        assertThrows(AssertionError.class, () -> invokeMatrixCheckRep(g));
    }

    // matrix: break idx.size() == n
    @Test
    public void matrixCheckRepIdxSizeMismatch() throws Exception {
        Set<Vertex> vertices = new HashSet<>();
        vertices.add(v(1));
        vertices.add(v(2));
        vertices.add(v(3));

        Set<Edge<Vertex>> edges = new HashSet<>();
        edges.add(new Edge<>(v(1), v(2), 1.0));
        edges.add(new Edge<>(v(2), v(3), 2.0));

        AdjacencyMatrixGraph<Vertex, Edge<Vertex>> g =
            new AdjacencyMatrixGraph<>(vertices, edges);

        // n and verticesByIdx are consistent; break idx.size()
        Field idxField = AdjacencyMatrixGraph.class.getDeclaredField("idx");
        idxField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Vertex, Integer> idx =
            (Map<Vertex, Integer>) idxField.get(g);

        // Remove one mapping
        Vertex someVertex = idx.keySet().iterator().next();
        idx.remove(someVertex);

        assertThrows(AssertionError.class, () -> invokeMatrixCheckRep(g));
    }

    // matrix: break edgeMatrix.length == n
    @Test
    public void matrixCheckRepMatrixLengthMismatch() throws Exception {
        Set<Vertex> vertices = new HashSet<>();
        vertices.add(v(1));
        vertices.add(v(2));
        vertices.add(v(3));

        Set<Edge<Vertex>> edges = new HashSet<>();
        edges.add(new Edge<>(v(1), v(2), 1.0));
        edges.add(new Edge<>(v(2), v(3), 2.0));

        AdjacencyMatrixGraph<Vertex, Edge<Vertex>> g =
            new AdjacencyMatrixGraph<>(vertices, edges);

        // Read n
        Field nField = AdjacencyMatrixGraph.class.getDeclaredField("n");
        nField.setAccessible(true);
        int n = nField.getInt(g);

        // Replace edgeMatrix with a shorter one
        Field matrixField = AdjacencyMatrixGraph.class.getDeclaredField("edgeMatrix");
        matrixField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Edge<Vertex>[][] oldMatrix =
            (Edge<Vertex>[][]) matrixField.get(g);

        // Shrink outer length by 1 (n-1)
        Edge<Vertex>[][] newMatrix =
            java.util.Arrays.copyOf(oldMatrix, n - 1);

        matrixField.set(g, newMatrix);

        assertThrows(AssertionError.class, () -> invokeMatrixCheckRep(g));
    }

    // matrix: break edgeMatrix[i].length == n for some row
    @Test
    public void matrixCheckRepRowLengthMismatch() throws Exception {
        Set<Vertex> vertices = new HashSet<>();
        vertices.add(v(1));
        vertices.add(v(2));
        vertices.add(v(3));

        Set<Edge<Vertex>> edges = new HashSet<>();
        edges.add(new Edge<>(v(1), v(2), 1.0));
        edges.add(new Edge<>(v(2), v(3), 2.0));

        AdjacencyMatrixGraph<Vertex, Edge<Vertex>> g =
            new AdjacencyMatrixGraph<>(vertices, edges);

        // Read n and edgeMatrix
        Field nField = AdjacencyMatrixGraph.class.getDeclaredField("n");
        nField.setAccessible(true);
        int n = nField.getInt(g);

        Field matrixField = AdjacencyMatrixGraph.class.getDeclaredField("edgeMatrix");
        matrixField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Edge<Vertex>[][] matrix =
            (Edge<Vertex>[][]) matrixField.get(g);

        // Keep outer length correct (matrix.length == n),
        // but make row 0 shorter than n.
        Edge<Vertex>[] shorterRow =
            java.util.Arrays.copyOf(matrix[0], n - 1);
        matrix[0] = shorterRow;

        assertThrows(AssertionError.class, () -> invokeMatrixCheckRep(g));
    }

    //===================Task 5 break rep for Algorithms.java=================

    /**
     * Break RI: parent.keySet() != rankByRoot.keySet().
     */
    @Test
    public void disjointSetParentRankKeySetMismatch() throws Exception {
        Object dsu = makeDisjointSet(Set.of(v(1), v(2)));

        Field rankField = dsu.getClass().getDeclaredField("rankByRoot");
        rankField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Vertex, Integer> rankMap =
            (Map<Vertex, Integer>) rankField.get(dsu);

        // Remove one key from rankByRoot so keysets differ.
        Vertex victim = rankMap.keySet().iterator().next();
        rankMap.remove(victim);

        assertThrows(AssertionError.class, () -> invokeDisjointSetCheckRep(dsu));
    }

    /**
     * Break RI: parent.get(x) must be in parent.keySet() (parent points outside universe).
     */
    @Test
    public void disjointSetParentPointsOutsideUniverse() throws Exception {
        Object dsu = makeDisjointSet(Set.of(v(1), v(2)));

        Field parentField = dsu.getClass().getDeclaredField("parent");
        parentField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Vertex, Vertex> parentMap =
            (Map<Vertex, Vertex>) parentField.get(dsu);

        // Take an existing element and point it to a brand-new vertex
        // that is not in parentMap.keySet().
        Vertex someKey = parentMap.keySet().iterator().next();
        parentMap.put(someKey, v(999));

        assertThrows(AssertionError.class, () -> invokeDisjointSetCheckRep(dsu));
    }

    /**
     * Break RI: rankByRoot.get(x) >= 0 (make a negative rank).
     */
    @Test
    public void disjointSetNegativeRank() throws Exception {
        Object dsu = makeDisjointSet(Set.of(v(1), v(2), v(3)));

        Field rankField = dsu.getClass().getDeclaredField("rankByRoot");
        rankField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Vertex, Integer> rankMap =
            (Map<Vertex, Integer>) rankField.get(dsu);

        Vertex target = rankMap.keySet().iterator().next();
        rankMap.put(target, -1); // violate rank >= 0

        assertThrows(AssertionError.class, () -> invokeDisjointSetCheckRep(dsu));
    }

    /**
     * Break RI: setCount must equal the number of roots.
     */
    @Test
    public void disjointSetSetCountMismatch() throws Exception {
        Object dsu = makeDisjointSet(Set.of(v(1), v(2), v(3)));

        Field setCountField = dsu.getClass().getDeclaredField("setCount");
        setCountField.setAccessible(true);
        int oldCount = (int) setCountField.get(dsu);

        // Make setCount wrong: larger than actual root count.
        setCountField.set(dsu, oldCount + 1);

        assertThrows(AssertionError.class, () -> invokeDisjointSetCheckRep(dsu));
    }

    @Test
    void utils_extractWord_cases() {
        assertEquals("hello", Utils.extractWord("hello"));
        assertEquals("world", Utils.extractWord("...world!!"));
        assertEquals("real-time", Utils.extractWord("\"real-time\""));
        assertEquals("", Utils.extractWord("!!!"));
        assertEquals("", Utils.extractWord("   "));
    }

    @Test
    void utils_isFile_and_readContent_file_and_invalid() throws IOException {
        // real file case
        File temp = createTempFileWithContent("utils-file", "line1\nline2");
        String path = temp.getAbsolutePath();

        assertTrue(Utils.isFile(path));

        String text = Utils.readContent(path);
        assertNotNull(text);
        assertTrue(text.contains("line1"));
        assertTrue(text.contains("line2"));

        // non-existing file
        String bogus = "definitely-not-a-real-file-" + System.nanoTime();
        assertFalse(Utils.isFile(bogus));

        // invalid name: neither file nor URL
        String invalid = "not a url or file :::";
        assertFalse(Utils.isValidURL(invalid));
        assertNull(Utils.readContent(invalid));
    }

    @Test
    void utils_isValidURL_variants() {
        // syntactically valid
        assertTrue(Utils.isValidURL("https://example.com"));

        // malformed protocol => MalformedURLException
        assertFalse(Utils.isValidURL("ht!tp://bad-url"));

        // valid URL but invalid URI => URISyntaxException
        assertFalse(Utils.isValidURL("http://example.com/["));
    }

    @Test
    void utils_readContent_urlBranchIOException() {
        // syntactically valid URL pointing at a non-existent domain:
        // isValidURL(...) should be true, but openStream() will throw IOException.
        String unreachable = "http://nonexistent-host-xyz-12345.invalid";

        assertTrue(Utils.isValidURL(unreachable));
        String content = Utils.readContent(unreachable);
        assertNull(content);
    }

    /* ===================== Document ===================== */

    @Test
    void document_basicCounting_and_accessors_and_unmodifiableMap() throws IOException {
        // Mix of punctuation, case, and numbers
        File file = createTempFileWithContent(
            "doc-basic",
            "Hello, HELLO!! world... 123 ! $$"
        );
        String path = file.getAbsolutePath();

        Document doc = new Document("doc1", path);

        assertEquals("doc1", doc.getDocumentId());
        assertEquals(path, doc.getDocumentURL());
        assertEquals("doc1", doc.toString());

        Map<String, Integer> counts = doc.getCountMap();
        assertEquals(3, counts.size());
        assertEquals(2, counts.get("hello"));
        assertEquals(1, counts.get("world"));
        assertEquals(1, counts.get("123"));

        // Unmodifiable view
        assertThrows(UnsupportedOperationException.class,
            () -> counts.put("new", 1));
    }

    @Test
    void document_twoConstructors_and_emptyFile_and_bothEmptyJSD() throws IOException {
        File empty = createTempFileWithContent("doc-empty", "");
        String path = empty.getAbsolutePath();

        Document d1 = new Document("id", path);
        Document d2 = new Document(path);   // id == url

        assertEquals("id", d1.getDocumentId());
        assertEquals(path, d1.getDocumentURL());

        assertEquals(path, d2.getDocumentId());
        assertEquals(path, d2.getDocumentURL());

        assertTrue(d1.getCountMap().isEmpty());
        assertTrue(d2.getCountMap().isEmpty());

        // Both empty ⇒ JSD = 0
        assertEquals(0L, d1.computeJSDiv(d2));
        assertEquals(0.0, Similarity.getJensenShannonDivergence(d1, d2));
    }

    @Test
    void document_jsd_oneEmpty_oneNonEmpty_and_nullOtherDoc() throws IOException {
        Document emptyDoc = makeDocFromContent("empty", "");
        Document nonEmpty = makeDocFromContent("nonEmpty", "apple banana");

        // One empty, one non-empty ⇒ raw JSD = 1.0 ⇒ scaled 1000
        assertEquals(1000L, emptyDoc.computeJSDiv(nonEmpty));
        assertEquals(1000.0, Similarity.getJensenShannonDivergence(emptyDoc, nonEmpty));

        // Null parameter propagates NPE from Similarity.rawJensenShannonDivergence
        assertThrows(NullPointerException.class,
            () -> emptyDoc.computeJSDiv(null));
        assertThrows(NullPointerException.class,
            () -> Similarity.getJensenShannonDivergence(nonEmpty, null));
    }

    @Test
    void document_jsd_generalCase_and_symmetry() throws IOException {
        // d1 has {apple, banana, date}, d2 has {apple, cherry}
        Document d1 = makeDocFromContent("d1", "apple banana date apple");
        Document d2 = makeDocFromContent("d2", "apple cherry");

        double jsd1 = Similarity.getJensenShannonDivergence(d1, d2);
        double jsd2 = Similarity.getJensenShannonDivergence(d2, d1);

        // Symmetric
        assertEquals(jsd1, jsd2, 1e-9);

        // Non-zero, within [0, 1000]
        assertTrue(jsd1 > 0.0);
        assertTrue(jsd1 <= 1000.0);

        // Document.computeJSDiv should match the rounded JSD from Similarity
        long fromDoc = d1.computeJSDiv(d2);
        assertEquals((long) jsd1, fromDoc);
    }

    @Test
    void document_constructor_ioFailureWrapsAsIOException() {
        // Path that does not exist and is not a URL
        String bogusPath = "nonexistent-file-" + System.nanoTime() + ".txt";

        assertThrows(IOException.class,
            () -> new Document("id", bogusPath));
    }

    /* ===================== DocumentVertex ===================== */

    @Test
    void documentVertex_wrapsDocument_and_exposesId() throws IOException {
        Document doc = makeDocFromContent("docV", "hello world");
        DocumentVertex dv = new DocumentVertex(7, doc);

        assertEquals(7, dv.id());
        assertSame(doc, dv.getDocument());
        assertEquals("docV", dv.getDocument().getDocumentId());
    }

    /* ===================== Similarity.groupSimilarDocuments ===================== */

    @Test
    void similarity_group_nullSetAndEmptySetCases() {
        // Null set → NPE
        assertThrows(NullPointerException.class,
            () -> Similarity.groupSimilarDocuments(null, 1));

        // Empty set, allowed k = 0 or 1 → empty result
        Set<Document> empty = Set.<Document>of();
        assertTrue(Similarity.groupSimilarDocuments(empty, 0).isEmpty());
        assertTrue(Similarity.groupSimilarDocuments(empty, 1).isEmpty());

        // Empty set, invalid k → IAE
        assertThrows(IllegalArgumentException.class,
            () -> Similarity.groupSimilarDocuments(empty, 2));
    }

    @Test
    void similarity_group_invalidGroupSizes_nonEmptySet() throws IOException {
        Document d1 = makeDocFromContent("g1", "a");
        Document d2 = makeDocFromContent("g2", "b");
        Set<Document> docs = Set.of(d1, d2);

        // k < 1
        assertThrows(IllegalArgumentException.class,
            () -> Similarity.groupSimilarDocuments(docs, 0));
        assertThrows(IllegalArgumentException.class,
            () -> Similarity.groupSimilarDocuments(docs, -1));

        // k > |docs|
        assertThrows(IllegalArgumentException.class,
            () -> Similarity.groupSimilarDocuments(docs, 3));
    }

    @Test
    void similarity_group_validSizes_one_two_and_all() throws IOException {
        Document d1 = makeDocFromContent("gd1", "apple apple apple");
        Document d2 = makeDocFromContent("gd2", "apple banana");
        Document d3 = makeDocFromContent("gd3", "zebra zulu");
        Set<Document> docs = Set.of(d1, d2, d3);

        // k = 1: one big group containing all documents
        Set<Set<Document>> groups1 = Similarity.groupSimilarDocuments(docs, 1);
        assertEquals(1, groups1.size());
        assertEquals(docs, groups1.iterator().next());

        // k = 3: each document in its own singleton group
        Set<Set<Document>> groups3 = Similarity.groupSimilarDocuments(docs, 3);
        assertEquals(3, groups3.size());
        int totalSize3 = groups3.stream().mapToInt(Set::size).sum();
        assertEquals(3, totalSize3);
        Set<Document> union3 = groups3.stream()
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
        assertEquals(docs, union3);

        // k = 2: two groups whose union is the full set (exact partitioning)
        Set<Set<Document>> groups2 = Similarity.groupSimilarDocuments(docs, 2);
        assertEquals(2, groups2.size());
        Set<Document> union2 = groups2.stream()
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
        assertEquals(docs, union2);
    }

    /** documentURL must be non-null and non-empty: break by setting it to null. */
    @Test
    void documentCheckRep_nullUrl() throws Exception {
        Document doc = makeDocFromContent("doc-url-null", "hello world");

        Field urlField = Document.class.getDeclaredField("documentURL");
        urlField.setAccessible(true);
        urlField.set(doc, null);   // violate RI: documentURL != null

        assertThrows(AssertionError.class, () -> invokeDocumentCheckRep(doc));
    }

    /** documentId must be non-null and non-empty: break by setting it to "". */
    @Test
    void documentCheckRep_emptyId() throws Exception {
        Document doc = makeDocFromContent("doc-id-empty", "hello world");

        Field idField = Document.class.getDeclaredField("documentId");
        idField.setAccessible(true);
        idField.set(doc, "");      // violate RI: !documentId.isEmpty()

        assertThrows(AssertionError.class, () -> invokeDocumentCheckRep(doc));
    }

    /** wordCounts must be non-null: break by setting it to null. */
    @Test
    void documentCheckRep_nullWordCounts() throws Exception {
        Document doc = makeDocFromContent("doc-wc-null", "hello world");

        Field wcField = Document.class.getDeclaredField("wordCounts");
        wcField.setAccessible(true);
        wcField.set(doc, null);    // violate RI: wordCounts != null

        assertThrows(AssertionError.class, () -> invokeDocumentCheckRep(doc));
    }

    /** Each key in wordCounts must be non-null: inject a null key. */
    @Test
    void documentCheckRep_nullWordKey() throws Exception {
        Document doc = makeDocFromContent("doc-null-key", "hello world");

        Field wcField = Document.class.getDeclaredField("wordCounts");
        wcField.setAccessible(true);

        Field twField = Document.class.getDeclaredField("totalWordCount");
        twField.setAccessible(true);

        Map<String, Integer> badMap = new HashMap<>();
        badMap.put(null, 1);         // violate RI: word != null

        wcField.set(doc, badMap);
        twField.setInt(doc, 1);      // keep sumOfCounts == totalWordCount so we hit the key assert

        assertThrows(AssertionError.class, () -> invokeDocumentCheckRep(doc));
    }

    /** Each key in wordCounts must be lowercase: inject an uppercase key. */
    @Test
    void documentCheckRep_uppercaseWordKey() throws Exception {
        Document doc = makeDocFromContent("doc-upper-key", "hello world");

        Field wcField = Document.class.getDeclaredField("wordCounts");
        wcField.setAccessible(true);

        Field twField = Document.class.getDeclaredField("totalWordCount");
        twField.setAccessible(true);

        Map<String, Integer> badMap = new HashMap<>();
        badMap.put("Hello", 2);      // violate RI: word.equals(word.toLowerCase(...))

        wcField.set(doc, badMap);
        twField.setInt(doc, 2);      // consistent total

        assertThrows(AssertionError.class, () -> invokeDocumentCheckRep(doc));
    }

    /** Each value in wordCounts must be > 0: inject a zero count. */
    @Test
    void documentCheckRep_nonPositiveCount() throws Exception {
        Document doc = makeDocFromContent("doc-nonpos-count", "hello world");

        Field wcField = Document.class.getDeclaredField("wordCounts");
        wcField.setAccessible(true);

        Field twField = Document.class.getDeclaredField("totalWordCount");
        twField.setAccessible(true);

        Map<String, Integer> badMap = new HashMap<>();
        badMap.put("hello", 0);      // violate RI: count > 0

        wcField.set(doc, badMap);
        twField.setInt(doc, 0);      // sumOfCounts == 0

        assertThrows(AssertionError.class, () -> invokeDocumentCheckRep(doc));
    }

    /** totalWordCount must equal sum of counts: break by making them disagree. */
    @Test
    void documentCheckRep_totalWordCountMismatch() throws Exception {
        Document doc = makeDocFromContent("doc-sum-mismatch", "hello world");

        Field wcField = Document.class.getDeclaredField("wordCounts");
        wcField.setAccessible(true);

        Field twField = Document.class.getDeclaredField("totalWordCount");
        twField.setAccessible(true);

        Map<String, Integer> badMap = new HashMap<>();
        badMap.put("hello", 1);      // sumOfCounts = 1

        wcField.set(doc, badMap);
        twField.setInt(doc, 2);      // violate RI: sumOfCounts == totalWordCount

        assertThrows(AssertionError.class, () -> invokeDocumentCheckRep(doc));
    }


}
