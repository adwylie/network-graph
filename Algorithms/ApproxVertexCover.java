package Algorithms;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import Model.EdgeInterface;
import Model.Graph;
import Model.VertexInterface;

//Algorithm from: Introduction to Algorithms, Third Edition; Cormen et al.
//pg. 1109

//This class is a modified vertex cover which also keeps track/finds a
// maximal matching for an input graph.

// Pseudo code:
//C = {}
//E' = G.E
//while E' != {}
//  let (u, v) be an arbitrary edge of E'
//  C = C U {u, v}
//  remove from E' every edge incident on either u or v
//return C

// Returns a 2-approximation.

public class ApproxVertexCover<V extends VertexInterface, E extends EdgeInterface> {
    
    private HashSet<V> vertexCoverVertices;
    private HashSet<E> vertexCoverEdges;
    
    public ApproxVertexCover(Graph<V, E> g) {
        // Start by setting up our variables.
        this.vertexCoverVertices = new HashSet<V>();
        this.vertexCoverEdges = new HashSet<E>();
        
        HashSet<E> graphEdges = new HashSet<E>();
        graphEdges.addAll(g.edges());
        
        // Main while loop. Pick arbitrary edges from our graphs edge set and
        // add the incident vertices to the vertex cover, afterwards removing
        // all incident edges to the vertices from the edge set.
        while (!graphEdges.isEmpty()) {
            // Not really 'random', though at the same time has no actual
            // guaranteed order.
            Iterator<E> graphEdgesIterator = graphEdges.iterator();
            E randomEdge = graphEdgesIterator.next();
            Set<V> edgeVertices = g.endVertices(randomEdge);
            Iterator<V> edgeVerticesIter = edgeVertices.iterator();
            V u = edgeVerticesIter.next();
            V v = edgeVerticesIter.next();
            
            this.vertexCoverVertices.add(u);
            this.vertexCoverVertices.add(v);
            this.vertexCoverEdges.add(randomEdge);
            
            // Get edges incident to u or v.
            HashSet<E> incident = new HashSet<E>();
            incident.addAll(g.incidentEdges(v));
            incident.addAll(g.incidentEdges(u));
            
            // Remove from E'
            graphEdges.removeAll(incident);
        }
    }

    public Set<V> getVertexCover() {
        return vertexCoverVertices;
    }
    
    public Set<E> getMaximalMatching() {
        return vertexCoverEdges;
    }

}
