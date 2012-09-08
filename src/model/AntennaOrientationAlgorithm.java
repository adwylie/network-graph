package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import logging.FileLogger;
import ui.NetworkGUI;
import algorithms.DijkstraSSSP;
import algorithms.KruskalMST;

// Calculates the relevant values for omnidirectional and directional antennas,
// along with providing methods for changing between them.
// All Sensors have the same range.

public class AntennaOrientationAlgorithm {

	// Set logging true if this is a compiled class, false if the class is
	// in a jar file.
	private static boolean logging = !AntennaOrientationAlgorithm.class
			.getProtectionDomain().getCodeSource().getLocation().toString()
			.contains("jar");

	private KruskalMST<Node, Link> mst;

	private WeightedGraph<Sensor, Link> omniNet;
	private WeightedGraph<Sensor, Link> dirNet;

	private double averageOmniAngle = 0;
	private double averageOmniRange = 0;
	private double totalOmniEnergyUse = 0;

	private double averageDirAngle = 0;
	private double averageDirRange = 0;
	private double totalDirEnergyUse = 0;

	private int averageIdx = 0;

	// /////////////////////////////////////////////////////////////////////////
	// //////////////////// Network Setup & Init. ////////////////////////////
	// /////////////////////////////////////////////////////////////////////////

	public AntennaOrientationAlgorithm(WeightedGraph<Node, Link> pNet) {
		this.mst = new KruskalMST<Node, Link>(pNet);

		// Build the actual networks.
		// The initial build uses the longest edge length for the sensor range.
		setupOmniNet();
		setupDirNet();
	}

	// Initialize a network by creating a new network with Sensors from a
	// network made with nodes.
	private void initializeNet(WeightedGraph<Sensor, Link> net,
			WeightedGraph<Node, Link> theMST) {

		if (logging) {
			// LOGGING
			FileLogger.log(Level.INFO, NetworkGUI.class.getName()
					+ ": initializeNet(net, mst);");
		}

		// Map Nodes to their corresponding Sensor, so that we can properly
		// build the network.
		Hashtable<Node, Sensor> sensorMap = new Hashtable<Node, Sensor>();

		Iterator<Node> mstNodes = theMST.vertices().iterator();
		Iterator<Link> mstLinks = theMST.edges().iterator();

		// Insert all of the vertices.
		while (mstNodes.hasNext()) {
			Node from = mstNodes.next();

			Sensor to = new Sensor(from);
			sensorMap.put(from, to);
			net.insertVertex(to);
		}

		// Insert all of the edges. This is a bit trickier since we need to
		// get the vertices of the original graph, map them to the new vertices,
		// then create an edge between the new vertices.
		while (mstLinks.hasNext()) {
			Link link = mstLinks.next();

			Iterator<Node> endNodes = theMST.endVertices(link).iterator();

			Sensor u = sensorMap.get(endNodes.next());
			Sensor v = sensorMap.get(endNodes.next());

			net.insertEdge(u, v, new Link(u.getName() + v.getName()));
		}
	}

	// Set up the omnidirectional network. This function computes the initial
	// sensor/antenna range to use, along with setting the proper sensor
	// properties.
	public void setupOmniNet() {

		if (logging) {
			// LOGGING
			FileLogger.log(Level.INFO, NetworkGUI.class.getName()
					+ ": setupOmniNet();");
		}

		this.omniNet = new WeightedGraph<Sensor, Link>();

		// Initialize the network.
		initializeNet(this.omniNet, this.mst.getMST());

		// Our antenna range needs to be at least as large as
		// the longest edge. (Range is measured as radius)
		List<Link> edges = new ArrayList<Link>(omniNet.edges());
		Collections.sort(edges);

		// The list is sorted in non-decreasing order, so get the last element.
		int numEdges = edges.size();
		float edgeLen = 0f;

		// Make sure there are no errors if there are no edges.
		if (numEdges > 0) {
			edgeLen = edges.get(numEdges - 1).getWeight();
		}

		setupOmniNet(edgeLen);
	}

