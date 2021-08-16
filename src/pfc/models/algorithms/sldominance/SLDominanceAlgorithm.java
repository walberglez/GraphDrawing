/**
 * SLDominanceAlgorithm.java
 * 01/08/2011 04:23:26
 */
package pfc.models.algorithms.sldominance;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeSet;

import pfc.models.Edge;
import pfc.models.Graph;
import pfc.models.ObservableModel;
import pfc.models.Vertex;
import pfc.models.algorithms.Rotation;
import pfc.resources.StringBundle;


/**
 * @author    Walber Gonzalez
 * 
 * UMLGraph
 * @navassoc - - "-dominance" Graph
 * @navassoc - - "-drawing" DominanceDrawing
 * @depend - - - Edge
 * @depend - - - Vertex
 * @depend - - - Rotation
 * @depend - - - StringBundle
 */
public class SLDominanceAlgorithm extends ObservableModel
{
	/**
	 * Enumerado con las etapas de ejecucion del algoritmo.
	 */
	private enum Step
	{
		PRELIMINARY_LAYOUT, COMPACTION
	}

	/**
	 * Grafo dominance
	 */
	private Graph					dominance;
	/**
	 * Dominance Drawing
	 */
	private DominanceDrawing		drawing;
	/**
	 * Rotaciones alrededor de las aristas salientes de cada vertice
	 */
	private Map<Vertex, Rotation>	rotationsOut;
	/**
	 * Rotaciones alrededor de las aristas entrantes a cada vertice
	 */
	private Map<Vertex, Rotation>	rotationsIn;
	/**
	 * estado actual de la ejecucion del algoritmo
	 */
	private	Step					step;
	/**
	 * objeto observador del grafo dual 
	 */
	private Observer				elementObserver;

	private Integer 				count; 
	
	/**
	 * @param stGraphG grafo al que se le aplicara el algoritmo.
	 */
	public SLDominanceAlgorithm( Graph stGraphG )
	{
		this.dominance = new Graph( stGraphG.toString( ) );
		
		this.drawing = new DominanceDrawing(
				dominance.areLoopsAllowed,
				dominance.areDirectedEdgesAllowed,
				dominance.areMultipleEdgesAllowed,
				dominance.areCyclesAllowed );
		
		this.rotationsOut = new HashMap<Vertex, Rotation>( );
		
		this.rotationsIn = new HashMap<Vertex, Rotation>( );
		
		this.elementObserver = new Observer( )
		{
			@Override
			public void update( Observable o, Object arg )
			{
				SLDominanceAlgorithm.this.setChanged( );
				SLDominanceAlgorithm.this.notifyObservers( );
			}
		};
		this.drawing.addObserver( elementObserver );
		
		this.step = Step.PRELIMINARY_LAYOUT;
	}

	/**
	 * @return drawing DominanceDrawing
	 */
	public DominanceDrawing getDrawing( )
	{
		return this.drawing;
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
		case PRELIMINARY_LAYOUT:
			algorithmExplanation = StringBundle.get( "sl_dominance_algorithm_step_preliminary_layout" );
			break;
		case COMPACTION:
			algorithmExplanation = StringBundle.get( "sl_dominance_algorithm_step_compaction" );
			break;
		}
		
