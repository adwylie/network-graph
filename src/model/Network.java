package model;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import algorithms.DijkstraSSSP;
import algorithms.KruskalMST;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-09-12
 */
public abstract class Network {

	protected WeightedGraph<Node, Link> physicalNetwork = null;
	protected KruskalMST<Node, Link> physicalNetworkMst = null;

	/**
	 * The logical network will always have the same graph structure, though
	 * sensor properties may be changed by antenna orientation methods.
	 */
	protected WeightedGraph<Sensor, Link> logicalNetwork = new WeightedGraph<Sensor, Link>();

	protected double averageAngle = 0;
	protected double averageRange = 0;
	protected double totalEnergyUse = 0;

	/**
	 * Constructor for a network.
	 * 
	 * @param physicalNetwork
	 *            a weighted graph of nodes and links which represents the
	 *            physical network configuration.
	 */
	public Network(WeightedGraph<Node, Link> physicalNetwork) {

		this.physicalNetwork = physicalNetwork;
		physicalNetworkMst = new KruskalMST<Node, Link>(physicalNetwork);

		initializeLogicalNetwork();
	}

	/**
	 * Initialize the logical network from the physical network.
	 * 
	 * This will create a weighted graph of sensors with links connecting them,
	 * based on the physical network's minimum spanning tree.
	 */
	private void initializeLogicalNetwork() {

		Hashtable<Node, Sensor> nodesToSensors = new Hashtable<Node, Sensor>();

		WeightedGraph<Node, Link> mstGraph = physicalNetworkMst.getMST();

		Iterator<Node> mstNodesIter = mstGraph.vertices().iterator();
		Iterator<Link> mstLinksIter = mstGraph.edges().iterator();

		// Insert all of the vertices first. For each node create a sensor,
		// record it in the map, then insert into the network.
		while (mstNodesIter.hasNext()) {

			Node node = mstNodesIter.next();

			Sensor sensor = new Sensor(node);
			nodesToSensors.put(node, sensor);
			logicalNetwork.insertVertex(sensor);
		}

		// Insert all of the edges. For each edge we get the connected vertices,
		// and then insert the edge between them.
		while (mstLinksIter.hasNext()) {

			Link link = mstLinksIter.next();

			Iterator<Node> endNodes = mstGraph.endVertices(link).iterator();

			// Each edge connects exactly two vertices.
			Sensor u = nodesToSensors.get(endNodes.next());
			Sensor v = nodesToSensors.get(endNodes.next());

			String edgeName = u.getName() + v.getName();

			logicalNetwork.insertEdge(u, v, new Link(edgeName));
		}
	}

	/**
	 * Get the diameter of the logical network graph, using euclidean distance.
	 * 
	 * @return the diameter of the logical network graph.
	 */
	public float getDiameter() {

		// To find the diameter of a graph, first find the shortest path between
		// each pair of vertices. The greatest length of any of these paths is
		// the diameter of the graph.
		float longestMinPath = 0f;

		Iterator<Sensor> outer = logicalNetwork.vertices().iterator();

		while (outer.hasNext()) {
			Iterator<Sensor> inner = logicalNetwork.vertices().iterator();

			Sensor u = outer.next();

			// With Dijkstra's we can find the shortest path from any vertex
			// back to the source vertex. So we can just find the dijkstra's
			// algorithm solution for each vertex, then find the max path
			// from any other vertex.
			DijkstraSSSP<Sensor, Link> sssp;
			sssp = new DijkstraSSSP<Sensor, Link>(logicalNetwork, u);

			while (inner.hasNext()) {

				Sensor v = inner.next();
				sssp.generatePath(u, v);

				if (sssp.getPathLength() > longestMinPath) {
					longestMinPath = sssp.getPathLength();
				}
			}
		}

		return longestMinPath;
	}

	/**
	 * Get the diameter of the logical network graph, using the number of hops.
	 * 
	 * @return the diameter of the logical network graph.
	 */
	public int getDiameterHops() {

		// Since we're using Euclidean distance (straight line links, no
		// negative
		// paths also) the shortest path by hops is the same as the shortest
		// path
		// by distance, we're just using a different metric; edges traversed
		// instead of edge length traversed.
		// For each pair of vertices get the path. Count the number of edges,
		// and keep record of the longest path.
		int longestMinPath = 0;

		Iterator<Sensor> outer = logicalNetwork.vertices().iterator();

		while (outer.hasNext()) {
			Iterator<Sensor> inner = logicalNetwork.vertices().iterator();

			Sensor u = outer.next();

			// With Dijkstra's we can find the shortest path from any vertex
			// back to the source vertex. So we can just find the dijkstra's
			// algorithm solution for each vertex, then find the max path
			// from any other vertex.
			DijkstraSSSP<Sensor, Link> sssp;
			sssp = new DijkstraSSSP<Sensor, Link>(logicalNetwork, u);

			while (inner.hasNext()) {

				Sensor v = inner.next();
				sssp.generatePath(u, v);

				if (sssp.getPathEdges().size() > longestMinPath) {
					longestMinPath = sssp.getPathEdges().size();
				}
			}
		}

		return longestMinPath;
	}

	/**
	 * Find the shortest path length for the logical network given two sensors.
	 * 
	 * @param u
	 *            a sensor.
	 * @param v
	 *            a sensor.
	 * 
	 * @return the length of the shortest path between the two sensors.
	 */
	public float getShortestPathLength(Sensor u, Sensor v) {

		DijkstraSSSP<Sensor, Link> sssp;
		sssp = new DijkstraSSSP<Sensor, Link>(logicalNetwork, u);
		sssp.generatePath(u, v);

		return sssp.getPathLength();
	}

