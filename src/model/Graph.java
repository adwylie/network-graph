package model;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class Graph<V extends VertexInterface, E extends EdgeInterface>
		implements GraphInterface<V, E> {

	// V/E maps to each other.
	// verticesMap: edges -> vertices
	// edgesMap: vertices -> edges
	protected Hashtable<E, HashSet<V>> verticesMap;
	protected Hashtable<V, HashSet<E>> edgesMap;

	// General container for V/E.
	protected HashSet<V> vertices;
	protected HashSet<E> edges;

	public Graph() {
		verticesMap = new Hashtable<E, HashSet<V>>();
		edgesMap = new Hashtable<V, HashSet<E>>();

		vertices = new HashSet<V>();
		edges = new HashSet<E>();
	}

	// Return an iterable collection of all the vertices in the graph.
	@Override
	public Set<V> vertices() {
		return vertices;
	}

	// Return an iterable collection of all the edges in the graph.
	@Override
	public Set<E> edges() {
		return edges;
	}

	// Return an iterable collection of the edges incident upon vertex v.
	@Override
	public Set<E> incidentEdges(V vertex) {
		return edgesMap.get(vertex);
	}

	// Return an iterable collection of the end vertices of edge e.
	@Override
	public Set<V> endVertices(E edge) {
		return verticesMap.get(edge);
	}

	// Return the end vertex of edge e which is distinct from
	// the input vertex v.
	@Override
	public V opposite(V vertex, E edge) {
		// Check that both the edge and vertex are in our graph.
		if (!vertices.contains(vertex) || !edges.contains(edge)) {
			return null;
		}
		// Get the list of vertices for the edge,
		// and return the vertex which was not passed in.
		HashSet<V> vertices = verticesMap.get(edge);
		if (vertices.contains(vertex)) {
			// We know this was a valid search.
			// Return the opposite vertex.
			Iterator<V> iter = vertices.iterator();
			while (iter.hasNext()) {
				V temp = iter.next();
				if (temp != vertex) {
					return temp;
				}
			}
		}
		return null;
	}

	// Test whether the vertices v and u are adjacent.
	@Override
	public boolean areAdjacent(V v, V u) {
		// Check that both vertices are in our graph.
		if (!vertices.contains(v) || !vertices.contains(u)) {
			return false;
		}
		// Two vertices are adjacent if they share a common edge.
		HashSet<E> vEdges = edgesMap.get(v);
		HashSet<E> uEdges = edgesMap.get(u);

		// Search through the smaller set.
		if (vEdges.size() <= uEdges.size()) {

			Iterator<E> iter = vEdges.iterator();
			while (iter.hasNext()) {
				if (uEdges.contains(iter.next())) {
					return true;
				}
			}
		} else {

			Iterator<E> iter = uEdges.iterator();
			while (iter.hasNext()) {
				if (vEdges.contains(iter.next())) {
					return true;
				}
			}
		}

		return false;
	}

	// Insert and return a new vertex storing element x.
	@Override
	public V insertVertex(V x) {
		// Check the vertex doesn't already exist.
		if (vertices.contains(x)) {
			return x;
		}

		// Housekeeping for generic hashset.
		vertices.add(x);

		// Add into our dictionary (for completeness)
		// Don't change verticesMap since no edges map to the vertex yet.
		edgesMap.put(x, new HashSet<E>());

		return x;
	}

	// Insert and return a new undirected edge with end vertices v and u,
	// storing element x.
	@Override
	public E insertEdge(V v, V u, E x) {
		// Check that the edge doesn't already exist,
		// and that the vertices do exist.
		if (edges.contains(x)) {
			return x;
		}

		// Housekeeping for generic hashset.
		edges.add(x);

		if (!vertices.contains(v) || !vertices.contains(u)) {
			// error - inserting edge between non-existent vertices.
			return null;
		}

		// Add to our dictionary. (verticesMap)
		HashSet<V> vertices = new HashSet<V>();
		vertices.add(u);
		vertices.add(v);

		verticesMap.put(x, vertices);

		// Add to our dictionary. (edgesMap)
		HashSet<E> vEdges = edgesMap.get(v);
		vEdges.add(x);

		HashSet<E> uEdges = edgesMap.get(u);
		uEdges.add(x);

		// Set the edge name.
		x.setName(v.getName() + u.getName());

		// Return the edge.
		return x;
	}

	// Remove vertex v and all its incident edges and return the element
	// stored at v.
	@Override
	public V removeVertex(V vertex) {
		// Housekeeping for generic hashset.
		if (!vertices.remove(vertex)) {
			// error - removal of non-existent vertex.
			return null;
		}

		// Do the same thing that we do to remove an edge.
		// Takes more time however since a vertex can join all the
		// edges, whereas an edge can only join two vertices.

		// So, we get all the edges which attach to the vertex and
		// remove them. Then we remove the vertex itself.
		HashSet<E> vertexEdges = edgesMap.get(vertex);
		Iterator<E> vertexEdgesIter = vertexEdges.iterator();

		while (vertexEdgesIter.hasNext()) {
			E tempEdge = vertexEdgesIter.next();
			removeEdge(tempEdge);
			// removeEdge modifies the values iterated over,
			// (edgesMap) so we need to refresh it every loop.
			vertexEdgesIter = vertexEdges.iterator();
		}

		edgesMap.remove(vertex);

		return vertex;
	}

	// Remove edge e and return the element stored at e.
	@Override
	public E removeEdge(E edge) {
		// Housekeeping for generic hashset.
		if (!edges.remove(edge)) {
			// error - removal of non-existent edge.
			return null;
		}

		// Find vertices which connect to the edge, remove the edge.
		// Look up edge to get its vertices, then look up the
		// vertices to get their edge set. Remove the edge from the
		// vertices edge set.
		HashSet<V> edgeVertices = verticesMap.get(edge);
		Iterator<V> edgeVerticesIter = edgeVertices.iterator();

		while (edgeVerticesIter.hasNext()) {
			V tempVertex = edgeVerticesIter.next();
			edgesMap.get(tempVertex).remove(edge);

			// Do I need to also remove any vertices
			// which are now sitting alone?
			/*
			 * if (edgesMap.get(tempVertex).isEmpty()) {
			 * removeVertex(tempVertex); }
			 */

		}

		// Remove the edge itself.
		verticesMap.remove(edge);

		return edge;
	}

	public String toString() {
		return "VerticesMap: " + verticesMap.toString() + "\nEdgesMap: "
				+ edgesMap.toString();
	}

	@Override
	public void replace(V v, Object x) {
		// TODO Auto-generated method stub

	}

	@Override
	public void replace(E e, Object x) {
		// TODO Auto-generated method stub

	}

}
