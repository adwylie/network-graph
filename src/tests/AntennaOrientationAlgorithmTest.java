package tests;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import logging.FileLogger;
import model.AntennaOrientationAlgorithm;
import model.Graph;
import model.Link;
import model.Node;
import model.Sensor;
import model.WeightedGraph;

import org.junit.BeforeClass;
import org.junit.Test;

public class AntennaOrientationAlgorithmTest {

    Node a = new Node("A", 0f, 0f);
    Node b = new Node("B", 0f, 4f);
    Node c = new Node("C", 0f, 12f);
    Node d = new Node("D", 3f, 8f);

    Link AB = new Link();
    Link BC = new Link();
    Link CD = new Link();
    Link BD = new Link();

    @BeforeClass
    public static void setUpClass() {
        // No logging for tests
        FileLogger.disableLogging();
    }

    @Test
    public void testSingleConnectionDir() {
        // Create the underlying fully connected network.
        WeightedGraph<Node, Link> pn = new WeightedGraph<Node, Link>();

        pn.insertVertex(a);
        pn.insertVertex(b);

        pn.insertEdge(a, b, AB);

        // Run the orientation algorithm.
        AntennaOrientationAlgorithm aoa = new AntennaOrientationAlgorithm(pn);

        WeightedGraph<Sensor, Link> dirNet = aoa.getDirNet();
        Iterator<Sensor> iter = dirNet.vertices().iterator();

        assertEquals(15f, iter.next().getAntennaAngle(), 0f);

        Sensor s = iter.next();
        if (s.getY() == 4f) {
            assertEquals(270f, s.getAntennaDirection(), 0.005f);
            assertEquals(15f, s.getAntennaAngle(), 0f);
        } else {
            assertEquals(90f, s.getAntennaDirection(), 0.005f);
            assertEquals(15f, s.getAntennaAngle(), 0f);
        }
    }

    @Test
    public void testThreeNodes() {
        // Make our graph.
        Node a = new Node("A", 2f, 6f);
        Node b = new Node("B", 0f, 4f);
        Node c = new Node("C", 0f, -7f);

        Link AB = new Link();
        Link BC = new Link();

        WeightedGraph<Node, Link> pn = new WeightedGraph<Node, Link>();

        pn.insertVertex(a);
        pn.insertVertex(b);
        pn.insertVertex(c);

        pn.insertEdge(a, b, AB);
        pn.insertEdge(b, c, BC);

        // Run the orientation algorithm.
        AntennaOrientationAlgorithm aoa = new AntennaOrientationAlgorithm(pn);

        Graph<Sensor, Link> dirNet = aoa.getDirNet();
        Iterator<Sensor> iter = dirNet.vertices().iterator();

        while (iter.hasNext()) {
            Sensor s = iter.next();
            if (s.getName() == "A") {
                assertEquals(15f, s.getAntennaAngle(), 0f);
                assertEquals(225f, s.getAntennaDirection(), 0f);

            } else if (s.getName() == "B") {
                assertEquals(135f, s.getAntennaAngle(), 0f);
                assertEquals(337.5f, s.getAntennaDirection(), 0f);

            } else if (s.getName() == "C") {
                assertEquals(15f, s.getAntennaAngle(), 0f);
                assertEquals(90f, s.getAntennaDirection(), 0f);
            }
        }
    }

