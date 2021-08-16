/**
 * OrthgonalAlgorithm.java
 * 26/07/2011 17:20:48
 */
package pfc.models.algorithms.orthogonal;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import pfc.models.Edge;
import pfc.models.Graph;
import pfc.models.ObservableModel;
import pfc.models.Vertex;
import pfc.models.algorithms.DirectedPath;
import pfc.models.algorithms.NonIntersectingPathList;
import pfc.models.algorithms.Rotation;
import pfc.models.algorithms.constrainedvisrep.ConstrainedVisRepAlgorithm;
import pfc.models.algorithms.visibilityrepresentation.EdgeSegment;
import pfc.models.algorithms.visibilityrepresentation.VertexSegment;
import pfc.models.algorithms.visibilityrepresentation.VisibilityRepresentationDrawing;
import pfc.resources.StringBundle;
import pfc.utilities.GraphUtilities;
import pfc.views.display.algorithms.visibilityrepresentation.VisRepDrawingDisplayView;


/**
 * @author Walber Gonzalez
 *
 * UMLGraph
 * @navassoc - - "-orthogonal" Graph
 * @navassoc - - "-digraph" Graph
 * @navassoc - - "-paths" NonIntersectingPathList
 * @navassoc - - "-visibility" ConstrainedVisRepAlgorithm
 * @depend - - - Edge
 * @depend - - - Vertex
 * @depend - - - DirectedPath
 * @depend - - - Rotation
 * @depend - - - EdgeSegment
 * @depend - - - VertexSegment
 * @depend - - - VisibilityRepresentationDrawing
 * @depend - - - GraphUtilities
 * @depend - - - StringBundle
 * @depend - - - VisRepDrawingDisplayView
 */
public class OrthogonalAlgorithm extends ObservableModel
{
	/**
	 * Enumerado con las etapas de ejecucion del algoritmo.
	 */
	private enum Step
	{
		ORIENTATION, PATHS, VISIBILITY, VERTICES, EDGES
	}
	
	/**
	 * Orthogonal graph
	 */
	private Graph						orthogonal;
	/**
	 * Oriented graph
	 */
	private Graph						digraph;
	/**
	 * Paths creados por el algoritmo
	 */
	private NonIntersectingPathList		paths;
	/**
	 * Relacion entre los paths y los vertices que representan.
	 */
	private Map<DirectedPath, Set<Vertex>>	pathToVertices;
	/**
	 * Representacion de Visibilidad con Restricciones
	 */
	private ConstrainedVisRepAlgorithm	visibility;
	/**
	 * estado actual de la ejecucion del algoritmo
	 */
	private	Step						step;
	/**
	 * objeto observador del grafo dual 
	 */
	private Observer					elementObserver;
	
	/**
	 * @param graph {@link Graph} al que se le aplicara el algoritmo.
	 */
	public OrthogonalAlgorithm( Graph graph )
	{		
		this.digraph = new Graph( graph.toString( ) );
		
		this.paths = new NonIntersectingPathList( );
		
		this.pathToVertices = new HashMap<DirectedPath, Set<Vertex>>( );
		
		this.visibility = null;
		
		this.orthogonal = new Graph( "orthogonal",
				graph.areLoopsAllowed,
				graph.areDirectedEdgesAllowed,
				graph.areMultipleEdgesAllowed,
				graph.areCyclesAllowed );

		this.elementObserver = new Observer( )
		{
			@Override
			public void update( Observable o, Object arg )
			{
				OrthogonalAlgorithm.this.setChanged( );
				OrthogonalAlgorithm.this.notifyObservers( );
			}
		};
		this.orthogonal.addObserver( elementObserver );
		
		this.step = Step.ORIENTATION;
	}
	
	/**
	 * @return the orthogonal
	 */
	public Graph getOrthogonal( )
	{
		return this.orthogonal;
	}

	/**
	 * @return the digraph
	 */
	public Graph getDigraph( )
	{
		return this.digraph;
	}

	/**
	 * @return the visibility drawing
	 */
	public VisibilityRepresentationDrawing getVisibilityDrawing( )
	{
		return ( this.visibility == null ) ? null : this.visibility.getDrawing( );
	}
	
