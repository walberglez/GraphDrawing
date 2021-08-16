/**
 * DigraphTester.java
 * 13/07/2011 19:52:12
 */
package pfc.utilities.graph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import pfc.models.Edge;
import pfc.models.Graph;
import pfc.models.Vertex;
import pfc.models.algorithms.Rotation;
import pfc.utilities.GeometryUtilities;

/**
 * @author walber
 * 
 * UMLGraph
 * @depend - - - Graph
 * @depend - - - Vertex
 * @depend - - - Edge
 */
public class DigraphTester
{
	/**
	 * Comprobar si es un st-graph
	 * 
	 * @param graph {@link Graph}
	 * @return {@link Boolean}
	 */
    public static boolean isSTGraph( Graph graph )
    {
        if ( graph.areCyclesAllowed || graph.areLoopsAllowed || graph.areMultipleEdgesAllowed
                || graph.areDirectedEdgesAllowed == false )
            return false;
        // grafo es debilmente conexo
//      if ( findWeaklyConnectedComponents( graph ).size() > 1 )
//          return false;
        // solo hay un source y un target
        int numberVertexS = 0;
        int numberVertexT = 0;
        for ( Vertex v : graph.vertices )
        {
            int sizeTo = graph.getEdgesTo( v ).size();
            int sizeFrom = graph.getEdgesFrom( v ).size();
            if ( sizeFrom == 0 && sizeTo != 0 )
                numberVertexT++;
            if ( sizeFrom != 0 && sizeTo == 0 )
                numberVertexS++;
            if ( numberVertexS > 1 || numberVertexT > 1 )
                return false;
        }
        return true;
    }
    
    /**
     * Precondition: Grafo debe ser ST-Graph
     * Comprobar si los vertices source y target del grafo estan en la cara externa del grafo.
     * 
     * @param graph {@link Graph}
     * @return {@link Boolean}
     */
    public static boolean areSTVerticesOnOuterFace( Graph graph )
    {
    	Vertex s = graph.getVertexSource( );
    	Vertex t = graph.getVertexTarget( );
    	Vertex current = GeometryUtilities.getTheClosestVertexToOrigin( graph );
		Vertex destination = current;
		Vertex vertexNew, vertexOld;
		boolean isSourceOnOuterFace = false;
		boolean isTargetOnOuterFace = false;
		
		vertexOld = new Vertex( 0, 0 );
		do {
			// obtener el siguiente vertice en el sentido clockwise a partir de vertexOld alrededor de current
			Rotation rot = new Rotation( current, graph.getNeighborsInOut( current ) );
			vertexNew = rot.getClockwiseVertex( vertexOld );
			
			if ( isSourceOnOuterFace == false && s.equals( current ) )
			{
				isSourceOnOuterFace = true;
			}
			if ( isTargetOnOuterFace == false && t.equals( current ) )
			{
				isTargetOnOuterFace = true;
			}
			if ( isSourceOnOuterFace && isTargetOnOuterFace )
			{
				return true;
			}
			vertexOld = current;
			current = vertexNew;
		} while ( destination.equals( current ) == false );
		
		return false;
    }
    
    /**
     * Comprobar si es un grafo reducido.
     * 
     * @param graph {@link Graph}
     * @return {@link Boolean}
     */
    public static boolean isReducedDigraph( Graph graph )
    {
    	if ( graph.areCyclesAllowed || graph.areLoopsAllowed || graph.areMultipleEdgesAllowed
                || graph.areDirectedEdgesAllowed == false )
            return false;
    	
    	for ( Edge e : graph.edges )
    	{
    		if ( isTransitiveEdge( graph, e ) )
    			return false;
    	}
    	return true;
    }

	/**
	 * Comprobar si la arista es transitiva.
	 * 
	 * @param graph {@link Graph}
	 * @param e {@link Edge}
	 * @return {@link Boolean}
	 */
	public static boolean isTransitiveEdge( Graph graph, Edge e )
	{
		Graph tmp = new Graph( graph.toString( ) );
		tmp.edges.remove( e );
		return areConnected( tmp, e.from, e.to );
	}
	
	/**
	 * Returns a {@code boolean} indicating whether or not there exists a path between the two vertices.
	 * 
	 * @param graph {@link Graph}
	 * @param from {@link Vertex} the vertex from which the path begins
	 * @param to {@link Vertex} the vertex at which the path ends
	 * @return {@code true} if there exists a path between the two vertices in the specified direction, {@code false} otherwise
	 * @see Vertex
	 * @see Edge
	 */
	public static boolean areConnected( Graph graph, Vertex from, Vertex to )
	{
		Set<Vertex> visited = new HashSet<Vertex>( );
		Queue<Vertex> toVisit = new LinkedList<Vertex>( );
		
		toVisit.add( from );
		
		while( toVisit.isEmpty( ) == false )
		{
			Vertex vertex = toVisit.poll( );
			if( to.equals( vertex ) )
				return true;
			visited.add( vertex );
			
			Set<Vertex> neighbors = graph.getNeighbors( vertex );
			
			for( Vertex neighbor : neighbors )
			{
				if( to.equals( neighbor ) )
					return true;
				if ( visited.contains( neighbor ) == false )
					toVisit.add( neighbor );
			}
		}
		
		return false;
	}
}
