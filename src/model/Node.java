package model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import ui.DrawableInterface;

public class Node implements VertexInterface, DrawableInterface {

	protected String name;
	protected float x;
	protected float y;

	public Node(String name, float x, float y) {
		this.x = x;
		this.y = y;
		this.name = name;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return this.name;
	}

	// Drawing things.
	private float radius = 6;

	public void drawMe(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillOval((int) (this.getX() - this.radius),
				(int) (this.getY() - this.radius), (int) (this.radius * 2),
				(int) (this.radius * 2));

		g.setColor(Color.BLACK);
		g.setFont(new Font("Dialog", 0, 14));
		g.drawString(getName(), (int) (this.getX() + (this.radius * 1.5f)),
				(int) (this.getY()));
	}

}
