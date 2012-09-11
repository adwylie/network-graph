package model;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import ui.Drawable;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2011-09-10
 */
public class Graph<V extends VertexInterface, E extends EdgeInterface>
		implements GraphInterface<V, E>, Drawable {

	// Vertex/Edge maps to each other.
	protected HashMap<E, Set<V>> edgesToVertices = new HashMap<E, Set<V>>();
	protected HashMap<V, Set<E>> verticesToEdges = new HashMap<V, Set<E>>();

	// General container for Vertices/Edges.
	protected Set<V> vertices = new HashSet<V>();
	protected Set<E> edges = new HashSet<E>();

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

		if (vertex == null) {
			return null;
		}

		return verticesToEdges.get(vertex);
	}

	@Override
	public Set<V> endVertices(E edge) {

		if (edge == null) {
			return null;
		}

		return edgesToVertices.get(edge);
	}

	@Override
	public V opposite(V vertex, E edge) {

		// Check that both the edge and vertex are in our graph.
		if (vertex == null || edge == null || !vertices.contains(vertex)
				|| !edges.contains(edge)) {
			return null;
		}

		// Get the vertices for the edge.
		Set<V> vertices = edgesToVertices.get(edge);

		if (vertices.contains(vertex)) {
			// We know this was a valid search. Return the opposite vertex.
			Iterator<V> iter = vertices.iterator();
			while (iter.hasNext()) {
				V v = iter.next();
				if (!v.equals(vertex)) {
					return v;
				}
			}
		}

		return null;
	}

	@Override
	public boolean areAdjacent(V v, V u) {

		// Check that both vertices are in our graph.
		if (v == null || u == null || !vertices.contains(v)
				|| !vertices.contains(u)) {
			return false;
		}

		// Two vertices are adjacent if they share a common edge.
		Set<E> vEdges = verticesToEdges.get(v);
		Set<E> uEdges = verticesToEdges.get(u);

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

		if (vertex == null) {
			return null;
		}

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

		if (edge == null) {
			return null;
		}

		// Check that the edge doesn't exist, and that the vertices do exist.
		if (edges.contains(edge)) {
			return edge;
		}

		if (v == null || u == null || !vertices.contains(v)
				|| !vertices.contains(u)) {
			// Error - inserting edge between non-existent vertices.
			return null;
		}

		// Housekeeping for generic hashset.
		edges.add(edge);

		// Add to the edges -> vertices dictionary.
		LinkedHashSet<V> vertices = new LinkedHashSet<V>();
		vertices.add(v);
		vertices.add(u);

		edgesToVertices.put(edge, vertices);

		// Add to the vertices -> edges dictionary.
		verticesToEdges.get(v).add(edge);
		verticesToEdges.get(u).add(edge);

		return edge;
	}

	@Override
	public V removeVertex(V vertex) {

		// Housekeeping for generic hashset.
		if (vertex == null || !vertices.remove(vertex)) {
			// Error - removal of non-existent vertex.
			return null;
		}

		// Get all the edges which attach to the vertex and
		// remove them. Then we remove the vertex itself.
		Set<E> vertexEdges = verticesToEdges.get(vertex);
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
		if (edge == null || !edges.remove(edge)) {
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

	@Override
	public void paint(Graphics g) {

		Iterator<V> verticesIterator = vertices().iterator();

		// Draw the vertices of the graph.
		while (verticesIterator.hasNext()) {

			V vertex = verticesIterator.next();

			if (vertex instanceof Drawable) {
				((Drawable) vertex).paint(g);
			}
		}

		// Draw the edges of the graph.
		// For each edge get its vertices, and then draw a line between the
		// vertices.
		Iterator<E> edgesIterator = edges().iterator();

		while (edgesIterator.hasNext()) {

			E edge = edgesIterator.next();
			Iterator<V> connectedVerticesIter = endVertices(edge).iterator();

			// We know each edge connects exactly two vertices.
			V v = connectedVerticesIter.next();
			V u = connectedVerticesIter.next();

			int vx = (int) v.getX();
			int vy = (int) v.getY();
			int ux = (int) u.getX();
			int uy = (int) u.getY();

			if (v instanceof Drawable && u instanceof Drawable) {
				g.setColor(Color.BLACK);
				g.drawLine(vx, vy, ux, uy);
			}
		}
	}

}
