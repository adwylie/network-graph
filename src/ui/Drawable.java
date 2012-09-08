package ui;

import java.awt.Graphics;

public interface Drawable {

	/**
	 * This method allows an object to specify how it should be drawn.
	 * 
	 * @param g
	 *            a graphics object for the object to be drawn on.
	 */
	public abstract void paint(Graphics g);
}