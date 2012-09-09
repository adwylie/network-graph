package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class OmnidirectionalNetwork extends Network {

	/**
	 * Constructor for a network.
	 * 
	 * @param physicalNetwork
	 *            a weighted graph of nodes and links which represents the
	 *            physical network configuration.
	 */
	public OmnidirectionalNetwork(WeightedGraph<Node, Link> physicalNetwork) {
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

		// Make sure there are no errors if there are no edges.
		if (numEdges > 0) {
			edgeLen = edges.get(numEdges - 1).getWeight();
		}

		setupOmniNet(edgeLen);
	}

	// Set up the omnidirectional network. This function takes as a parameter
	// the sensor range. (all sensors have identical range)
	private void setupOmniNet(float sensorRange) {

		// Reset variables used for statistics.
		averageAngle = 0;
		averageRange = 0;
		totalEnergyUse = 0;

		// Now set the antennas.
		Iterator<Sensor> sensorsIterator = logicalNetwork.vertices().iterator();
		int averageIdx = 0;

		while (sensorsIterator.hasNext()) {
			Sensor sensor = sensorsIterator.next();
			sensor.setAntennaType(AntennaType.OMNIDIRECTIONAL);
			sensor.setAntennaRange(sensorRange);

			// Keep track of average angles & range
			averageAngle = (averageAngle * averageIdx / (averageIdx + 1))
					+ (sensor.getAntennaAngle() * 1 / (averageIdx + 1));
			averageRange = (averageRange * averageIdx / (averageIdx + 1))
					+ (sensor.getAntennaRange() * 1 / (averageIdx + 1));
			// Area = 1/2 r^2 angle.
			totalEnergyUse += (0.5d * Math.pow(sensor.getAntennaRange(), 2) * sensor
					.getAntennaAngle());

			averageIdx++;
		}

	}

	// When the omnidirectional sensor range is set, we simply remove all
	// edges, then run through all the vertex pairs to add an edge where
	// appropriate.
	public void updateOmniRange(float newRange) {

		// Remove all of the edges. Note that we need to reset the iterator as
		// the set that it is based on is changing.
		Iterator<Link> edgesIter = logicalNetwork.edges().iterator();
		while (edgesIter.hasNext()) {
			logicalNetwork.removeEdge(edgesIter.next());
			// Concurrent Modification Exception
			edgesIter = logicalNetwork.edges().iterator();
		}

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

				if (getDistance(u, v) <= newRange && v != u
						&& !logicalNetwork.areAdjacent(u, v)) {
					logicalNetwork.insertEdge(u, v,
							new Link(u.getName() + v.getName()));
				}
			}
		}

		// After changing our graph structure, rerun our algorithm to set
		// the antenna ranges, angles, etc.
		setupOmniNet(newRange);
	}

}
