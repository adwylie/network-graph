package ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JPanel;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2011-09-10
 */
class JCanvas extends JPanel {

	private static final long serialVersionUID = 1L;
	private Set<Drawable> components = new HashSet<Drawable>();

	private AffineTransform baseTransform = null;
	private AffineTransform canvasTransform = null;

	/**
	 * Create a new canvas object.
	 * 
	 * Any objects which implement the Drawable interface may be added to the
	 * canvas object, and will then be painted on the canvas.
	 */
	public JCanvas() {
		setLayout(null);
		setDoubleBuffered(true);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		// If we are drawing with a transformation then apply the transformation
		// to the graphics object before drawing.
		if (canvasTransform != null) {

			baseTransform = g2.getTransform();

			AffineTransform combinedTransform;
			combinedTransform = new AffineTransform(baseTransform);
			combinedTransform.concatenate(canvasTransform);

			g2.setTransform(combinedTransform);
		}

		// Draw all of the components.
		Iterator<Drawable> componentsIter = components.iterator();

		while (componentsIter.hasNext()) {
			componentsIter.next().paint(g2);
		}

		// If we are using a transformation then we need to reset the graphics
		// object after drawing the contained components.
		if (canvasTransform != null) {
			g2.setTransform(baseTransform);
		}
	}

	@Deprecated
	public AffineTransform getCanvasTransform() {
		return canvasTransform;
	}

	/**
	 * Set the transformation which is applied when drawing objects contained in
	 * the canvas.
	 * 
	 * @param affineTransform
	 *            an AffineTransform to be applied when drawing objects
	 *            contained in the canvas.
	 */
	public void setTransform(AffineTransform affineTransform) {
		canvasTransform = affineTransform;
	}

	@Deprecated
	public AffineTransform getOriginTransform() {
		return baseTransform;
	}

	/**
	 * Add a specified object to the canvas.
	 * 
	 * @param drawable
	 *            the object to add to the canvas.
	 */
	public void add(Drawable drawable) {
		components.add(drawable);
		repaint();
	}

	/**
	 * Remove an object from the canvas.
	 * 
	 * @param drawable
	 *            the object to remove from the canvas.
	 */
	public void remove(Drawable drawable) {
		components.remove(drawable);
		repaint();
	}

	/**
	 * Clear all objects from the canvas.
	 */
	public void clear() {
		components.clear();
		repaint();
	}

}
