package model;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Iterator;

import ui.DrawableInterface;

// TODO: something for directed graph??

public class WeightedGraph<V extends VertexInterface, E extends WeightedEdgeInterface>
		extends Graph<V, E> implements DrawableInterface {

	// Insert and return a new undirected edge with end vertices v and u,
	// storing element x.
	public E insertEdge(V v, V u, E x) {
		// Check that the edge doesn't already exist,
		// and that the vertices do exist.
		if (edges.contains(x)) {
			return x;
		}

		// Housekeeping for generic hashset.
		edges.add(x);

		if (!vertices.contains(v) || !vertices.contains(u)) {
			// error - inserting edge between non-existent vertices.
			return null;
		}

		// Add to our dictionary. (verticesMap)
		HashSet<V> vertices = new HashSet<V>();
		vertices.add(u);
		vertices.add(v);

		edgesToVertices.put(x, vertices);

		// Add to our dictionary. (edgesMap)
		HashSet<E> vEdges = verticesToEdges.get(v);
		vEdges.add(x);

		HashSet<E> uEdges = verticesToEdges.get(u);
		uEdges.add(x);

		// Set the edge weight & name.

		// Weight is currently dependent on euclidean distance between
		// the vertices which are connected by the edge.
		float xLength = Math.abs(u.getX() - v.getX());
		float yLength = Math.abs(u.getY() - v.getY());

		float length = (float) Math.sqrt(Math.pow(xLength, 2)
				+ Math.pow(yLength, 2));

		x.setWeight(length);

		x.setName(v.getName() + u.getName());

		// Return the edge.
		return x;
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
