package cpen221.graphs.graph;

/**
 * <p>Represents an immutable graph vertex. Each vertex has an associated id.
 * No two vertices in the same graph should have the same id.</p>
 *
 * <p>Supports basic operations such as retrieving the id as well as equality.
 * One would normally create subtypes of {@code Vertex} in an application.</p>
 *
 * @author CPEN 221 Staff
 */
public class Vertex {

    /*
     * RI:
     *   - id is immutable.
     *
     * AF:
     *   - This object represents a graph vertex whose unique identifier is id().
     */


    private final int id;

    /**
     * Create a new vertex
     *
     * @param id is a numeric identifier for the vertex
     */
    public Vertex(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Vertex other) {
            return (this.id == other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public int id() {
        return id;
    }

    /// --- any new methods, if necessary, go below this line --- ///

}
