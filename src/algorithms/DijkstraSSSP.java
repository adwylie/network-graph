package algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import model.Vertex;
import model.WeightedEdgeInterface;
import model.WeightedGraph;

// Algorithm from: Introduction to Algorithms, Third Edition; Cormen et al.
// pg. 658
// http://en.wikipedia.org/wiki/Dijkstra%27s_algorithm

// Note:
// I'm tired. and just want to finish this assignment. this implementation is
// going to be gruesome.

public class DijkstraSSSP<V extends Vertex, E extends WeightedEdgeInterface> {

	private WeightedGraph<V, E> graph;
	private V source;

	private Hashtable<V, V> optimalPrevious = new Hashtable<V, V>();

	private ArrayList<V> currentPathVerts;
	private ArrayList<E> currentPathEdges;
	private float currentPathLength;

	public DijkstraSSSP(WeightedGraph<V, E> graph, V source) {

		this.graph = graph;
		this.source = source;

		runAlgorithm();
	}

	private void runAlgorithm() {

		// Check the query is valid.
		if (!graph.vertices().contains(source)) {
			return;
		}

		HashSet<V> vertices = new HashSet<V>(graph.vertices());
		Hashtable<V, Float> distanceVals = new Hashtable<V, Float>();

		// Initialize the distance & previous node.
		Iterator<V> verticesIter = vertices.iterator();
		while (verticesIter.hasNext()) {
			V v = verticesIter.next();
			distanceVals.put(v, Float.MAX_VALUE);
			// optimalPrevious.put(v, null);
		}

		distanceVals.put(source, 0f);

		while (!vertices.isEmpty()) {
			// Get vertex with smallest distance in distanceVals.
			V closest = null;
			Iterator<V> vertexIter = vertices.iterator();
			while (vertexIter.hasNext()) {
				V v = vertexIter.next();
				if (closest == null) {
					closest = v;
				}
				if (distanceVals.get(v) < distanceVals.get(closest)) {
					closest = v;
				}
			}

			// Check if the graph is disconnected.
			if (distanceVals.get(closest) == Float.MAX_VALUE) {
				break;
			}

			// Remove the closest vertex.
			vertices.remove(closest);

			// Get the neighbors of closest.
			HashSet<V> neighbors = new HashSet<V>();
			Iterator<E> edges = graph.incidentEdges(closest).iterator();
			while (edges.hasNext()) {
				Iterator<V> verts = graph.endVertices(edges.next()).iterator();
				while (verts.hasNext()) {
					V v = verts.next();
					if (v != closest) {
						neighbors.add(v);
					}
				}
			}

			// Relax.
			Iterator<V> neighborsIter = neighbors.iterator();
			while (neighborsIter.hasNext()) {
				V neighbor = neighborsIter.next();
				float alt = distanceVals.get(closest);

				// Get distance between closest and neighbor.
				Set<E> vEdges = graph.incidentEdges(closest);
				Set<E> uEdges = graph.incidentEdges(neighbor);

				Iterator<E> iter = vEdges.iterator();

				while (iter.hasNext()) {
					E e = iter.next();
					if (uEdges.contains(e)) {
						alt += e.getWeight();
					}
				}

				// Determine whether or not to relax.
				if (alt < distanceVals.get(neighbor)) {
					distanceVals.put(neighbor, alt);
					optimalPrevious.put(neighbor, closest);
				}
			}

		}
	}

	public void generatePath(V to) {

		currentPathVerts = new ArrayList<V>();
		currentPathEdges = new ArrayList<E>();
		currentPathLength = 0f;

		// If the path is from a vertex to itself...
		if (source == to) {
			currentPathVerts.add(source);
			return;
		}

		// Otherwise calculate the path.
		ArrayList<V> tempPath = new ArrayList<V>();

		V x = to;
		tempPath.add(x);
		do {
			// Check that the path exists; the graph needs to be connected.
			if (!optimalPrevious.containsKey(x)) {
				return;
			}
			x = optimalPrevious.get(x);
			tempPath.add(x);
		} while (x != source);

		// Path is found in reverse order. Invert it.
		for (int i = tempPath.size() - 1; i >= 0; i--) {
			currentPathVerts.add(tempPath.get(i));
		}

		// From the path of vertices generate the edge path.
		for (int i = 0; i < currentPathVerts.size() - 1; i++) {
			V v = currentPathVerts.get(i);
			V u = currentPathVerts.get(i + 1);

			Set<E> vEdges = graph.incidentEdges(v);
			Set<E> uEdges = graph.incidentEdges(u);

			Iterator<E> iter = vEdges.iterator();

			while (iter.hasNext()) {
				E e = iter.next();
				// Assuming graph adds edges named after
				// 'fromVertex + toVertex'. For example, an edge going from
				// vertex 'A' to vertex 'B' should be named 'AB'.
				// if (uEdges.contains(e) &&
				// e.getName().equals(v.getName() + u.getName())) {
				if (uEdges.contains(e)) {
					currentPathEdges.add(e);
					currentPathLength += e.getWeight();
				}
			}
		}
	}

	@Deprecated
	public void generatePath(V source, V to) {

		// Check that we have a valid query.
		if (source != this.source) {
			return;
		} else {
			generatePath(to);
		}
	}

	public List<V> getPathVerts() {
		return currentPathVerts;
	}

	public List<E> getPathEdges() {
		return currentPathEdges;
	}

	public float getPathLength() {
		return currentPathLength;
	}

}