/**
 * BipolarOrientationTarjanAlgorithm.java
 * 11/07/2011 18:26:37
 */
package pfc.utilities.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pfc.models.Edge;
import pfc.models.Graph;
import pfc.models.Vertex;
import pfc.settings.UserSettings;
import pfc.utilities.GeometryUtilities;


/**
 * Complejidad: O(|V|+|E|)
 * Grafo debe ser biconexo.
 * @see "Two streamlined depth-first search algorithms by R. Tarjan (1986)"
 * @author walber
 * 
 * UMLGraph
 * @navassoc - - "1\n-graph" Graph
 * @navassoc - - "1\n-directed" Graph
 * @navassoc - - "1\n-source" Vertex
 * @navassoc - - "1\n-target" Vertex
 * @depend - - - Edge
 * @depend - - - GeometryUtilities
 * @depend - - - UserSettings
 */
public class BipolarOrientationTarjanAlgorithm
{
    private final Graph                 graph;
    private Graph                       directed;
    private final Vertex                source;
    private final Vertex                target;
    private final Map<Vertex, Integer>  pre_dfs_num = new HashMap<Vertex, Integer>( );
    private final Map<Vertex, Vertex>   low         = new HashMap<Vertex, Vertex>( );
    private final Map<Vertex, Vertex>   parents     = new HashMap<Vertex, Vertex>( );
    private final Map<Vertex, Boolean>  sign        = new HashMap<Vertex, Boolean>( );
    private final List<Vertex>          listL       = new ArrayList<Vertex>( );
    private final Deque<Vertex>         preorder    = new ArrayDeque<Vertex>( );
    private int                         current     = 0;
    private final boolean               plus        = true;
    private final boolean               minus       = false;

    /**
     * @param graph Graph
     */
    public BipolarOrientationTarjanAlgorithm( Graph graph )
    {
        this.graph = graph;
        // vertice source es el mas alejado del origen de coordenadas
        source = GeometryUtilities.getTheFarthestVertexToOrigin( this.graph );
        // vertice target es el mas proximo al origen de coordenadas
        target = GeometryUtilities.getTheClosestVertexToOrigin( this.graph );
    }
            
    /**
     * Convierte las aristas del grafo planar no dirigido en dirigidas.
     * PRECONDICION: grafo debe ser no dirigido y planar.
     * POSTCONDICION: grafo es dirigido sin ciclos, sin aristas multiples y sin bucles.
     * @return grafo dirigido Graph
     */
    public Graph orientPlanarGraph( )
    {
        directed = new Graph( UserSettings.instance.defaultGraphName.get( ), false, true, false, false );
        for ( Vertex v : graph.vertices )
        {
            // agregar vertice al nuevo grafo dirigido
            directed.vertices.add( v );
            // inicializar pre_dfs_num
            pre_dfs_num.put( v, 0 );
        }
        current = 1;
        pre_dfs_num.put( source, current );
        dfs( target );
        listL.add( source );
        listL.add( target );
        sign.put( source, minus );
        while ( preorder.isEmpty( ) == false )
        {
            Vertex v = preorder.pop( );
            if ( v != source && v != target )
            {
                Vertex pV = parents.get( v );
                if ( sign.get( low.get( v ) ) == minus )
                {
                    listL.add( listL.indexOf( pV ), v );
                    sign.put( pV, plus );
                }
                else if ( sign.get( low.get( v ) ) == plus )
                {
                    listL.add( listL.indexOf( pV ) + 1, v );
                    sign.put( pV, minus );
                }
            }
        }
        // agregar las aristas dirigidas
        for ( Edge e : graph.edges )
        {
            int numberVertexFrom = listL.indexOf( e.from );
            int numberVertexTo = listL.indexOf( e.to );
            Edge directedEdge;
            if ( numberVertexFrom < numberVertexTo )
                directedEdge = new Edge( true, e.from, e.to );
            else
                directedEdge = new Edge( true, e.to, e.from );
            directed.edges.add( directedEdge );
        }
        return directed;
    }
    
    private void dfs( Vertex v )
    {
        pre_dfs_num.put( v, ++current );
        low.put( v, v );
        for ( Vertex w : graph.getNeighbors( v ) )
        {
            if ( pre_dfs_num.get( w ) == 0 )
            {
                dfs( w );
                parents.put( w, v );
                preorder.push( w );
                if ( pre_dfs_num.get( low.get( w ) ) < pre_dfs_num.get( low.get( v ) ) )
                    low.put(v, low.get( w ) );
            }
            else if ( ( pre_dfs_num.get( w ) != 0 )
                    && ( pre_dfs_num.get( w ) < pre_dfs_num.get( low.get( v ) ) ) )
                low.put( v, w );
        }
    }
     
}