	// Set up the omnidirectional network. This function takes as a parameter
	// the sensor range. (all sensors have identical range)
	private void setupOmniNet(float sensorRange) {

		if (logging) {
			// LOGGING
			FileLogger.log(Level.INFO, NetworkGUI.class.getName()
					+ ": setupOmniNet(" + sensorRange + ");");
		}

		// Reset variables used for statistics.
		averageOmniAngle = 0;
		averageOmniRange = 0;
		totalOmniEnergyUse = 0;

		// Now set the antennas.
		Iterator<Sensor> sensorsIterator = omniNet.vertices().iterator();
		averageIdx = 0;

		while (sensorsIterator.hasNext()) {
			Sensor sensor = sensorsIterator.next();
			sensor.setAntennaType(AntennaType.OMNIDIRECTIONAL);
			sensor.setAntennaRange(sensorRange);

			// Keep track of average angles & range
			this.averageOmniAngle = (this.averageOmniAngle * averageIdx / (averageIdx + 1))
					+ (sensor.getAntennaAngle() * 1 / (averageIdx + 1));
			this.averageOmniRange = (this.averageOmniRange * averageIdx / (averageIdx + 1))
					+ (sensor.getAntennaRange() * 1 / (averageIdx + 1));
			// Area = 1/2 r^2 angle.
			this.totalOmniEnergyUse += (0.5d * Math.pow(
					sensor.getAntennaRange(), 2) * sensor.getAntennaAngle());

			averageIdx++;
		}

	}

	// Set up the directional network. This function computes the initial
	// sensor/antenna range to use.
	public void setupDirNet() {

		if (logging) {
			// LOGGING
			FileLogger.log(Level.INFO, NetworkGUI.class.getName()
					+ ": setupDirNet();");
		}

		this.dirNet = new WeightedGraph<Sensor, Link>();

		// Initialize the network.
		initializeNet(this.dirNet, this.mst.getMST());

		// Our antenna range needs to be at least as large as
		// the longest edge. (Range is measured as radius)
		List<Link> edges = new ArrayList<Link>(dirNet.edges());
		Collections.sort(edges);

		// The list is sorted in non-decreasing order, so get the last element.
		int numEdges = edges.size();
		float edgeLen = 0f;

		if (numEdges > 0) {
			edgeLen = edges.get(numEdges - 1).getWeight();
		}

		setupDirNet(edgeLen);
	}

	// Set up the directional network. This function takes as a parameter the
	// sensor range to set. (all sensors have identical range. It calls the
	// function setSensorPropsDirNet to set each sensors properties.
	private void setupDirNet(float sensorRange) {

		if (logging) {
			// LOGGING
			FileLogger.log(Level.INFO, NetworkGUI.class.getName()
					+ ": setupDirNet(" + sensorRange + ");");
		}

		// Reset the variables used for statistics.
		averageDirAngle = 0;
		averageDirRange = 0;
		totalDirEnergyUse = 0;
		averageIdx = 0;

		// For each vertex.
		Set<Sensor> vertices = dirNet.vertices();
		Iterator<Sensor> verticesIter = vertices.iterator();

		while (verticesIter.hasNext()) {
			// Get the connected edges.
			Sensor sensor = verticesIter.next();
			Set<Link> edges = dirNet.incidentEdges(sensor);
			// Get the opposite vertices'.
			HashSet<Sensor> connectedVertices = new HashSet<Sensor>();
			Iterator<Link> edgesIter = edges.iterator();
			while (edgesIter.hasNext()) {
				connectedVertices
						.add(dirNet.opposite(sensor, edgesIter.next()));
			}
			// Pass in values to set sensor properties.
			setSensorPropsDirNet(sensor, connectedVertices, sensorRange);
		}

	}

