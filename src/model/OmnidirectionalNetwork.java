package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-09-12
 */
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

		// Set the return network to be the same as the logical network.

		// Set each sensor to have a range which is just large enough to reach
		// all its connected vertices.

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
		Iterator<Sensor> verticesIter = logicalNetwork.vertices().iterator();

		while (verticesIter.hasNext()) {
			network.insertVertex(verticesIter.next());
		}

		// Add edges which have the proper sensor range to the network.
		Iterator<Sensor> vertsUIter = logicalNetwork.vertices().iterator();
		Iterator<Sensor> vertsVIter;
		int index = 0;

		// For every pair of vertices, if their distance is less than or equal
		// to the new distance we want to create an edge between them. Only do
		// this if the vertices are not the same.
		while (vertsUIter.hasNext()) {

			Sensor u = vertsUIter.next();

			u.setAntennaType(AntennaType.OMNIDIRECTIONAL);
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

			// Keep track of average angles & range.
			float sensorAngle = u.getAntennaAngle();

			double previousWeightedAngle = averageAngle * index / (index + 1);
			double previousWeightedRange = averageRange * index / (index + 1);

			double nextWeightedAngle = sensorAngle * 1 / (index + 1);
			double nextWeightedRange = sensorRange * 1 / (index + 1);

			averageAngle = previousWeightedAngle + nextWeightedAngle;
			averageRange = previousWeightedRange + nextWeightedRange;

			// Area = 1/2 r^2 angle.
			totalEnergyUse += (0.5d * Math.pow(sensorRange, 2) * sensorAngle);

			index++;
		}

		return network;
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

		// Make sure there are no errors if there are no edges.
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
