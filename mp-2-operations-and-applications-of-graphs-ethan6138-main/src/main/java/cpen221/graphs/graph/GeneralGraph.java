package cpen221.graphs.graph;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Collections;


public abstract class GeneralGraph<V extends Vertex, E extends Edge<V>> implements Graph<V, E> {

    /*
     * AF/RI:
     *   - This class does not impose a concrete representation for graphs; concrete
     *     subclasses (e.g., AdjacencyListGraph, AdjacencyMatrixGraph) define and
     *     maintain their own representation invariants and abstraction functions.
     */

    /**
     * Default edge membership check by delegating the (v1, v2) overload.
     *
     * @param e the edge to check; may be {@code null}
     * @return {@code false} for {@code null}; otherwise the result of {@code hasEdge(e.v1(), e.v2())}
     */
    @Override
    public boolean hasEdge(E e) {
        if (e == null) {
            return false;
        }
        return hasEdge(e.v1(), e.v2());
    }

    /* === Task 2: default stubs (throwing) === */

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException until overridden by a subclass
     */
    @Override
    public double edgeLengthSum() {
        throw new UnsupportedOperationException("edgeLengthSum: Task 2 not implemented");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException until overridden by a subclass
     */
    @Override
    public E getEdge(V v1, V v2) {
        throw new UnsupportedOperationException("getEdge(V,V): Task 2 not implemented");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException until overridden by a subclass
     */
    @Override
    public Set<V> getVertices() {
        throw new UnsupportedOperationException("getVertices: Task 2 not implemented");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException until overridden by a subclass
     */
    @Override
    public Set<E> getEdges() {
        throw new UnsupportedOperationException("getEdges(): Task 2 not implemented");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException until overridden by a subclass
     */
    @Override
    public Set<E> getEdges(V v) {
        throw new UnsupportedOperationException("getEdges(V): Task 2 not implemented");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException until overridden by a subclass
     */
    @Override
    public Map<V, E> getNeighbours(V v) {
        throw new UnsupportedOperationException("getNeighbours(V): Task 2 not implemented");
    }

    /* === Task 3: default stubs (throwing) === */

    /**
     * {@inheritDoc}
     */
    @Override
    public double pathCost(List<V> path, PathCostType costType) {
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(costType, "costType must not be null");

        if (path.size() < 2) {
            throw new IllegalArgumentException("path must be at least 2 vertices");
        }

        double sum = 0.0;
        double bottleNeck = 0.0;

        for (int i = 0; i < path.size() - 1; i++) {
            V a = path.get(i);
            V b = path.get(i + 1);
            E e = getEdge(a, b);

            if (e == null) {
                throw new IllegalArgumentException("non-adjacent pair in path");
            }

            double length = e.length();
            sum += length;
            if (length > bottleNeck) {
                bottleNeck = length;
            }
        }

        switch (costType) {
            case SUM_EDGES -> {
                return sum;
            }
            case MAX_EDGE -> {
                return bottleNeck;
            }
            default -> throw new IllegalArgumentException("unsupported costType");
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<V> minimumCostPath(V source, V sink, PathCostType costType) {
        Objects.requireNonNull(costType, "Null cost type");

        if (!hasVertex(source) || !hasVertex(sink)) {
            throw new IllegalArgumentException("source or sink not found");
        }
        if (source.equals(sink)) {
            return List.of(source);
        }

        return switch (costType) {
            case SUM_EDGES -> dijkstraSum(source, sink);
            case MAX_EDGE -> dijkstraMax(source, sink);
        };
    }

    //helpers, pretty self-explanatory

    //nested class! could use lambda expression too
    class Q {
        final V v;
        final double key;

        Q(V v, double key) {
            this.v = v;
            this.key = key;
        }
    }

    //im realizing now that these following methods could be compressed into
    //one, with a switch case to determine what we use. No time to implement.
    private List<V> dijkstraSum(V source, V sink) {
        Map<V, Double> distance = new HashMap<>();
        Map<V, V> previous = new HashMap<>();

        PriorityQueue<Q> pq = new PriorityQueue<>(Comparator.comparingDouble(q -> q.key));

        for (V v : getVertices()) {
            distance.put(v, Double.POSITIVE_INFINITY);
        }
        distance.put(source, 0.0);
        pq.add(new Q(source, 0.0));

        while (!pq.isEmpty()) {
            Q top = pq.poll();
            V u = top.v;
            double key = top.key;

            if (key > distance.get(u)) {
                continue;
            }
            if (u.equals(sink)) {
                break;
            }

            for (var e : getNeighbours(u).entrySet()) {
                V v = e.getKey();
                double w = e.getValue().length();
                double cand = distance.get(u) + w;

                if (cand < distance.get(v)) {
                    distance.put(v, cand);
                    previous.put(v, u);
                    pq.add(new Q(v, cand));
                }
            }
        }
        return reconstruct(previous, source, sink);
    }

    private List<V> dijkstraMax(V source, V sink) {
        Map<V, Double> cost = new HashMap<>();
        Map<V, V> prev = new HashMap<>();
        PriorityQueue<Q> pq = new PriorityQueue<>(Comparator.comparingDouble(q -> q.key));

        for (V v : getVertices()) {
            cost.put(v, Double.POSITIVE_INFINITY);
        }
        cost.put(source, 0.0);
        pq.add(new Q(source, 0.0));

        while (!pq.isEmpty()) {
            Q top = pq.poll();
            V u = top.v;
            double key = top.key;

            if (key > cost.get(u)) {
                continue;
            }
            if (u.equals(sink)) {
                break;
            }

            for (var e : getNeighbours(u).entrySet()) {
                V v = e.getKey();
                double w = e.getValue().length();
                double cand = Math.max(cost.get(u), w);
                if (cand < cost.get(v)) {
                    cost.put(v, cand);
                    prev.put(v, u);
                    pq.add(new Q(v, cand));
                }
            }
        }
        return reconstruct(prev, source, sink);
    }

    private List<V> reconstruct(Map<V, V> prev, V start, V end) {
        if (!start.equals(end) && !prev.containsKey(end)) {
            return List.of(); // unreachable
        }
        LinkedList<V> path = new LinkedList<>();
        V curl = end;
        path.addFirst(curl);
        while (!curl.equals(start)) {
            curl = prev.get(curl);
            if (curl == null) {
                return List.of(); // safety
            }
            path.addFirst(curl);
        }
        return path;
    }

    /* === Task 4: default stubs (throwing) === */

    /**
     * {@inheritDoc}
     *
     * @implNote This implementation processes vertices in ascending {@code id()} order.
     * If multiple components tie for largest, it selects the component whose minimum vertex id is smallest.
     * For {@code getCenter}, ties among vertices are broken by choosing the vertex with the smallest {@code id()}.
     */
    @Override
    public double getDiameter(PathCostType costType) {
        Objects.requireNonNull(costType, "costtype is null");
        Set<V> largestComponent = largestComponent();

        if (largestComponent.isEmpty()) {
            return 0.0;
        }

        double best = 0.0;
        for (V vertex : largestComponent) {
            double eccentricity = eccentricity(vertex, costType);
            if (eccentricity > best) {
                best = eccentricity;
            }
        }

        return best;
    }

    /**
     * {@inheritDoc}
     *
     * @implNote See tie-breaking note in {@link #getDiameter(PathCostType)}.
     */
    @Override
    public V getCenter(PathCostType costType) {
        Objects.requireNonNull(costType, "costtype is null");
        Set<V> largestComponent = largestComponent();
        if (largestComponent.isEmpty()) {
            throw new IllegalStateException("empty graph has no center");
        }

        List<V> order = new ArrayList<>(largestComponent);

        order.sort(Comparator.comparingInt(Vertex::id));

        V bestVertex = null;
        double bestEcc = Double.POSITIVE_INFINITY;
        for (V vertex : order) {
            double eccentricity = eccentricity(vertex, costType);
            if (eccentricity < bestEcc) {
                bestEcc = eccentricity;
                bestVertex = vertex;
            }
        }

        return bestVertex;
    }

    //task 4 helpers!

    private Set<V> largestComponent() {
        Set<V> visited = new HashSet<>();
        Set<V> best = Collections.emptySet();

        List<V> order = new ArrayList<>(getVertices());
        order.sort(Comparator.comparingInt(Vertex::id));

        for (V start : order) {
            if (visited.contains(start)) {
                continue;
            }

            Set<V> component = new HashSet<>();
            ArrayDeque<V> stack = new ArrayDeque<>();
            visited.add(start);
            component.add(start);
            stack.add(start);

            while (!stack.isEmpty()) {
                V u = stack.removeFirst();

                for (V current : getNeighbours(u).keySet()) {
                    if (!visited.contains(current)) {
                        visited.add(current);
                        component.add(current);
                        stack.addLast(current);
                    }
                }
            }

            if (component.size() > best.size()) {
                best = component;
            }
        }

        return best;
    }

    //dispatches based on the path type provided
    private Map<V, Double> distances(V source, PathCostType costType) {
        return switch (costType) {
            case SUM_EDGES -> distsSum(source);
            case MAX_EDGE -> distsBottleNeck(source);
        };
    }

    private Map<V, Double> distsSum(V source) {
        Map<V, Double> distance = new HashMap<>();
        PriorityQueue<Q> que = new PriorityQueue<>(Comparator.comparingDouble(q -> q.key));

        distance.put(source, 0.0);
        que.add(new Q(source, 0.0));

        while (!que.isEmpty()) {
            Q current = que.poll();
            V u = current.v;
            double key = current.key;

            if (key != distance.get(u)) {
                continue;
            }

            for (var nbr : getNeighbours(u).entrySet()) {
                V v = nbr.getKey();
                double w = nbr.getValue().length();
                double nd = key + w;
                double old = distance.getOrDefault(v, Double.POSITIVE_INFINITY);
                if (nd < old) {
                    distance.put(v, nd);
                    que.add(new Q(v, nd));
                }
            }
        }

        return distance;
    }

    private Map<V, Double> distsBottleNeck(V source) {
        Map<V, Double> cost = new HashMap<>();
        PriorityQueue<Q> que = new PriorityQueue<>(Comparator.comparingDouble(q -> q.key));

        cost.put(source, 0.0);
        que.add(new Q(source, 0.0));

        while (!que.isEmpty()) {
            Q current = que.poll();
            V u = current.v;
            double key = current.key;

            if (key != cost.get(u)) {
                continue;
            }

            for (var nbr : getNeighbours(u).entrySet()) {
                V v = nbr.getKey();
                double w = nbr.getValue().length();
                double nb = Math.max(key, w);
                double old = cost.getOrDefault(v, Double.POSITIVE_INFINITY);
                if (nb < old) {
                    cost.put(v, nb);
                    que.add(new Q(v, nb));
                }
            }
        }

        return cost;
    }

    private double eccentricity(V vertex, PathCostType costType) {
        Map<V, Double> distance = distances(vertex, costType);
        double ecc = 0.0;

        for (double doobs : distance.values()) {
            if (doobs > ecc) {
                ecc = doobs;
            }
        }

        return ecc;
    }





    /* === Task 5: default stubs (throwing) === */

    /**
     * {@inheritDoc}
     *
     * <p>This default implementation treats the graph as undirected and computes
     * its connected components using a breadth-first search over the neighbour
     * relation given by {@link #getNeighbours(Vertex)}.</p>
     */
    @Override
    public Set<Set<V>> getComponents() {
        Set<Set<V>> components = new HashSet<>();
        Set<V> visited = new HashSet<>();

        for (V start : getVertices()) {
            if (visited.contains(start)) {
                continue;
            }

            Set<V> component = new HashSet<>();
            ArrayDeque<V> queue = new ArrayDeque<>();

            visited.add(start);
            queue.add(start);

            while (!queue.isEmpty()) {
                V u = queue.removeFirst();
                component.add(u);

                for (V nbr : getNeighbours(u).keySet()) {
                    if (!visited.contains(nbr)) {
                        visited.add(nbr);
                        queue.addLast(nbr);
                    }
                }
            }

            components.add(component);
        }

        return components;
    }
}

