/**
 * GraphUtilities.java
 */
package pfc.utilities;

import java.util.*;

import pfc.models.*;
import pfc.utilities.graph.*;


/**
 * @author Cameron Behar
 * @author walber
 *
 * UMLGraph
 * @depend - - - DepthFirstSearchFinder
 * @depend - - - BipolarOrientation
 * @depend - - - StronglyConnectedComponentsFinder
 * @depend - - - WeaklyConnectedComponentsFinder
 * @depend - - - BiconnectedComponentsFinder
 * @depend - - - STGraphTester
 * @depend - - - PlanarEmbeddingTester
 * @depend - - - DegreeCounter
 * @depend - - - RoyFloydWarshallDistancesAlgorithm
 */
public class GraphUtilities
{
	public static Collection<Vertex> getVerticesDepthFirstSearch ( Vertex v, Graph graph )
	{
		return new DepthFirstSearchFinder( ).depthFirstSearch( v, graph );
	}
	
	public static Collection<Edge> getEdgesSpanningTree ( Vertex v, Graph graph )
	{
		return new DepthFirstSearchFinder( ).spanningTree( v, graph );
	}
	
	public static Map<Float, Vertex> getTopologicalSorting ( Vertex v, Graph graph )
	{
		return new DepthFirstSearchFinder( ).topologicalSorting( v, graph );
	}
	
	public static Map<Vertex, Float> getTopologicalNumbering ( Vertex v, Graph graph )
	{
		return new DepthFirstSearchFinder( ).topologicalNumbering( v, graph );
	}
	
	public static Map<Vertex, Float> getTopologicalNumbering ( Vertex v, Graph graph, float seed, float weight )
	{
		return new DepthFirstSearchFinder( seed, weight ).topologicalNumbering( v, graph );
	}
	
	public static Graph getDirectedPlanarGraph ( Graph graph )
	{
	    return new BipolarOrientationTarjanAlgorithm( graph ).orientPlanarGraph();
	}

	public static Collection<Collection<Vertex>> findStronglyConnectedComponents( Graph graph )
	{
		return ( graph.areDirectedEdgesAllowed ? new StronglyConnectedComponentsFinder( ).find( graph ) : new WeaklyConnectedComponentsFinder( ).find( graph ) );
	}
	
	public static Collection<Collection<Vertex>> findWeaklyConnectedComponents( Graph graph )
	{
		return new WeaklyConnectedComponentsFinder( ).find( graph );
	}
	
	public static Set<Set<Vertex>> findBiconnectedComponents( Graph graph )
	{
	    return new BiconnectedComponentsFinder( ).getBiconnectedComponents( graph );
	}
	
	public static Set<Vertex> findBiconnectedCutPoints( Graph graph )
    {
        return new BiconnectedComponentsFinder( ).getCutPoints( graph );
    }
	
	public static boolean isBiconnected( Graph graph )
	{
	    return new BiconnectedComponentsFinder( ).isBiconnected( graph );
	}
	
	public static boolean isSTGraph( Graph graph )
    {
	    return DigraphTester.isSTGraph( graph );
    }

	public static boolean areSTOnOuterFace( Graph graph )
    {
	    return DigraphTester.areSTVerticesOnOuterFace( graph );
    }
	
	public static boolean isReducedDigraph( Graph graph )
    {
	    return DigraphTester.isReducedDigraph( graph );
    }
	
	public static boolean isTransitiveEdge( Graph graph, Edge e )
    {
	    return DigraphTester.isTransitiveEdge( graph, e );
    }
	
	public static boolean isPlanarEmbedding( Graph graph )
	{
	    return PlanarEmbeddingTester.isPlanarEmbedding( graph );
	}
	
	public static int getMaximumDegree( Graph g )
	{
		return DegreeCounter.getMaximumDegree( g );
	}
	
	public static int getMaximumDegree( Graph g, List<Vertex> selected )
	{
		return DegreeCounter.getMaximumDegree( g, selected );
	}
	
	public static double[ ][ ] getDistanceMatrix( Graph graph, boolean weighted )
	{
		return RoyFloydWarshallDistancesAlgorithm.getDistanceMatrix( graph, weighted );
	}

}
