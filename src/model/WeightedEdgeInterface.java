package model;

public interface WeightedEdgeInterface extends EdgeInterface,
		Comparable<WeightedEdgeInterface> {

	/**
	 * Get the weight of the edge.
	 * 
	 * @return the edge's weight.
	 */
	float getWeight();

	/**
	 * Set the weight of the edge.
	 * 
	 * @param weight
	 *            the edge's weight.
	 */
	void setWeight(float weight);
}
