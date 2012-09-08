package model;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Iterator;

import ui.DrawableInterface;

public class WeightedGraph<V extends VertexInterface, E extends WeightedEdgeInterface>
		extends Graph<V, E> implements DrawableInterface {

	@Override
	public E insertEdge(V v, V u, E x) {

		// Check that the edge doesn't exist, and that the vertices do exist.
		if (edges.contains(x)) {
			return x;
		}

		E edge = super.insertEdge(v, u, x);

		// Weight is currently the Euclidean distance between the vertices which
		// are connected by the edge.
		double xLengthSquared = Math.pow(Math.abs(u.getX() - v.getX()), 2);
		double yLengthSquared = Math.pow(Math.abs(u.getY() - v.getY()), 2);
		double length = Math.sqrt(xLengthSquared + yLengthSquared);

		edge.setWeight((float) length);

		return edge;
	}

	// Drawing things
	public void drawMe(Graphics g) {

		Iterator<V> verticesIterator = this.vertices().iterator();

		// Draw vertices.
		while (verticesIterator.hasNext()) {
			V v = verticesIterator.next();
			// TODO: Can I make V implement this interface?
			if (v instanceof DrawableInterface) {
				((DrawableInterface) v).drawMe(g);
			}
		}

		// Draw edges.
		verticesIterator = this.vertices().iterator();

		while (verticesIterator.hasNext()) {
			V v = verticesIterator.next();
			Iterator<E> e = this.incidentEdges(v).iterator();
			while (e.hasNext()) {
				V u = this.opposite(v, e.next());

				// TODO: Can I make V implement this interface?
				if (v instanceof DrawableInterface
						&& u instanceof DrawableInterface) {
					g.setColor(Color.BLACK);
					g.drawLine((int) v.getX(), (int) v.getY(), (int) u.getX(),
							(int) u.getY());
				}
			}
		}

	}

}
