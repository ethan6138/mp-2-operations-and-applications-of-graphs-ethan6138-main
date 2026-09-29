package cpen221.graphs;

import cpen221.graphs.graph.AdjacencyListGraph;
import cpen221.graphs.graph.AdjacencyMatrixGraph;
import cpen221.graphs.graph.Edge;
import cpen221.graphs.graph.PathCostType;
import cpen221.graphs.graph.Vertex;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Task3Tests {

    //Tests for list version
    private static Vertex v(int id) {
        return new Vertex(id);
    }

    @Test
    void pathCost_valid_and_invalid() {
        var g = new AdjacencyListGraph<>();
        Vertex a=v(1), b=v(2), c=v(3);
        g.addVertex(a); g.addVertex(b); g.addVertex(c);
        g.addEdge(new Edge<>(a,b, 2.0));
        g.addEdge(new Edge<>(b,c, 5.0));

        assertEquals(7.0, g.pathCost(List.of(a,b,c), PathCostType.SUM_EDGES), 1e-9);
        assertEquals(5.0, g.pathCost(List.of(a,b,c), PathCostType.MAX_EDGE), 1e-9);

        assertThrows(IllegalArgumentException.class, () -> g.pathCost(List.of(a), PathCostType.SUM_EDGES));
        assertThrows(NullPointerException.class, () -> g.pathCost(null, PathCostType.SUM_EDGES));
        assertThrows(IllegalArgumentException.class, () -> g.pathCost(List.of(a, v(999)), PathCostType.SUM_EDGES)); // non-adjacent pair
    }

    @Test
    void dijkstra_sum_improves_tentative_path() {
        var g = new AdjacencyListGraph<>();
        Vertex n1=v(1), n2=v(2), n3=v(3), n4=v(4);

        g.addVertex(n1); g.addVertex(n2); g.addVertex(n3); g.addVertex(n4);
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
        Vertex n1=v(1), n2=v(2), n3=v(3), n4=v(4);

        g.addVertex(n1); g.addVertex(n2); g.addVertex(n3); g.addVertex(n4);
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
        Vertex a=v(1), b=v(2), c=v(3), x=v(99);
        g.addVertex(a); g.addVertex(b); g.addVertex(c);
        g.addEdge(new Edge<>(a,b, 1.0));
        // c is isolated, x not in graph

        assertEquals(List.of(a), g.minimumCostPath(a, a, PathCostType.SUM_EDGES)); // source==sink
        assertTrue(g.minimumCostPath(a, c, PathCostType.SUM_EDGES).isEmpty());     // unreachable

        assertThrows(IllegalArgumentException.class, () -> g.minimumCostPath(x, b, PathCostType.SUM_EDGES));
        assertThrows(IllegalArgumentException.class, () -> g.minimumCostPath(a, x, PathCostType.MAX_EDGE));
    }

    //for the matrix version
    @Test
    void dijkstra_sum_and_bottleneck_on_matrix() {
        Vertex a=v(1), b=v(2), c=v(3), d=v(4), x=v(99);

        var g = new AdjacencyMatrixGraph<>(
            Set.of(a,b,c,d,x),
            Set.of(
                new Edge<>(a,b,2.0),
                new Edge<>(b,c,10.0),
                new Edge<>(a,c,5.0),
                new Edge<>(c,d,1.0),
                new Edge<>(b,d,100.0)
            )
        );

        // sum: a->c->d (5+1=6) beats a->b->d (2+100)
        assertEquals(List.of(a,c,d), g.minimumCostPath(a, d, PathCostType.SUM_EDGES));
        assertEquals(6.0, g.pathCost(List.of(a,c,d), PathCostType.SUM_EDGES), 1e-9);

        // bottleneck: a->c->d has max edge 5; a->b->d has 100 -> pick a-c-d
        assertEquals(List.of(a,c,d), g.minimumCostPath(a, d, PathCostType.MAX_EDGE));
        assertEquals(5.0, g.pathCost(List.of(a,c,d), PathCostType.MAX_EDGE), 1e-9);

        // unreachable from A to isolated? here x is isolated: empty
        assertTrue(g.minimumCostPath(a, x, PathCostType.SUM_EDGES).isEmpty());
    }

    @Test
    void pathCost_matrix_valid_and_invalid() {
        Vertex a=v(1), b=v(2), c=v(3);

        var g = new AdjacencyMatrixGraph<>(
            Set.of(a,b,c),
            Set.of(new Edge<>(a,b,2.0), new Edge<>(b,c, 0.0))
        );

        assertEquals(2.0, g.pathCost(List.of(a,b), PathCostType.SUM_EDGES), 1e-9);
        assertEquals(2.0, g.pathCost(List.of(a,b), PathCostType.MAX_EDGE), 1e-9);
        assertEquals(2.0, g.pathCost(List.of(a,b,c), PathCostType.MAX_EDGE), 1e-9); // max{2,0}=2

        assertThrows(IllegalArgumentException.class, () -> g.pathCost(List.of(a), PathCostType.SUM_EDGES));
        assertThrows(IllegalArgumentException.class, () -> g.pathCost(List.of(a, v(999)), PathCostType.SUM_EDGES));
    }
}
