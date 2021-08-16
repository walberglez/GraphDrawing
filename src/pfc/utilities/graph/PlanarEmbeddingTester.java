/**
 * PlanarEmbeddingTester.java
 * 13/07/2011 19:48:14
 */
package pfc.utilities.graph;

import pfc.models.Edge;
import pfc.models.Graph;
import pfc.utilities.GeometryUtilities;

/**
 * PlanarEmbeddingTesting es una clase que sirve para comprobar
 * si un grafo esta dibujado en el plano de forma planar y
 * si una arista se corta con las que forman un grafo dado.  
 * @author walber
 * 
 * UMLGraph
 * @depend - - - Graph
 * @depend - - - Edge
 * @depend - - - GeometryUtilities
 */
public class PlanarEmbeddingTester
{
    /**
     * Determinar si un grafo esta dibujado en el plano de forma planar.
     * Complejidad: O(|E|*|E|)
     * 
     * @param graph Graph
     * @return boolean si el grafo dado tiene un planar embedding.
     */
    public static boolean isPlanarEmbedding( Graph graph )
    {
        for ( Edge e : graph.edges )
            if ( keepPlanarEmbeddingEdge( graph, e ) == false )
                return false;

        return true;
    }

    /**
     * Determinar si la arista dada corta alguna de las aristas
     * que forman el grafo dado.
     * Complejidad: O(|E|)
     * 
     * @param graph Graph
     * @param e1 Edge
     * @return boolean si la arista e1 corta alguna otra arista en el grafo graph.
     */
    public static boolean keepPlanarEmbeddingEdge( Graph graph, Edge e1 )
    {
        for ( Edge e2 : graph.edges )
            if ( e1 != e2 && areCrossed( e1, e2 ) )
                return false;
                
        return true;
    }

    /**
     * @param e1 Edge
     * @param e2 Edge
     * @return boolean whether edges e1 and e2 are crossed.
     */
    private static boolean areCrossed( Edge e1, Edge e2 )
    {
        if ( e1.isAdjacent( e2 ) )
            return getNumberOfCrossings( e1, e2 ) > 1;
        else
            return getNumberOfCrossings( e1, e2 ) > 0;
    }

    /**
     * @param e1 Edge
     * @param e2 Edge
     * @return int number of crossings points between e1 and e2.
     */
    private static int getNumberOfCrossings( Edge e1, Edge e2 )
    {
        // e1 es lineal y e2 es lineal
        if ( e1.isLinear( ) && e2.isLinear( ) )
            return GeometryUtilities.getCrossings(
                        e1.getLine( ), e2.getLine( ) ).size( );
        // e1 es lineal y e2 es arco
        else if ( e1.isLinear( ) && e2.isLinear( ) == false )
            return GeometryUtilities.getCrossings(
                        e1.getLine( ), e2.getArc( ), e2.getCenter( ) ).size( );
        // e2 es lineal y e1 es arco
        else if ( e2.isLinear( ) && e1.isLinear( ) == false )
            return GeometryUtilities.getCrossings(
                        e2.getLine( ), e1.getArc( ), e1.getCenter( ) ).size( );
        // e1 es arco y e2 es arco
        else if ( e1.isLinear( ) == false && e2.isLinear( ) == false )
            return GeometryUtilities.getCrossings(
                        e1.getArc( ), e1.getCenter( ), e2.getArc( ), e2.getCenter( ) ).size( );
        return 0;
    }

}
