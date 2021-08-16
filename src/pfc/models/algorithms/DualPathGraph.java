/**
 * DualPathGraph.java
 * 20/07/2011 02:49:20
 */
package pfc.models.algorithms;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pfc.models.Edge;
import pfc.models.Graph;
import pfc.models.Vertex;
import pfc.utilities.GeometryUtilities;


/**
 * @author walber
 *
 * UMLGraph
 * @navassoc - - "-stGraphG" Graph
 * @navassoc - - "-pathsPI" NonIntersectingPathList
 * @navassoc - - "-vertexS" Vertex
 * @navassoc - - "-vertexT" Vertex
 * @navassoc - - "-faceS" Vertex
 * @navassoc - - "-faceT" Vertex
 * @navassoc - - "*\n-leftEdgesFaces" Edge
 * @navassoc - - "*\n-rightEdgesFaces" Edge
 * @navassoc - - "*\n-rotations" Rotation
 * @navassoc - - "*\n-pathsFaces" DirectedPath
 * @depend - - - GeometryUtilities
 */
public class DualPathGraph extends Graph
{
	/**
	 * Grafo al cual se le aplicara el algoritmo. El grafo G es planar, st, aciclico, conexo. 
	 */
	private final Graph				stGraphG;
	/**
	 * Paths del grafo
	 */
	private NonIntersectingPathList	pathsPI;
	/**
	 * vertices source de G 
	 */
	private Vertex					vertexS;
	/**
	 * vertices target de G
	 */
	private Vertex					vertexT;
	/**
	 * face source de G
	 */
	private Vertex					faceS;
	/**
    * face target de G
	 */
	private Vertex					faceT;
	/** 
	 * caras left(e). Para cada arista e, tenemos una cara que esta a su
	 * izquierda.
	 */
	private Map<Edge, Vertex>		leftEdgesFaces;
	/** 
	 * caras right(v). Para cada arista e, tenemos una cara que esta a su
	 * derecha.
	 */
	private Map<Edge, Vertex>		rightEdgesFaces;
	/**
	 * conjunto de rotaciones alrededor de cada vertice del grafo primal G. 
	 */
	private Map<Vertex, Rotation>	rotations;
	/**
	 * caras que representan a los caminos
	 */
	private Map<DirectedPath, Vertex>		pathsFaces;
	
	/**
	 * @param stGraph Graph
	 */
	public DualPathGraph ( Graph stGraph, NonIntersectingPathList paths )
	{
		super( "dual", false, true, true, false );
		
		this.stGraphG = stGraph;
		this.pathsPI = paths;
		this.leftEdgesFaces = new HashMap<Edge, Vertex>( );
		this.rightEdgesFaces = new HashMap<Edge, Vertex>( );
		this.pathsFaces = new HashMap<DirectedPath, Vertex>( );
		
		this.setVertexS( );
		this.setVertexT( );
		
		this.rotations = new HashMap<Vertex, Rotation>( );
		this.constructRotations( );
	}

	/**
	 * cara izquierda de la arista e.
	 * @param e Edge
	 * @return vertex Vertex
	 */
	public Vertex left ( Edge e )
	{
		return this.leftEdgesFaces.get( e );
	}
	
	/**
	 * cara derecha de la arista e.
	 * @param e Edge
	 * @return vertex Vertex
	 */
	public Vertex right ( Edge e )
	{
		return this.rightEdgesFaces.get( e );
	}
	
	/**
	 * vertice origen de la arista e.
	 * @param e Edge
	 * @return vertex Vertex
	 */
	public Vertex orig ( Edge e )
	{
		return e.from;
	}
	
	/**
	 * vertice destino de la arista e.
	 * @param e Edge
	 * @return vertex Vertex
	 */
	public Vertex dest ( Edge e )
	{
		return e.to;
	}
	
	/**
	 * cara que representa el path dado.
	 * @param p Path
	 * @return face Vertex
	 */
	public Vertex getPathFace( DirectedPath p )
	{
		return this.pathsFaces.get( p );
	}
	
	/**
	 * @return    the vertexS
	 */
	public Vertex getVertexS() {
		return vertexS;
	}

	/**
	 * @return    the vertexT
	 */
	public Vertex getVertexT() {
		return vertexT;
	}

	/**
	 * @return    the faceS
	 */
	public Vertex getFaceS() {
		return faceS;
	}

	/**
	 * @return    the faceT
	 */
	public Vertex getFaceT() {
		return faceT;
	}

