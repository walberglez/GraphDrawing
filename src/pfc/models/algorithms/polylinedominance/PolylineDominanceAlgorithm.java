/**
 * PolylineDominanceAlgorithm.java
 * 03/08/2011 23:51:33
 */
package pfc.models.algorithms.polylinedominance;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import pfc.models.Edge;
import pfc.models.Graph;
import pfc.models.ObservableModel;
import pfc.models.Vertex;
import pfc.models.algorithms.sldominance.DominanceDrawing;
import pfc.models.algorithms.sldominance.SLDominanceAlgorithm;
import pfc.resources.StringBundle;
import pfc.utilities.GraphUtilities;

/**
 * @author    Walber Gonzalez
 * 
 * UMLGraph
 * @navassoc - - "-slDominance" SLDominanceAlgorithm
 * @navassoc - - "-reduced" Graph
 * @navassoc - - "*\n-dummyVertices" Vertex
 * @depend - - - Edge
 * @depend - - - DominanceDrawing
 * @depend - - - StringBundle
 * @depend - - - StringBundle
 */
public class PolylineDominanceAlgorithm extends ObservableModel
{
	/**
	 * Enumerado con las etapas de ejecucion del algoritmo.
	 */
	private enum Step
	{
		REDUCED, SL_DOMINANCE, POLYLINE_BENDS
	}

	/**
	 * Striaght-Line Dominance
	 */
	private SLDominanceAlgorithm	slDominance;
	/**
	 * Reduced Graph
	 */
	private Graph					reduced;
	/**
	 * Dummy Vertices
	 */
	private List<Vertex>			dummyVertices;
	/**
	 * estado actual de la ejecucion del algoritmo
	 */
	private	Step					step;
	/**
	 * objeto observador del grafo dual 
	 */
	private Observer				elementObserver;
	
	/**
	 * @param stGraphG grafo al que se le aplicara el algoritmo.
	 */
	public PolylineDominanceAlgorithm( Graph stGraphG )
	{		
		this.reduced = new Graph( stGraphG.toString( ) );

		this.elementObserver = new Observer( )
		{
			@Override
			public void update( Observable o, Object arg )
			{
				PolylineDominanceAlgorithm.this.setChanged( );
				PolylineDominanceAlgorithm.this.notifyObservers( );
			}
		};
		this.reduced.addObserver( elementObserver );
		
		this.step = Step.REDUCED;
	}

	/**
	 * @return the dominance drawing
	 */
	public DominanceDrawing getDominanceDrawing( )
	{
		return ( this.slDominance == null ) ? null : this.slDominance.getDrawing( );
	}

	/**
	 * @return the reduced
	 */
	public Graph getReduced( )
	{
		return this.reduced;
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
		case REDUCED:
			algorithmExplanation = StringBundle.get( "polyline_dominance_algorithm_step_reduced" );
			break;
		case SL_DOMINANCE:
			algorithmExplanation = StringBundle.get( "polyline_dominance_algorithm_step_sl_dominance" );
			break;
		case POLYLINE_BENDS:
			algorithmExplanation = StringBundle.get( "polyline_dominance_algorithm_step_polyline_bends" );
			break;
		}
		
		return algorithmExplanation;
	}

	/**
	 * Ejecuta las distintas etapas del algoritmo Polyline Dominance
	 * descrito en el libro {@link "http://www.cs.brown.edu/~rt/gdbook.html"}, pag 127.
	 */
	public void executeAlgorithm ( )
	{
		this.step = Step.REDUCED;
		this.makeGraphReduced( );
		
		this.step = Step.SL_DOMINANCE;
		this.constructSLDominanceDrawing( );
		
		this.step = Step.POLYLINE_BENDS;
		this.turnDummyVerticesIntoBends( );
	}

	/**
	 * Reemplazar cada arista transitiva (u,v) por un par de nuevas aristas
	 * (u,x) y (x,v) donde x es un nuevo vertice dummy.
	 */
	private void makeGraphReduced( )
	{
		this.dummyVertices = new ArrayList<Vertex>( );
		Graph tmp = new Graph( this.reduced.toString( ) );
		// convertir cada arista transitiva en dos aristas con un nuevo vertice dummy
		for ( Edge e : tmp.edges )
		{
			if ( GraphUtilities.isTransitiveEdge( tmp, e ) )
				insertDummyVertex( e );
		}
	}
	
	/**
	 * @param e {@link Edge}
	 */
	private void insertDummyVertex( Edge e )
	{
		// Vertice dummy
		Vertex dummy = new Vertex(
				e.getHandlePoint2D( ).getX( ),
				e.getHandlePoint2D( ).getY( ) );
		
		this.reduced.suspendNotifications( true );
		
		// Eliminar la arista transitiva
		this.reduced.edges.remove( e );
		// Insertar el vertice from, si no estaba
		if ( this.reduced.vertices.contains( e.from ) == false )
			this.reduced.vertices.add( e.from );
		// Insertar el vertice to, si no estaba
		if ( this.reduced.vertices.contains( e.to ) == false )
			this.reduced.vertices.add( e.to );
		
		this.reduced.suspendNotifications( false );
		
		// Insertar el vertice dummy
		this.reduced.vertices.add( dummy );
		// Insertar las arista (e.from, dummy)
		Edge fromDummy = new Edge( e.isDirected, e.from, dummy );
		this.reduced.edges.add( fromDummy );
		// Insertar las arista (dummy, e.to)
		Edge dummyTo = new Edge( e.isDirected, dummy, e.to );
		this.reduced.edges.add( dummyTo );
		// Marcar el vertice dummy para que sea eliminado
		this.dummyVertices.add( dummy );
	}

	/**
	 * Construir Stright-Line Dominance Drawing a partir del grafo reduced.
	 */
	private void constructSLDominanceDrawing( )
	{
		this.slDominance = new SLDominanceAlgorithm( this.reduced );
		this.slDominance.executeAlgorithm( );
		
		this.slDominance.addObserver( elementObserver );
		
		this.setChanged( );
		this.notifyObservers( );
	}
	
	/**
	 * Eliminar los vertices dummy agregando la antigua arista pero ahora
	 * con bends en el punto donde se ubicaban los vertices dummy.
	 * Cada vertice dummy solo puede tener grado de entrada = 1 y grado de salida = 1.
	 */
	private void turnDummyVerticesIntoBends( )
	{
		Graph graph = this.slDominance.getDrawing( ).getGraph( ); 
		Vertex from;
		Vertex to;
		Edge e;
		List<Point2D> bends;
		
		for ( Vertex d : this.dummyVertices )
		{
			// Determinar vertices from y to de la arista
			from = graph.getNeighborsIn( d ).iterator( ).next( );
			to = graph.getNeighborsOut( d ).iterator( ).next( );
			// Crear la nueva arista
			e = new Edge( true, from, to );
			// Agregar lista de bends
			bends = new ArrayList<Point2D>( );
			bends.add( graph.vertices.get( graph.vertices.indexOf( d ) ).getPoint2D( ) );
			e.setBends( bends );
			// Borrar el vertice dummy y se borraran las dos aristas incidentes
			graph.edges.removeAll( graph.getEdges( from, d ) );
			graph.edges.removeAll( graph.getEdges( d, to ) );
			graph.vertices.remove( d );
			// Agregar la nueva arista
			graph.edges.add( e );
		}
	}
}
