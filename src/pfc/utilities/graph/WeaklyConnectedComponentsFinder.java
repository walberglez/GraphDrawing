package pfc.utilities.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

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
public class WeaklyConnectedComponentsFinder
{
    private static class Node
    {
        public Node     parent  = null;
        public Vertex   vertex;
        public int      rank    = 0;
        
        public Node( Vertex vertex )
        {
            this.vertex = vertex;
        }
    }
    
    private final Random    random  = new Random( );
    
    public Collection<Collection<Vertex>> find( Graph graph )
    {
        if( graph.vertices.isEmpty( ) )
            return new LinkedList<Collection<Vertex>>( );
        
        Map<Vertex, Node> vertexNodes = new HashMap<Vertex, Node>( );
        
        for( Vertex vertex : graph.vertices )
            vertexNodes.put( vertex, new Node( vertex ) );
        
        for( Edge edge : graph.edges )
            if( !edge.isLoop )
                this.unionSets( vertexNodes.get( edge.from ), vertexNodes.get( edge.to ) );
        
        Map<Node, Collection<Vertex>> components = new HashMap<Node, Collection<Vertex>>( );
        for( Node node : vertexNodes.values( ) )
            if( node.parent == null )
                components.put( node, new LinkedList<Vertex>( ) );
        for( Node node : vertexNodes.values( ) )
            components.get( node.parent == null ? node : this.findParent( node.parent ) ).add( node.vertex );
        
        return components.values( );
    }
    
    private Node findParent( Node node )
    {
        Node root = node;
        
        while( root.parent != null )
            root = root.parent;
        
        Node nextNode;
        while( node.parent != null )
        {
            nextNode = node.parent;
            node.parent = root;
            node = nextNode;
        }
        
        return root;
    }
    
    private void unionSets( Node a, Node b )
    {
        if( a != null && b != null )
        {
            Node rootA = this.findParent( a );
            Node rootB = this.findParent( b );
            
            if( rootA != rootB )
                if( rootA.rank > rootB.rank || this.random.nextBoolean( ) )
                    rootA.parent = rootB;
                else
                    rootB.parent = rootA;
        }
    }
}