	/**
	 * vertice Source del st-graph G
	 */
	private void setVertexS ( )
	{
		for ( Vertex v : this.stGraphG.vertices )
			if ( this.isVertexSource( v ) )
			{
				this.vertexS = v;
				break;
			}
	}

	/**
	 * vertice Target del st-graph G
	 */
	private void setVertexT ( )
	{
		for ( Vertex v : this.stGraphG.vertices )
			if ( this.isVertexTarget( v ) )
			{
				this.vertexT = v;
				break;
			}
	}
	
	/**
	 * Si todas las aristas son entrantes y ninguna saliente
	 * @param v
	 * @return boolean
	 */
	private boolean isVertexSource ( Vertex v )
	{
		return ( ( this.stGraphG.getEdgesFrom( v ).isEmpty() == false )
				&& ( this.stGraphG.getEdgesTo( v ).isEmpty() ) );
	}
	
	/**
	 * Si todas las aristas son salientes y ninguna entrante
	 * @param v
	 * @return boolean
	 */
	private boolean isVertexTarget ( Vertex v )
	{
		return ( ( this.stGraphG.getEdgesTo( v ).isEmpty() == false )
				&& ( this.stGraphG.getEdgesFrom( v ).isEmpty() ) );
	}

	/**
	 * Contruir las rotaciones de cada vertice en el grafo primal G.
	 */
	private void constructRotations( )
	{
		for ( Vertex v : this.stGraphG.vertices )
			this.rotations.put( v, new Rotation( v, this.getAllNeighbors( v ) ) );
	}
	
	/**
	 * Obtener todos los vertices vecinos del vertice v
	 * @param v Vertex
	 * @return Set<Vertex>
	 * @see #constructRotations( )
	 */
	private Set<Vertex> getAllNeighbors ( Vertex v )
	{
		Set<Vertex> neighbors = new HashSet<Vertex>( );
		for ( Edge edge : this.stGraphG.getEdges( v ) )
			neighbors.add( ( edge.from == v ) ? edge.to : edge.from );
				
		return neighbors;
	}
	
	/**
	 * Construir el grafo dual con paths
	 */
	public void constructDualPathGraph ( )
	{
		// insertar nuevos paths por cada arista no contenida
		this.coverAllEdges( );
		// ubicar las dos caras externas
		this.setOuterFaces( );
		// ubicar las caras internas
		this.determineInnerFaces( );
		// insertar nuevas caras para cada caminos
		this.setPathFaces( );
		// crear aristas del grafo dual
		this.setDualPathEdges( );
	}

	/**
	 * Insertar un nuevo path en PI compuesto por cada arista de G
	 * que no pertenezca a ningún path de PI.
	 */
	private void coverAllEdges( )
	{
		for ( Edge e : this.stGraphG.edges )
		{
			if ( pathsPI.containsEdge( e ) == false )
				addSingleEdgePath( e );
		}
	}

	/**
	 * Agregar un nuevo path para la arista e.
	 * @param e Edge
	 */
	private void addSingleEdgePath( Edge e )
	{
		List<Vertex> vertices = new ArrayList<Vertex>( );
		List<Edge> edges = new ArrayList<Edge>( );
		
		vertices.add( e.from );
		vertices.add( e.to );
		edges.add( e );
		this.pathsPI.add( vertices, edges );
	}
	
	/**
	 * Colocar en el grafo Dual las dos caras externas.
	 * La ubicacion se determina segun un estimado con 
	 * la altura maxima del grafo
	 */
	private void setOuterFaces ( )
	{
		Rectangle2D rect = GeometryUtilities.getBounds( this.stGraphG );
		
		Double displ = 30.0;//rect.getHeight( ) * 0.3;
		
		this.faceS = new Vertex( rect.getCenterX(), rect.getMaxY() + displ );
		this.setNewVertexLabelAvailable( this.faceS );
		this.vertices.add( this.faceS );
		
		this.faceT = new Vertex( rect.getCenterX(), rect.getMinY() - displ );
		this.setNewVertexLabelAvailable( this.faceT );
		this.vertices.add( this.faceT );
		
		// determinar aristas externas
		this.determineOuterEdges ( );
	}
	
