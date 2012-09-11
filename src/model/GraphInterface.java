package model;

import java.util.Set;

public interface GraphInterface<V extends VertexInterface, E extends EdgeInterface> {

	/**
	 * Return a Set of all the vertices in the graph.
	 * 
	 * @return the vertices in the graph.
	 */
	Set<V> vertices();

	/**
	 * Return a Set of all the edges in the graph.
	 * 
	 * @return the edges in the graph.
	 */
	Set<E> edges();

	/**
	 * Return a Set of the edges incident upon a vertex.
	 * 
	 * @param vertex
	 *            a vertex in the graph.
	 * 
	 * @return a Set of edges incident to the input vertex.
	 */
	Set<E> incidentEdges(V vertex);

	/**
	 * Return a Set of the vertices which are connected by an edge.
	 * 
	 * @param edge
	 *            an edge whose vertices will be found.
	 * 
	 * @return a Set of the vertices which are connected by the input edge.
	 */
	Set<V> endVertices(E edge);

	/**
	 * Return the vertex connected to an edge, which is not the passed in
	 * vertex. If the passed in vertex and edge are not connected then the null
	 * value will be returned.
	 * 
	 * @param vertex
	 * @param edge
	 * 
	 * @return the vertex connected to the input edge, which is opposite of the
	 *         input vertex.
	 */
	V opposite(V vertex, E edge);

	/**
	 * Return whether two vertices are adjacent (connected by an edge).
	 * 
	 * @param v
	 *            a vertex.
	 * @param u
	 *            a vertex.
	 * 
	 * @return whether the two vertices are adjacent.
	 */
	boolean areAdjacent(V v, V u);

	/**
	 * Replace the element stored at vertex v with the object x.
	 * 
	 * @param vertex
	 *            the vertex to be changed.
	 * @param object
	 *            the new object to store in the vertex.
	 */
	void replace(V vertex, Object object);

	/**
	 * Replace the element stored at edge e with the object x.
	 * 
	 * @param edge
	 *            the edge to be changed.
	 * @param object
	 *            the new object to store in the vertex.
	 */
	void replace(E edge, Object object);

	/**
	 * Insert a new vertex in the graph and return it.
	 * 
	 * @param vertex
	 *            a vertex.
	 * 
	 * @return the inserted vertex.
	 */
	V insertVertex(V vertex);

	/**
	 * Insert and return a new directed edge linking from vertex v to vertex u.
	 * 
	 * @param v
	 *            a vertex.
	 * @param u
	 *            a vertex.
	 * @param e
	 *            an edge to join the two input vertices.
	 * 
	 * @return the edge which was added to the graph.
	 */
	E insertEdge(V v, V u, E e);

	/**
	 * Remove a vertex v and all its incident edges, and return the vertex v.
	 * 
	 * @param vertex
	 *            a vertex.
	 * 
	 * @return the vertex which was removed from the graph.
	 */
	V removeVertex(V vertex);

	/**
	 * Remove an edge e, and return it.
	 * 
	 * @param edge
	 *            an edge.
	 * 
	 * @return the edge which was removed from the graph.
	 */
	E removeEdge(E edge);

}