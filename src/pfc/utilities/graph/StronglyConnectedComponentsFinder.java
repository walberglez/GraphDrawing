package pfc.utilities.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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
public class StronglyConnectedComponentsFinder
{
    private int                                     index       = 0;
    private final Stack<Vertex>                     stack       = new Stack<Vertex>( );
    private final Map<Vertex, Integer>              indices     = new HashMap<Vertex, Integer>( );
    private final Map<Vertex, Integer>              lowLinks    = new HashMap<Vertex, Integer>( );
    private final Map<Vertex, Boolean>              isOnStack   = new HashMap<Vertex, Boolean>( );
    private final Collection<Collection<Vertex>>    components  = new LinkedList<Collection<Vertex>>( );
    
    public Collection<Collection<Vertex>> find( Graph graph )
    {
        if( graph.vertices.isEmpty( ) )
            return this.components;
        
        for( Vertex vertex : graph.vertices )
            this.isOnStack.put( vertex, false );
        
        for( Vertex vertex : graph.vertices )
            if( !this.indices.containsKey( vertex ) )
                this.tarjansAlgorithm( vertex, graph );
        
        return this.components;
    }
    
    private void tarjansAlgorithm( Vertex from, Graph graph )
    {
        this.indices.put( from, this.index );
        this.lowLinks.put( from, this.index );
        ++this.index;
        
        this.stack.push( from );
        this.isOnStack.put( from, true );
        
        for( Edge edge : graph.getEdgesFrom( from ) )
        {
            Vertex to = edge.to;
            
            if( !this.indices.containsKey( to ) )
            {
                this.tarjansAlgorithm( to, graph );
                this.lowLinks.put( from, Math.min( this.lowLinks.get( from ), this.lowLinks.get( to ) ) );
            }
            else if( this.isOnStack.get( to ) )
                this.lowLinks.put( from, Math.min( this.lowLinks.get( from ), this.indices.get( to ) ) );
        }
        
        if( this.lowLinks.get( from ).equals( this.indices.get( from ) ) )
        {
            Vertex to;
            List<Vertex> component = new LinkedList<Vertex>( );
            
            do
            {
                to = this.stack.pop( );
                this.isOnStack.put( to, false );
                component.add( to );
            } while( to != from );
            
            this.components.add( component );
        }
    }
}
