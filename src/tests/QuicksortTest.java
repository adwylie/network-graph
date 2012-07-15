package tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
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

        List<Link> oracleLinks = new ArrayList<Link>();
        oracleLinks.add(AB);
        oracleLinks.add(CD);
        oracleLinks.add(BD);
        oracleLinks.add(BC);

        assertEquals(oracleLinks, sortedLinks);

    }

    @Test
    public void simpleTest01() {

        Integer[] intArray = { 4, 6, 7, 8, 64, 75, 73, 3 };
        Integer[] oracleIntArray = { 3, 4, 6, 7, 8, 64, 73, 75 };

        List<Integer> intList = Arrays.asList(intArray);
        Quicksort<Integer> intSorter = new Quicksort<Integer>(intList);

        List<Integer> sortedInts = intSorter.getSortedList();
        List<Integer> oracleInts = Arrays.asList(oracleIntArray);

        assertEquals(oracleInts, sortedInts);

    }

    @Test
    public void simpleTest02() {

        Integer[] intArray = { 4, 6, 7, 8, -8, 64, 75, 73, 3, 0, -1, -5 };
        Integer[] oracleIntArray = { -8, -5, -1, 0, 3, 4, 6, 7, 8, 64, 73, 75 };

        List<Integer> intList = Arrays.asList(intArray);
        Quicksort<Integer> intSorter = new Quicksort<Integer>(intList);

        List<Integer> sortedInts = intSorter.getSortedList();
        List<Integer> oracleInts = Arrays.asList(oracleIntArray);

        assertEquals(oracleInts, sortedInts);

    }
}
