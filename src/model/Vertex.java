package model;

public abstract class Vertex {

	protected String name;
	protected float x;
	protected float y;

	/**
	 * Get the x coordinate of the vertex.
	 * 
	 * @return the x coordinate of the vertex.
	 */
	public float getX() {
		return x;
	}

	/**
	 * Get the y coordinate of the vertex.
	 * 
	 * @return the y coordinate of the vertex.
	 */
	public float getY() {
		return y;
	}

	/**
	 * Get the name of the vertex.
	 * 
	 * @return the name of the vertex.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the direction vector from this vertex to another.
	 * 
	 * Find the direction vector from this vertex to another. The result is an
	 * angle in degrees with respect to the polar coordinate system.
	 * 
	 * @param to
	 *            a Vertex.
	 * 
	 * @return an angle in degrees.
	 */
	public float getDirection(Vertex to) {

		// Convert the vector 'fromVertex -> toVertex' to a point, by moving the
		// vector (tail) to the origin.
		float yPos = to.getY() - getY();
		float xPos = to.getX() - getX();

		// Then get the angle from the point by using polar coordinate formula.
		float direction = (float) Math.atan2(yPos, xPos);

		// Wikipedia: In both cases, the result is an angle in radians in the
		// range (-Pi, Pi]. If desired an angle in the range [0, 2Pi) may be
		// obtained by adding 2Pi to the value if it is negative.
		if (direction < 0) {
			direction += 2 * Math.PI;
		}

		// Convert from radians to degrees.
		direction = (float) Math.toDegrees(direction);

		return direction;
	}

	/**
	 * Get the distance from this vertex to another.
	 * 
	 * @param to
	 *            a vertex.
	 * 
	 * @return the distance between vertices.
	 */
	public float getDistance(Vertex to) {

		// Get the distance, using the Pythagorean Theorem!
		double xDistSquared = Math.pow((getX() - to.getX()), 2);
		double yDistSquared = Math.pow((getY() - to.getY()), 2);
		float distance = (float) Math.sqrt((xDistSquared + yDistSquared));

		return distance;
	}

}