	/**
	 * Obtener la explicacion del algoritmo dependiendo de la etapa actual
	 * de ejecucion en la que se encuentra.
	 * @return explanation {@code String}
	 */
	public String getAlgorithmExplanation ( )
	{
		String algorithmExplanation = new String( );
		
		switch ( this.step )
		{
		case ORIENTATION:
			algorithmExplanation = StringBundle.get( "orthogonal_algorithm_step_orientation" );
			break;
		case PATHS:
			algorithmExplanation = StringBundle.get( "orthogonal_algorithm_step_paths" );
			break;
		case VISIBILITY:
			algorithmExplanation = StringBundle.get( "orthogonal_algorithm_step_visibility" );
			break;
		case VERTICES:
			algorithmExplanation = StringBundle.get( "orthogonal_algorithm_step_vertices" );
			break;
		case EDGES:
			algorithmExplanation = StringBundle.get( "orthogonal_algorithm_step_edges" );
			break;
		}
		
		return algorithmExplanation;
	}

	/**
	 * Ejecuta las distintas etapas del algoritmo Orthogonal
	 * descrito en el libro {@link "http://www.cs.brown.edu/~rt/gdbook.html"}, pag 131.
	 */
	public void executeAlgorithm ( )
	{
		this.step = Step.ORIENTATION;
		this.orientGraph( );
		
		this.step = Step.PATHS;
		this.selectPaths( );
		
		this.step = Step.VISIBILITY;
		this.executeVisibility( );
		
		this.step = Step.VERTICES;
		this.constructVertices( );
		
		this.step = Step.EDGES;
		this.constructEdges( );
	}

	/**
	 * Orientar el grafo generando un st-Graph.
	 */
	private void orientGraph( )
	{
		// comprobar si ya esta dirigido el grafo
		if ( this.digraph.areDirectedEdgesAllowed == false )
			this.digraph = GraphUtilities.getDirectedPlanarGraph( this.digraph );
		
		this.digraph.addObserver( this.elementObserver );
		
		// notificar ejecucion completa de orientation
		this.setChanged( );
		this.notifyObservers( );
	}
	
	/**
	 * Crear un conjunto de n-2 directed paths del digraph
	 * asociados con todos los vertices excepto source y target.
	 * Pseudocodigo:
	 * 	Para cada vertice v de D distinto de s y t hacer:
	 *		crear un path piV con solo dos aristas e' y e".
	 *		determinar e' y e":
	 *		- si v tiene solo dos aristas entrantes
	 *			e' es la arista entrante situada mas a la izquierda de v.
	 *			e" es la arista saliente situada mas a la derecha de v.
	 *		- si v tiene una o tres aristas entrantes
	 *			e' es la arista entrante situada en el medio de las otras aristas entrantes de v.
	 *			e" es la arista saliente situada en el medio de las otras aristas salientes de v.
	 *	Unificar todos los paths creados que compartan una arista para conseguir que sean nonintersecting.
	 */
	private void selectPaths( )
	{
		List<DirectedPath> allPaths = new ArrayList<DirectedPath>( );
		Vertex s = this.digraph.getVertexSource( );
		Vertex t = this.digraph.getVertexTarget( );
		
		for ( Vertex v : this.digraph.vertices )
		{
			if ( v.equals( s ) == false && v.equals( t ) == false )
				joinPaths( allPaths, createPath( v ) );
		}
		for ( DirectedPath path : allPaths )
		{
			if ( this.paths.add( path ) == false )
				System.out.println( "Error adding path while execution of orthogonal drawing algorithm" );
		}
		for ( DirectedPath path : this.paths )
		{
			this.digraph.suspendNotifications( true );
			path.setSelected( true );
			this.digraph.suspendNotifications( false );

			// notificar seleccion completa de un path
			this.setChanged( );
			this.notifyObservers( );
			
			this.digraph.suspendNotifications( true );
			path.setSelected( false );
			this.digraph.suspendNotifications( false );
		}
	}

