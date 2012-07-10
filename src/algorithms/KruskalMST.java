package algorithms;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import model.VertexInterface;
import model.WeightedEdgeInterface;
import model.WeightedGraph;


// Algorithm from: Introduction to Algorithms, Third Edition; Cormen et al.
// pg. 631

public class KruskalMST<V extends VertexInterface, E extends WeightedEdgeInterface> {
    
    private WeightedGraph<V, E> MST;
    private float MSTWeight;
    
    public KruskalMST(WeightedGraph<V, E> graph) {
        // Allocate the instance variables needed.
        this.MST = new WeightedGraph<V, E>();
        this.MSTWeight = 0;
        
        // We will maintain a forest. In each step add an edge of minimum
        // weight that does not create a cycle.
        
        // First, create |V| sets, each with only a single vertex xi in it.
        // Use an (ArrayList) list so that list[i] can correspond to xi
        List<HashSet<V>> forests = new ArrayList<HashSet<V>>();
        Iterator<V> verticesIterator = graph.vertices().iterator();
        
        // Use a hash table to keep track of which vertex is in which forest.
        Hashtable<V, Integer> vertexHash = new Hashtable<V, Integer>();
        
        while (verticesIterator.hasNext()) {
            // Create a forest for each vertex, along with adding the vertex
            // and recording which forest it is in.
            HashSet<V> newForest = new HashSet<V>();
            V temp = verticesIterator.next();
            newForest.add(temp);
            forests.add(newForest);
            vertexHash.put(temp, forests.indexOf(newForest));
        }
        
        // We sort edges, and then grow the forest to get our MST.
        Quicksort<E> edgeSorter = new Quicksort<E>(graph.edges());
        List<E> sortedEdges = edgeSorter.getSortedList();
        
        // Take each edge by non-decreasing order by weight, and join the sets
        // containing the vertices referenced by the edge only if a cycle is
        // not formed.
        for (int k = 0; k < sortedEdges.size(); k++) {
            E edge = sortedEdges.get(k);
            
            // Get the forest (set) that our vertex u is found in. Similarly
            // do the same for vertex v.
            Set<V> edgeVertices = graph.endVertices(edge);
            Iterator<V> edgeVerticesIter = edgeVertices.iterator();
            int i = vertexHash.get(edgeVerticesIter.hasNext() ?
                    edgeVerticesIter.next() : -1);
            int j = vertexHash.get(edgeVerticesIter.hasNext() ?
                    edgeVerticesIter.next() : -1);
            
            // If the vertices are found in different forests then join them
            // if they are not already joined.
            // Also keep track of the forest which the vertices are in, and
            // also build/keep track of our MST.
            if (i != j) {
                int smallForestIdx;
                int largeForestIdx;
                
                if (forests.get(j).size() <= forests.get(i).size()) {
                    smallForestIdx = j;
                    largeForestIdx = i;
                } else {
                    smallForestIdx = i;
                    largeForestIdx = j;
                }

                // Move all vertices in the hash in the smaller forest to the
                // larger forest.
                Iterator<V> forestIterator =
                        forests.get(smallForestIdx).iterator();
                while (forestIterator.hasNext()) {
                    V temp = forestIterator.next();
                    vertexHash.remove(temp);
                    vertexHash.put(temp, largeForestIdx);
                }

                forests.get(largeForestIdx).addAll(
                        forests.get(smallForestIdx));
                forests.get(smallForestIdx).clear();
                
                // Add vertices and edges to the graph.
                Iterator<V> vToAdd = forests.get(largeForestIdx).iterator();
                Iterator<V> evToAdd = edgeVertices.iterator();
                
                while (vToAdd.hasNext()) {
                    this.MST.insertVertex(vToAdd.next());
                }
                
                this.MST.insertEdge(evToAdd.next(), evToAdd.next(), edge);
                
                // Keep track of the weight.
                this.MSTWeight += edge.getWeight();
                
            }
        }
        
    }

    public WeightedGraph<V, E> getMST() {
        return MST;
    }
    
    public float getMSTWeight() {
        return MSTWeight;
    }

    
}
