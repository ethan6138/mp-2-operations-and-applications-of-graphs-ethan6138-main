package cpen221.graphs.graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Algorithms {
    /*
     * RI (Algorithms):
     *   - Algorithms is a pure utility class:
     *       - it has no instance fields;
     *       - it cannot be instantiated (private constructor).
     *   - Therefore it maintains no mutable representation of its own.
     *
     * AF (Algorithms):
     *   - Algorithms is simply a namespace that groups together
     *     static graph algorithms (e.g., partitionGraph). It does
     *     not itself represent an abstract data value.
     */

    // Utility class, not meant to be instantiated.

    // Utility class, not meant to be instantiated.
    private Algorithms() {
    }

    /**
     * Simple disjoint-set / union–find structure over a set of elements.
     * Supports near-constant-time find/union operations.
     * RI (DisjointSet):
     *   - parent and rankByRoot are non-null.
     *   - parent.keySet() and rankByRoot.keySet() are equal.
     *   - For every element x in parent.keySet():
     *       - x != null.
     *       - parent.get(x) != null and parent.get(x) is also in parent.keySet().
     *   - Roots are exactly the elements r such that parent.get(r).equals(r).
     *   - For every element x in rankByRoot.keySet():
     *       - rankByRoot.get(x) != null and rankByRoot.get(x) >= 0.
     *   - setCount == number of roots:
     *       setCount == |{ x in parent.keySet() : parent.get(x).equals(x) }|.
     * AF (DisjointSet):
     *   - Each root r (parent.get(r) == r) represents one equivalence class
     *     containing all elements x such that find(x) == r.
     *   - The set of these equivalence classes forms a partition of the
     *     original element set.
     *   - setCount is the number of equivalence classes (i.e., partitions).
     */
    private static final class DisjointSet<V> {
        private final Map<V, V> parent = new HashMap<>();
        private final Map<V, Integer> rankByRoot = new HashMap<>();
        private int setCount;

        DisjointSet(Set<V> elements) {
            for (V element : elements) {
                parent.put(element, element);
                rankByRoot.put(element, 0);
            }
            setCount = elements.size();
            checkRep();
        }

        V find(V element) {
            V currentParent = parent.get(element);
            if (currentParent == null) {
                throw new IllegalArgumentException("Unknown element: " + element);
            }
            if (!currentParent.equals(element)) {
                currentParent = find(currentParent);
                parent.put(element, currentParent);
            }
            return currentParent;
        }

        boolean union(V a, V b) {
            V rootA = find(a);
            V rootB = find(b);
            if (rootA.equals(rootB)) {
                return false;
            }

            int rankA = rankByRoot.get(rootA);
            int rankB = rankByRoot.get(rootB);

            if (rankA < rankB) {
                parent.put(rootA, rootB);
            } else if (rankA > rankB) {
                parent.put(rootB, rootA);
            } else {
                parent.put(rootB, rootA);
                rankByRoot.put(rootA, rankA + 1);
            }

            setCount--;
            checkRep();
            return true;
        }

        int getSetCount() {
            return setCount;
        }

        /** Check the representation invariant. */
        private void checkRep() {
            assert parent != null; //always true but still part of the rep invariant
            assert rankByRoot != null; //Also always true
            assert parent.keySet().equals(rankByRoot.keySet());

            int rootCount = 0;

            for (Map.Entry<V, V> entry : parent.entrySet()) {
                V element = entry.getKey();
                V p = entry.getValue();

                // keys and parent pointers are non-null
                assert element != null;
                assert p != null;

                // parent pointer always points inside the universe
                assert parent.containsKey(p);

                // rank defined and non-negative
                Integer rank = rankByRoot.get(element);
                assert rank != null;
                assert rank >= 0;

                if (element.equals(p)) {
                    rootCount++;
                }
            }

            // setCount matches actual number of roots
            assert rootCount == setCount;
            assert setCount >= 0;
            assert setCount <= parent.size();
        }
    }

    /**
     * Partitions the given undirected graph into {@code numberOfPartitions} components
     * that satisfy the shortest-edge and nearest-neighbour rules from the handout.
     *
     * <p>The input graph is not mutated. The returned graph has the same vertex set,
     * but only those edges whose endpoints lie in the same partition. Equivalently,
     * it is the disjoint union of the induced subgraphs for each partition.</p>
     *
     * <p>Behaviour:
     * <ul>
     *   <li>Vertices always belong to exactly one partition.</li>
     *   <li>If {@code numberOfPartitions == 1}, the returned graph is (up to
     *       representation) identical to the input graph.</li>
     *   <li>If {@code numberOfPartitions == |V|}, each vertex forms its own
     *       partition and the returned graph has no edges.</li>
     *   <li>Let {@code C} be the number of connected components in the input graph.
     *       Then {@code numberOfPartitions} must satisfy
     *       {@code C <= numberOfPartitions <= |V|}; otherwise an
     *       {@link IllegalArgumentException} is thrown.</li>
     *   <li>If the graph has no vertices, {@code numberOfPartitions} must be 0.
     *       The returned graph is empty.</li>
     * </ul>
     *
     * @param graph              the graph to partition (non-null)
     * @param numberOfPartitions desired number of partitions
     * @return a new graph representing the union of the induced subgraphs
     * @throws NullPointerException     if {@code graph} is {@code null}
     * @throws IllegalArgumentException if {@code numberOfPartitions} is not in
     *                                  the range {@code [C, |V|]}, where {@code C}
     *                                  is the number of connected components
     */
    public static <V extends Vertex> Graph<V, Edge<V>> partitionGraph(
        Graph<V, Edge<V>> graph, int numberOfPartitions) {

        Objects.requireNonNull(graph, "graph");

        Set<V> allVertices = graph.getVertices();
        int numVertices = allVertices.size();

        if (numVertices == 0) {
            // With no vertices, the only sensible request is 0 partitions.
            if (numberOfPartitions != 0) {
                throw new IllegalArgumentException("No vertices to partition");
            }
            return new AdjacencyListGraph<>();
        }

        // Minimum possible partitions = number of connected components.
        int minNumPartitions = graph.getComponents().size();

        if (numberOfPartitions < minNumPartitions || numberOfPartitions > numVertices) {
            throw new IllegalArgumentException(
                "numberOfPartitions must be between " + minNumPartitions
                    + " and " + numVertices);
        }

        // Union–find over all vertices.
        DisjointSet<V> partitionStructure = new DisjointSet<>(allVertices);

        // Collect and sort edges by non-decreasing length.
        List<Edge<V>> allEdges = new ArrayList<>(graph.getEdges());
        allEdges.sort(Comparator.comparingDouble(Edge::length));

        // Merge along real edges from shortest to longest
        // while we still have more components than requested.
        for (Edge<V> edge : allEdges) {
            if (partitionStructure.getSetCount() <= numberOfPartitions) {
                break;
            }
            partitionStructure.union(edge.v1(), edge.v2());
        }

        // Build the output graph: same vertices, but keep only edges whose
        // endpoints end up in the same partition.
        AdjacencyListGraph<V, Edge<V>> outputGraph = new AdjacencyListGraph<>();
        for (V vertex : allVertices) {
            outputGraph.addVertex(vertex);
        }

        for (Edge<V> edge : allEdges) {
            V endpoint1 = edge.v1();
            V endpoint2 = edge.v2();
            if (partitionStructure.find(endpoint1).equals(partitionStructure.find(endpoint2))) {
                outputGraph.addEdge(edge);
            }
        }

        return outputGraph;
    }

}
