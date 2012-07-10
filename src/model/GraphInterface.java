package model;
import java.util.Set;

public interface GraphInterface<V extends VertexInterface, E extends EdgeInterface> {

    // Return an iterable collection of all the vertices in the graph.
    Set<V> vertices();

    // Return an iterable collection of all the edges in the graph.
    Set<E> edges();

    // Return an iterable collection of the edges incident upon vertex v.
    Set<E> incidentEdges(V vertex);

    // Return an iterable collection of the end vertices of edge e.
    Set<V> endVertices(E edge);

    // Return the end vertex of edge e which is distinct from
    // the input vertex v.
    V opposite(V vertex, E edge);

    // Test whether the vertices v and u are adjacent.
    boolean areAdjacent(V v, V u);
    
    // Replace the element stored at vertex v with x.
    // void replace(V v, V x);
    
    // Replace the element stored at edge e with x.
    // void replace(E e, E x);
    
    // Insert and return a new vertex storing element x.
    V insertVertex(V x);

    // Insert and return a new undirected edge with end vertices v and u,
    // storing element x.
    E insertEdge(V v, V u, E x);

    // Remove vertex v and all its incident edges and return the element
    // stored at v.
    V removeVertex(V vertex);

    // Remove edge e and return the element stored at e.
    E removeEdge(E edge);

}