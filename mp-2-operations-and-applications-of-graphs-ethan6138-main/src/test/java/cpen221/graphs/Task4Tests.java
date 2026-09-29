package cpen221.graphs;

import cpen221.graphs.graph.AdjacencyListGraph;
import cpen221.graphs.graph.Edge;
import cpen221.graphs.graph.PathCostType;
import cpen221.graphs.graph.Vertex;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class Task4Tests {

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

    // Tie for largest component size: component with the smallest minimum id is chosen
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

}
