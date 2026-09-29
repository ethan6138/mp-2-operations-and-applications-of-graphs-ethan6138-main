package cpen221.graphs.applications.textsimilarity;

import cpen221.graphs.graph.Vertex;

/**
 * A graph vertex that wraps a {@link Document} for use in graph-based
 * text similarity applications.
 *
 * <p>Each {@code DocumentVertex} has:</p>
 * <ul>
 *   <li>a numeric vertex id (inherited from {@link Vertex}) that is unique
 *       within a particular graph, and</li>
 *   <li>an associated {@link Document} instance.</li>
 * </ul>
 *
 * <p>Representation invariant:</p>
 * <ul>
 *   <li>{@code document} is non-null</li>
 * </ul>
 *
 * <p>Abstraction function:
 *   AF(id, document) represents a graph vertex with identifier {@code id}
 *   that “stands for” the underlying textual {@code document} in a similarity
 *   graph.</p>
 */
public class DocumentVertex extends Vertex {

    private final Document document;

    /**
     * Create a new {@code DocumentVertex} with the given numeric id and
     * associated document.
     *
     * @param vertexId unique vertex identifier for use in a graph
     * @param document non-null textual document represented by this vertex
     */
    public DocumentVertex(int vertexId, Document document) {
        super(vertexId);
        this.document = document;
    }

    /**
     * Return the {@link Document} associated with this vertex.
     *
     * @return the underlying document; never {@code null}
     */
    public Document getDocument() {
        return document;
    }

}