	public float getShortestPathLength(String u, String v) {

		Iterator<Sensor> sensorIter = logicalNetwork.vertices().iterator();
		Sensor from = null;
		Sensor to = null;

		while (sensorIter.hasNext()) {
			Sensor sensor = sensorIter.next();

			if (sensor.getName().equals(u)) {
				from = sensor;
			}

			if (sensor.getName().equals(v)) {
				to = sensor;
			}
		}

		if (from != null && to != null) {
			return getShortestPathLength(from, to);
		}

		return 0f;
	}

	/**
	 * Find the shortest path length in hops for the logical network given two
	 * sensors.
	 * 
	 * @param u
	 *            a sensor.
	 * @param v
	 *            a sensor.
	 * 
	 * @return the length in hops of the shortest path between the two sensors.
	 */
	public int getShortestPathLengthHops(Sensor u, Sensor v) {

		DijkstraSSSP<Sensor, Link> sssp;
		sssp = new DijkstraSSSP<Sensor, Link>(logicalNetwork, u);
		sssp.generatePath(u, v);

		return sssp.getPathEdges().size();
	}

	public int getShortestPathLengthHops(String u, String v) {

		Iterator<Sensor> sensorIter = logicalNetwork.vertices().iterator();
		Sensor from = null;
		Sensor to = null;

		while (sensorIter.hasNext()) {
			Sensor sensor = sensorIter.next();

			if (sensor.getName().equals(u)) {
				from = sensor;
			}

			if (sensor.getName().equals(v)) {
				to = sensor;
			}
		}

		if (from != null && to != null) {
			return getShortestPathLengthHops(from, to);
		}

		return 0;
	}

	/**
	 * Find the shortest path between two sensors in the logical network.
	 * 
	 * @param u
	 *            a sensor.
	 * @param v
	 *            a sensor.
	 * 
	 * @return the shortest path between the two sensors.
	 */
	public List<Sensor> getShortestPath(Sensor u, Sensor v) {

		DijkstraSSSP<Sensor, Link> sssp;
		sssp = new DijkstraSSSP<Sensor, Link>(logicalNetwork, u);
		sssp.generatePath(u, v);

		return sssp.getPathVerts();
	}

	public List<Sensor> getShortestPath(String u, String v) {

		Iterator<Sensor> sensorIter = logicalNetwork.vertices().iterator();
		Sensor from = null;
		Sensor to = null;

		while (sensorIter.hasNext()) {
			Sensor sensor = sensorIter.next();

			if (sensor.getName().equals(u)) {
				from = sensor;
			}

			if (sensor.getName().equals(v)) {
				to = sensor;
			}
		}

		if (from != null && to != null) {
			return getShortestPath(from, to);
		}

		return null;
	}

	/**
	 * Find the average shortest path for the logical network.
	 * 
	 * @return the average shortest path for the logical network.
	 */
	public float getAverageShortestPathLength() {

		// Similar to finding the diameter we iterate through every pair of
		// vertices and get their shortest path length. Then we add the length
		// to the total. After going through all pairs of vertices we divide to
		// find the average.
		float averageMinPath = 0f;
		int count = 0;

		Iterator<Sensor> outer = logicalNetwork.vertices().iterator();

		while (outer.hasNext()) {
			Iterator<Sensor> inner = logicalNetwork.vertices().iterator();

			Sensor u = outer.next();

			// With Dijkstra's we can find the shortest path from any vertex
			// back to the source vertex. So we can just find the dijkstra's
			// algorithm solution for each vertex, then find the max path
			// from any other vertex.
			DijkstraSSSP<Sensor, Link> sssp;
			sssp = new DijkstraSSSP<Sensor, Link>(logicalNetwork, u);

			while (inner.hasNext()) {

				Sensor v = inner.next();
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
	 * Find the average shortest path for the logical network using hops.
	 * 
	 * @return the average shortest path for the logical network using hops.
	 */
	public float getAverageShortestPathLengthHops() {

		float averageMinPath = 0f;
		float count = 0;

		Iterator<Sensor> outer = logicalNetwork.vertices().iterator();
		while (outer.hasNext()) {
			Iterator<Sensor> inner = logicalNetwork.vertices().iterator();

			Sensor u = outer.next();

			// With Dijkstra's we can find the shortest path from any vertex
			// back to the source vertex. So we can just find the dijkstra's
			// algorithm solution for each vertex, then find the max path
			// from any other vertex.
			DijkstraSSSP<Sensor, Link> sssp;
			sssp = new DijkstraSSSP<Sensor, Link>(logicalNetwork, u);

			while (inner.hasNext()) {

				Sensor v = inner.next();
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

	/**
	 * Get the physical network.
	 * 
	 * @return a weighted graph of nodes and links, representing the physical
	 *         network configuration.
	 */
	public WeightedGraph<Node, Link> getPhysicalNetwork() {
		return physicalNetwork;
	}

	/**
	 * Get the logical network.
	 * 
	 * @return a weighted graph of sensors and links, representing the logical
	 *         network configuration.
	 */
	public WeightedGraph<Sensor, Link> getLogicalNetwork() {
		return logicalNetwork;
	}

	/**
	 * Get the average sensor angle for all sensors in the logical network.
	 * 
	 * @return the average sensor angle.
	 */
	public double getAverageAngle() {
		return averageAngle;
	}

	/**
	 * Get the average sensor range for all sensors in the logical newtork.
	 * 
	 * @return the average sensor range.
	 */
	public double getAverageRange() {
		return averageRange;
	}

	/**
	 * Get the total energy use for all sensors in the logical network.
	 * 
	 * @return the total energy use.
	 */
	public double getTotalEnergyUse() {
		return totalEnergyUse;
	}

}
