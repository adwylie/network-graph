package tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import logging.FileLogger;
import model.AntennaOrientationAlgorithm;
import model.Link;
import model.Node;
import model.Sensor;
import model.WeightedGraph;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import algorithms.DijkstraSSSP;

public class DijkstraSSSPTest {

    @BeforeClass
    public static void setUpClass() {
        FileLogger.disableLogging();
    }

    @Before
    public void setUp() throws Exception {
    }

    Node a = new Node("A", 0f, 0f);
    Node b = new Node("B", 0f, 4f);
    Node c = new Node("C", 0f, 12f);
    Node d = new Node("D", 3f, 8f);

    @Test
    public void simpleTest() {
        // Create the underlying fully connected network.
        WeightedGraph<Node, Link> pn = new WeightedGraph<Node, Link>();

        pn.insertVertex(a);
        pn.insertVertex(b);

        pn.insertEdge(a, b, new Link());

        // Run the orientation algorithm.
        AntennaOrientationAlgorithm aoa = new AntennaOrientationAlgorithm(pn);

        WeightedGraph<Sensor, Link> dirNet = aoa.getDirNet();

        // The graph only has one edge.
        Link expectedE = dirNet.edges().iterator().next();
        ArrayList<Sensor> expectedV = new ArrayList<Sensor>(dirNet.vertices());

        DijkstraSSSP<Sensor, Link> sssp = new DijkstraSSSP<Sensor, Link>(
            dirNet, expectedV.get(0));
        sssp.generatePath(expectedV.get(0), expectedV.get(1));

        assertEquals(expectedV, sssp.getPathVerts());

        assertEquals(1, sssp.getPathEdges().size());
        assertEquals(expectedE, sssp.getPathEdges().get(0));

        assertEquals(4f, sssp.getPathLength(), 0f);
    }

    @Test
    public void harderTest() {
        // Create the initial network.
        WeightedGraph<Node, Link> pn = new WeightedGraph<Node, Link>();

        pn.insertVertex(a);
        pn.insertVertex(b);
        pn.insertVertex(c);
        pn.insertVertex(d);

        Link AB = new Link();
        Link AD = new Link();
        Link BC = new Link();
        Link BD = new Link();

        pn.insertEdge(a, b, AB);
        pn.insertEdge(a, d, AD);
        pn.insertEdge(b, c, BC);
        pn.insertEdge(b, d, BD);

        // Compute shortest path for each node to vertex a.
        DijkstraSSSP<Node, Link> sssp = new DijkstraSSSP<Node, Link>(pn, a);

        // List for checking tests.
        ArrayList<Link> path = new ArrayList<Link>();

        // a -> d
        sssp.generatePath(a, d);

        path.add(AD);
        assertEquals(path, sssp.getPathEdges());
        assertEquals(8.5f, sssp.getPathLength(), 0.3f);
        path.clear();

        // a -> c
        sssp.generatePath(a, c);

        path.add(AB);
        path.add(BC);
        assertEquals(path, sssp.getPathEdges());
        assertEquals(12f, sssp.getPathLength(), 0f);
        path.clear();

        // a -> b
        sssp.generatePath(a, b);

        path.add(AB);
        assertEquals(path, sssp.getPathEdges());
        assertEquals(4f, sssp.getPathLength(), 0f);
        path.clear();

    }

}
