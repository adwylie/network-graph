package model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import ui.Drawable;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2011-09-10
 */
public class Node extends Vertex implements Drawable {

	/**
	 * Constructor for a Node object.
	 * 
	 * @param name
	 *            the name of the node.
	 * @param x
	 *            the x position of the node.
	 * @param y
	 *            the y position of the node.
	 */
	public Node(String name, float x, float y) {
		this.x = x;
		this.y = y;
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	// Drawing related code.
	private float radius = 4;

	@Override
	public void paint(Graphics g) {

		g.setColor(Color.BLACK);

		// Draw the node itself.
		int xPos = (int) (getX() - radius);
		int yPos = (int) (getY() - radius);
		int width = (int) (radius * 2);
		int height = width;

		g.fillOval(xPos, yPos, width, height);

		// Label the node.
		int nameXPos = (int) (getX() + (radius * 1.5f));
		int nameYPos = (int) (getY());

		g.setFont(new Font("Dialog", Font.BOLD, 14));
		g.drawString(getName(), nameXPos, nameYPos);
	}

}
