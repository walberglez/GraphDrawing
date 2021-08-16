/**
 * ConstrainedPolylineAlgorithm.java
 * 25/07/2011 16:21:42
 */
package pfc.models.algorithms.constrainedpolyline;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import pfc.models.Edge;
import pfc.models.Graph;
import pfc.models.ObservableModel;
import pfc.models.Vertex;
import pfc.models.algorithms.DirectedPath;
import pfc.models.algorithms.NonIntersectingPathList;
import pfc.models.algorithms.constrainedvisrep.ConstrainedVisRepAlgorithm;
import pfc.models.algorithms.visibilityrepresentation.EdgeSegment;
import pfc.models.algorithms.visibilityrepresentation.VertexSegment;
import pfc.models.algorithms.visibilityrepresentation.VisibilityRepresentationDrawing;
import pfc.resources.StringBundle;
import pfc.views.display.algorithms.visibilityrepresentation.VisRepDrawingDisplayView;


/**
 * @author    Walber Gonzalez
 * 
 * UMLGraph
 * @navassoc - - "-polyline" Graph
 * @navassoc - - "-visibility" ConstrainedVisRepAlgorithm
 * @navassoc - - "-paths" NonIntersectingPathList
 * @depend - - - DirectedPath
 * @depend - - - Edge
 * @depend - - - Vertex
 * @depend - - - EdgeSegment
 * @depend - - - VertexSegment
 * @depend - - - VisibilityRepresentationDrawing
 * @depend - - - VisRepDrawingDisplayView
 * @depend - - - StringBundle
 */
public class ConstrainedPolylineAlgorithm extends ObservableModel
{
	/**
	 * Enumerado con las etapas de ejecucion del algoritmo.
	 */
	private enum Step
	{
		VISIBILITY, HORIZONTAL_VERTICES, VERTICAL_EDGES
	}

	/**
	 * Representacion de Visibilidad con Restricciones
	 */
	private ConstrainedVisRepAlgorithm			visibility;
	/**
	 * Polyline graph
	 */
	private Graph								polyline;
	/**
	 * Paths definidos por el usuario
	 */
	private final NonIntersectingPathList		paths;
	/**
	 * estado actual de la ejecucion del algoritmo
	 */
	private	Step		step;
	/**
	 * objeto observador del grafo dual 
	 */
	private Observer	elementObserver;
	
	/**
	 * @param stGraphG grafo al que se le aplicara el algoritmo.
	 */
	public ConstrainedPolylineAlgorithm( Graph stGraphG, NonIntersectingPathList paths  )
	{
		this.paths = paths;

		this.visibility = new ConstrainedVisRepAlgorithm( stGraphG, paths );
		
		this.polyline = new Graph( "polyline", false, true, false, false );

		this.elementObserver = new Observer( )
		{
			@Override
			public void update( Observable o, Object arg )
			{
				ConstrainedPolylineAlgorithm.this.setChanged( );
				ConstrainedPolylineAlgorithm.this.notifyObservers( );
			}
		};
		this.polyline.addObserver( elementObserver );
		
		this.step = Step.VISIBILITY;
	}

	/**
	 * @return the visibility drawing
	 */
	public VisibilityRepresentationDrawing getVisibilityDrawing( )
	{
		return ( this.visibility == null ) ? null : this.visibility.getDrawing( );
	}
	
	/**
	 * @return the polyline
	 */
	public Graph getPolyline( )
	{
		return this.polyline;
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
		case VISIBILITY:
			algorithmExplanation = StringBundle.get( "constrained_polyline_algorithm_step_visibility" );
			break;
		case HORIZONTAL_VERTICES:
			algorithmExplanation = StringBundle.get( "constrained_polyline_algorithm_step_horizontal_vertices" );
			break;
		case VERTICAL_EDGES:
			algorithmExplanation = StringBundle.get( "constrained_polyline_algorithm_step_vertical_edges" );
			break;
		}
		
