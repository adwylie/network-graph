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

		resetStatistics();
		WeightedGraph<Sensor, Link> network = new WeightedGraph<Sensor, Link>();

		// Add all vertices in the logical network to the new network.
		Iterator<Sensor> verticesIter = logicalNetwork.vertices().iterator();

		while (verticesIter.hasNext()) {
			network.insertVertex(verticesIter.next());
		}

		// For each vertex find the connected vertices. From them find the
		// required sensor length (in the helper function).
		verticesIter = logicalNetwork.vertices().iterator();

		while (verticesIter.hasNext()) {

			float range = 0f;
			Sensor v = verticesIter.next();
			Set<Link> edges = logicalNetwork.incidentEdges(v);
			HashSet<Sensor> adjacentVerts = new HashSet<Sensor>();

			Iterator<Link> edgeIter = edges.iterator();

			while (edgeIter.hasNext()) {

				Link e = edgeIter.next();

				// Only count outgoing edges.
				if (logicalNetwork.endVertices(e).iterator().next().equals(v)) {

					float edgeWeight = e.getWeight();

					if (edgeWeight >= range) {
						range = edgeWeight;
					}

					// Add the adjacent vertex and connect the vertices.
					Sensor u = logicalNetwork.opposite(v, e);
					adjacentVerts.add(u);
					Link link = new Link(v.getName() + u.getName());
					network.insertEdge(v, u, link);
				}
			}

			// Set up the part of the network wrt/ the current sensor.
			setSensorProps(v, adjacentVerts, range);

			// Now we catch the stragglers. For each vertex we need to check
			// whether it is contained within another vertex's coverage area.
			Iterator<Sensor> vertsIter = logicalNetwork.vertices().iterator();

			while (vertsIter.hasNext()) {

				Sensor u = vertsIter.next();

				// The vertices have to be within range, and not adjacent.
				if (v.getDistance(u) <= range && !network.areAdjacent(v, u)) {

					// The u vertex must also be covered (directional sensor).
					// Since the angle is centered on direction we want to half
					// it when evaluating coverage when comparing direction.
					float vDir = v.getAntennaDirection();
					float vAngle = v.getAntennaAngle() / 2f;
					float uDir = v.getDirection(u);

					if ((((vDir - uDir + 360) % 360) <= vAngle)
							|| (((uDir - vDir + 360) % 360) <= vAngle)) {

						Link link = new Link(v.getName() + u.getName());
						network.insertEdge(v, u, link);
					}
				}
			}
		}

		return network;
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

		return network;
	}

	// This function computes the direction and angle of a sensor. It is given
	// the 'fromSensor' along with a set of 'toSensors'. It uses polar
	// coordinates calculated and then sorted to locate the largest angle.
	private void setSensorProps(Sensor fromSensor, Set<Sensor> toSensors,
			float range) {

		// If fromSensor doesn't connect to any other sensors, set default
		// properties and return.
		if (toSensors.size() == 0) {

			fromSensor.setAntennaType(AntennaType.DIRECTIONAL);
			fromSensor.setAntennaDirection(0f);
			fromSensor.setAntennaAngle(0f);
			fromSensor.setAntennaRange(0f);

			updateStats(fromSensor);

			return;
		}

		// Angles gives us a set of angles wrt/ the polar 0 degree position.
		// From these we need to find the difference between each pair (in
		// sorted order), so that we can find our desired angle, and from that
		// the direction.
		ArrayList<Float> angles = getAngles(fromSensor, toSensors);

		Collections.sort(angles);

		float largestAngle = -Float.MAX_VALUE;
		int largestAngleIdx = Integer.MIN_VALUE;

		// So we're walking around the unit circle looking for the largest
		// angle. First iteration we look at our first and last static angles.
		// With angles 40 and 270 as an example, we'll get a negative angle;
		// 40 - 270 = -230. So we correct this by adding 360 followed by a
		// modulus 360 to get the actual angle; -230 + 360 = 130, which is the
		// angle between polar coordinates 270 to 40.
		int numAngles = angles.size();

		for (int i = 0; i < numAngles; i++) {

			// Get the overlap condition first.
			int lastIdx = ((i - 1) + numAngles) % numAngles;

			float angle = ((angles.get(i) - angles.get(lastIdx)) + 360) % 360;

			if (angle > largestAngle) {
				largestAngle = angle;
				largestAngleIdx = i;
			}
		}

		// If a vertex only connects to one other vertex give it an angle which
		// can be seen on the ui.
		float angle = (largestAngle == 0f) ? 10f : (360f - largestAngle);

		// Find the direction needed using the opposite vertices positions.
		// So, after finding the largest angle, say 180 degress for example, we
		// also know that it starts at 90 degrees mark and ends at 270 degrees
		// mark. So, our direction will be 0 degrees (as the largest angle is
		// the part that is removed from the sensor range).
		float angleCurrent = angles.get(largestAngleIdx);
		int lastAngleIdx = ((largestAngleIdx - 1) + numAngles) % numAngles;
		float angleLast = angles.get(lastAngleIdx);

		float direction = getDirection(angleLast, angleCurrent);

		// Set the values.
		fromSensor.setAntennaType(AntennaType.DIRECTIONAL);
		fromSensor.setAntennaDirection(direction);
		fromSensor.setAntennaAngle(angle);
		fromSensor.setAntennaRange(range);

		updateStats(fromSensor);
	}

	/**
	 * Get a Set of the angles from a Sensor to all of a Set of sensors.
	 * 
	 * @param from
	 *            the sensor to measure direction from.
	 * @param to
	 *            a Set of sensors to measure to.
	 * 
	 * @return a set of the angles from a sensor to all of a set of sensors.
	 */
	private ArrayList<Float> getAngles(Sensor from, Set<Sensor> to) {

		ArrayList<Float> angles = new ArrayList<Float>();

		Iterator<Sensor> toIter = to.iterator();

		while (toIter.hasNext()) {

			angles.add(from.getDirection(toIter.next()));
		}

		return angles;
	}

	/**
	 * Get the direction of a sensor.
	 * 
	 * The direction is found given two angles which make up the largest angle.
	 * The angle at which the largest angle (between connected vertices) starts
	 * is the start angle, and the angle at which the largest angle ends is the
	 * end angle. The largest angle found between static angles is the
	 * difference between these.
	 * 
	 * @param startAngle
	 *            the static angle where the largest angle begins.
	 * 
	 * @param endAngle
	 *            the static angle where the largest angle ends.
	 * 
	 * @return the direction of a sensor wrt/ the input static angles.
	 */
	private float getDirection(float startAngle, float endAngle) {

		// Set the direction to initially be half way between the static angles.
		float direction = ((endAngle + startAngle) / 2f);

		// Add 180 degrees if the largest angle doesn't lay across the fourth
		// quadrant -> first quadrant divide.
		if (endAngle > startAngle) {
			direction += 180f;
		}

		// Make sure the calculated direction is a valid degree value.
		direction %= 360f;

		return direction;
	}

	/**
	 * Update the network statistics, using info from a newly set sensor.
	 * 
	 * @param setSensor
	 *            the sensor whose properties were initialized/set.
	 */
	private void updateStats(Sensor setSensor) {

		// Keep track of average angles & range.
		float sensorAngle = setSensor.getAntennaAngle();
		float sensorRange = setSensor.getAntennaRange();

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
		index = 0;
	}

}
