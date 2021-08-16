/**
 * DualGraph.java
 */
package pfc.models.algorithms;

import java.awt.geom.*;
import java.util.*;

import pfc.models.*;
import pfc.utilities.GeometryUtilities;


/**
 * @author    walber
 * 
 * UMLGraph
 * @navassoc - - "-stGraphG" Graph
 * @navassoc - - "-vertexS" Vertex
 * @navassoc - - "-vertexT" Vertex
 * @navassoc - - "-faceS" Vertex
 * @navassoc - - "-faceT" Vertex
 * @navassoc - - "*\n-leftVerticesFaces" Vertex
 * @navassoc - - "*\n-rightVerticesFaces" Vertex
 * @navassoc - - "*\n-leftEdgesFaces" Edge
 * @navassoc - - "*\n-rightEdgesFaces" Edge
 * @navassoc - - "*\n-rotations" Rotation
 * @depend - - - GeometryUtilities
 */
public class DualGraph extends Graph
{
	/**
	 * Grafo al cual se le aplicara el algoritmo. El grafo G es planar, st, aciclico, conexo. 
	 */
	private final Graph					stGraphG;
	/**
	 * vertices source de G 
	 */
	private Vertex						vertexS;
	/**
	 * vertices target de G
	 */
	private Vertex						vertexT;
	/**
	 * face source de G
	 */
	private Vertex						faceS;
	/**
     * face target de G
	 */
	private Vertex						faceT;
	/** 
	 * caras left(v). Para cada vertice v, tenemos una cara que separa
	 * las aristas entrantes de las salientes en el sentido de las agujas
	 * del reloj.
	 */
	private Map<Vertex, Vertex>	leftVerticesFaces;
	/** 
	 * caras right(v). Para cada vertice v, tenemos una cara que separa
	 * las aristas salientes de las entrantes en el sentido de las agujas
	 * del reloj.
	 */
	private Map<Vertex, Vertex>	rightVerticesFaces;
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
	 * @param stGraph Graph
	 */
	public DualGraph ( Graph stGraph )
	{
		super( "dual", false, true, true, false );
		
		this.stGraphG = stGraph;
		this.leftVerticesFaces = new HashMap<Vertex, Vertex>( );
		this.rightVerticesFaces = new HashMap<Vertex, Vertex>( );
		this.leftEdgesFaces = new HashMap<Edge, Vertex>( );
		this.rightEdgesFaces = new HashMap<Edge, Vertex>( );
		
		this.vertexS = this.stGraphG.getVertexSource( );
		this.vertexT = this.stGraphG.getVertexTarget( );
		
		this.rotations = new HashMap<Vertex, Rotation>( );
		this.constructRotations( );
	}

	/**
	 * cara que separa las aristas entrantes de las salientes de un vértice v
	 * en el sentido de las agujas del reloj.
	 * precondition: esta determinado el left(e).
	 * @param v Vertex
	 * @return vertice Vertex
	 */
	public Vertex left ( Vertex v )
	{
		return this.leftVerticesFaces.get( v );
	}
	
	/**
	 * cara que separa las aristas salientes de las entrantes a un vértice v
	 * en el sentido de las agujas del reloj.
	 * precondition: esta determinado el right(e).
	 * @param v Vertex
	 * @return vertice Vertex
	 */
	public Vertex right ( Vertex v )
	{	
		return this.rightVerticesFaces.get( v );	
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
	 * Contruir las rotaciones de cada vertice en el grafo primal G.
	 */
	private void constructRotations( )
	{
		for ( Vertex v : this.stGraphG.vertices )
			this.rotations.put( v, new Rotation( v, this.stGraphG.getNeighborsInOut( v ) ) );
	}
	
	/**
	 * Construir el grafo dual
	 */
	public void constructDualGraph ( )
	{
		// ubicar las dos caras externas
		this.setOuterFaces ( );
		// ubicar las caras internas
		this.determineInnerFaces( );
		// crear aristas del grafo dual
		this.setDualEdges ( );
		// determinar caras izquierdas y derechas de los vertices de G
		this.setLeftRightVerticesFaces( );
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
	 * Construye las aristas del grafo dual G* determinadas por las caras left(e)
	 * y right(e) de cada arista e de G.
	 */
	private void setDualEdges( )
	{
		Edge newEdge;
		
		for ( Edge e : this.stGraphG.edges )
		{
//			if ( this.orig( e ) != this.vertexS || this.dest( e ) != this.vertexT )
//			{
				newEdge = new Edge( true, this.left( e ), this.right( e ) );
				newEdge.handleX.set( e.handleX.get( ) );
				newEdge.handleY.set( e.handleY.get( ) );
				this.edges.add( newEdge );
//			}
		}
	}
	
	/**
	 * Determinar para cada vertice de G cuales son sus caras derecha e izquierda.
	 * El resultado queda almacenado en las estructuras de datos correspondientes.
	 * Precondition: ya han sido determinadas las caras izquierda y derecha
	 * de cada aristas de G.
	 * @see #leftVerticesFaces
	 * @see #rightVerticesFaces
	 */
	private void setLeftRightVerticesFaces ( )
	{
		for ( Vertex v : this.stGraphG.vertices )
		{
			/*
			 *  Si v es el vertice source o target,
			 *  cara izquierda es faceS y cara derecha es faceT
			 */
			if ( this.vertexS.equals( v ) || this.vertexT.equals( v ) )
			{
				this.leftVerticesFaces.put( v, this.faceS );
				this.rightVerticesFaces.put( v, this.faceT );
			}
			else
			{
				Iterator<Edge> edgesIn;
				Iterator<Edge> edgesOut;
				Edge e1;
				Edge e2;
				
				edgesIn = this.stGraphG.getEdgesTo( v ).iterator( );
				// Para todas las aristas entrantes a v
				while ( edgesIn.hasNext( ) )
				{
					e1 = edgesIn.next( );
					
					Vertex left = this.leftEdgesFaces.get( e1 );
					Vertex right = this.rightEdgesFaces.get( e1 );
					
					edgesOut = this.stGraphG.getEdgesFrom( v ).iterator( );
					// Para todas las aristas salientes de v
					while ( edgesOut.hasNext( ) )
					{
						e2 = edgesOut.next( );
						/*
						 * Si la cara izquierda de la arista entrante e1 es la misma
						 * que la cara izquierda de la arista saliente e2,
						 * left es la cara izquierda de v.
						 */
						if ( left.equals( this.leftEdgesFaces.get( e2 ) ) )
							this.leftVerticesFaces.put( v, left );
						/*
						 * Si la cara derecha de la arista entrante e1 es la misma
						 * que la cara derecha de la arista saliente e2,
						 * right es la cara derecha de v.
						 */
						if ( right.equals( this.rightEdgesFaces.get( e2 ) ) )
							this.rightVerticesFaces.put( v, right );
					}
				}
			}
		}
	}
	
}
