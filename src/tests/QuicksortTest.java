package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import model.Link;
import model.Node;
import model.WeightedGraph;

import org.junit.Test;

import algorithms.Quicksort;


public class QuicksortTest {

    @Test
    public void test() {
        Node a = new Node("A", 0f, 0f);
        Node b = new Node("B", 0f, 3f);
        Node c = new Node("C", 0f, 12f);
        Node d = new Node("D", 3f, 8f);
        
        Link AB = new Link();
        Link BC = new Link();
        Link CD = new Link();
        Link BD = new Link();
        
        WeightedGraph<Node, Link> pn = new WeightedGraph<Node, Link>();
        
        pn.insertVertex(a);
        pn.insertVertex(b);
        pn.insertVertex(c);
        pn.insertVertex(d);
        
        pn.insertEdge(a, b, AB);
        pn.insertEdge(b, c, BC);
        pn.insertEdge(b, d, BD);
        pn.insertEdge(c, d, CD);
        
        Quicksort<Link> linkSorter = new Quicksort<Link>(pn.edges());
        
        List<Link> sortedLinks = linkSorter.getSortedList();
        
        List<Link> testLinks = new ArrayList<Link>();
        testLinks.add(AB);
        testLinks.add(CD);
        testLinks.add(BD);
        testLinks.add(BC);
        
        //System.out.println(sortedLinks);
        //System.out.println(testLinks);
        
        assertEquals(testLinks, sortedLinks);
    
    }

}
