package cpen221.graphs.applications.textsimilarity;

import cpen221.graphs.graph.AdjacencyListGraph;
import cpen221.graphs.graph.Algorithms;
import cpen221.graphs.graph.Edge;
import cpen221.graphs.graph.Graph;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for computing document similarity and grouping documents
 * using graph partitioning.
 *
 * <p>This class has no mutable state; all methods are static.</p>
 */
public class Similarity {

    private Similarity() {
        // not instantiable
    }

    private static final double LOG_BASE_2 = Math.log(2.0);
    private static final double HALF_WEIGHT = 0.5;
    private static final double JSD_SCALING_FACTOR = 1000.0;

    /**
     * Compute the base-2 logarithm.
     */
    private static double logBase2(double value) {
        return Math.log(value) / LOG_BASE_2;
    }

    /**
     * Package-private helper that computes the <em>raw</em> Jensen–Shannon divergence
     * between two documents (no rounding or scaling).
     *
     * @param firstDocument  non-null
     * @param secondDocument non-null
     * @return JSD(firstDocument || secondDocument) in [0, 1]
     */
    static double rawJensenShannonDivergence(Document firstDocument, Document secondDocument) {
        Objects.requireNonNull(firstDocument, "firstDocument");
        Objects.requireNonNull(secondDocument, "secondDocument");

        Map<String, Integer> frequencyMapFirst = firstDocument.getCountMap();
        Map<String, Integer> frequencyMapSecond = secondDocument.getCountMap();

        int totalWordCountFirst = firstDocument.getTotalWords();
        int totalWordCountSecond = secondDocument.getTotalWords();

        if (totalWordCountFirst == 0 && totalWordCountSecond == 0) {
            // both empty → identical distributions
            return 0.0;
        }
        if (totalWordCountFirst == 0 || totalWordCountSecond == 0) {
            // one empty, one non-empty → maximally different for our purposes
            return 1.0;
        }

        // Union of all words that appear in at least one of the documents.
        Set<String> jointVocabulary = new HashSet<>();
        jointVocabulary.addAll(frequencyMapFirst.keySet());
        jointVocabulary.addAll(frequencyMapSecond.keySet());

        double divergenceAccumulator = 0.0;

        for (String word : jointVocabulary) {
            double probabilityFirst =
                frequencyMapFirst.getOrDefault(word, 0) / (double) totalWordCountFirst;
            double probabilitySecond =
                frequencyMapSecond.getOrDefault(word, 0) / (double) totalWordCountSecond;
            double mixtureProbability = HALF_WEIGHT * (probabilityFirst + probabilitySecond);

            // By definition, term is 0 when probability is 0.
            if (probabilityFirst > 0.0) {
                divergenceAccumulator += HALF_WEIGHT * probabilityFirst
                    * logBase2(probabilityFirst / mixtureProbability);
            }
            if (probabilitySecond > 0.0) {
                divergenceAccumulator += HALF_WEIGHT * probabilitySecond
                    * logBase2(probabilitySecond / mixtureProbability);
            }
        }

        return divergenceAccumulator;
    }

    /**
     * Compute the Jensen–Shannon divergence between two documents.
     *
     * <p>The raw JSD is a real number in [0, 1]. This method:
     * <ol>
     *   <li>Computes the raw JSD between the two term-frequency distributions.</li>
     *   <li>Scales it by 1000.</li>
     *   <li>Rounds to the nearest integer using {@link Math#round(double)}.</li>
     * </ol>
     *
     * <p>The returned value is that integer expressed as a {@code double}, so it is
     * effectively an integer-valued number between 0.0 and 1000.0 inclusive:
     * <ul>
     *   <li>0.0 means "identical distributions".</li>
     *   <li>1000.0 means "maximally different distributions".</li>
     * </ul>
     *
     * @param firstDocument  non-null document
     * @param secondDocument non-null document
     * @return scaled and rounded JSD in [0.0, 1000.0]
     * @throws NullPointerException if {@code d1} or {@code d2} is null
     */
    public static double getJensenShannonDivergence(Document firstDocument,
                                                    Document secondDocument) {
        double divergence = rawJensenShannonDivergence(firstDocument, secondDocument);
        return Math.round(JSD_SCALING_FACTOR * divergence);
    }

