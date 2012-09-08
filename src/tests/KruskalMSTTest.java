package tests;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import model.Link;
import model.Node;
import model.WeightedGraph;

import org.junit.Test;

import algorithms.KruskalMST;

public class KruskalMSTTest {

	@Test
	public void test() {
		Node a = new Node("A", 0f, 0f);
		Node b = new Node("B", 0f, 4f);
		Node c = new Node("C", 0f, 12f);
		Node d = new Node("D", 3f, 8f);

		Link AB = new Link(a.getName() + b.getName());
		Link BC = new Link(b.getName() + c.getName());
		Link CD = new Link(c.getName() + d.getName());
		Link BD = new Link(b.getName() + d.getName());

		WeightedGraph<Node, Link> pn = new WeightedGraph<Node, Link>();

		pn.insertVertex(a);
		pn.insertVertex(b);
		pn.insertVertex(c);
		pn.insertVertex(d);

		pn.insertEdge(a, b, AB);
		pn.insertEdge(b, c, BC);
		pn.insertEdge(c, d, CD);
		pn.insertEdge(b, d, BD);

		// MST should be AB, BD, CD.
		// Weight should be 14.

		KruskalMST<Node, Link> mst = new KruskalMST<Node, Link>(pn);

		assertEquals(14f, mst.getMSTWeight(), 0);

		Set<Link> edges = mst.getMST().edges();

		HashSet<Link> testLinks = new HashSet<Link>();
		testLinks.add(CD);
		testLinks.add(AB);
		testLinks.add(BD);

		assertEquals(testLinks, edges);
	}

}
