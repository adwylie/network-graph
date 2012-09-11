package model;

import java.awt.Color;
import java.awt.Graphics;

import ui.Drawable;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2011-09-10
 */
public class Sensor extends Node implements Drawable {

	private AntennaType antennaType;
	private float antennaRange;

	// Antenna angle is in degrees, centered on antennaDirection.
	private float antennaAngle;

	// Polar coordinates are used, degrees.
	private float antennaDirection;

	/**
	 * Constructor for a Sensor object.
	 * 
	 * Initially, the sensor will be set to have an omnidirectional antenna
	 * type.
	 * 
	 * @param name
	 *            the name of the Sensor.
	 * @param x
	 *            the x position of the Sensor.
	 * @param y
	 *            the y position of theh Sensor.
	 */
	public Sensor(String name, float x, float y) {
		super(name, x, y);
		antennaType = AntennaType.OMNIDIRECTIONAL;
		antennaAngle = 360f;
	}

	/**
	 * Constructor for a Sensor object.
	 * 
	 * This constructor takes a Node, the superclass type. Like the other
	 * constructor, the antenna type defaults to an omnidirectional antenna.
	 * 
	 * @param node
	 *            the node to construct a Sensor from.
	 */
	public Sensor(Node node) {
		super(node.getName(), node.getX(), node.getY());
		antennaType = AntennaType.OMNIDIRECTIONAL;
		antennaAngle = 360f;
	}

	/**
	 * Get the antenna type that this Sensor is using.
	 * 
	 * @return the antenna type that this Sensor is using.
	 */
	public AntennaType getAntennaType() {
		return antennaType;
	}

	/**
	 * Set the antenna type that this Sensor will use.
	 * 
	 * @param antennaType
	 *            the antenna type that this Sensor will use.
	 */
	public void setAntennaType(AntennaType antennaType) {

		this.antennaType = antennaType;

		// An omnidirectional antenna has a set angle 360.
		if (antennaType == AntennaType.OMNIDIRECTIONAL) {
			antennaDirection = 0f;
			antennaAngle = 360f;
		}
	}

	/**
	 * Get the antenna range that this Sensor is using.
	 * 
	 * @return the antenna range that this Sensor is using.
	 */
	public float getAntennaRange() {
		return antennaRange;
	}

	/**
	 * Set the antenna range that this Sensor will use.
	 * 
	 * @param antennaRange
	 *            the antenna range that this Sensor will use.
	 */
	public void setAntennaRange(float antennaRange) {
		this.antennaRange = antennaRange;
	}

	/**
	 * Get the antenna angle that this Sensor is using.
	 * 
	 * @return the antenna angle that this Sensor is using.
	 */
	public float getAntennaAngle() {
		return antennaAngle;
	}

	/**
	 * Set the antenna angle that this Sensor will use.
	 * 
	 * @param antennaAngle
	 *            the antenna angle that this Sensor will use.
	 */
	public void setAntennaAngle(float antennaAngle) {

		// An omnidirectional antenna cannot have its angle set.
		if (antennaType == AntennaType.DIRECTIONAL) {
			this.antennaAngle = antennaAngle;
		}
	}

	/**
	 * Get the antenna direction that this Sensor is using.
	 * 
	 * @return the antenna direction that this Sensor is using.
	 */
	public float getAntennaDirection() {
		return antennaDirection;
	}

	/**
	 * Set the antenna direction that this Sensor will use.
	 * 
	 * @param antennaDirection
	 *            the antenna direction that this Sensor will use.
	 */
	public void setAntennaDirection(float antennaDirection) {

		// An omnidirectional antenna cannot have its direction set.
		if (antennaType == AntennaType.DIRECTIONAL) {
			this.antennaDirection = antennaDirection;
		}
	}

	// Drawing related code.
	private int transparentBlue = Color.BLUE.getRGB() & 0x28FFFFFF;
	private Color antennaColor = new Color(transparentBlue, true);

	@Override
	public void paint(Graphics g) {

		g.setColor(antennaColor);

		// The Java coordinate system has the origin in the top left instead of
		// bottom left (normal when graphing). So we have to mirror the angles
		// along the y-axis. The scaling which mirrors the y-axis has nothing to
		// do with the angles, which is why this still has to be done (scaling
		// is done in the CanvasPanel class).
		float newDirection = antennaDirection + (180f - antennaDirection) * 2;

		int xPos = (int) (x - antennaRange);
		int yPos = (int) (y - antennaRange);
		int width = (int) (antennaRange * 2);
		int height = width;
		int startAngle = (int) (newDirection - (antennaAngle / 2));
		int arcAngle = (int) (antennaAngle);

		g.fillArc(xPos, yPos, width, height, startAngle, arcAngle);

		// After drawing the area taken up by the sensor, draw the node itself.
		super.paint(g);
	}

}
