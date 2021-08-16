/**
 * NonIntersectingPaths.java
 * 14/07/2011 13:21:58
 */
package pfc.models.algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import pfc.models.Edge;
import pfc.models.Vertex;


/**
 * @author Walber Gonzalez
 *
 * UMLGraph
 * @depend - - - DirectedPath
 * @depend - - - Edge
 * @depend - - - Vertex
 * @depend - - - Rotation
 */
public class NonIntersectingPathList extends ArrayList<DirectedPath>
{
    private static final long serialVersionUID = -3421654400996922589L;

    public NonIntersectingPathList( )
    {
        super( );
    }
    
    /**
     * Agregar un nuevo path a la lista comprobando
     * si no se intercepta con los otros paths ya creados.
     * @param vertices List<Vertex>
     * @param edges List<Edge>
     * @return boolean si se ha agregado el nuevo path
     */
    public boolean add( List<Vertex> vertices, List<Edge> edges )
    {
        DirectedPath path = new DirectedPath( );
        if ( path.createPath( vertices, edges ) == false )
            return false;
        
        if ( isPathNonIntersecting( path ) == false )
            return false;
        
        return super.add( path );
    }
    
    /**
     * Si los paths son internal vertex disjoint dos a dos.
     * @return boolean
     */
    public boolean areVertexDisjoint( )
    {
    	for ( Iterator<DirectedPath> it1 = this.iterator( ); it1.hasNext( ); )
    	{
    		DirectedPath p1 = it1.next( );
    		for ( Iterator<DirectedPath> it2 = this.iterator( ); it2.hasNext( ); )
        	{
    			DirectedPath p2 = it2.next( );
    			if ( p1 != p2 && areVertexDisjointPaths( p1, p2 ) == false)
    				return false;
        	}
    	}
    	return true;
    }
    
    /**
     * Comprobar si algun camino contiene la arista dada.
     * @param e Edge
     * @return boolean
     */
    public boolean containsEdge( Edge e )
    {
    	for ( Iterator<DirectedPath> it = this.iterator( ); it.hasNext( ); )
    	{
    		if ( it.next( ).containsEdge( e ) )
    			return true;
    	}
    	return false;
    }
    
    /**
     * Comprobar si algun camino contiene el vertice dado.
     * @param v Vertex
     * @return boolean
     */
    public boolean containsVertex( Vertex v )
    {
    	for ( Iterator<DirectedPath> it = this.iterator( ); it.hasNext( ); )
    	{
    		if ( it.next( ).containsVertex( v ) )
    			return true;
    	}
    	return false;
    }
    
    /**
     * Obtener todos los paths a los que pertenece el vertice v.
     * @param v Vertex
     * @return all Set<Vertex>
     */
    public Set<DirectedPath> getAll( Vertex v )
    {
    	Set<DirectedPath> all = new HashSet<DirectedPath>( );
    	for ( Iterator<DirectedPath> it = this.iterator( ); it.hasNext( ); )
        {
            DirectedPath p = it.next( );
            if ( p.containsVertex( v ) )
                all.add( p );
        }
        return all;
    }
    
    /**
     * Obtener el path de a los que pertenece el vertice v con mayor distancia.
     * @param v {@link Vertex}
     * @return {@link DirectedPath}
     */
    public DirectedPath getLongestPath( Vertex v )
    {
    	DirectedPath longest = null;
    	Set<DirectedPath> all = this.getAll( v );
    	for ( Iterator<DirectedPath> it = all.iterator( ); it.hasNext( ); )
    	{
    		DirectedPath p = it.next( );
    		if ( longest == null || p.getLength( ) > longest.getLength( ) )
    			longest = p;
    	}
    	return longest;
    }
    
    /**
     * Obtener el path al que pertenece la arista dada.
     * @param e Edge
     * @return Path
     */
    public DirectedPath get( Edge e )
    {
    	for ( Iterator<DirectedPath> it = this.iterator( ); it.hasNext( ); )
    	{
    		DirectedPath p = it.next( );
    		if ( p.containsEdge( e ) )
    			return p;
    	}
    	return null;
    }
    
    /**
     * Eliminar todos los caminos a los que pertenece el vertice dado.
     * @param v Vertex
     * @return boolean si se elimino algun camino.
     */
    public boolean removeVertex( Vertex v )
    {
        boolean modified = false;
        for ( Iterator<DirectedPath> it = this.iterator( ); it.hasNext( ); )
        {
            DirectedPath p = it.next( );
            if ( p.containsVertex( v ) )
            {
                it.remove( );
                modified = true;
            }
        }
        return modified;
    }