	// This function computes the direction and angle of a sensor. It is given
	// the 'fromSensor' along with a set of 'toSensors'. It uses polar
	// coordinates calculated and then sorted to locate the largest angle.
	// The angle is then set at 360 - the largest angle, and directed towards
	// the largest angle - 180 modulus 360.
	// Although it is not used currently, the longest range needed is also
	// calculated.
	private void setSensorPropsDirNet(Sensor fromSensor, Set<Sensor> toSensors,
			float range) {

		// Find the angle needed using the toVertices positions.
		ArrayList<Float> angles = new ArrayList<Float>();
		Iterator<Sensor> toSensorsIter = toSensors.iterator();
		float longestRange = 0f;

		// For each vertex, move it, then get the angle.
		while (toSensorsIter.hasNext()) {
			Sensor toSensor = toSensorsIter.next();

			// Get the distance. Pythagorean Theorem.
			float distance = getDistance(fromSensor, toSensor);

			if (distance > longestRange) {
				longestRange = distance;
			}

			angles.add(getDirection(fromSensor, toSensor));
		}

		float direction = 0f;
		float angle = 0f;

		if (angles.size() > 0) {
			// Sort the angles.
			Collections.sort(angles);

			// Find the largest angle,
			// Sensor angle is (360 - the largest difference between angles),
			// Sensor direction is the

			float largestAngle = -1;
			int largestAngleIdx = -1;

			for (int i = 0; i < angles.size(); i++) {
				// Get the overlap condition first.

				int lastAngleIdx = i - 1;
				if (lastAngleIdx < 0) {
					lastAngleIdx = angles.size() + lastAngleIdx;
				}

				float tempAngle = angles.get(i) - angles.get(lastAngleIdx);

				// So we're walking around the circle looking for the largest
				// angle. First iteration we look at our first and last points.
				// Since we'll get a negative angle (modulus 360)
				// (ie 270 - 40 = -230) we correct this by adding 360 to get
				// the actual angle. (ie 360 - 270 + 40 = 130 = -230 + 360)
				if (tempAngle < 0f) {
					tempAngle += 360f;
				}
				if (tempAngle > largestAngle) {
					largestAngle = tempAngle;
					largestAngleIdx = i;
				}
			}

			// If a vertex only connects to one node give it an angle which can
			// be seen on the visualization/GUI.
			angle = (largestAngle == 0f) ? 15f : (360f - largestAngle);

			// Find the direction needed using the opposite vertices positions.
			float angleCurrent = angles.get(largestAngleIdx);

			int lastAngleIdx = largestAngleIdx - 1;
			if (lastAngleIdx < 0) {
				lastAngleIdx = angles.size() + lastAngleIdx;
			}
			float angleLast = angles.get(lastAngleIdx);

			if (angleCurrent > angleLast) {
				direction = (((angleCurrent + angleLast) / 2f) + 180f) % 360f;
			} else {
				// Largest angle lays across the last
				// quadrant/first quadrant divide.
				direction = ((angleCurrent + angleLast) / 2f) % 360f;
			}
		}

		// Set the values.
		fromSensor.setAntennaType(AntennaType.DIRECTIONAL);
		fromSensor.setAntennaDirection(direction);
		fromSensor.setAntennaAngle(angle);
		fromSensor.setAntennaRange(range);

		// Keep track of average angles & range
		this.averageDirAngle = (this.averageDirAngle * averageIdx / (averageIdx + 1))
				+ (fromSensor.getAntennaAngle() * 1 / (averageIdx + 1));
		this.averageDirRange = (this.averageDirRange * averageIdx / (averageIdx + 1))
				+ (fromSensor.getAntennaRange() * 1 / (averageIdx + 1));
		this.totalDirEnergyUse += (0.5f * Math.pow(
				fromSensor.getAntennaRange(), 2) * fromSensor.getAntennaAngle());
		averageIdx++;
	}

	// /////////////////////////////////////////////////////////////////////////
	// //////////////////// Utility Functions ////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////

	private float getDirection(Sensor from, Sensor to) {
		// Get the direction vector.
		// Convert the vector 'fromVertex -> toVertex' to a point,
		// by moving the vector (tail) to the origin. Then get the angle
		// from the point by applying polar coordinate formula.

		// Wiki: In both cases, the result is an angle in radians in the
		// range (-Pi, Pi]. If desired an angle in the range [0, 2Pi) may be
		// obtained by adding 2Pi to the value if it is negative.

		float direction = (float) Math.atan2((to.getY() - from.getY()),
				(to.getX() - from.getX()));

		if (direction < 0) {
			direction += 2 * Math.PI;
		}

		direction = (float) Math.toDegrees(direction);

		return direction;
	}

	private float getDistance(Sensor from, Sensor to) {
		// Get the distance. Pythagorean Theorem.
		float distance = (float) Math.sqrt((Math.pow((from.getX() - to.getX()),
				2) + Math.pow((from.getY() - to.getY()), 2)));

		return distance;
	}

	// /////////////////////////////////////////////////////////////////////////
	// ///////////////////// Range Update Functions ///////////////////////////
	// /////////////////////////////////////////////////////////////////////////

