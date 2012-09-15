package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import model.Vertex;
import model.WeightedEdgeInterface;
import model.WeightedGraph;

/**
 * Algorithm from: Introduction to Algorithms, Third Edition; Cormen et al. pg.
 * 658 http://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
 * 
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-09-15
 */
public class DijkstraSSSP<V extends Vertex, E extends WeightedEdgeInterface> {

	// Passed in parameters.
	private WeightedGraph<V, E> graph;
	private V source;

	// Fields used to create the sssp.
	private PriorityQueue<V> vertices;
	private HashMap<V, V> predecessor = new HashMap<V, V>();
	private HashMap<V, Float> distance = new HashMap<V, Float>();

	// Fields used to hold current generated path properties.
	private ArrayList<V> currentPathVerts;
	private ArrayList<E> currentPathEdges;
	private float currentPathLength;

	public DijkstraSSSP(WeightedGraph<V, E> graph, V source) {

		this.graph = graph;
		this.source = source;

		// Check the query is valid.
		if (!graph.vertices().contains(source)) {
			return;
		}

		dijkstra();
	}

	// Allow the priority queue to be keyed by vertex distance values.
	class VComparator implements Comparator<V> {

		@Override
		public int compare(V o1, V o2) {

			float result = distance.get(o1) - distance.get(o2);

			if (result > 0) {
				return 1;
			} else if (result < 0) {
				return -1;
			}

			return 0;
		}
	}

	// Initialize the distance & previous node quantities for each vertex.
	private void initializeSingleSource() {

		Iterator<V> verticesIter = graph.vertices().iterator();

		while (verticesIter.hasNext()) {

			V v = verticesIter.next();

			distance.put(v, Float.MAX_VALUE);
			predecessor.put(v, null);
		}

		distance.put(source, 0f);
	}

	// Relax an edge; w is the weight of edge u -> v.
	private void relax(V u, V v, float w) {

		if (distance.get(v) > (distance.get(u) + w)) {

			distance.put(v, distance.get(u) + w);
			predecessor.put(v, u);
		}
	}

	//
	private void dijkstra() {

		initializeSingleSource();

		// There isn't a priority queue constructor that allows us to specify
		// both a comparator and a collection. :(
		int pqInitialCapacity = graph.vertices().size();
		vertices = new PriorityQueue<V>(pqInitialCapacity, new VComparator());
		vertices.addAll(graph.vertices());

		while (!vertices.isEmpty()) {

			// Get vertex with smallest distance to the source.
			V u = vertices.poll();

			// Check if the graph is disconnected.
			// if (distance.get(u) == Float.MAX_VALUE) {
			// break;
			// }

			// For each vertex adjacent to u (the closest vertex to source)
			// relax the edge connecting them.
			Iterator<E> edgesIter = graph.incidentEdges(u).iterator();

			while (edgesIter.hasNext()) {

				E e = edgesIter.next();
				V v = graph.opposite(u, e);

				// Only use outgoing edges from u to v. An edge from u -> v will
				// show as having u in the first position of its connecting edge
				// end vertices, with v in the second position. (we already know
				// the edge has v in the second position as we used u and the
				// edge to find it)
				if (graph.endVertices(e).iterator().next().equals(u)) {
					relax(u, v, e.getWeight());
				}
			}
		}
	}

	public void generatePath(V destination) {

		currentPathVerts = new ArrayList<V>();
		currentPathEdges = new ArrayList<E>();
		currentPathLength = 0f;

		// Calculate the path.
		while (destination != null) {

			currentPathVerts.add(destination);
			destination = predecessor.get(destination);
		}

		// Path is found in reverse order. Reverse it.
		Collections.reverse(currentPathVerts);

		// From the path of vertices generate the edge path. While doing this
		// update the path length.
		for (int i = 0; i < currentPathVerts.size() - 1; i++) {

			V v = currentPathVerts.get(i);
			V u = currentPathVerts.get(i + 1);

			Iterator<E> incidentEdgesIter = graph.incidentEdges(v).iterator();

			while (incidentEdgesIter.hasNext()) {

				E e = incidentEdgesIter.next();

				Iterator<V> endVerticesIter = graph.endVertices(e).iterator();

				V from = endVerticesIter.next();
				V to = endVerticesIter.next();

				if (from.equals(v) && to.equals(u)) {
					currentPathEdges.add(e);
					currentPathLength += e.getWeight();
				}
			}
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