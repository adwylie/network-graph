package ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2011-09-10
 */
class CanvasPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private AffineTransform canvasTransform;
    private AffineTransform originTransform;
    private ArrayList<Drawable> components;

    public CanvasPanel() {

        this.setDoubleBuffered(true);
        this.components = new ArrayList<Drawable>();
        this.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Network"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        this.setPreferredSize(new Dimension(600, 600));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        originTransform = g2.getTransform();
        canvasTransform = g2.getTransform();

        // Move origin to screen center.
        canvasTransform.translate(getWidth() / 2, getHeight() / 2);

        g2.setTransform(canvasTransform);

        for (int i = 0; i < components.size(); i++) {
            components.get(i).paint(g2);
        }

        g2.setTransform(originTransform);
    }

    public AffineTransform getCanvasTransform() {
        return canvasTransform;
    }

    public AffineTransform getOriginTransform() {
        return originTransform;
    }

    public void draw(Drawable o) {
        components.add(o);
    }

    public void clear() {
        components.clear();
    }
}