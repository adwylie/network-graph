package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import model.Graph;
import model.Link;
import model.Node;

import org.junit.Test;

import algorithms.ApproxVertexCover;

public class ApproxVertexCoverTest {

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

		Graph<Node, Link> pn = new Graph<Node, Link>();

		pn.insertVertex(a);
		pn.insertVertex(b);
		pn.insertVertex(c);
		pn.insertVertex(d);

		pn.insertEdge(a, b, AB);
		pn.insertEdge(b, c, BC);
		pn.insertEdge(c, d, CD);
		pn.insertEdge(b, d, BD);

		ApproxVertexCover<Node, Link> avc = new ApproxVertexCover<Node, Link>(
				pn);

		Set<Link> maximalMatching = avc.getMaximalMatching();

		// The maximal matching is either (BC), or (BD), or (AB, CD)
		HashSet<Link> testLinkA = new HashSet<Link>();
		testLinkA.add(BC);

		HashSet<Link> testLinkB = new HashSet<Link>();
		testLinkB.add(BD);

		HashSet<Link> testLinkC = new HashSet<Link>();
		testLinkC.add(AB);
		testLinkC.add(CD);

		if (maximalMatching.size() > 1) {
			assertEquals(testLinkC, maximalMatching);
		} else {
			if (!maximalMatching.equals(testLinkA)
					&& !maximalMatching.equals(testLinkB)) {
				fail();
			}
		}

	}

}