	/**
	 * Crear path relacionado con el vertice v.
	 * @param v {@link Vertex}
	 * @return {@link DirectedPath}
	 */
	private DirectedPath createPath( Vertex v ) {
		// path
		DirectedPath path = new DirectedPath( );
		// aristas del path
		Edge e1 = null;
		Edge e2 = null;
		// lista de vertices del path
		List<Vertex> pathVertices = new ArrayList<Vertex>( );
		// lista de aristas del path
		List<Edge> pathEdges = new ArrayList<Edge>( );
		// aristas entrantes de v
		Set<Edge> in = this.digraph.getEdgesTo( v );
		int inSize = in.size( );
		// aristas salientes a v
		Set<Edge> out = this.digraph.getEdgesFrom( v );
		
		if ( inSize == 2 )
		{
			e1 = getLeftmostIncomingEdge( v, in, out );
			e2 = getRightmostOutcomingEdge( v, in, out );
		}
		else if ( inSize == 1 || inSize == 3 )
		{
			e1 = getMedianIncomingEdge( v, in, out );
			e2 = getMedianOutcomingEdge( v, in, out );
		}
		// actualizar las listas de vertices y aristas del path
		pathVertices.add( v );
		pathVertices.add( e1.from );
		pathVertices.add( e2.to );
		pathEdges.add( e1 );
		pathEdges.add( e2 );
		// crear el path, no debe haber problemas
		if ( path.createPath( pathVertices, pathEdges ) == false )
			System.out.println( "Error creating path while execution of orthogonal drawing algorithm" );
		
		// actualizar la asociacion de vertices y paths
		this.pathToVertices.put( path, new HashSet<Vertex>( ) );
		this.pathToVertices.get( path ).add( v );
		return path;
	}
	
	/**
	 * e' es la arista entrante situada mas a la izquierda de v.
	 * @param v {@link Vertex} vertice asociado al path
	 * @param in {@link Set} lista de aristas entrantes a v
	 * @param out {@link Set} lista de aristas salientes de v
	 * @return e1 {@link Edge}
	 */
	private static Edge getLeftmostIncomingEdge( Vertex v, Set<Edge> in, Set<Edge> out )
	{
		Set<Vertex> neighborsIn = getNeighborsIn( in );
		Set<Vertex> neighborsOut = getNeighborsOut( out );
		Set<Vertex> neighbors = new HashSet<Vertex>( );
		neighbors.addAll( neighborsIn );
		neighbors.addAll( neighborsOut );
		
		Rotation rot = new Rotation( v, neighbors );
		
		for ( Iterator<Edge> it = in.iterator( ); it.hasNext( ); )
		{
			Edge eIn = it.next( );
			// Si la arista entrante tiene hacia la derecha una arista saliente 
			if ( neighborsOut.contains( rot.getClockwiseVertex( eIn.from ) ) )
				return eIn;
		}
		return null;
	}
	
	/**
	 * e" es la arista saliente situada mas a la derecha de v.
	 * @param v {@link Vertex} vertice asociado al path
	 * @param in {@link Set} lista de aristas entrantes a v
	 * @param out {@link Set} lista de aristas salientes de v
	 * @return e2 {@link Edge}
	 */
	private static Edge getRightmostOutcomingEdge( Vertex v, Set<Edge> in, Set<Edge> out )
	{
		// Si solo hay una eleccion posible, seleccionarla.
		if ( out.size( ) == 1 )
			return out.iterator( ).next( );
		
		Set<Vertex> neighborsIn = getNeighborsIn( in );
		Set<Vertex> neighborsOut = getNeighborsOut( out );
		Set<Vertex> neighbors = new HashSet<Vertex>( );
		neighbors.addAll( neighborsIn );
		neighbors.addAll( neighborsOut );
		
		Rotation rot = new Rotation( v, neighbors );
		
		for ( Iterator<Edge> it = out.iterator( ); it.hasNext( ); )
		{
			Edge eOut = it.next( );
			// Si la arista saliente tiene hacia la derecha una arista entrante 
			if ( neighborsIn.contains( rot.getClockwiseVertex( eOut.to ) ) )
				return eOut;
		}
		return null;
	}
	