		return algorithmExplanation;
	}

	/**
	 * Ejecuta las distintas etapas del algoritmo Constrained Upward Polyline
	 * descrito en el libro {@link "http://www.cs.brown.edu/~rt/gdbook.html"}, pag 110.
	 */
	public void executeAlgorithm ( )
	{
		this.step = Step.VISIBILITY;
		this.visibility.executeAlgorithm( );
		// notificar ejecucion completa de visibilidad
		this.setChanged( );
		this.notifyObservers( );
		
		this.step = Step.HORIZONTAL_VERTICES;
		this.constructHorizontalVertices( );
		
		this.step = Step.VERTICAL_EDGES;
		this.constructVerticalEdges( );
	}
	
	/**
	 * Determinar para cada vertice v de G las coordenadas
	 * de su segmento horizontal.
	 * Pseudo-codigo:
	 * 	Para cada vertice v de G hacer
	 * 		reemplazar el segmento horizontal (vértice) por	un punto
	 *  	P(v) = (x(v), y(v)) en T(v) tal como sigue:
	 *  	si v pertenece a un path pi de PI,
	 *			x(v) = X(pi)
	 *			y(v) = Y(v)
	 *		en otro caso,
	 *			elegir cualquier punto del segmento horizontal de v 
	 */
	private void constructHorizontalVertices ( )
	{
		Vertex v;
		Point2D pV;
		for ( VertexSegment vS : getVisibilityDrawing( ).vertexSegments )
		{
			v = new Vertex( vS.vertex.toString( ) );
			// poner etiqueta correcta del vertice
			v.label.set( v.tag.get( ) );
			// poner coordenadas nuevas
			if ( this.paths.containsVertex( vS.vertex ) )
			{
				pV = getPathPositioning( vS, this.paths.getLongestPath( vS.vertex ) );
			}
			else
			{
				pV = getLongEdgePositioning( vS );
			}
			v.x.set( pV.getX( ) );
			v.y.set( pV.getY( ) );
			
			this.polyline.vertices.add( v );
		}
	}
	
	/**
	 * Determinar para cada arista e de G las coordenadas
	 * de su segmento vertical.
	 * Pseudo-codigo:
	 * 	Para cada arista e de G hacer
	 * 		si es una arista corta (short edge : yT(T(e)) - yB(T(e)) = 1 : y(v) - y(u) = 1)
	 * 			reemplazar el segmento vertical (arista) por un	segmento lineal desde P(u) hasta P(v)
	 * 		si es una arista larga (no short edge)
	 *			reemplazar el segmento vertical (arista) por un	segmento polilineal desde P(u) hasta P(v)
	 *			pasando	por los puntos (x(T(u,v),y(u)+1) y (x(T(u,v),y(v)-1)
	 */
	private void constructVerticalEdges ( )
	{
		Edge e;
		List<Point2D> bends;
		for ( EdgeSegment eS : getVisibilityDrawing( ).edgeSegments )
		{
			int indFrom = polyline.vertices.indexOf( eS.edge.from );
			int indTo = polyline.vertices.indexOf( eS.edge.to );
			e = new Edge( true, polyline.vertices.get( indFrom ), polyline.vertices.get( indTo ) );
			
			if ( eS.isShortEdge( ) == false )
			{
				bends = new ArrayList<Point2D>( );
				// (x(T(u,v),y(u)+1)
				bends.add( VisRepDrawingDisplayView.getCoordinatePoint(
						eS.xCoordinate.get( ),
						getVisibilityDrawing( ).getVertexSegment( eS.edge.from ).yCoordinate.get( ) + 1,
						getVisibilityDrawing( ) ) );
				// (x(T(u,v),y(v)-1)
				bends.add( VisRepDrawingDisplayView.getCoordinatePoint(
						eS.xCoordinate.get( ),
						getVisibilityDrawing( ).getVertexSegment( eS.edge.to ).yCoordinate.get( ) - 1,
						getVisibilityDrawing( ) ) );
				e.setBends( bends );
			}
			
			polyline.edges.add( e );
		}
	}

	/**
	 * long-edge positioning:
	 * - Seleccionar el punto del segmento-vertice
	 * desde donde parte una arista larga, si existe.
	 * - Seleccionar el punto medio del segmento, en otro caso.
	 * @param vS VertexSegment
	 * @return Point2D
	 */
	private Point2D getLongEdgePositioning( VertexSegment vS )
	{
		int pos = 0;
		// obtener todas las aristas incidentes en el vertice del segmento 
		Set<Edge> edges = this.visibility.getStGraphG( ).getEdges( vS.vertex );
		// eliminar todos las aristas que sean short
		for ( Iterator<Edge> it = edges.iterator( ); it.hasNext( ); )
		{
			if ( getVisibilityDrawing( ).getEdgeSegment( it.next( ) ).isShortEdge( ) )
				it.remove( );
		}
		// Si no hay aristas long, seleccionar median-positioning 
		if ( edges.isEmpty( ) )
		{
			return getMedianPositioning( vS );
		}
		// Si solo hay una arista long, seleccionar esa
		else if ( edges.size( ) == 1 )
		{
			pos = getVisibilityDrawing( ).getEdgeSegment( edges.iterator( ).next( ) ).xCoordinate.get( );
		}
		// Hay mas de una arista long, cual selecciono?
		else
		{
			Iterator<Edge> it = edges.iterator( );
			pos = getVisibilityDrawing( ).getEdgeSegment( it.next( ) ).xCoordinate.get( );
		}
		return VisRepDrawingDisplayView.getCoordinatePoint( pos, vS.yCoordinate.get( ), getVisibilityDrawing( ) );
	}
	
	/**
	 * median positioning:
	 * - Seleccionar el punto medio del segmento vertice.
	 * @param vS VertexSegment
	 * @return Point2D
	 */
	private Point2D getMedianPositioning( VertexSegment vS )
	{
		int pos = ( vS.xRightCoordinate.get( ) + vS.xLeftCoordinate.get( ) ) / 2;
		return VisRepDrawingDisplayView.getCoordinatePoint( pos, vS.yCoordinate.get( ), getVisibilityDrawing( ) );
	}
	
	private Point2D getPathPositioning( VertexSegment vS, DirectedPath path )
	{
		return VisRepDrawingDisplayView.getCoordinatePoint(
				this.visibility.numberX( this.visibility.getPathFace( path ) ),
				this.visibility.numberY( vS.vertex ),
				getVisibilityDrawing( ) );
	}

}

