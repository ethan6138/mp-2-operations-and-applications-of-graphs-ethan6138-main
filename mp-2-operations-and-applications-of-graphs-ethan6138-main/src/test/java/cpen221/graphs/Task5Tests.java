package cpen221.graphs;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import cpen221.graphs.graph.AdjacencyListGraph;
import cpen221.graphs.graph.Algorithms;
import cpen221.graphs.graph.Edge;
import cpen221.graphs.graph.Graph;
import cpen221.graphs.graph.Vertex;

public class Task5Tests{

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
     *   Component 1: 1 - 2 - 3  (weights 1, 2)
     *   Component 2: 4 - 5      (weight 3)
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

}
