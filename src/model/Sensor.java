package model;

import java.awt.Color;
import java.awt.Graphics;

import ui.Drawable;

public class Sensor extends Node implements Drawable {

	// Properties of the Sensors antenna.
	private AntennaType antennaType;
	private float antennaRange;

	// Antenna angle is in degrees, centered on antennaDirection.
	private float antennaAngle;

	// Polar coordinates are used, degrees.
	private float antennaDirection;

	public Sensor(String name, float x, float y) {
		super(name, x, y);
		this.antennaType = AntennaType.OMNIDIRECTIONAL;
		this.antennaAngle = 360f;
	}

	public Sensor(Node location) {
		super(location.getName(), location.getX(), location.getY());
		this.antennaType = AntennaType.OMNIDIRECTIONAL;
		this.antennaAngle = 360f;
	}

	public AntennaType getAntennaType() {
		return this.antennaType;
	}

	public void setAntennaType(AntennaType antennaType) {
		this.antennaType = antennaType;
		// An omnidirectional antenna has angle 360.
		if (antennaType == AntennaType.OMNIDIRECTIONAL) {
			this.antennaDirection = 0f;
			this.antennaAngle = 360f;
		}
	}

	public float getAntennaRange() {
		return this.antennaRange;
	}

	public void setAntennaRange(float antennaRange) {
		this.antennaRange = antennaRange;
	}

	public float getAntennaAngle() {
		return this.antennaAngle;
	}

	public void setAntennaAngle(float antennaAngle) {
		// An omnidirectional antenna cannot have its angle set.
		if (antennaType == AntennaType.DIRECTIONAL) {
			this.antennaAngle = antennaAngle;
		}
	}

	public float getAntennaDirection() {
		return this.antennaDirection;
	}

	public void setAntennaDirection(float antennaDirection) {
		// An omnidirectional antenna cannot have its direction set.
		if (antennaType == AntennaType.DIRECTIONAL) {
			this.antennaDirection = antennaDirection;
		}
	}

	// Drawing things.
	private Color antennaColor = new Color(Color.BLUE.getRed(),
			Color.BLUE.getGreen(), Color.BLUE.getBlue(), 55);

	public void paint(Graphics g) {
		g.setColor(antennaColor);
		// The coordinate system has origin in the top left instead of bottom
		// left. So we have to flip/mirror the angles along the y-axis.
		// The scaling which mirrors the y-axis has nothing to do with the
		// angles, which is why this still has to be done.
		float newAntennaDirection = this.antennaDirection
				+ (180f - this.antennaDirection) * 2;

		g.fillArc((int) (this.x - this.antennaRange),
				(int) (this.y - this.antennaRange),
				(int) (this.antennaRange * 2), (int) (this.antennaRange * 2),
				// Start angle.
				(int) (newAntennaDirection - this.antennaAngle / 2),
				// Arc angle.
				(int) (this.antennaAngle));

		super.paint(g);
	}

}