	/**
	 * e' es la arista entrante situada en el medio de las otras aristas entrantes de v.
	 * @param v {@link Vertex} vertice asociado al path
	 * @param in {@link Set} lista de aristas entrantes a v
	 * @param out {@link Set} lista de aristas salientes de v
	 * @return e1 {@link Edge}
	 */
	private static Edge getMedianIncomingEdge( Vertex v, Set<Edge> in, Set<Edge> out )
	{
		// Si solo hay una eleccion posible, seleccionarla.
		if ( in.size( ) == 1 )
			return in.iterator( ).next( );
		
		// Ahora hay tres elecciones posibles, seleccionar la del medio.
		Set<Vertex> neighborsIn = getNeighborsIn( in );
		Set<Vertex> neighborsOut = getNeighborsOut( out );
		Set<Vertex> neighbors = new HashSet<Vertex>( );
		neighbors.addAll( neighborsIn );
		neighbors.addAll( neighborsOut );
		
		Rotation rot = new Rotation( v, neighbors );
		
		for ( Iterator<Edge> it = in.iterator( ); it.hasNext( ); )
		{
			Edge eIn = it.next( );
			// Si la arista entrante tiene hacia la derecha y hacia la izquierda aristas entrantes
			if ( neighborsIn.contains( rot.getClockwiseVertex( eIn.from ) )
					&& neighborsIn.contains( rot.getCounterClockwiseVertex( eIn.from ) ) )
				return eIn;
		}
		return null;
	}
	
	/**
	 * e" es la arista saliente situada en el medio de las otras aristas salientes de v.
	 * @param v {@link Vertex} vertice asociado al path
	 * @param in {@link Set} lista de aristas entrantes a v
	 * @param out {@link Set} lista de aristas salientes de v
	 * @return e2 {@link Edge}
	 */
	private static Edge getMedianOutcomingEdge( Vertex v, Set<Edge> in, Set<Edge> out )
	{
		// Si solo hay una eleccion posible, seleccionarla.
		int outSize = out.size( );
		if ( outSize == 1 )
			return out.iterator( ).next( );

		// Ahora hay dos o tres elecciones posibles, seleccionar la del medio.
		Set<Vertex> neighborsIn = getNeighborsIn( in );
		Set<Vertex> neighborsOut = getNeighborsOut( out );
		Set<Vertex> neighbors = new HashSet<Vertex>( );
		neighbors.addAll( neighborsIn );
		neighbors.addAll( neighborsOut );

		Rotation rot = new Rotation( v, neighbors );

		for ( Iterator<Edge> it = out.iterator( ); it.hasNext( ); )
		{
			Edge eOut = it.next( );
			// Si la arista saliente tiene hacia la derecha una arista saliente
			if ( outSize == 2
					&& neighborsOut.contains( rot.getClockwiseVertex( eOut.to ) ) )
				return eOut;
			// Si la arista saliente tiene hacia la derecha y hacia la izquierda aristas salientes
			if ( outSize == 3
					&& neighborsOut.contains( rot.getClockwiseVertex( eOut.to ) )
					&& neighborsOut.contains( rot.getCounterClockwiseVertex( eOut.to ) ) )
				return eOut;
		}
		return null;
	}

	private static Set<Vertex> getNeighborsIn( Set<Edge> in )
	{
		Set<Vertex> neighborsIn = new HashSet<Vertex>( );
		for ( Iterator<Edge> it = in.iterator( ); it.hasNext( ); )
			neighborsIn.add( it.next( ).from );
		return neighborsIn;
	}
	
	private static Set<Vertex> getNeighborsOut( Set<Edge> out )
	{
		Set<Vertex> neighborsOut = new HashSet<Vertex>( );
		for ( Iterator<Edge> it = out.iterator( ); it.hasNext( ); )
			neighborsOut.add( it.next( ).to );
		return neighborsOut;
	}

	/**
	 * Unificar todos los paths creados que compartan una arista
	 * para conseguir que sean nonintersecting.
	 * @param allPaths {@link List} lista de paths ya creados y unidos entre si
	 * @param path {@link DirectedPath} path que se quiere unir a los demas
	 * @param v {@link Vertex} vertice al cual representa el path
	 */
	private void joinPaths( List<DirectedPath> allPaths, DirectedPath path )
	{
		for ( Iterator<DirectedPath> it = allPaths.iterator( ); it.hasNext( ); )
		{
			DirectedPath p = it.next( );
			if ( p.isEdgeDisjoint( path ) == false )
			{
				path.join( p );
				// actualizar asociaciones de vertices y paths
				this.pathToVertices.get( path ).addAll( this.pathToVertices.get( p ) );
				this.pathToVertices.remove( p );
				it.remove( );
			}
		}
		allPaths.add( path );
	}

