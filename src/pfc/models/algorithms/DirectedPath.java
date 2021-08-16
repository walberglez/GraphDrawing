/**
 * DirectedPath.java
 * 14/07/2011 13:19:43
 */
package pfc.models.algorithms;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pfc.models.Edge;
import pfc.models.Vertex;


/**
 * @author Walber Gonzalez
 *
 * UMLGraph
 * @navassoc - - "*\n-vertices" Vertex
 * @navassoc - - "*\n-edges" Edge
 * @navassoc - - "1\n-start" Vertex
 * @navassoc - - "1\n-end" Vertex
 */
public class DirectedPath
{
    /**
     * Vertices
     */
    private Set<Vertex>   			vertices;
    /**
     * Edges
     */
    private Set<Edge>     			edges;
    /**
     * Map de vertices con sus aristas incidentes
     */
	private Map<Vertex, Set<Edge>>	incidences;
    /**
     * Start Vertex
     */
    private Vertex              	start;
    /**
     * End Vertex
     */
    private Vertex              	end;
    /**
     * Length
     */
    private int                		length;
    
    /**
     * Contructor del Path
     */
    public DirectedPath( )
    {
        this.vertices = new HashSet<Vertex>( );
        this.edges = new HashSet<Edge>( );
        this.incidences = new HashMap<Vertex, Set<Edge>>( );
        this.start = null;
        this.end = null;
        this.length = 0;
    }
    
    /**
     * Cada vertice debe aparecer solo dos veces excepto los vertices start y end.
     * Solo puede haber un vertice start y un vertice end.
     * El numero de vertices debe estar relacionado con el numero de aristas de la
     * siguiente forma: |V| = |E| + 1. 
     * @param vertices List<Vertex>
     * @param edges List<Edges>
     * @return boolean si el path creado es correcto.
     */
    public boolean createPath( List<Vertex> vertices, List<Edge> edges )
    {        
        // Comprobar longitud de vertices y aristas: |V| = |E| + 1 y al menos una arista.
        if ( edges.isEmpty( ) || vertices.size( ) != edges.size( ) + 1 )
            return false;
        // inicializar la lista de incidentes en cada vertice
        for ( Vertex v : vertices )
        {
        	this.incidences.put( v, new HashSet<Edge>( ) );
        }
        if ( updateEdges( edges ) == false )
        	return false;
        if ( updateVertices( ) == false )
        	return false;
        if ( traverse( ) == false )
        	return false;
        this.length = this.edges.size( );
        return true;
    }

    /**
     * Agregar las aristas a la lista comprobando que cada vertice pertenece al map de incidentes.
     * @param edges List<Edge>
     * @return boolean si se cumplen las condiciones para las aristas del camino.
     */
    private boolean updateEdges( List<Edge> edges )
    {
    	for ( Edge e : edges )
        {
            // Deben existir los dos vertices de e en la lista vertices
            if ( this.incidences.containsKey( e.from ) == false 
            		|| this.incidences.containsKey( e.to ) == false )
                return false;
            // Se agregan las aristas a su conjunto
            this.edges.add( e );
            // Se agrega la arista a la lista de incidentes de los vertices
            this.incidences.get( e.from ).add( e );
            this.incidences.get( e.to ).add( e );
        }
    	return true;
    }

    /**
     * Agregar los vertices a la lista comprobando que solo haya un vertice start y end.
     * Ademas, cada vertice interno debe tener dos vecinos.
     * @return boolean si se cumplen las condiciones para los vertices del camino.
     */
	private boolean updateVertices( )
    {
    	for ( Map.Entry<Vertex, Set<Edge>> entry : this.incidences.entrySet( ) )
        {
        	// Si este vertice tiene solo una arista incidente, es un vertice hoja
        	if ( entry.getValue( ).size( ) == 1 )
        	{
        		Edge e = entry.getValue( ).iterator( ).next( ); 
        		// Si la unica arista incidente tiene este vertice como from, es start
        		if ( entry.getKey( ).equals( e.from ) && this.start == null )
        		{
        			this.start = entry.getKey( );
        			this.vertices.add( this.start );
        		}
        		// Si la unica arista incidente tiene este vertice como to, es end
        		else if ( entry.getKey( ).equals( e.to ) && this.end == null )
        		{
        			this.end = entry.getKey( );
        			this.vertices.add( this.end );
        		}
    			// start y end estan ya asignados, error (mas de dos vertices hoja)
        		else
        			return false;
        	}
        	// Si este vertice tiene dos aristas incidentes, es un vertice interno del path
        	else if ( entry.getValue( ).size( ) == 2 )
        	{
        		this.vertices.add( entry.getKey( ) );
        	}
        	// error (vertices solo pueden ser de grado 2)
        	else
        		return false;
        }
    	return true;
    }

	/**
	 * Recorrido desde start hasta end del path.
	 * Precondition: start y end solo tienen una arista incidente.
	 * 				 todos los vertices internos tienen dos vecinos.
	 * @return boolean si el recorrido es posible.
	 */
	private boolean traverse( )
	{
		Vertex current = this.start;
		while ( current.equals( this.end ) == false )
		{
			current = getVertexFrom( current );
			// Si no hay vertice desde current, error (no se puede continuar el camino)
			if ( current == null )
				return false;
		}
		return true;
	}
	
	/**
     * @return the vertices
     */
    public Set<Vertex> getVertices( )
    {
        return this.vertices;
    }

    /**
     * @return the edges
     */
    public Set<Edge> getEdges( )
    {
        return this.edges;
    }

    /**
     * @return the start
     */
    public Vertex getStart( )
    {
        return this.start;
    }

    /**
     * @return the end
     */
    public Vertex getEnd( )
    {
        return this.end;
    }

