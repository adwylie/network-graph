package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashSet;

import model.Graph;
import model.Link;
import model.Node;

import org.junit.Before;
import org.junit.Test;

public class GraphTest {

	Node a;
	Node b;
	Node c;
	Node d;

	Link AB;
	Link AC;
	Link BC;
	Link CD;
	Link BD;

	Graph<Node, Link> pn;

	@Before
	public void setUp() throws Exception {

		a = new Node("A", 0f, 0f);
		b = new Node("B", 0f, 3f);
		c = new Node("C", 0f, 12f);
		d = new Node("D", 3f, 8f);

		AB = new Link(a.getName() + b.getName());
		AC = new Link(a.getName() + c.getName());
		BC = new Link(b.getName() + c.getName());
		CD = new Link(c.getName() + d.getName());
		BD = new Link(b.getName() + d.getName());

		pn = new Graph<Node, Link>();
	}

	@Test
	public void testVertices() {

		HashSet<Node> testNodes = new HashSet<Node>();

		assertEquals(testNodes, pn.vertices());

		pn.insertVertex(a);
		testNodes.add(a);

		assertEquals(testNodes, pn.vertices());

		pn.insertVertex(b);
		testNodes.add(b);

		assertEquals(testNodes, pn.vertices());

		pn.insertEdge(a, b, AB);

		assertEquals(testNodes, pn.vertices());
	}

	@Test
	public void testEdges() {

		HashSet<Link> testLinks = new HashSet<Link>();

		assertEquals(testLinks, pn.edges());

		pn.insertVertex(a);
		pn.insertVertex(b);
		pn.insertVertex(c);

		pn.insertEdge(a, b, AB);
		testLinks.add(AB);

		assertEquals(testLinks, pn.edges());

		pn.insertEdge(b, c, BC);
		testLinks.add(BC);

		assertEquals(testLinks, pn.edges());
	}

	@Test
	public void testIncidentEdges() {

		HashSet<Link> testLinks = new HashSet<Link>();

		pn.insertVertex(a);
		pn.insertVertex(b);
		pn.insertVertex(c);

		pn.insertEdge(a, b, AB);
		pn.insertEdge(b, c, BC);

		testLinks.add(AB);
		testLinks.add(BC);
		assertEquals(testLinks, pn.incidentEdges(b));

		testLinks.clear();
		testLinks.add(AB);
		assertEquals(testLinks, pn.incidentEdges(a));
	}

	@Test
	public void testEndVertices() {

		HashSet<Node> testLinks = new HashSet<Node>();

		pn.insertVertex(a);
		pn.insertVertex(b);
		pn.insertVertex(c);

		pn.insertEdge(a, b, AB);
		pn.insertEdge(b, c, BC);

		testLinks.add(b);
		testLinks.add(c);
		assertEquals(testLinks, pn.endVertices(BC));

		testLinks.clear();
		testLinks.add(b);
		testLinks.add(a);
		assertEquals(testLinks, pn.endVertices(AB));
	}

	@Test
	public void testOpposite() {

		pn.insertVertex(a);
		pn.insertVertex(b);
		pn.insertVertex(c);

		pn.insertEdge(a, b, AB);
		pn.insertEdge(b, c, BC);

		assertEquals(b, pn.opposite(a, AB));
		assertEquals(a, pn.opposite(b, AB));
		assertEquals(b, pn.opposite(c, BC));
		// Non-existent Link
		assertNull(pn.opposite(c, AC));
		// Non-existent Vertex
		assertNull(pn.opposite(d, AC));
		// Non-attached Vertex
		assertNull(pn.opposite(c, AB));
	}

	@Test
	public void testAreAdjacent() {

		pn.insertVertex(a);
		pn.insertVertex(b);
		pn.insertVertex(c);

		pn.insertEdge(a, b, AB);
		pn.insertEdge(b, c, BC);

		assertEquals(true, pn.areAdjacent(a, b));
		assertEquals(false, pn.areAdjacent(b, a));
		assertEquals(true, pn.areAdjacent(b, c));
		assertEquals(false, pn.areAdjacent(c, b));
		assertEquals(false, pn.areAdjacent(a, c));
		assertEquals(false, pn.areAdjacent(a, d));
	}

	@Test
	public void testRemoval() {
		pn.insertVertex(a);
		pn.insertVertex(b);
		pn.insertVertex(c);
		pn.insertVertex(d);

		pn.insertEdge(a, b, AB);
		pn.insertEdge(b, c, BC);
		pn.insertEdge(b, d, BD);

		// Debug trace to test these.
		pn.removeEdge(AB);
		pn.removeVertex(a);
		pn.removeVertex(b);

	}

}
