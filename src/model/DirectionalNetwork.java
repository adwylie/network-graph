package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DirectionalNetwork extends Network {

	int averageIdx = 0;

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

	@Override
	public void setupLogicalNetwork() {

		// Our antenna range needs to be at least as large as
		// the longest edge. (Range is measured as radius)
		List<Link> edges = new ArrayList<Link>(logicalNetwork.edges());
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

		// Reset the variables used for statistics.
		averageAngle = 0;
		averageRange = 0;
		totalEnergyUse = 0;

		// For each vertex.
		Set<Sensor> vertices = logicalNetwork.vertices();
		Iterator<Sensor> verticesIter = vertices.iterator();

		while (verticesIter.hasNext()) {
			// Get the connected edges.
			Sensor sensor = verticesIter.next();
			Set<Link> edges = logicalNetwork.incidentEdges(sensor);
			// Get the opposite vertices'.
			HashSet<Sensor> connectedVertices = new HashSet<Sensor>();
			Iterator<Link> edgesIter = edges.iterator();
			while (edgesIter.hasNext()) {
				connectedVertices.add(logicalNetwork.opposite(sensor,
						edgesIter.next()));
			}
			// Pass in values to set sensor properties.
			setSensorPropsDirNet(sensor, connectedVertices, sensorRange);
			averageIdx = 0;
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
		averageAngle = (averageAngle * averageIdx / (averageIdx + 1))
				+ (fromSensor.getAntennaAngle() * 1 / (averageIdx + 1));
		averageRange = (averageRange * averageIdx / (averageIdx + 1))
				+ (fromSensor.getAntennaRange() * 1 / (averageIdx + 1));
		totalEnergyUse += (0.5f * Math.pow(fromSensor.getAntennaRange(), 2) * fromSensor
				.getAntennaAngle());
		averageIdx++;
	}

	// Similar to the above function (updateOmniRange), this function updates
	// the edges in the directional network with respect to the input range.
	public void updateDirRange(float newRange) {

		// Get iterators for the vertices.
		Iterator<Sensor> vertsUIter = logicalNetwork.vertices().iterator();
		Iterator<Sensor> vertsVIter;

		// For every pair of vertices, if their distance is less than or equal
		// to the new distance we want to create an edge between them. Only do
		// this if the vertices are not the same. (no self loops)
		while (vertsUIter.hasNext()) {
			Sensor u = vertsUIter.next();
			u.setAntennaRange(newRange);
			vertsVIter = logicalNetwork.vertices().iterator();

			while (vertsVIter.hasNext()) {
				Sensor v = vertsVIter.next();

				// If the distance is less than the range then we add an edge.
				// We only add an edge if they are not already connected.
				if (getDistance(u, v) <= newRange) {
					if (v != u && !logicalNetwork.areAdjacent(u, v)) {
						logicalNetwork.insertEdge(u, v, new Link(u.getName()
								+ v.getName()));
					}
				} else {
					// If the distance is greater than the range the we remove
					// the edge. Clearly we can only do this if there is an
					// edge to remove.
					if (logicalNetwork.areAdjacent(u, v)) {
						// Find the edge and remove it.
						Set<Link> vEdges = logicalNetwork.incidentEdges(v);
						Set<Link> uEdges = logicalNetwork.incidentEdges(u);

						Iterator<Link> iter = vEdges.iterator();
						while (iter.hasNext()) {
							Link e = iter.next();
							if (uEdges.contains(e)) {
								logicalNetwork.removeEdge(e);
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
		setupDirNet(newRange);
	}

}