    /**
     * @return the length
     */
    public int getLength( )
    {
        return this.length;
    }
    
    /**
 	 * @param v Vertex
 	 * @return Vertex vertice en el path a partir de v.
 	 */
 	public Vertex getVertexFrom( Vertex v )
 	{
 		for ( Iterator<Edge> it = this.incidences.get( v ).iterator( ); it.hasNext( ); )
 		{
 			Edge e = it.next( );
 			if ( v.equals( e.from ) )
 				return e.to;
 		}
 		return null;
 	}
 	
 	/**
 	 * @param v Vertex
 	 * @return Vertex vertice en el path hasta v.
 	 */
 	public Vertex getVertexTo( Vertex v )
 	{
 		for ( Iterator<Edge> it = this.incidences.get( v ).iterator( ); it.hasNext( ); )
 		{
 			Edge e = it.next( );
 			if ( v.equals( e.to ) )
 				return e.from;
 		}
 		return null;
 	}

    /**
     * Vecinos del vertice v en el path
     * @param v Vertex
     * @return Set<Vertex>
     */
    public Set<Vertex> getNeighbors( Vertex v )
    {
        Set<Vertex> neighbors = new HashSet<Vertex>( );
        for ( Iterator<Edge> it = this.incidences.get( v ).iterator( ); it.hasNext( ); )
		{
			Edge e = it.next( );
			neighbors.add( v.equals( e.from ) ? e.to : e.from );
		}
        return neighbors;
    }
    
    /**
     * Obtener el punto central del path
     * @return Point2D
     */
    public Point2D getCentralPoint( )
    {
    	if ( this.length % 2 == 0 )
    		return getCentralVertex( ).getPoint2D( );
    	else
    		return getCentralEdge( ).getHandlePoint2D();
    }
    
	/**
     * Obtener el vertice central del path
	 * @return Vertex
	 */
	private Vertex getCentralVertex( )
	{
		Vertex center = this.start;
		for ( int pos = 0; pos < ( this.length / 2 ); pos++ )
		{
			center = getVertexFrom( center );
		}
		return center;
	}

    /**
     * Obtener la arista central del path
	 * @return Edge
	 */
	private Edge getCentralEdge( )
	{
		Vertex center = getCentralVertex( );
		for ( Iterator<Edge> it = this.incidences.get( center ).iterator( ); it.hasNext( ); )
 		{
 			Edge e = it.next( );
 			if ( center.equals( e.from ) )
 				return e;
 		}
		return null;
	}

	/**
     * @param v Vertex
     * @return boolean si es un vertice start o end.
     */
    public boolean isOuterVertex( Vertex v )
    {
        return this.start == v || this.end == v;
    }

    /**
     * @param v Vertex
     * @return boolean si path contiene el vertice v.
     */
    public boolean containsVertex( Vertex v )
    {
        return this.vertices.contains( v );
    }
    
    /**
     * @param e Edge
     * @return boolean si path contiene la arista e.
     */
    public boolean containsEdge( Edge e )
    {
        return this.edges.contains( e );
    }
    
    /**
     * Vertices disjuntos: todos los vertices excepto los vertices start y end
     * no deben ser vertices de path.
     * @param path Path
     * @return Set<Vertex> vertices comunes a ambos paths.
     */
    public Set<Vertex> getCommonVertices( DirectedPath path )
    {
        Set<Vertex> commonVertices = new HashSet<Vertex>( );
        
        for ( Vertex v : this.vertices )
            if ( this.isOuterVertex( v ) == false &&
        			path.isOuterVertex( v ) == false &&
        			path.containsVertex( v ) )
                commonVertices.add( v );

        return commonVertices;
    }
    
    /**
     * Vertices disjuntos: todos los vertices excepto los vertices start y end
     * no deben ser vertices de path.
     * @param path Path
     * @return boolean si los conjuntos de vertices son disjuntos.
     */
    public boolean isVertexDisjoint( DirectedPath path )
    {        
        for ( Vertex v : this.vertices )
        {
        	if ( this.isOuterVertex( v ) == false &&
        			path.isOuterVertex( v ) == false &&
        			path.containsVertex( v ) )
        		return false;
        }
        return true;
    }
    
    /**
     * Aristas disjuntas: ninguna arista pertenece al path.
     * @param path Path
     * @return boolean si los conjuntos de aristas son disjuntos.
     */
    public boolean isEdgeDisjoint( DirectedPath path )
    {        
        for ( Edge e : this.edges )
            if ( path.containsEdge( e ) )
                return false;

        return true;
    }
    
    /**
     * Unir al path dado.
     * Precondition: paths no son edge disjoints.
     * @param path {@link DirectedPath}
     */
    public void join( DirectedPath path )
    {
    	Set<Vertex> vertices = new HashSet<Vertex>( this.vertices );
    	Set<Edge> edges = new HashSet<Edge>( this.edges );
    	
    	vertices.addAll( path.getVertices( ) );
    	edges.addAll( path.getEdges( ) );
    	
    	this.vertices = new HashSet<Vertex>( );
        this.edges = new HashSet<Edge>( );
        this.incidences = new HashMap<Vertex, Set<Edge>>( );
        this.start = null;
        this.end = null;
        this.length = 0;
        
    	this.createPath(
    			new ArrayList<Vertex>( vertices ),
    			new ArrayList<Edge>( edges ) );
    }
    
    /**
     * Seleccionar todos los elementos del camino.
     * @param selected boolean
     */
    public void setSelected( boolean selected )
    {
        for ( Vertex v : this.vertices )
            v.isSelected.set( selected );
        
        for ( Edge e : this.edges )
            e.isSelected.set( selected );
    }
}
