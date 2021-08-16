package pfc.utilities.graph;

import java.util.HashMap;
import java.util.Map;

import pfc.models.Edge;
import pfc.models.Graph;
import pfc.models.Vertex;


/**
 * 
 * @author walber
 *
 * UMLGraph
 * @depend - - - Graph
 * @depend - - - Edge
 * @depend - - - Vertex
 */
public class RoyFloydWarshallDistancesAlgorithm {

    public static double[ ][ ] getDistanceMatrix( Graph graph, boolean weighted )
    {
        double[ ][ ] distances = new double[graph.vertices.size( )][graph.vertices.size( )];
        
        // Initialize the distance matrix
        for( int i = 0; i < graph.vertices.size( ); ++i )
            for( int j = 0; j < graph.vertices.size( ); ++j )
                distances[i][j] = ( i == j ? 0.0 : Double.POSITIVE_INFINITY );
        
        // Add non-loop edges to the distance matrix
        Map<Vertex, Integer> indices = new HashMap<Vertex, Integer>( );
        for( int i = 0; i < graph.vertices.size( ); ++i )
            indices.put( graph.vertices.get( i ), i );
        
        for( Edge edge : graph.edges )
            if( !edge.isLoop )
            {
                int from = indices.get( edge.from ), to = indices.get( edge.to );
                
                if( weighted )
                {
                    if( edge.weight.get( ) < distances[from][to] )
                        distances[from][to] = edge.weight.get( );
                    
                    if( !edge.isDirected && edge.weight.get( ) < distances[to][from] )
                        distances[to][from] = edge.weight.get( );
                }
                else
                {
                    distances[from][to] = 1.0;
                    
                    if( !edge.isDirected )
                        distances[to][from] = 1.0;
                }
            }
        
        // Run the Roy-Floyd-Warshall algorithm
        for( int k = 0; k < graph.vertices.size( ); ++k )
            for( int i = 0; i < graph.vertices.size( ); ++i )
                for( int j = 0; j < graph.vertices.size( ); ++j )
                    distances[i][j] = Math.min( distances[i][j], distances[i][k] + distances[k][j] );
        
        return distances;
    }
}