    /**
     * Partition a set of documents into {@code numberOfGroups} groups, such that
     * documents in the same group are (heuristically) more similar to each other.
     *
     * <p>This method:
     * <ul>
     *   <li>Builds a complete weighted graph on the documents where the edge
     *       weights are the scaled JSD values.</li>
     *   <li>Uses graph partitioning to cut edges until the desired number of
     *       connected components (groups) is reached.</li>
     * </ul>
     *
     * <p>Constraints:
     * <ul>
     *   <li>{@code numberOfGroups} must be &gt; 0.</li>
     *   <li>{@code numberOfGroups} must be ≤ {@code documentSet.size()}.</li>
     *   <li>If {@code documentSet} is empty, then {@code numberOfGroups} must be
     *       either 0 or 1. In both cases this method returns an empty set of groups.</li>
     * </ul>
     *
     * <p>The returned value is a set of groups (each group is a {@code Set<Document>})
     * whose disjoint union equals {@code documentSet}.</p>
     *
     * @param documentSet    set of documents to cluster (maybe empty but not null)
     * @param numberOfGroups desired number of groups
     * @return a set of document groups that forms a partition of {@code documentSet}
     * @throws NullPointerException     if {@code documentSet} is null
     * @throws IllegalArgumentException if {@code numberOfGroups} violates the constraints
     */
    public static Set<Set<Document>> groupSimilarDocuments(Set<Document> documentSet,
                                                           int numberOfGroups) {
        Objects.requireNonNull(documentSet, "documentSet");

        int documentCount = documentSet.size();

        if (documentCount == 0) {
            if (numberOfGroups != 0 && numberOfGroups != 1) {
                throw new IllegalArgumentException(
                    "Cannot form non-trivial groups from an empty set");
            }
            return Collections.emptySet();
        }

        if (numberOfGroups < 1 || numberOfGroups > documentCount) {
            throw new IllegalArgumentException(
                "numberOfGroups must be between 1 and |documentSet|");
        }

        // Build a complete undirected graph over the documents.
        AdjacencyListGraph<DocumentVertex, Edge<DocumentVertex>> fullSimilarityGraph =
            new AdjacencyListGraph<>();

        Map<Document, DocumentVertex> documentToVertexMap = new HashMap<>();
        int nextVertexId = 0;
        for (Document document : documentSet) {
            DocumentVertex vertex = new DocumentVertex(nextVertexId++, document);
            fullSimilarityGraph.addVertex(vertex);
            documentToVertexMap.put(document, vertex);
        }

        // Add weighted edges between every unordered pair of vertices.
        List<Document> documentList = new ArrayList<>(documentSet);
        for (int i = 0; i < documentList.size(); i++) {
            for (int j = i + 1; j < documentList.size(); j++) {
                Document firstDocument = documentList.get(i);
                Document secondDocument = documentList.get(j);
                double edgeLength =
                    getJensenShannonDivergence(firstDocument, secondDocument);

                DocumentVertex firstVertex = documentToVertexMap.get(firstDocument);
                DocumentVertex secondVertex = documentToVertexMap.get(secondDocument);

                fullSimilarityGraph.addEdge(
                    new Edge<>(firstVertex, secondVertex, edgeLength)
                );
            }
        }

        Graph<DocumentVertex, Edge<DocumentVertex>> partitionedGraph =
            Algorithms.partitionGraph(fullSimilarityGraph, numberOfGroups);

        // Convert connected components back into sets of documents.
        Set<Set<Document>> groupedDocuments = new HashSet<>();

        for (Set<DocumentVertex> vertexComponent : partitionedGraph.getComponents()) {
            if (vertexComponent.isEmpty()) {
                continue;
            }
            Set<Document> documentGroup = vertexComponent.stream().
                map(DocumentVertex::getDocument).
                collect(Collectors.toSet());
            groupedDocuments.add(documentGroup);
        }

        return groupedDocuments;
    }
}