	/**
	 * Edjecutar el algoritmo Constrained Visibility Representation.
	 */
	private void executeVisibility( )
	{
		this.visibility = new ConstrainedVisRepAlgorithm( this.digraph, this.paths);
		this.visibility.executeAlgorithm( );
		
		// notificar ejecucion completa de visibilidad
		this.setChanged( );
		this.notifyObservers( );
	}

	/**
	 * Pseudocodigo:
	 * 	Para cada vertice v de D hacer:
	 *		- si v es source
	 *			dibujar v con P(v) en la interseccion del segmento vertice de v
	 *			con el segmento arista de la arista saliente situada en el medio
	 *			de las otras aristas salientes.
	 *		- si v es target
	 *			dibujar v con P(v) en la interseccion del segmento vertice de v
	 *			con el segmento arista de la arista entrante situada en el medio
	 *			de las otras aristas entrantes.
	 *		- si v es distinto de source y de target
	 *			dibujar v con P(v) en la interseccion del segmento vertice de v
	 *			con los segmentos aristas del path piV.
	 */
	private void constructVertices( )
	{
		Vertex newV;
		VertexSegment vS;
		Point2D pV;
		for ( Vertex v : this.digraph.vertices )
		{
			vS = getVisibilityDrawing( ).getVertexSegment( v );
			newV = new Vertex( vS.vertex.toString( ) );
			// poner etiqueta correcta del vertice
			newV.label.set( newV.tag.get( ) );
			// Si es vertice source
			if ( v.equals( this.digraph.getVertexSource( ) ) )
				pV = getPositioning( vS, this.digraph.getEdgesFrom( v ) );
			// Si es vertice target
			else if ( v.equals( this.digraph.getVertexTarget( ) ) )
				pV = getPositioning( vS, this.digraph.getEdgesTo( v ) );
			else
				pV = getPathPositioning( vS );
			
			newV.x.set( pV.getX( ) );
			newV.y.set( pV.getY( ) );
			
			this.orthogonal.vertices.add( newV );
		}
	}
	
	/**
	 * Obtener P(v) en la interseccion del segmento vertice de v
	 * con el segmento arista situado en el medio de las otras aristas.
	 * @param vS {@link VertexSegment}
	 * @param neighbors {@link Set}
	 * @return {@link Point2D}
	 */
	private Point2D getPositioning( VertexSegment vS, Set<Edge> neighbors )
	{
		return VisRepDrawingDisplayView.getCoordinatePoint(
				getSTAxeXCoordinate( neighbors ),
				vS.yCoordinate.get( ),
				getVisibilityDrawing( ) );
	}
	
	private int getSTAxeXCoordinate( Set<Edge> neighbors )
	{
		ArrayList<Integer> positions = new ArrayList<Integer>( );
		for ( Edge e : neighbors )
		{
			positions.add( getVisibilityDrawing( ).getEdgeSegment( e ).xCoordinate.get( ) );
		}
		Collections.sort( positions );
		int ind = positions.size( ) / 2;
		int pos = positions.get( ind );
		return pos;
	}
	
	/**
	 * Obtener P(v) en la interseccion del segmento vertice de v
	 * con los segmentos aristas del path piV.
	 * Precondition: el vertice del segmento vertice es distinto de source y target.
	 * @param vS {@link VertexSegment}
	 * @return {@link Point2D}
	 */
	private Point2D getPathPositioning( VertexSegment vS )
	{
		DirectedPath path = getAssociatedPath( vS.vertex );
		Edge e = path.getEdges( ).iterator( ).next( );
		int pos = getVisibilityDrawing( ).getEdgeSegment( e ).xCoordinate.get( );
		return VisRepDrawingDisplayView.getCoordinatePoint(
				pos,
				vS.yCoordinate.get( ),
				getVisibilityDrawing( ) );
	}
	
	/**
	 * Obtener el path asociado al vertice dado
	 * @param v {@link Vertex}
	 * @return {@link DirectedPath}
	 */
	private DirectedPath getAssociatedPath( Vertex v )
	{
		for ( Map.Entry<DirectedPath, Set<Vertex>> entry : this.pathToVertices.entrySet( ) )
		{
			if ( entry.getValue( ).contains( v ) )
				return entry.getKey( );
		}
		return null;
	}
	
