package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Polyline implements Drawable {

	List<Point> points = new ArrayList<Point>();
	Color color = null;

	public void add(Point point) {

		points.add(point);
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public void paint(Graphics g) {

		g.setColor(color);

		for (int i = 0; i < points.size() - 1; i++) {
			Point v = points.get(i);
			Point u = points.get(i + 1);

			g.drawLine(v.x, v.y, u.x, u.y);
		}
	}

}
