package model;

import java.util.Hashtable;
import java.util.Iterator;

import algorithms.PrimMST;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-09-12
 */
public abstract class Network {

	protected WeightedGraph<Node, Link> physicalNetwork = null;
	private PrimMST<Node, Link> physicalNetworkMst = null;

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
		physicalNetworkMst = new PrimMST<Node, Link>(physicalNetwork);

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

		WeightedGraph<Node, Link> mstGraph = physicalNetworkMst.getMst();

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

		// Reset the sensor properties as they may be set by subclass methods.
		Iterator<Sensor> vertsIter = logicalNetwork.vertices().iterator();

		while (vertsIter.hasNext()) {

			Sensor v = vertsIter.next();

			v.setAntennaRange(0f);
			v.setAntennaDirection(0f);
			v.setAntennaAngle(0f);
			v.setAntennaType(AntennaType.OMNIDIRECTIONAL);
		}

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