	/**
	 * Pseudocodigo
	 * 	Para cada arista e=(u,v) de D hacer:
	 *		- si u y v son distintos de source y de target
	 *			dibujar e como una cadena ortogonal con los puntos:
	 *			P(u), la interseccion del segmento vertice de u con el segmento arista de e,
	 *			la interseccion del segmento vertice de v con el segmento arista de e y P(v).
	 *		- si u o v es source o target
	 *		
	 */
	private void constructEdges( )
	{
		Edge newE;
		
		for ( Edge e : this.digraph.edges )
		{			
			int indFrom = orthogonal.vertices.indexOf( e.from );
			int indTo = orthogonal.vertices.indexOf( e.to );
			newE = new Edge(
					orthogonal.areDirectedEdgesAllowed,
					orthogonal.vertices.get( indFrom ),
					orthogonal.vertices.get( indTo ) );
			newE.setBends( calculateBends( e, newE ) );

			orthogonal.edges.add( newE );
		}
	}
	
	/**
	 * Calcular los codos que presenta esta arista en el dibujo ortogonal.
	 * - si u y v son distintos de source y de target
	 *		dibujar e como una cadena ortogonal con los puntos:
	 *		P(u), la interseccion del segmento vertice de u con el segmento arista de e,
	 *		la interseccion del segmento vertice de v con el segmento arista de e y P(v).
	 *	- si u o v es source o target
	 *
	 * @param e {@link Edge} arista de digraph
	 * @param newE {@link Edge} arista de orthogonal
	 * @return {@link List} lista de codos de la arista.
	 */
	private List<Point2D> calculateBends( Edge e, Edge newE )
	{
		List<Point2D> bends = new ArrayList<Point2D>( );
		EdgeSegment eS = getVisibilityDrawing( ).getEdgeSegment( e );
		Vertex s = this.digraph.getVertexSource( );
		Vertex t = this.digraph.getVertexTarget( );
		
		// Si arista parte de s o termina en t 
		if ( isProblematicSTEdge( eS, s ) )
		{
			// (x(u),y(u)-1)
			Point2D bendS = getBendSourcePositioning( s, -1 );
			// Si el codo S es distinto de P(u), lo agrego
			if ( bendS.equals( newE.from.getPoint2D( ) ) == false )
				bends.add( bendS );
			// (x(T(u,v),y(u)-1)
			Point2D bendFrom = getBendFromSTPositioning( eS, -1 );
			// Si el codo From es distinto de P(u), lo agrego
			if ( bendFrom.equals( newE.from.getPoint2D( ) ) == false )
				bends.add( bendFrom );
			// (x(T(u,v),y(v)-1)
			Point2D bendTo = getBendToSTPositioning( eS, -1 );
			// Si el codo To es distinto de P(v), lo agrego
			if ( bendTo.equals( newE.to.getPoint2D( ) ) == false )
				bends.add( bendTo );
		}
		else if ( isProblematicSTEdge( eS, t ) )
		{
			// (x(T(u,v),y(u)+1)
			Point2D bendFrom = getBendFromSTPositioning( eS, 1 );
			// Si el codo From es distinto de P(u), lo agrego
			if ( bendFrom.equals( newE.from.getPoint2D( ) ) == false )
				bends.add( bendFrom );
			// (x(T(u,v),y(v)+1)
			Point2D bendTo = getBendToSTPositioning( eS, 1 );
			// Si el codo To es distinto de P(v), lo agrego
			if ( bendTo.equals( newE.to.getPoint2D( ) ) == false )
				bends.add( bendTo );
			// (x(u),y(u)+1)
			Point2D bendT = getBendTargetPositioning( t, 1 );
			// Si el codo T es distinto de P(u), lo agrego
			if ( bendT.equals( newE.to.getPoint2D( ) ) == false )
				bends.add( bendT );
		}
		// Si arista no parte de s ni termina en t
		else
		{
			// (x(T(u,v),y(u))
			Point2D bendFrom = getBendFromPositioning( eS );
			// Si el codo From es distinto de P(u), lo agrego
			if ( bendFrom.equals( newE.from.getPoint2D( ) ) == false )
				bends.add( bendFrom );
			// (x(T(u,v),y(v))
			Point2D bendTo = getBendToPositioning( eS );
			// Si el codo To es distinto de P(v), lo agrego
			if ( bendTo.equals( newE.to.getPoint2D( ) ) == false )
				bends.add( bendTo );
		}
		return bends;
	}
	