	/**
	 * Determina las aristas que estan conectadas con las caras externas.
	 */
	private void determineOuterEdges( )
	{
		Vertex current = GeometryUtilities.getTheClosestVertexToOrigin( stGraphG );
		Vertex destination = current;
		Vertex vertexNew, vertexOld;
		Edge edgeTmp;
		Set<Edge> edgesTmp;
		
		vertexOld = new Vertex( 0, 0 );
		do {
			// obtener el siguiente vertice en el sentido clockwise a partir de vertexOld alrededor de current
			vertexNew = this.rotations.get( current ).getClockwiseVertex( vertexOld );
			
			edgesTmp = this.stGraphG.getEdges( current, vertexNew );
			if ( edgesTmp.isEmpty( ) )
			{
				edgeTmp = ( Edge ) this.stGraphG.getEdges( vertexNew, current ).toArray()[ 0 ];
				this.rightEdgesFaces.put( edgeTmp, this.faceT );
			}
			else
			{
				edgeTmp = ( Edge ) edgesTmp.toArray()[ 0 ];
				this.leftEdgesFaces.put( edgeTmp, this.faceS );
			}
			vertexOld = current;
			current = vertexNew;
		} while ( destination.equals( current ) == false );
	}

	/**
	 * Determina para cada arista su cara derecha, si aun no esta definida.
	 */
	private void determineInnerFaces ( )
	{
		for ( Edge e : this.stGraphG.edges )
			if ( this.rightEdgesFaces.containsKey( e ) == false )
				this.determineInnerFace ( e );
	}
	
	/**
	 * Determina la cara interna derecha a la que pertenece la arista e.
	 * Realiza un recorrido en sentido clockwise y actualiza la informacion
	 * de las demas aristas de la cara. Finalmente, inserta la nueva cara 
	 * en el grafo dual.
	 * @param e Edge
	 */
	private void determineInnerFace( Edge e )
	{
		List<Point2D> faceVerticesPoints = new ArrayList<Point2D>( );
		Vertex face = new Vertex( );
		Vertex current = this.orig( e );
		Vertex destination = this.dest( e );
		Vertex vertexNew, vertexOld;
		Edge edgeTmp;
		Set<Edge> edgesTmp;
				
		faceVerticesPoints.add( destination.getPoint2D( ) );
		faceVerticesPoints.add( current.getPoint2D( ) );
		
		this.rightEdgesFaces.put( e, face );
		
		vertexOld = destination;
		while ( destination.equals( current ) == false )
		{
			// obtener el siguiente vertice en el sentido clockwise a partir de vertexOld alrededor de current
			vertexNew = this.rotations.get( current ).getClockwiseVertex( vertexOld );

			faceVerticesPoints.add( vertexNew.getPoint2D( ) );
			
			edgesTmp = this.stGraphG.getEdges( current, vertexNew );
			if ( edgesTmp.isEmpty( ) )
			{
				edgeTmp = ( Edge ) this.stGraphG.getEdges( vertexNew, current ).toArray()[ 0 ];
				this.rightEdgesFaces.put( edgeTmp, face );
			}
			else
			{
				edgeTmp = ( Edge ) edgesTmp.toArray()[ 0 ];
				this.leftEdgesFaces.put( edgeTmp, face );
			}
			vertexOld = current;
			current = vertexNew;
		};
		
		this.setInnerFace( faceVerticesPoints, face );
	}
	
	/**
	 * @param polygon puntos que rodean la cara
	 * @param face vertice cara del dual
	 */
	private void setInnerFace ( List<Point2D> polygon, Vertex face )
	{
		Point2D p = GeometryUtilities.getPolygonCentroid( polygon );
		face.x.set( p.getX( ) );
		face.y.set( p.getY( ) );
		this.setNewVertexLabelAvailable( face );
		this.vertices.add( face );
	}
	
	/**
	 * Insertar por cada path de PI, una nueva face en el punto central del path.
	 */
	private void setPathFaces( )
	{
		for ( DirectedPath p : pathsPI )
		{
			Point2D center = p.getCentralPoint( );
			
			Vertex v = new Vertex( center.getX( ), center.getY( ) );
			this.setNewVertexLabelAvailable( v );
			v.tag.set( "path" );
			this.vertices.add( v );
			this.pathsFaces.put( p, v );
		}
	}
	
	/**
	 * Determinar las aristas del grafo Gpi.
	 * Para cada arista e de G que pertenece a un path p crear una arista (left(e), p)
	 * y otra arista (p, right(e)).
	 */
	private void setDualPathEdges( )
	{
		for ( DirectedPath p : pathsPI )
		{
			for ( Edge e : p.getEdges( ) )
			{
				// arista (left(e), p)
				this.edges.add( new Edge( true, this.left( e ), this.pathsFaces.get( p ) ) );
				// arista (p, right(e))
				this.edges.add( new Edge( true, this.pathsFaces.get( p ), this.right( e ) ) );
			}
		}
	}
}