	// When the omnidirectional sensor range is set, we simply remove all
	// edges, then run through all the vertex pairs to add an edge where
	// appropriate.
	public void updateOmniRange(float newRange) {

		if (logging) {
			// LOGGING
			FileLogger.log(Level.INFO, NetworkGUI.class.getName()
					+ ": updateOmniRange(" + newRange + ");");
		}

		// Remove all of the edges. Note that we need to reset the iterator as
		// the set that it is based on is changing.
		Iterator<Link> edgesIter = this.omniNet.edges().iterator();
		while (edgesIter.hasNext()) {
			this.omniNet.removeEdge(edgesIter.next());
			// Concurrent Modification Exception
			edgesIter = this.omniNet.edges().iterator();
		}

		// Get iterators for the vertices.
		Iterator<Sensor> vertsUIter = this.omniNet.vertices().iterator();
		Iterator<Sensor> vertsVIter;

		// For every pair of vertices, if their distance is less than or equal
		// to the new distance we want to create an edge between them. Only do
		// this if the vertices are not the same. (no self loops)
		while (vertsUIter.hasNext()) {
			Sensor u = vertsUIter.next();
			u.setAntennaRange(newRange);
			vertsVIter = this.omniNet.vertices().iterator();

			while (vertsVIter.hasNext()) {
				Sensor v = vertsVIter.next();

				if (getDistance(u, v) <= newRange && v != u
						&& !this.omniNet.areAdjacent(u, v)) {
					omniNet.insertEdge(u, v,
							new Link(u.getName() + v.getName()));
				}
			}
		}

		// After changing our graph structure, rerun our algorithm to set
		// the antenna ranges, angles, etc.
		this.setupOmniNet(newRange);
	}

	// Similar to the above function (updateOmniRange), this function updates
	// the edges in the directional network with respect to the input range.
	public void updateDirRange(float newRange) {

		if (logging) {
			// LOGGING
			FileLogger.log(Level.INFO, NetworkGUI.class.getName()
					+ ": setupDirRange(" + newRange + ");");
		}

		// Get iterators for the vertices.
		Iterator<Sensor> vertsUIter = this.dirNet.vertices().iterator();
		Iterator<Sensor> vertsVIter;

		// For every pair of vertices, if their distance is less than or equal
		// to the new distance we want to create an edge between them. Only do
		// this if the vertices are not the same. (no self loops)
		while (vertsUIter.hasNext()) {
			Sensor u = vertsUIter.next();
			u.setAntennaRange(newRange);
			vertsVIter = this.dirNet.vertices().iterator();

			while (vertsVIter.hasNext()) {
				Sensor v = vertsVIter.next();

				// If the distance is less than the range then we add an edge.
				// We only add an edge if they are not already connected.
				if (getDistance(u, v) <= newRange) {
					if (v != u && !this.dirNet.areAdjacent(u, v)) {
						dirNet.insertEdge(u, v,
								new Link(u.getName() + v.getName()));
					}
				} else {
					// If the distance is greater than the range the we remove
					// the edge. Clearly we can only do this if there is an
					// edge to remove.
					if (this.dirNet.areAdjacent(u, v)) {
						// Find the edge and remove it.
						Set<Link> vEdges = this.dirNet.incidentEdges(v);
						Set<Link> uEdges = this.dirNet.incidentEdges(u);

						Iterator<Link> iter = vEdges.iterator();
						while (iter.hasNext()) {
							Link e = iter.next();
							if (uEdges.contains(e)) {
								this.dirNet.removeEdge(e);
								// Concurrent Modification Error
								iter = vEdges.iterator();
							}
						}
					}
				}

			}
		}

		// After changing our graph structure, rerun our algorithm to set
		// the antenna ranges, angles, etc.
		this.setupDirNet(newRange);
	}

	// /////////////////////////////////////////////////////////////////////////
	// ////////////// Diameter/Length/Route Base Functions /////////////////////
	// /////////////////////////////////////////////////////////////////////////

