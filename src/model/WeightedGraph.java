package model;

import ui.Drawable;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2011-09-10
 */
public class WeightedGraph<V extends Vertex, E extends WeightedEdgeInterface>
		extends Graph<V, E> implements Drawable {

	@Override
	public E insertEdge(V v, V u, E e) {

		// Check that the edge doesn't exist, and that the vertices do exist.
		if (edges.contains(e)) {
			return e;
		}

		E edge = super.insertEdge(v, u, e);

		// Weight is currently the Euclidean distance between the vertices which
		// are connected by the edge.
		double xLengthSquared = Math.pow(Math.abs(u.getX() - v.getX()), 2);
		double yLengthSquared = Math.pow(Math.abs(u.getY() - v.getY()), 2);
		double length = Math.sqrt(xLengthSquared + yLengthSquared);

		edge.setWeight((float) length);

		return edge;
	}

}