		return algorithmExplanation;
	}

	/**
	 * Ejecuta las distintas etapas del algoritmo Straight-Line Dominance
	 * descrito en el libro {@link "http://www.cs.brown.edu/~rt/gdbook.html"}, pag 116.
	 */
	public void executeAlgorithm ( )
	{
		this.preprocessGraph( );
		
		this.step = Step.PRELIMINARY_LAYOUT;
		this.assignPreliminaryCoordinates( );
		
		this.step = Step.COMPACTION;
		this.assignFinalCoordinates( );
	}
	
	/**
	 * Construir las rotaciones de cada vertice del grafo respecto a sus vecinos
	 * a traves de sus aristas salientes.
	 */
	private void preprocessGraph( )
	{
		for ( Vertex v : this.dominance.vertices )
		{
			this.rotationsOut.put( v, new Rotation( v, this.dominance.getNeighborsOut( v ), true ) );
			this.rotationsIn.put( v, new Rotation( v, this.dominance.getNeighborsIn( v ), true ) );
		}
	}
	
	/**
	 * @param v {@link Vertex}
	 * @param n {@link Vertex}
	 * @return {@link Edge} next outgoing edge from vertex n which is neighbor of v. 
	 */
	private Edge nextOut( Vertex v, Vertex n )
	{
		Vertex to = this.rotationsOut.get( v ).higher( n );
		if ( to == null )
			return null;
		return this.dominance.getEdges( v, to ).iterator( ).next( );
	}
	
	/**
	 * @param v {@link Vertex}
	 * @param n {@link Vertex}
	 * @return {@link Edge} pred outgoing edge from vertex n which is neighbor of v. 
	 */
	private Edge predOut( Vertex v, Vertex n )
	{
		Vertex to = this.rotationsOut.get( v ).lower( n );
		if ( to == null )
			return null;
		return this.dominance.getEdges( v, to ).iterator( ).next( );
	}
	
	/**
	 * @param v {@link Vertex}
	 * @return {@link Edge} leftmost outgoing edge of vertex v. 
	 */
	private Edge firstOut( Vertex v )
	{
		Vertex to = this.rotationsOut.get( v ).first( );
		if ( to == null )
			return null;
		return this.dominance.getEdges( v, to ).iterator( ).next( );
	}
	
	/**
	 * @param v {@link Vertex}
	 * @return {@link Edge} rightmost incoming edge of vertex v. 
	 */
	private Edge lastOut( Vertex v )
	{
		Vertex to = this.rotationsOut.get( v ).last( );
		if ( to == null )
			return null;
		return this.dominance.getEdges( v, to ).iterator( ).next( );
	}
	
	/**
	 * @param v {@link Vertex}
	 * @return {@link Edge} leftmost incoming edge of vertex v. 
	 */
	private Edge firstIn( Vertex v )
	{
		Vertex from = this.rotationsIn.get( v ).last( );
		if ( from == null )
			return null;
		return this.dominance.getEdges( from, v ).iterator( ).next( );
	}
	
	/**
	 * @param v {@link Vertex}
	 * @return {@link Edge} rightmost incoming edge of vertex v. 
	 */
	private Edge lastIn( Vertex v )
	{
		Vertex from = this.rotationsIn.get( v ).first( );
		if ( from == null )
			return null;
		return this.dominance.getEdges( from, v ).iterator( ).next( );
	}
	
	/**
	 * Asignar las coordenadas preliminares X e Y a cada vertice del grafo.
	 */
	private void assignPreliminaryCoordinates( )
	{
		this.count = 0;
		labelX( this.dominance.getVertexSource( ) );
		this.count = 0;
		labelY( this.dominance.getVertexSource( ) );
		
		// agregar vertices al drawing
		for ( Vertex v : this.dominance.vertices )
		{
			this.drawing.getGraph( ).vertices.add( new Vertex( v.toString( ) ) );	
		}
		
		// agregar todas las aristas al drawing
		for ( Edge e : this.dominance.edges )
		{
			int indFrom = drawing.getGraph( ).vertices.indexOf( e.from );
			int indTo = drawing.getGraph( ).vertices.indexOf( e.to );
			e = new Edge(
					drawing.getGraph( ).areDirectedEdgesAllowed,
					drawing.getGraph( ).vertices.get( indFrom ),
					drawing.getGraph( ).vertices.get( indTo ) );
			this.drawing.getGraph( ).edges.add( e );
		}
	}

	/**
	 * Asignar coordenada X preliminar al vertice v.
	 * @param v {@link Vertex}
	 * @param count {@link Integer}
	 */
	private void labelX( Vertex v )
	{
		// asignacion de la coordenada X
		v.x.set( count.doubleValue( ) );
		count++;
		if ( v.equals( this.dominance.getVertexTarget( ) ) == false )
		{
			Edge e = firstOut( v );
			do {
				if ( e.equals( lastIn( e.to ) ) )
				{
					labelX( e.to );
				}
				e = nextOut( v, e.to );
			} while ( e != null );
		}
	}
	
	/**
	 * Asignar coordenada Y preliminar al vertice v.
	 * @param v {@link Vertex}
	 * @param count {@link Integer}
	 */
	private void labelY( Vertex v )
	{
		// asignacion de la coordenada Y
		v.y.set( count.doubleValue( ) );
		count++;
		if ( v.equals( this.dominance.getVertexTarget( ) ) == false )
		{
			Edge e = lastOut( v );
			do {
				if ( e.equals( firstIn( e.to ) ) )
				{
					labelY( e.to );
				}
				e = predOut( v, e.to );
			} while ( e != null );
		}
	}

	/**
	 * Asignar las coordenadas finales x e y cada vertice del grafo.
	 */
	private void assignFinalCoordinates( )
	{
		assignFinalCoordinatesX( );
		assignFinalCoordinatesY( );
	}

	/**
	 * Asignar las coordenadas x finales a los vertices
	 */
	private void assignFinalCoordinatesX( )
	{
		// Vertice ordenados segun su coordenada x
		TreeSet<Vertex>	xCoordinateVertices = initXCoordinateVertices( );
		Vertex u = xCoordinateVertices.first( );
		Vertex v;		
		while ( ( v = xCoordinateVertices.higher( u ) ) != null )
		{
			this.drawing.getGraph( ).suspendNotifications( true );
			
			if ( u.y.get( ) > v.y.get( ) || equalsDegreeInOut( u, v ) )
			{
				v.x.set( u.x.get( ) + 1 );
			}
			else
			{
				v.x.set( u.x.get( ) );
			}			
			u = v;
			this.drawing.getGraph( ).suspendNotifications( false );
			// notificar cambio de coordenada x de v
			this.setChanged( );
			this.notifyObservers( );
		}
	}

	/**
	 * Asignar las coordenadas y finales a los vertices
	 */
	private void assignFinalCoordinatesY( )
	{
		// Vertice ordenados segun su coordenada y
		TreeSet<Vertex>	yCoordinateVertices = initYCoordinateVertices( );
		Vertex u = yCoordinateVertices.first( );
		Vertex v;
		while ( ( v = yCoordinateVertices.higher( u ) ) != null )
		{
			this.drawing.getGraph( ).suspendNotifications( true );

			if ( u.x.get( ) > v.x.get( ) || equalsDegreeInOut( u, v ) )
			{
				v.y.set( u.y.get( ) + 1 );
			}
			else
			{
				v.y.set( u.y.get( ) );
			}
			u = v;
			this.drawing.getGraph( ).suspendNotifications( false );
			// notificar cambio de coordenada x de v
			this.setChanged( );
			this.notifyObservers( );
		}
	}
	
	/**
	 * Crear una lista de los vertices ordenados segun su coordenada X
	 */
	private TreeSet<Vertex> initXCoordinateVertices( )
	{
		TreeSet<Vertex>	xCoordinateVertices = new TreeSet<Vertex>( new Comparator<Vertex>( ) {
			@Override
			public int compare( Vertex o1, Vertex o2 )
			{
				return o1.x.get( ).compareTo( o2.x.get( ) );
			}
		} );
		// agregar todos los vertices para que los ordene.
		xCoordinateVertices.addAll( this.drawing.getGraph( ).vertices );
		return xCoordinateVertices;
	}

	/**
	 * Crear una lista de los vertices ordenados segun su coordenada Y
	 */
	private TreeSet<Vertex> initYCoordinateVertices( )
	{
		TreeSet<Vertex> yCoordinateVertices = new TreeSet<Vertex>( new Comparator<Vertex>( ) {
			@Override
			public int compare( Vertex o1, Vertex o2 )
			{
				return o1.y.get( ).compareTo( o2.y.get( ) );
			}
		} );
		// agregar todos los vertices para que los ordene.
		yCoordinateVertices.addAll( this.drawing.getGraph( ).vertices );
		return yCoordinateVertices;
	}
	
	/**
	 * Comparar si grado de salida de u es igual al grado de entrada en v.
	 * @param u {@link Vertex}
	 * @param v {@link Vertex}
	 * @return {@link Boolean}
	 */
	private boolean equalsDegreeInOut( Vertex u, Vertex v )
	{
		return this.dominance.getEdges( u, v ).isEmpty( ) == false
				&& ( this.dominance.getEdgesFrom( u ).size( ) == 1 )
						&& ( this.dominance.getEdgesTo( v ).size( ) == 1 ); 
	}
}