	// To find the diameter of a graph, first find the shortest path between
	// each pair of vertices. The greatest length of any of these paths is the
	// diameter of the graph.
	private float getGraphDiameterLength(WeightedGraph<Sensor, Link> graph) {

		float longestMinPath = 0f;

		Iterator<Sensor> outer = graph.vertices().iterator();
		while (outer.hasNext()) {
			Iterator<Sensor> inner = graph.vertices().iterator();

			Sensor u = outer.next();

			// With Dijkstra's we can find the shortest path from any vertex
			// back to the source vertex. So we can just find the dijkstra's
			// algorithm solution for each vertex, then find the max path
			// from any other vertex.
			DijkstraSSSP<Sensor, Link> sssp = new DijkstraSSSP<Sensor, Link>(
					graph, u);

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

	// Find the diameter of a graph, with respect to the number of hops.
	// Since we're using Euclidean distance (straight line links, no negative
	// paths also) the shortest path by hops is the same as the shortest path
	// by distance, we're just using a different metric; edges traversed
	// instead of edge length traversed.
	// For each pair of vertices get the path. Count the number of edges,
	// and keep record of the longest path.
	private int getGraphDiameterLengthHops(WeightedGraph<Sensor, Link> graph) {

		int longestMinPath = 0;

		Iterator<Sensor> outer = graph.vertices().iterator();
		while (outer.hasNext()) {
			Iterator<Sensor> inner = graph.vertices().iterator();

			Sensor u = outer.next();

			// With Dijkstra's we can find the shortest path from any vertex
			// back to the source vertex. So we can just find the dijkstra's
			// algorithm solution for each vertex, then find the max path
			// from any other vertex.
			DijkstraSSSP<Sensor, Link> sssp = new DijkstraSSSP<Sensor, Link>(
					graph, u);

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

	// Find the shortest route length for a graph g given the names two nodes.
	private float getShortestRouteLength(WeightedGraph<Sensor, Link> g,
			String u, String v) {

		Sensor fromSensor = null;
		Sensor toSensor = null;

		Iterator<Sensor> vertsIter = g.vertices().iterator();
		while (vertsIter.hasNext()) {
			Sensor current = vertsIter.next();
			if (fromSensor != null && toSensor != null) {
				break;
			}
			if (current.getName().equals(u)) {
				fromSensor = current;
			}
			if (current.getName().equals(v)) {
				toSensor = current;
			}
		}

		if (fromSensor == null || toSensor == null) {
			return 0f;
		}

		return getShortestRouteLength(g, fromSensor, toSensor);
	}

	// Find the shortest route length for a graph g given two nodes.
	private float getShortestRouteLength(WeightedGraph<Sensor, Link> g,
			Sensor u, Sensor v) {

		DijkstraSSSP<Sensor, Link> sssp = new DijkstraSSSP<Sensor, Link>(g, u);
		sssp.generatePath(u, v);

		return sssp.getPathLength();
	}

	// Find the shortest route length with hops for a graph g given the names
	// of two nodes.
	private int getShortestRouteLengthHops(WeightedGraph<Sensor, Link> g,
			String u, String v) {

		Sensor fromSensor = null;
		Sensor toSensor = null;

		Iterator<Sensor> vertsIter = g.vertices().iterator();
		while (vertsIter.hasNext()) {
			Sensor current = vertsIter.next();
			if (fromSensor != null && toSensor != null) {
				break;
			}
			if (current.getName().equals(u)) {
				fromSensor = current;
			}
			if (current.getName().equals(v)) {
				toSensor = current;
			}
		}

		if (fromSensor == null || toSensor == null) {
			return 0;
		}

		return getShortestRouteLengthHops(g, fromSensor, toSensor);
	}

	// Find the shortest route length with hops for a graph g given two nodes.
	private int getShortestRouteLengthHops(WeightedGraph<Sensor, Link> g,
			Sensor u, Sensor v) {

		DijkstraSSSP<Sensor, Link> sssp = new DijkstraSSSP<Sensor, Link>(g, u);
		sssp.generatePath(u, v);

		return sssp.getPathEdges().size();
	}

	// Find the shortest route for a graph g given the names of two nodes.
	private List<Sensor> getShortestRoute(WeightedGraph<Sensor, Link> g,
			String u, String v) {

		Sensor fromSensor = null;
		Sensor toSensor = null;

		Iterator<Sensor> vertsIter = g.vertices().iterator();
		while (vertsIter.hasNext()) {
			Sensor current = vertsIter.next();
			if (fromSensor != null && toSensor != null) {
				break;
			}
			if (current.getName().equals(u)) {
				fromSensor = current;
			}
			if (current.getName().equals(v)) {
				toSensor = current;
			}
		}

		if (fromSensor == null || toSensor == null) {
			return new ArrayList<Sensor>();
		}

		return getShortestRoute(g, fromSensor, toSensor);
	}

	// Find the shortest route for a graph g given two nodes.
	private List<Sensor> getShortestRoute(WeightedGraph<Sensor, Link> g,
			Sensor u, Sensor v) {

		DijkstraSSSP<Sensor, Link> sssp = new DijkstraSSSP<Sensor, Link>(g, u);
		sssp.generatePath(u, v);

		return sssp.getPathVerts();
	}

	// Find the average shortest path for a graph.
	// Similar to finding the diameter we iterate through every pair of
	// vertices and get their shortest path length. Then we add the length to
	// the total. After going through all pairs of vertices we divide to find
	// the average.
	private float getAverageShortestRouteLength(
			WeightedGraph<Sensor, Link> graph) {

		float averageMinPath = 0f;
		int count = 0;

		Iterator<Sensor> outer = graph.vertices().iterator();
		while (outer.hasNext()) {
			Iterator<Sensor> inner = graph.vertices().iterator();

			Sensor u = outer.next();

			// With Dijkstra's we can find the shortest path from any vertex
			// back to the source vertex. So we can just find the dijkstra's
			// algorithm solution for each vertex, then find the max path
			// from any other vertex.
			DijkstraSSSP<Sensor, Link> sssp = new DijkstraSSSP<Sensor, Link>(
					graph, u);

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

	// Find the average shortest path for a graph using hops.
	private float getAverageShortestRouteLengthHops(
			WeightedGraph<Sensor, Link> graph) {

		float averageMinPath = 0f;
		float count = 0;

		Iterator<Sensor> outer = graph.vertices().iterator();
		while (outer.hasNext()) {
			Iterator<Sensor> inner = graph.vertices().iterator();

			Sensor u = outer.next();

			// With Dijkstra's we can find the shortest path from any vertex
			// back to the source vertex. So we can just find the dijkstra's
			// algorithm solution for each vertex, then find the max path
			// from any other vertex.
			DijkstraSSSP<Sensor, Link> sssp = new DijkstraSSSP<Sensor, Link>(
					graph, u);

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

	// /////////////////////////////////////////////////////////////////////////
	// ////// Diameter/Length/Route Derived Functions //////////////////////////
	// /////////////////////////////////////////////////////////////////////////

	// Omnidirectional method for graph diameter length.
	public float getOmniDiameterLength() {

		float diameter = getGraphDiameterLength(this.omniNet);

		if (logging) {
			// LOGGING
			FileLogger.log(Level.INFO, NetworkGUI.class.getName()
					+ ": getOmniDiameterLength()/" + diameter + ";");
		}

		return diameter;
	}

	// Directional method for graph diameter length.
	public float getDirDiameterLength() {

		float diameter = getGraphDiameterLength(this.dirNet);

		if (logging) {
			// LOGGING
			FileLogger.log(Level.INFO, NetworkGUI.class.getName()
					+ ": getDirDiameterLength()/" + diameter + ";");
		}

		return diameter;
	}

	// Omnidirectional method for shortest route length with hops.
	public int getOmniDiameterLengthHops() {

		int diameter = getGraphDiameterLengthHops(this.omniNet);

		if (logging) {
			// LOGGING
			FileLogger.log(Level.INFO, NetworkGUI.class.getName()
					+ ": getOmniDiameterLengthHops()/" + diameter + ";");
		}

		return diameter;
	}

	// Directional method for shortest route length with hops.
	public int getDirDiameterLengthHops() {

		int diameter = getGraphDiameterLengthHops(this.dirNet);

		if (logging) {
			// LOGGING
			FileLogger.log(Level.INFO, NetworkGUI.class.getName()
					+ ": getDirDiameterLengthHops()/" + diameter + ";");
		}

		return diameter;
	}

	// Omnidirectional method for shortest route length.
	public float getOmniShortestRouteLength(String u, String v) {

		float srl = getShortestRouteLength(this.omniNet, u, v);

		if (logging) {
			// LOGGING
			FileLogger.log(Level.INFO, NetworkGUI.class.getName()
					+ ": getOmniShortestRouteLength(" + u + ", " + v + ")/"
					+ srl + ";");
		}

		return srl;
	}

	// Directional method for shortest route length.
	public float getDirShortestRouteLength(String u, String v) {

		float srl = getShortestRouteLength(this.dirNet, u, v);

		if (logging) {
			// LOGGING
			FileLogger.log(Level.INFO, NetworkGUI.class.getName()
					+ ": getDirShortestRouteLength(" + u + ", " + v + ")/"
					+ srl + ";");
		}

		return srl;
	}

	// Omnidirectional method for shortest route length with hops.
	public int getOmniShortestRouteLengthHops(String u, String v) {

		int hops = getShortestRouteLengthHops(this.omniNet, u, v);

		if (logging) {
			// LOGGING
			FileLogger.log(Level.INFO, NetworkGUI.class.getName()
					+ ": getOmniShortestRouteLengthHops()/" + hops + ";");
		}

		return hops;
	}

	// Directional method for shortest route length with hops.
	public int getDirShortestRouteLengthHops(String u, String v) {

		int hops = getShortestRouteLengthHops(this.dirNet, u, v);

		if (logging) {
			// LOGGING
			FileLogger.log(Level.INFO, NetworkGUI.class.getName()
					+ ": getDirShortestRouteLengthHops()/" + hops + ";");
		}

		return hops;
	}

	// Omnidirectional method for shortest route.
	public List<Sensor> getOmniShortestRoute(String u, String v) {

		List<Sensor> sr = getShortestRoute(this.omniNet, u, v);

		if (logging) {
			// LOGGING
			FileLogger.log(Level.INFO,
					NetworkGUI.class.getName() + ": getOmniShortestRoute(" + u
							+ ", " + v + ")/" + sr.toString() + ";");
		}

		return sr;
	}

	// Directional method for shortest route.
	public List<Sensor> getDirShortestRoute(String u, String v) {

		List<Sensor> sr = getShortestRoute(this.dirNet, u, v);

		if (logging) {
			// LOGGING
			FileLogger.log(Level.INFO,
					NetworkGUI.class.getName() + ": getDirShortestRoute(" + u
							+ ", " + v + ")/" + sr.toString() + ";");
		}

		return sr;
	}

	// Omnidirectional method for average shortest route length.
	public float getOmniAverageShortestRouteLength() {

		float length = getAverageShortestRouteLength(this.omniNet);

		if (logging) {
			// LOGGING
			FileLogger.log(Level.INFO, NetworkGUI.class.getName()
					+ ": getOmniAverageShortestRouteLength()/" + length + ";");
		}

		return length;
	}

	// Directional method for average shortest route length.
	public float getDirAverageShortestRouteLength() {

		float length = getAverageShortestRouteLength(this.dirNet);

		if (logging) {
			// LOGGING
			FileLogger.log(Level.INFO, NetworkGUI.class.getName()
					+ ": getDirAverageShortestRouteLength()/" + length + ";");
		}

		return length;
	}

	// Omnidirectional method for average shortest route length with hops.
	public float getOmniAverageShortestRouteLengthHops() {

		float length = getAverageShortestRouteLengthHops(this.omniNet);

		if (logging) {
			// LOGGING
			FileLogger.log(Level.INFO, NetworkGUI.class.getName()
					+ ": getOmniAverageShortestRouteLengthHops()/" + length
					+ ";");
		}

		return length;
	}

	// Directional method for average shortest route length with hops.
	public float getDirAverageShortestRouteLengthHops() {

		float length = getAverageShortestRouteLengthHops(this.dirNet);

		if (logging) {
			// LOGGING
			FileLogger.log(Level.INFO, NetworkGUI.class.getName()
					+ ": getDirAverageShortestRouteLengthHops()/" + length
					+ ";");
		}

		return length;
	}

	// /////////////////////////////////////////////////////////////////////////
	// //////////////////////// Accessor Methods ///////////////////////////////
	// /////////////////////////////////////////////////////////////////////////

	public WeightedGraph<Sensor, Link> getOmniNet() {
		return omniNet;
	}

	public WeightedGraph<Sensor, Link> getDirNet() {
		return dirNet;
	}

	public double getOmniAverageAngle() {
		return averageOmniAngle;
	}

	public double getOmniAverageRange() {
		return averageOmniRange;
	}

	public double getOmniTotalEnergyUse() {
		return totalOmniEnergyUse;
	}

	public double getDirAverageAngle() {
		return averageDirAngle;
	}

	public double getDirAverageRange() {
		return averageDirRange;
	}

	public double getDirTotalEnergyUse() {
		return totalDirEnergyUse;
	}

}
