package pfc.utilities.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import pfc.models.Edge;
import pfc.models.Graph;
import pfc.models.Vertex;


/**
 * Clase para determinar la ordenacion topologica y la numeracion topologica
 * de un grafo. Se utiliza la busqueda en profundidad, por lo que se puede determinar
 * el spanning tree que resulta de la ejecucion de este algoritmo.
 * La complejidad de todos los metodos de esta clase es: O(|V|+|E|).
 * @author walber
 *
 * UMLGraph
 * @navassoc - - "*\n-verticesExplored" Vertex
 * @navassoc - - "*\n-edgesExplored" Edge
 * @navassoc - - "*\n-topologicalSorting" Vertex
 * @navassoc - - "1\n-target" Vertex
 * @depend - - - Edge
 * @depend - - - Graph
 */
public class DepthFirstSearchFinder
{
    private final Collection<Vertex> 	verticesExplored = new ArrayList<Vertex>( );
    private final Collection<Edge>		edgesExplored = new ArrayList<Edge>( );
    private final Map<Float, Vertex> 	topologicalSorting = new TreeMap<Float, Vertex>( );
    private final Map<Vertex, Float> 	topologicalNumbering = new HashMap<Vertex, Float>( );
    private Float 						order = new Float( 0 );
    private final Float 				seed;
    private final Float 				weight;
    
    /**
	 * Crear objeto con seed igual a 0 y weight igual a 1 en la numeracion topologica. 
	 */
	public DepthFirstSearchFinder( )
	{
		this.seed = new Float( 0 );
		this.weight = new Float( 1 );
	}
	
    /**
	 * @param seed float valor asignado al vertice source del grafo en la numeracion topologica.
	 * @param weight float peso asignado a cada arista en la numeracion topologica.
	 */
	public DepthFirstSearchFinder( float seed, float weight )
	{
		this.seed = new Float( seed );
		this.weight = new Float( weight );
	}

	/**
     * Complejidad: O(|V|+|E|)
     * @param v Vertex
     * @param graph Graph
     * @return busqueda en profundidad
     */
    public Collection<Vertex> depthFirstSearch ( Vertex v, Graph graph )
    {
        if ( this.verticesExplored.isEmpty( ) )
            depthFirstSearchAlgorithm( v, graph );
        return this.verticesExplored;
    }
    
    /**
     * Complejidad: O(|V|+|E|)
     * @param v Vertex
     * @param graph Graph
     * @return spanning tree
     */
    public Collection<Edge> spanningTree ( Vertex v, Graph graph )
    {
        if ( this.edgesExplored.isEmpty( ) )
            depthFirstSearchAlgorithm( v, graph );
        return this.edgesExplored;
    }
    
    /**
     * Complejidad: O(|V|+|E|)
     * @param v Vertex
     * @param graph Graph
     * @return ordenacion topologica de los vertices del grafo
     * 
     * {@link "http://en.wikipedia.org/wiki/Topological_sorting"}
     */
    public Map<Float, Vertex> topologicalSorting ( Vertex v, Graph graph )
    {
        this.order = ( graph.vertices.size( ) * this.weight ) + this.seed;
        this.depthFirstSearch( v, graph );
        for ( Vertex n : graph.vertices )
            if ( this.verticesExplored.contains( n ) == false )
                this.depthFirstSearch( n, graph );
        
        return this.topologicalSorting;
    }
    
    /**
     * Complejidad: O(|V|+|E|)
     * @param v Vertex
     * @param graph Graph
     * @return numeracion topologica de los vertices del grafo
     * 
     * {@link "http://en.wikipedia.org/wiki/Longest_path_problem"}
     */
    public Map<Vertex, Float> topologicalNumbering ( Vertex v, Graph graph )
    {
        Float numberU;
    	// inicializar a cero toda la numeracion de los vertices
        for ( Vertex w : graph.vertices )
        {
            this.topologicalNumbering.put( w, this.seed );
        }
        // realizar la ordenacion topologica
        this.topologicalSorting( v, graph );
        // actualizar la numeracion de cada vertice y sus vecinos
        for ( Vertex u : this.topologicalSorting.values( ) )
        {
            numberU = this.topologicalNumbering.get( u );
        	for ( Vertex w : graph.getNeighbors( u ) )
            {
                if ( this.topologicalNumbering.get( w ) <= numberU )
                    this.topologicalNumbering.put( w, numberU + this.weight );
            }
        }
        return this.topologicalNumbering;
    }
    
    /**
     * Busqueda en profundidad modificada para marcar en postorden el vertice visitado.
     * Complejidad: O(|V|+|E|)
     * @param v Vertex
     * @param graph Graph
     */
    private void depthFirstSearchAlgorithm ( Vertex v, Graph graph )
    {
        verticesExplored.add( v );
        for ( Vertex n : graph.getNeighbors( v ) )
        {
            if ( this.verticesExplored.contains( n ) == false )
            {
                Edge e = ( Edge ) graph.getEdges( v, n ).toArray( )[ 0 ];
                edgesExplored.add( e );
                depthFirstSearchAlgorithm( n, graph );
            }
        }
        this.order -= this.weight;
        this.topologicalSorting.put( order, v );
    }
}
