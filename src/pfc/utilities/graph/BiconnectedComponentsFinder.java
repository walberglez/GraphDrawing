package pfc.utilities.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import pfc.models.Edge;
import pfc.models.Graph;
import pfc.models.Vertex;


/**
 * Finds all biconnected components (bicomponents) of an undirected graph.  
 * A graph is a biconnected component if 
 * at least 2 vertices must be removed in order to disconnect the graph.  (Graphs 
 * consisting of one vertex, or of two connected vertices, are also biconnected.)  Biconnected
 * components of three or more vertices have the property that every pair of vertices in the component
 * are connected by two or more vertex-disjoint paths.
 * <p>
 * Running time: O(|V| + |E|) where |V| is the number of vertices and |E| is the number of edges
 * @see "Depth first search and linear graph algorithms by R. E. Tarjan (1972), SIAM J. Comp."
 * 
 * UMLGraph
 * @depend - - - Graph
 * @depend - - - Edge
 * @depend - - - Vertex
 */
public class BiconnectedComponentsFinder
{
    private final Map<Vertex, Integer> dfs_num = new HashMap<Vertex, Integer>( );
    private final Map<Vertex, Integer> high = new HashMap<Vertex, Integer>( );
    private final Map<Vertex, Vertex>  parents = new HashMap<Vertex, Vertex>( );
    private final Set<Set<Vertex>>     bicomponents = new LinkedHashSet<Set<Vertex>>( );
    private final Set<Vertex>          cut_points = new HashSet<Vertex>( );
    private final Stack<Edge>          stack = new Stack<Edge>( );
    private int                        converse_depth;

    /**
     * @param graph Graph
     * @return Boolean whether the given graph is biconnected
     */
    public boolean isBiconnected( Graph graph )
    {
        if ( cut_points.isEmpty( ) )
            find( graph );
        //cut_points.isEmpty( ) && 
        return bicomponents.size( ) == 1;
    }
    
    /**
     * @param graph the graph whose cutpoints are to be extracted
     * @return the {@code Set} of cutpoints
     */
    public Set<Vertex> getCutPoints( Graph graph )
    {
        // TODO falla cutpoints
        if ( cut_points.isEmpty( ) )
            find( graph );
        return cut_points;
    }
    
    /**
     * @param graph the graph whose bicomponents are to be extracted
     * @return the {@code Set} of bicomponents
     */
    public Set<Set<Vertex>> getBiconnectedComponents( Graph graph )
    {
        if ( bicomponents.isEmpty( ) )
            find( graph );
        return bicomponents;
    }
    
    /**
     * Extracts the bicomponents and cutpoints from the graph.
     * @param graph the graph whose bicomponents are to be extracted
     */ 
    public void find( Graph graph ) {
        if ( graph.vertices.isEmpty( ) )
            return;

        // initialize DFS number for each vertex to 0
        for ( Vertex v : graph.vertices )
            dfs_num.put( v, 0 );

        for ( Vertex v : graph.vertices )
        {
            if ( dfs_num.get( v ) == 0 ) // if we haven't hit this vertex yet...
            {
                converse_depth = graph.vertices.size( );
                // find the biconnected components for this subgraph, starting from v
                findBiconnectedComponents( graph, v, bicomponents );

                // if we only visited one vertex, this method won't have
                // ID'd it as a biconnected component, so mark it as one
                if ( graph.vertices.size() - converse_depth == 1 )
                {
                    Set<Vertex> s = new HashSet<Vertex>( );
                    s.add( v );
                    bicomponents.add( s );
                }
            }
        }
    }

    /**
     * <p>Stores, in <code>bicomponents</code>, all the biconnected
     * components that are reachable from <code>v</code>.</p>
     * 
     * <p>The algorithm basically proceeds as follows: do a depth-first
     * traversal starting from <code>v</code>, marking each vertex with
     * a value that indicates the order in which it was encountered (dfs_num), 
     * and with
     * a value that indicates the highest point in the DFS tree that is known
     * to be reachable from this vertex using non-DFS edges (high).  (Since it
     * is measured on non-DFS edges, "high" tells you how far back in the DFS
     * tree you can reach by two distinct paths, hence biconnectivity.) 
     * Each time a new vertex w is encountered, push the edge just traversed
     * on a stack, and call this method recursively.  If w.high is no greater than
     * v.dfs_num, then the contents of the stack down to (v,w) is a 
     * biconnected component (and v is an articulation point, that is, a 
     * component boundary).  In either case, set v.high to max(v.high, w.high), 
     * and continue.  If w has already been encountered but is 
     * not v's parent, set v.high max(v.high, w.dfs_num) and continue. 
     * 
     * <p>(In case anyone cares, the version of this algorithm on p. 224 of 
     * Udi Manber's "Introduction to Algorithms: A Creative Approach" seems to be
     * wrong: the stack should be initialized outside this method, 
     * (v,w) should only be put on the stack if w hasn't been seen already,
     * and there's no real benefit to putting v on the stack separately: just
     * check for (v,w) on the stack rather than v.  Had I known this, I could
     * have saved myself a few days.  JRTOM)</p>
     * 
     */
    private void findBiconnectedComponents( Graph graph, Vertex v, Set<Set<Vertex>> bicomponents )
    {
        int v_dfs_num = converse_depth;
        dfs_num.put( v, v_dfs_num );
        converse_depth--;
        high.put( v, v_dfs_num );

        for ( Edge vw : graph.getEdges( v ) )
        {
            Vertex w = vw.from == v ? vw.to : vw.from; 
            int w_dfs_num = dfs_num.get( w );//get(w, dfs_num);
            if (w_dfs_num == 0) // w hasn't yet been visited
            {
                parents.put( w, v ); // v is w's parent in the DFS tree
                stack.push( vw );
                findBiconnectedComponents( graph, w, bicomponents );
                int w_high = high.get( w );//get(w, high);
                if ( w_high <= v_dfs_num )
                {
                    // v disconnects w from the rest of the graph,
                    // i.e., v is an articulation point
                    // thus, everything between the top of the stack and
                    // v is part of a single biconnected component
                    cut_points.add( v );
                    Set<Vertex> bicomponent = new HashSet<Vertex>( );
                    Edge e;
                    do
                    {
                        e = stack.pop( );
                        bicomponent.add( e.from );
                        bicomponent.add( e.to );
                    } while ( e != vw );
                    bicomponents.add( bicomponent );
                }
                high.put( v, Math.max( w_high, high.get( v ) ) );
            }
            else if ( w != parents.get( v ) ) // (v,w) is a back or a forward edge
                high.put( v, Math.max( w_dfs_num, high.get( v ) ) );
        }
    }
}