	/**
	 * codo situado en ( x ( T ( u, v ), y( u ) )
	 * @param eS {@link EdgeSegment}
	 * @return {@link Point2D}
	 */
	private Point2D getBendFromPositioning( EdgeSegment eS )
	{
		return VisRepDrawingDisplayView.getCoordinatePoint(
				eS.xCoordinate.get( ),
				getVisibilityDrawing( ).getVertexSegment( eS.edge.from ).yCoordinate.get( ),
				getVisibilityDrawing( ) );
	}
	
	/**
	 * codo situado en ( x ( T ( u, v ), y( v ) )
	 * @param eS {@link EdgeSegment}
	 * @return {@link Point2D}
	 */
	private Point2D getBendToPositioning( EdgeSegment eS )
	{
		return VisRepDrawingDisplayView.getCoordinatePoint(
				eS.xCoordinate.get( ),
				getVisibilityDrawing( ).getVertexSegment( eS.edge.to ).yCoordinate.get( ),
				getVisibilityDrawing( ) );
	}
	
	/**
	 * codo situado en ( x ( T ( u, v ), y( u ) +- 1 )
	 * @param eS {@link EdgeSegment}
	 * @param despl {@link Integer}
	 * @return {@link Point2D}
	 */
	private Point2D getBendFromSTPositioning( EdgeSegment eS, int despl )
	{
		return VisRepDrawingDisplayView.getCoordinatePoint(
				eS.xCoordinate.get( ),
				getVisibilityDrawing( ).getVertexSegment( eS.edge.from ).yCoordinate.get( ) + despl,
				getVisibilityDrawing( ) );
	}
	
	/**
	 * codo situado en ( x ( T ( u, v ), y( v ) +- 1 )
	 * @param eS {@link EdgeSegment}
	 * @param despl {@link Integer}
	 * @return {@link Point2D}
	 */
	private Point2D getBendToSTPositioning( EdgeSegment eS, int despl )
	{
		return VisRepDrawingDisplayView.getCoordinatePoint(
				eS.xCoordinate.get( ),
				getVisibilityDrawing( ).getVertexSegment( eS.edge.to ).yCoordinate.get( ) + despl,
				getVisibilityDrawing( ) );
	}
	
	/**
	 * codo situado en ( x ( u ), y( u ) +- 1 )
	 * @param s {@link Vertex}
	 * @param despl {@link Integer}
	 * @return {@link Point2D}
	 */
	private Point2D getBendSourcePositioning( Vertex s, int despl )
	{
		return VisRepDrawingDisplayView.getCoordinatePoint(
				getSTAxeXCoordinate( this.digraph.getEdgesFrom( s ) ),
				getVisibilityDrawing( ).getVertexSegment( s ).yCoordinate.get( ) + despl,
				getVisibilityDrawing( ) );
	}
	
	/**
	 * codo situado en ( x ( u ), y( v ) +- 1 )
	 * @param t {@link Vertex}
	 * @param despl {@link Integer}
	 * @return {@link Point2D}
	 */
	private Point2D getBendTargetPositioning( Vertex t, int despl )
	{
		return VisRepDrawingDisplayView.getCoordinatePoint(
				getSTAxeXCoordinate( this.digraph.getEdgesTo( t ) ),
				getVisibilityDrawing( ).getVertexSegment( t ).yCoordinate.get( ) + despl,
				getVisibilityDrawing( ) );
	}
	
	/**
	 * Si la arista es causante de problemas.
	 * @param eS {@link EdgeSegment}
	 * @param st {@link Vertex}
	 * @return {@link Boolean}
	 */
	private boolean isProblematicSTEdge( EdgeSegment eS, Vertex st )
	{
		return ( eS.edge.from.equals( st ) || eS.edge.to.equals( st ) ) &&
				this.digraph.getEdges( st ).size( ) == 4 &&
				getVisibilityDrawing( ).getVertexSegment( st ).xLeftCoordinate.get( ) == eS.xCoordinate.get( );
	}
}
