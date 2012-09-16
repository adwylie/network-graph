package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-09-12
 */
public class DirectionalNetwork extends Network {

	int index = 0;

	/**
	 * Constructor for a network.
	 * 
	 * @param physicalNetwork
	 *            a weighted graph of nodes and links which represents the
	 *            physical network configuration.
	 */
	public DirectionalNetwork(WeightedGraph<Node, Link> physicalNetwork) {
		super(physicalNetwork);
	}

	/**
	 * Create a network from the logical network which is optimal.
	 * 
	 * @param sameRange
	 *            a boolean value indicating whether all sensors will have the
	 *            same range.
	 * 
	 * @return the constructed network.
	 */
	public WeightedGraph<Sensor, Link> createOptimalNetwork(boolean sameRange) {

		if (sameRange) {
			return createNetwork(getOptimalAntennaRange());
		} else {
			return createOptimalNetwork();
		}
	}

	/**
	 * Create a network from the logical network which is optimal.
	 * 
	 * Sensors may have different ranges.
	 * 
	 * @return the constructed network.
	 */
	private WeightedGraph<Sensor, Link> createOptimalNetwork() {

		return null;
	}

	/**
	 * Create a network from the logical network.
	 * 
	 * The network created will be configured such that all sensors have the
	 * same range.
	 * 
	 * @param sensorRange
	 *            the range for each sensor.
	 * 
	 * @return the constructed network.
	 */
	public WeightedGraph<Sensor, Link> createNetwork(float sensorRange) {

		resetStatistics();
		WeightedGraph<Sensor, Link> network = new WeightedGraph<Sensor, Link>();

		// Add all vertices in the logical network to the new network.
		Iterator<Sensor> vertsIter = logicalNetwork.vertices().iterator();

		while (vertsIter.hasNext()) {
			network.insertVertex(vertsIter.next());
		}

		// Add edges which have the proper sensor range to the network.
		Iterator<Sensor> vertsUIter = logicalNetwork.vertices().iterator();
		Iterator<Sensor> vertsVIter;
		index = 0;

		// For every pair of vertices, if their distance is less than or equal
		// to the new distance we want to create an edge between them. Only do
		// this if the vertices are not the same.
		while (vertsUIter.hasNext()) {

			Sensor u = vertsUIter.next();

			u.setAntennaType(AntennaType.DIRECTIONAL);
			u.setAntennaRange(sensorRange);

			vertsVIter = logicalNetwork.vertices().iterator();

			while (vertsVIter.hasNext()) {
				Sensor v = vertsVIter.next();

				// If the distance is less than the range then we add an edge.
				if (u.getDistance(v) <= sensorRange && v != u) {

					Link newEdge = new Link(u.getName() + v.getName());
					network.insertEdge(u, v, newEdge);

				}
			}
		}

		// Set the antenna ranges & angles.
		vertsIter = network.vertices().iterator();

		while (vertsIter.hasNext()) {

			Sensor sensor = vertsIter.next();

			// First get the connected edges. Then filter them by using only
			// outgoing edges. According to the edges we can then find the
			// correct angle and distance.
			Iterator<Link> edgeIter = network.incidentEdges(sensor).iterator();
			HashSet<Sensor> connectedVertices = new HashSet<Sensor>();

			while (edgeIter.hasNext()) {
				Link link = edgeIter.next();

				// Only use outgoing edges.
				if (network.endVertices(link).iterator().next().equals(sensor)) {

					Sensor s = network.opposite(sensor, link);
					connectedVertices.add(s);
				}
			}

			setSensorProps(sensor, connectedVertices, sensorRange);
		}
		index = 0;

		return network;
	}

	// This function computes the direction and angle of a sensor. It is given
	// the 'fromSensor' along with a set of 'toSensors'. It uses polar
	// coordinates calculated and then sorted to locate the largest angle.
	// The angle is then set at 360 - the largest angle, and directed towards
	// the largest angle - 180 modulus 360.
	// Although it is not used currently, the longest range needed is also
	// calculated.
	private void setSensorProps(Sensor fromSensor, Set<Sensor> toSensors,
			float range) {

		// Find the angle needed using the toVertices positions.
		ArrayList<Float> angles = new ArrayList<Float>();
		Iterator<Sensor> toSensorsIter = toSensors.iterator();
		float longestRange = 0f;

		// For each vertex, move it, then get the angle.
		while (toSensorsIter.hasNext()) {
			Sensor toSensor = toSensorsIter.next();

			// Get the distance. Pythagorean Theorem.
			float distance = fromSensor.getDistance(toSensor);

			if (distance > longestRange) {
				longestRange = distance;
			}

			angles.add(fromSensor.getDirection(toSensor));
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

		// Keep track of average angles & range.
		float sensorAngle = fromSensor.getAntennaAngle();
		float sensorRange = fromSensor.getAntennaRange();

		double previousWeightedAngle = averageAngle * index / (index + 1);
		double previousWeightedRange = averageRange * index / (index + 1);

		double nextWeightedAngle = sensorAngle * 1 / (index + 1);
		double nextWeightedRange = sensorRange * 1 / (index + 1);

		averageAngle = previousWeightedAngle + nextWeightedAngle;
		averageRange = previousWeightedRange + nextWeightedRange;
		totalEnergyUse += (0.5f * Math.pow(sensorRange, 2) * sensorAngle);

		index++;
	}

	/**
	 * Get the longest edge weight connecting two vertices.
	 * 
	 * Only edges in the logical network (MST of the input network) are taken
	 * into account. This produces the optimal antenna range in the case that
	 * all sensors must have the same range.
	 * 
	 * @return the longest edge weight.
	 */
	private float getOptimalAntennaRange() {

		// Our antenna range needs to be at least as large as
		// the longest edge. (Range is measured as radius)
		List<Link> edges = new ArrayList<Link>(logicalNetwork.edges());
		Collections.sort(edges);

		// The list is sorted in non-decreasing order, so get the last element.
		int numEdges = edges.size();
		float edgeLength = 0f;

		if (numEdges > 0) {
			edgeLength = edges.get(numEdges - 1).getWeight();
		}

		return edgeLength;
	}

	/**
	 * Reset the variables used for statistics.
	 */
	private void resetStatistics() {
		averageAngle = 0;
		averageRange = 0;
		totalEnergyUse = 0;
	}

}