    /**
     * Eliminar todos los caminos a los que pertenece la arista dada.
     * @param e Edge
     * @return boolean si se elimino algun camino.
     */
    public boolean removeEdge( Edge e )
    {
        boolean modified = false;
        for ( Iterator<DirectedPath> it = this.iterator( ); it.hasNext( ); )
        {
            DirectedPath p = it.next( );
            if ( p.containsEdge( e ) )
            {
                it.remove( );
                modified = true;
            }
        }
        return modified;
    }
    
    /**
     * Eliminar todos los caminos a los que pertenecen los vertices dados en la lista.
     * @param vertices List
     * @return boolean si se elimino algun camino.
     */
    public boolean removeAllVertices( List<Vertex> vertices )
    {
        boolean modified = false;
        for ( Vertex v : vertices )
        {
            modified = modified || removeVertex( v );
        }
        return modified;
    }

    /**
     * Eliminar todos los caminos a los que pertenecen las aristas dadas en la lista.
     * @param edges List
     * @return boolean si se elimino algun camino.
     */
    public boolean removeAllEdges( List<Edge> edges )
    {
        boolean modified = false;
        for ( Edge e : edges )
        {
            modified = modified || removeEdge( e );
        }
        return modified;
    }

    /**
     * Seleccionar todos los caminos.
     * @param selected boolean
     */
    public void setSelected( boolean selected )
    {
        for ( Iterator<DirectedPath> it = this.iterator( ); it.hasNext( ); )
            it.next( ).setSelected( selected );
    }
    
    /**
     * @param path Path
     * @return boolean si el path no se intercepta con los demas.
     */
    private boolean isPathNonIntersecting( DirectedPath path )
    {
        for ( Iterator<DirectedPath> it = this.iterator( ); it.hasNext( ); )
            if ( areNonIntersectingPaths( path, it.next( ) ) == false )
                return false;
        
        return true;
    }

    /**
     * NonIntersecting: aristas disjuntas y las aristas alrededor
     * de un vertice comun no se cruzan.
     * @param path1 Path
     * @param path2 Path
     * @return boolean si los dos paths no se interceptan.
     */
    private static boolean areNonIntersectingPaths( DirectedPath path1, DirectedPath path2 )
    {
        Set<Vertex> commonVertices = path1.getCommonVertices( path2 );
        // Si los paths son vertices disjuntos y no son un path=1, son non-intersecting
        if ( commonVertices.isEmpty( ) && path1.getLength( ) > 1 && path2.getLength( ) > 1 )
            return true;
        // Si los paths no son aristas disjuntos, no son non-intersecting
        if ( path1.isEdgeDisjoint( path2 ) == false )
            return false;
        // Si las aristas alrededor de un vertice comun de los paths se cruzan, no son non-intersecting
        for ( Vertex v : commonVertices )
            if ( areCrossedAtCommonVertex( path1, path2, v ) )
                return false;

        return true;
    }

    /**
     * Si los paths son vertices disjuntos
     * @param path1 DirectedPath
     * @param path2 DirectedPath
     * @return boolean
     */
    private static boolean areVertexDisjointPaths( DirectedPath path1, DirectedPath path2 )
    {
        return path1.isVertexDisjoint( path2 );
    }
    
    /**
     * Path estan cruzados en un vertice comun si los cuatro vertices
     * de los dos paths (v1 y v3 del path1, v2 y v4 del path2) tienen
     * el orden v1->v2->v2->v4 alrededor del vertice comun v
     * en el sentido clockwise. 
     * Precondition: v es un vertice interno del camino.  
     * @param path1 Path
     * @param path2 Path
     * @param v Vertex
     * @return boolean si los paths se cruzan alrededor del vertice v.
     */
    private static boolean areCrossedAtCommonVertex(
            DirectedPath path1, DirectedPath path2, Vertex v )
    {
        Set<Vertex> neighborsP1 = path1.getNeighbors( v );
        Set<Vertex> neighborsP2 = path2.getNeighbors( v );
        Set<Vertex> neighbors = new HashSet<Vertex>( );
        
        neighbors.addAll( neighborsP1 );
        neighbors.addAll( neighborsP2 );
        
        Rotation rot = new Rotation( v , neighbors );
        
        for ( Vertex v1 : neighborsP1 )
            if ( neighborsP2.contains( rot.getClockwiseVertex( v1 ) ) == false )
                return false;

        return true;
    }
}