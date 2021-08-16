/**
 * DegreeCounter.java
 * 26/07/2011 14:19:42
 */
package pfc.utilities.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pfc.models.Edge;
import pfc.models.Graph;
import pfc.models.Vertex;


/**
 * @author Walber Gonzalez
 *
 * UMLGraph
 * @depend - - - Graph
 * @depend - - - Edge
 * @depend - - - Vertex
 */
public class DegreeCounter
{
	/**
	 * @param g {@link Graph}
	 * @return int maximum degree of the graph g.
	 */
	public static int getMaximumDegree( Graph g )
	{
		// si el grafo esta vacio, devolvemos grado 0 aunque no es del todo cierto
		if ( g.vertices.isEmpty( ) )
			return 0;
		
		Map<Vertex, Integer> vertexDegrees = new HashMap<Vertex, Integer>( );
		for( Vertex vertex : g.vertices )
			vertexDegrees.put( vertex, 0 );
		
		for( Edge edge : g.edges )
		{
			Integer outDegrees = vertexDegrees.get( edge.from );
			if( outDegrees != null )
				vertexDegrees.put( edge.from, outDegrees + 1 );
			
			Integer inDegrees = vertexDegrees.get( edge.to );
			if( inDegrees != null )
				vertexDegrees.put( edge.to, inDegrees + 1 );
		}

		return Collections.max( vertexDegrees.values( ) );
	}
	
	/**
	 * @param g {@link Graph}
	 * @param selectedVertices {@link List}
	 * @return int maximum degree of selected vertices in graph g.
	 */
	public static int getMaximumDegree( Graph g, List<Vertex> selectedVertices )
	{
		// si selected esta vacio, devolvemos grado 0
		if ( selectedVertices.isEmpty( ) )
			return 0;
				
		Map<Vertex, Integer> vertexDegrees = new HashMap<Vertex, Integer>( );
		for( Vertex vertex : selectedVertices )
			vertexDegrees.put( vertex, 0 );
		
		for( Edge edge : g.edges )
		{
			Integer outDegrees = vertexDegrees.get( edge.from );
			if( outDegrees != null )
				vertexDegrees.put( edge.from, outDegrees + 1 );
			
			Integer inDegrees = vertexDegrees.get( edge.to );
			if( inDegrees != null )
				vertexDegrees.put( edge.to, inDegrees + 1 );
		}

		return Collections.max( vertexDegrees.values( ) );
	}
}
