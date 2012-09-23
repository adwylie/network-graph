package model;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-09-23
 */
public abstract class WirelessNetwork extends Network {

	public WirelessNetwork(WeightedGraph<Node, Link> physicalNetwork) {
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
	public abstract WeightedGraph<Sensor, Link> createOptimalNetwork(
			boolean sameRange);

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
	public abstract WeightedGraph<Sensor, Link> createNetwork(float sensorRange);
}
