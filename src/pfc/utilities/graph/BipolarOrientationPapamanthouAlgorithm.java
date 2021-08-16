package pfc.utilities.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pfc.models.Edge;
import pfc.models.Graph;
import pfc.models.Vertex;
import pfc.settings.UserSettings;
import pfc.utilities.GeometryUtilities;


/**
 * Complejidad: O(|V|*|E|)
 * @see "Parameterized st-Orientations of Graphs: Algorithms and Experiments by C. Papamanthou and I. G. Tollis (2007)"
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
public class BipolarOrientationPapamanthouAlgorithm
{
    private final Graph                graph;
    private Graph                      directed;
    private final Vertex               source;
    private final Vertex               target;
    private Integer                    counter = 0;
    private final int                  n;
    private final Set<Vertex>          setQ = new HashSet<Vertex>( );
    private final Map<Vertex, Integer> timestamp = new HashMap<Vertex, Integer>( );
    private final Map<Vertex, Integer> stNumbering = new HashMap<Vertex, Integer>( );
    private Map<Vertex, Set<Vertex>>   leafBlocks;

    /**
     * @param graph Graph
     */
    public BipolarOrientationPapamanthouAlgorithm( Graph graph )
    {
        this.graph = new Graph( graph.toString( ) );
        // vertice source es el mas alejado del origen de coordenadas
        source = GeometryUtilities.getTheFarthestVertexToOrigin( this.graph );
        // vertice target es el mas proximo al origen de coordenadas
        target = GeometryUtilities.getTheClosestVertexToOrigin( this.graph );
        // numero de vertices iniciales de G
        n = this.graph.vertices.size( );
    }
            
    /**
     * Convierte las aristas del grafo planar no dirigido en dirigidas.
     * PRECONDICION: grafo debe ser no dirigido y planar.
     * POSTCONDICION: grafo es dirigido sin ciclos, sin aristas multiples y sin bucles.
     * @return grafo dirigido Graph
     */
    public Graph orientPlanarGraph( )
    {
        // initialize F (line 1)
        directed = new Graph( UserSettings.instance.defaultGraphName.get( ), false, true, false, false );
        // agregar los vertices al nuevo grafo dirigido
        for ( Vertex v : graph.vertices )
        {
            directed.vertices.add( v );
        }
        // initialize timestamp m(i) (line 2)
        for ( Vertex v: this.graph.vertices )
        {
            timestamp.put( v, 0 );
        }
        // initialize a counter j (line 3)
        counter = 0;
        // insert s into Q (line 4)
        setQ.add( source );
        // call recursive algorithm (line 5)
        stOrientationRecursive( source );
        return directed;
    }
    
    private void stOrientationRecursive( Vertex v )
    {
        counter++;
        // set st-numbering of v
        stNumbering.put( v, counter );
        for ( Edge e : graph.getEdges( v ) )
        {
            // add a new directed edge (v,n) to F
            Vertex to = (v == e.from ? e.to : e.from);
            directed.edges.add( new Edge( true, v, to ) );
        }
        // get neighbors of vertex v
        Set<Vertex> neighborsV = graph.getNeighbors( v );
        // remove v from G and remove edges of v too
        graph.vertices.remove( v );
        // remove vertex target from neighbors if it's there
        neighborsV.remove( target );
        // add neighbors of vertex v to Q
        setQ.addAll( neighborsV );
        // remove vertex v from Q if it's there
        setQ.remove( v );
        // set timestamp of neighbors of vertex v
        for ( Vertex n : neighborsV )
        {
            timestamp.put( n, counter );
        }
        if ( setQ.isEmpty( ) )
        {
            // set st-numbering of vertex target and end
            stNumbering.put( target, n );
            return;
        }
        else
        {
            // update the block-cutpoint tree
            updateBlocksCutPointsTree( );
            for ( Vertex h : leafBlocks.keySet() )
            {
                Set<Vertex> nextSources = leafBlocks.get( h );
                nextSources.remove( h );
                nextSources.retainAll( setQ );
                stOrientationRecursive( chooseNextSource( nextSources ) );
            }
        }
    }

    /**
     * the final st-oriented graph has relatively small longest path length
     * MIN-STN : {v -> Q : m(v) = min{m(i) : i -> Q}}
     * @param nextSources Set
     * @return Vertex
     */
    private Vertex chooseNextSource(Set<Vertex> nextSources)
    {
        Vertex nextSource = null;
        for ( Vertex v : nextSources )
            if ( nextSource == null || timestamp.get( v ) < timestamp.get( nextSource ) )
                nextSource = v;

        return nextSource;
    }

    /**
     * Crea la estructura del arbol de bloques componentes biconexas y vertices corte.
     * @param graph Graph
     */
    private void updateBlocksCutPointsTree( )
    {
        BiconnectedComponentsFinder biFinder = new BiconnectedComponentsFinder( );
        Set<Set<Vertex>> bicomponents = biFinder.getBiconnectedComponents( this.graph );
        Set<Vertex> cut_points = biFinder.getCutPoints( this.graph );
        leafBlocks = new HashMap<Vertex, Set<Vertex>>( );
        Vertex cutpointBlock = null;
        
        for ( Set<Vertex> component : bicomponents )
        {
            int degree = 0;
            // block that contains vertex target can not be a leaf block
            if ( cut_points.contains( target ) || component.contains( target ) == false )
            {
                for ( Vertex cutpoint : cut_points )
                {
                    if ( component.contains( cutpoint ) )
                    {
                        degree++;
                        cutpointBlock = cutpoint;
                    }
                    if ( degree > 1 )
                        break;
                }
            }
            // leaf block
            if ( degree == 1 )
                leafBlocks.put( cutpointBlock, component );
        }
    }
}