    @Test
    public void testOtherFunctionality() {
        // Use the provided graph values.
        WeightedGraph<Node, Link> pn = new WeightedGraph<Node, Link>();

        pn.insertVertex(a);
        pn.insertVertex(b);
        pn.insertVertex(c);
        pn.insertVertex(d);

        pn.insertEdge(a, b, AB);
        pn.insertEdge(b, c, BC);
        pn.insertEdge(b, d, BD);
        pn.insertEdge(c, d, CD);

        // Calculate the orientation.
        AntennaOrientationAlgorithm aoa = new AntennaOrientationAlgorithm(pn);

        // Verify calculated values for statistical averages.
        // Expected values calculated on paper by me!
        assertEquals(69.85f, aoa.getDirAverageAngle(), 0.01f);
        assertEquals(5f, aoa.getDirAverageRange(), 0.01f);
        assertEquals(14f, aoa.getDirDiameterLength(), 0.01f);
        assertEquals(3, aoa.getDirDiameterLengthHops());
        assertEquals(7.83f, aoa.getDirAverageShortestRouteLength(), 0.01);
        assertEquals(1.66f, aoa.getDirAverageShortestRouteLengthHops(), 0.01);
        assertEquals(3492.38f, aoa.getDirTotalEnergyUse(), 0.01f);

        assertEquals(360f, aoa.getOmniAverageAngle(), 0.01f);
        assertEquals(5f, aoa.getOmniAverageRange(), 0.01f);
        assertEquals(14f, aoa.getOmniDiameterLength(), 0.01f);
        assertEquals(3, aoa.getOmniDiameterLengthHops());
        assertEquals(7.83f, aoa.getOmniAverageShortestRouteLength(), 0.01);
        assertEquals(1.66f, aoa.getOmniAverageShortestRouteLengthHops(), 0.01);
        assertEquals(18000f, aoa.getOmniTotalEnergyUse(), 0.01f);

        // Find some paths, test them.
        assertEquals(14f, aoa.getDirShortestRouteLength("A", "C"), 0.01f);
        assertEquals(14f, aoa.getDirShortestRouteLength("C", "A"), 0.01f);
        assertEquals(5f, aoa.getDirShortestRouteLength("B", "D"), 0.01f);
        assertEquals(9f, aoa.getDirShortestRouteLength("A", "D"), 0.01f);
        assertEquals(3, aoa.getDirShortestRouteLengthHops("A", "C"));
        assertEquals(3, aoa.getDirShortestRouteLengthHops("C", "A"));
        assertEquals(1, aoa.getDirShortestRouteLengthHops("B", "D"));
        assertEquals(2, aoa.getDirShortestRouteLengthHops("A", "D"));

        assertEquals(14f, aoa.getOmniShortestRouteLength("A", "C"), 0.01f);
        assertEquals(14f, aoa.getOmniShortestRouteLength("C", "A"), 0.01f);
        assertEquals(5f, aoa.getOmniShortestRouteLength("B", "D"), 0.01f);
        assertEquals(9f, aoa.getOmniShortestRouteLength("A", "D"), 0.01f);
        assertEquals(3, aoa.getOmniShortestRouteLengthHops("A", "C"));
        assertEquals(3, aoa.getOmniShortestRouteLengthHops("C", "A"));
        assertEquals(1, aoa.getOmniShortestRouteLengthHops("B", "D"));
        assertEquals(2, aoa.getOmniShortestRouteLengthHops("A", "D"));

        // Set range on directional network and retest the stats.
        aoa.updateDirRange(8.2f);
        aoa.updateOmniRange(8.2f);

        // Verify calculated values for statistical averages.
        assertEquals(84.53f, aoa.getDirAverageAngle(), 0.01f);
        assertEquals(8.2f, aoa.getDirAverageRange(), 0.01f);
        assertEquals(7.16f, aoa.getDirAverageShortestRouteLength(), 0.01);
        assertEquals(1.33f, aoa.getDirAverageShortestRouteLengthHops(), 0.01);
        assertEquals(12f, aoa.getDirDiameterLength(), 0.01f);
        assertEquals(2, aoa.getDirDiameterLengthHops());
        assertEquals(11367.93f, aoa.getDirTotalEnergyUse(), 0.01f);

        assertEquals(360f, aoa.getOmniAverageAngle(), 0.01f);
        assertEquals(8.2f, aoa.getOmniAverageRange(), 0.01f);
        assertEquals(7.16f, aoa.getOmniAverageShortestRouteLength(), 0.01);
        assertEquals(1.33f, aoa.getOmniAverageShortestRouteLengthHops(), 0.01);
        assertEquals(12f, aoa.getOmniDiameterLength(), 0.01f);
        assertEquals(2, aoa.getOmniDiameterLengthHops());
        assertEquals(48412.8f, aoa.getOmniTotalEnergyUse(), 0.01f);

        // Again, test some paths.
        assertEquals(9f, aoa.getDirShortestRouteLength("A", "D"), 0.01f);
        assertEquals(4f, aoa.getDirShortestRouteLength("A", "B"), 0.01f);
        assertEquals(12f, aoa.getDirShortestRouteLength("C", "A"), 0.01f);
        assertEquals(2, aoa.getDirShortestRouteLengthHops("A", "D"));
        assertEquals(1, aoa.getDirShortestRouteLengthHops("A", "B"));
        assertEquals(2, aoa.getDirShortestRouteLengthHops("C", "A"));

        assertEquals(9f, aoa.getOmniShortestRouteLength("A", "D"), 0.01f);
        assertEquals(4f, aoa.getOmniShortestRouteLength("A", "B"), 0.01f);
        assertEquals(12f, aoa.getOmniShortestRouteLength("C", "A"), 0.01f);
        assertEquals(2, aoa.getOmniShortestRouteLengthHops("A", "D"));
        assertEquals(1, aoa.getOmniShortestRouteLengthHops("A", "B"));
        assertEquals(2, aoa.getOmniShortestRouteLengthHops("C", "A"));

    }

}
