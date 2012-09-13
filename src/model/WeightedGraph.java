package model;

import java.util.Iterator;
import java.util.List;

import ui.Drawable;
import algorithms.DijkstraSSSP;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2011-09-10
 */
public class WeightedGraph<V extends Vertex, E extends WeightedEdgeInterface>
		extends Graph<V, E> implements Drawable {

	@Override
	public E insertEdge(V v, V u, E e) {

		// Check that the edge doesn't exist, and that the vertices do exist.
		if (edges.contains(e)) {
			return e;
		}

		E edge = super.insertEdge(v, u, e);

		// Weight is currently the Euclidean distance between the vertices which
		// are connected by the edge.
		double xLengthSquared = Math.pow(Math.abs(u.getX() - v.getX()), 2);
		double yLengthSquared = Math.pow(Math.abs(u.getY() - v.getY()), 2);
		double length = Math.sqrt(xLengthSquared + yLengthSquared);

		edge.setWeight((float) length);

		return edge;
	}

	/**
	 * Get the diameter of the graph, using Euclidean distance.
	 * 
	 * @return the diameter of the graph.
	 */
	public float getDiameter() {

		// To find the diameter of a graph, first find the shortest path between
		// each pair of vertices. The greatest length of any of these paths is
		// the diameter of the graph.
		float longestMinPath = 0f;

		Iterator<V> outer = vertices().iterator();

		while (outer.hasNext()) {
			Iterator<V> inner = vertices().iterator();

			V u = outer.next();

			// With Dijkstra's we can find the shortest path from any vertex
			// back to the source vertex. So we can just find the dijkstra's
			// algorithm solution for each vertex, then find the max path
			// from any other vertex.
			DijkstraSSSP<V, E> sssp = new DijkstraSSSP<V, E>(this, u);

			while (inner.hasNext()) {

				V v = inner.next();
				sssp.generatePath(u, v);

				if (sssp.getPathLength() > longestMinPath) {
					longestMinPath = sssp.getPathLength();
				}
			}
		}

		return longestMinPath;
	}

	/**
	 * Get the diameter of the graph, using the number of hops.
	 * 
	 * @return the diameter of the graph.
	 */
	public int getDiameterHops() {

		// Since we're using Euclidean distance (straight line links, no
		// negative paths also) the shortest path by hops is the same as the
		// shortest path by distance, we're just using a different metric; edges
		// traversed instead of edge length traversed. For each pair of vertices
		// get the path. Count the number of edges, and keep record of the
		// longest path.
		int longestMinPath = 0;

		Iterator<V> outer = vertices().iterator();

		while (outer.hasNext()) {
			Iterator<V> inner = vertices().iterator();

			V u = outer.next();

			// With Dijkstra's we can find the shortest path from any vertex
			// back to the source vertex. So we can just find the dijkstra's
			// algorithm solution for each vertex, then find the max path
			// from any other vertex.
			DijkstraSSSP<V, E> sssp = new DijkstraSSSP<V, E>(this, u);

			while (inner.hasNext()) {

				V v = inner.next();
				sssp.generatePath(u, v);

				if (sssp.getPathEdges().size() > longestMinPath) {
					longestMinPath = sssp.getPathEdges().size();
				}
			}
		}

		return longestMinPath;
	}

	/**
	 * Find the shortest path length for the graph between two vertices.
	 * 
	 * @param u
	 *            a vertex.
	 * @param v
	 *            a vertex.
	 * 
	 * @return the length of the shortest path between the two vertices.
	 */
	public float getShortestPathLength(V u, V v) {

		DijkstraSSSP<V, E> sssp = new DijkstraSSSP<V, E>(this, u);
		sssp.generatePath(u, v);

		return sssp.getPathLength();
	}

	public float getShortestPathLength(String u, String v) {

		Iterator<V> vertexIter = vertices().iterator();
		V from = null;
		V to = null;

		while (vertexIter.hasNext()) {
			V vertex = vertexIter.next();

			if (vertex.getName().equals(u)) {
				from = vertex;
			}

			if (vertex.getName().equals(v)) {
				to = vertex;
			}
		}

		if (from != null && to != null) {
			return getShortestPathLength(from, to);
		}

		return 0f;
	}

	/**
	 * Find the shortest path length in hops for the graph between two vertices.
	 * 
	 * @param u
	 *            a vertex.
	 * @param v
	 *            a vertex.
	 * 
	 * @return the length in hops of the shortest path between the two vertices.
	 */
	public int getShortestPathLengthHops(V u, V v) {

		DijkstraSSSP<V, E> sssp = new DijkstraSSSP<V, E>(this, u);
		sssp.generatePath(u, v);

		return sssp.getPathEdges().size();
	}

	public int getShortestPathLengthHops(String u, String v) {

		Iterator<V> vertexIter = vertices().iterator();
		V from = null;
		V to = null;

		while (vertexIter.hasNext()) {
			V vertex = vertexIter.next();

			if (vertex.getName().equals(u)) {
				from = vertex;
			}

			if (vertex.getName().equals(v)) {
				to = vertex;
			}
		}

		if (from != null && to != null) {
			return getShortestPathLengthHops(from, to);
		}

		return 0;
	}

	/**
	 * Find the shortest path between two vertices in the graph.
	 * 
	 * @param u
	 *            a vertex.
	 * @param v
	 *            a vertex.
	 * 
	 * @return the shortest path between the two vertices.
	 */
	public List<V> getShortestPath(V u, V v) {

		DijkstraSSSP<V, E> sssp = new DijkstraSSSP<V, E>(this, u);
		sssp.generatePath(u, v);

		return sssp.getPathVerts();
	}

	public List<V> getShortestPath(String u, String v) {

		Iterator<V> vertexIter = vertices().iterator();
		V from = null;
		V to = null;

		while (vertexIter.hasNext()) {
			V vertex = vertexIter.next();

			if (vertex.getName().equals(u)) {
				from = vertex;
			}

			if (vertex.getName().equals(v)) {
				to = vertex;
			}
		}

		if (from != null && to != null) {
			return getShortestPath(from, to);
		}

		return null;
	}

	/**
	 * Find the average shortest path for the graph.
	 * 
	 * @return the average shortest path for the graph.
	 */
	public float getAverageShortestPathLength() {

		// Similar to finding the diameter we iterate through every pair of
		// vertices and get their shortest path length. Then we add the length
		// to the total. After going through all pairs of vertices we divide to
		// find the average.
		float averageMinPath = 0f;
		int count = 0;

		Iterator<V> outer = vertices().iterator();

		while (outer.hasNext()) {
			Iterator<V> inner = vertices().iterator();

			V u = outer.next();

			// With Dijkstra's we can find the shortest path from any vertex
			// back to the source vertex. So we can just find the dijkstra's
			// algorithm solution for each vertex, then find the max path
			// from any other vertex.
			DijkstraSSSP<V, E> sssp = new DijkstraSSSP<V, E>(this, u);

			while (inner.hasNext()) {

				V v = inner.next();

				if (v.equals(u)) {
					continue;
				}

				sssp.generatePath(u, v);

				averageMinPath += sssp.getPathLength();
				count++;
			}
		}

		averageMinPath /= count;

		return averageMinPath;
	}

	/**
	 * Find the average shortest path for the graph using hops.
	 * 
	 * @return the average shortest path for the graph using hops.
	 */
	public float getAverageShortestPathLengthHops() {

		float averageMinPath = 0f;
		float count = 0;

		Iterator<V> outer = vertices().iterator();
		while (outer.hasNext()) {
			Iterator<V> inner = vertices().iterator();

			V u = outer.next();

			// With Dijkstra's we can find the shortest path from any vertex
			// back to the source vertex. So we can just find the dijkstra's
			// algorithm solution for each vertex, then find the max path
			// from any other vertex.
			DijkstraSSSP<V, E> sssp = new DijkstraSSSP<V, E>(this, u);

			while (inner.hasNext()) {

				V v = inner.next();

				if (v.equals(u)) {
					continue;
				}

				sssp.generatePath(u, v);

				averageMinPath += sssp.getPathEdges().size();
				count++;
			}
		}

		averageMinPath /= count;

		return averageMinPath;
	}

}
