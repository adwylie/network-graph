package model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Graph<V extends VertexInterface, E extends EdgeInterface>
		implements GraphInterface<V, E> {

	// Vertex/Edge maps to each other.
	protected HashMap<E, HashSet<V>> edgesToVertices = new HashMap<E, HashSet<V>>();
	protected HashMap<V, HashSet<E>> verticesToEdges = new HashMap<V, HashSet<E>>();

	// General container for Vertices/Edges.
	protected HashSet<V> vertices = new HashSet<V>();
	protected HashSet<E> edges = new HashSet<E>();

	@Override
	public Set<V> vertices() {
		return vertices;
	}

	@Override
	public Set<E> edges() {
		return edges;
	}

	@Override
	public Set<E> incidentEdges(V vertex) {
		return verticesToEdges.get(vertex);
	}

	@Override
	public Set<V> endVertices(E edge) {
		return edgesToVertices.get(edge);
	}

	@Override
	public V opposite(V vertex, E edge) {

		// Check that both the edge and vertex are in our graph.
		if (!vertices.contains(vertex) || !edges.contains(edge)) {
			return null;
		}

		// Get the vertices for the edge.
		HashSet<V> vertices = edgesToVertices.get(edge);

		if (vertices.contains(vertex)) {
			// We know this was a valid search. Return the opposite vertex.
			Iterator<V> iter = vertices.iterator();
			while (iter.hasNext()) {
				V temp = iter.next();
				if (!temp.equals(vertex)) {
					return temp;
				}
			}
		}

		return null;
	}

	@Override
	public boolean areAdjacent(V v, V u) {

		// Check that both vertices are in our graph.
		if (!vertices.contains(v) || !vertices.contains(u)) {
			return false;
		}

		// Two vertices are adjacent if they share a common edge.
		HashSet<E> vEdges = verticesToEdges.get(v);
		HashSet<E> uEdges = verticesToEdges.get(u);

		return !Collections.disjoint(vEdges, uEdges);
	}

	@Override
	public void replace(V v, Object x) {
		// TODO Auto-generated method stub
	}

	@Override
	public void replace(E e, Object x) {
		// TODO Auto-generated method stub
	}

	@Override
	public V insertVertex(V vertex) {

		// Check the vertex doesn't already exist.
		if (vertices.contains(vertex)) {
			return vertex;
		}

		// Housekeeping for generic hashset.
		vertices.add(vertex);

		// Add into our dictionary (for completeness)
		// Don't change verticesMap since no edges map to the vertex yet.
		verticesToEdges.put(vertex, new HashSet<E>());

		return vertex;
	}

	@Override
	public E insertEdge(V v, V u, E edge) {

		// Check that the edge doesn't exist, and that the vertices do exist.
		if (edges.contains(edge)) {
			return edge;
		}

		if (!vertices.contains(v) || !vertices.contains(u)) {
			// Error - inserting edge between non-existent vertices.
			return null;
		}

		// Housekeeping for generic hashset.
		edges.add(edge);

		// Add to the edges -> vertices dictionary.
		HashSet<V> vertices = new HashSet<V>();
		vertices.add(u);
		vertices.add(v);

		edgesToVertices.put(edge, vertices);

		// Add to the vertices -> edges dictionary.
		verticesToEdges.get(v).add(edge);
		verticesToEdges.get(u).add(edge);

		// Set the edge name.
		edge.setName(v.getName() + u.getName());

		return edge;
	}

	@Override
	public V removeVertex(V vertex) {

		// Housekeeping for generic hashset.
		if (!vertices.remove(vertex)) {
			// Error - removal of non-existent vertex.
			return null;
		}

		// Get all the edges which attach to the vertex and
		// remove them. Then we remove the vertex itself.
		HashSet<E> vertexEdges = verticesToEdges.get(vertex);
		Iterator<E> vertexEdgesIter = vertexEdges.iterator();

		while (vertexEdgesIter.hasNext()) {

			removeEdge(vertexEdgesIter.next());

			// Removing an edge modifies the set being iterated over,
			// so we need to update it when it's changed.
			vertexEdgesIter = vertexEdges.iterator();
		}

		verticesToEdges.remove(vertex);

		return vertex;
	}

	@Override
	public E removeEdge(E edge) {

		// Housekeeping for generic hashset.
		if (!edges.remove(edge)) {
			// Error - removal of non-existent edge.
			return null;
		}

		// Find vertices which connect to the edge, remove the edge.
		// Look up edge to get its vertices, then look up the vertices to get
		// their edge set. Remove the edge from the vertices edge set.
		Iterator<V> edgesToVerticesIter = edgesToVertices.get(edge).iterator();

		while (edgesToVerticesIter.hasNext()) {

			V vertex = edgesToVerticesIter.next();
			verticesToEdges.get(vertex).remove(edge);
		}

		edgesToVertices.remove(edge);

		return edge;
	}

	@Override
	public String toString() {
		return "VerticesMap: " + edgesToVertices.toString() + "\nEdgesMap: "
				+ verticesToEdges.toString();
	}

}
