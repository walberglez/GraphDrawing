/**
 * VisibilityRepresentationAlgorithm.java
 */
package pfc.models.algorithms.visibilityrepresentation;

import java.util.*;

import pfc.models.*;
import pfc.models.algorithms.DualGraph;
import pfc.resources.StringBundle;
import pfc.utilities.GraphUtilities;


/**
 * @author    Walber Gonzalez
 * 
 * UMLGraph
 * @navassoc - - "-stGraphG" Graph
 * @navassoc - - "-stDualG" DualGraph
 * @navassoc - - "-drawing" VisibilityRepresentationDrawing
 * @depend - - - GraphUtilities
 * @depend - - - StringBundle
 */
public class VisibilityRepresentationAlgorithm extends ObservableModel
{
	/**
	 * Enumerado con las etapas de ejecucion del algoritmo.
	 */
	private enum Step
	{
		DUAL_GRAPH,	TOPO_NUM_GRAPH_Y, TOPO_NUM_DUAL_X, HORIZONTAL_VERTICES, VERTICAL_EDGES
	}

	/**
	 * Grafo al cual se le aplicara el algoritmo. El grafo G es planar, st, aciclico, conexo.
	 */
	private final Graph			stGraphG;
	/**
	 * Grafo Dual G* de G.
	 */
	private DualGraph			stDualG;
	/**
	 * numeracion topologica Y del grafo G
	 */
	private Map<Vertex, Float>	topologicalNumberingY;
	/**
	 * numeracion topologica X del grafo G
	 */
	private Map<Vertex, Float>	topologicalNumberingX;
	/**
	 * modelo de la Representacion de Visibilidad
	 */
	private VisibilityRepresentationDrawing	drawing;
	/**
	 * estado actual de la ejecucion del algoritmo
	 */
	private	Step				step;
	/**
	 * objeto observador del grafo dual 
	 */
	private Observer			elementObserver;
	
	/**
	 * @param stGraphG grafo al que se le aplicara el algoritmo.
	 */
	public VisibilityRepresentationAlgorithm ( Graph stGraphG )
	{		
		this.stGraphG = new Graph( stGraphG.toString( ) );
		
		this.stDualG = new DualGraph( this.stGraphG );

		this.drawing = new VisibilityRepresentationDrawing( );

		this.elementObserver = new Observer( )
		{
			@Override
			public void update(Observable o, Object arg)
			{
				VisibilityRepresentationAlgorithm.this.setChanged( );
				VisibilityRepresentationAlgorithm.this.notifyObservers( );
			}
		};
		this.stDualG.addObserver( elementObserver );
		this.stGraphG.addObserver( elementObserver );
		this.drawing.addObserver( elementObserver );
				
		this.topologicalNumberingY = new HashMap<Vertex, Float>( );
		this.topologicalNumberingX = new HashMap<Vertex, Float>( );
				
		this.step = Step.DUAL_GRAPH;
	}
	
	/**
	 * @return    Graph dual
	 */
	public Graph getStDualG ( )
	{
		return this.stDualG;
	}
	
	/**
	 * @return    Graph graph
	 */
	public Graph getStGraphG ( )
	{
		return this.stGraphG;
	}
	
	/**
	 * @return    the topologicalNumberingY
	 */
	public Map<Vertex, Float> getTopologicalNumberingY( )
	{
		return topologicalNumberingY;
	}

	/**
	 * @return    the topologicalNumberingX
	 */
	public Map<Vertex, Float> getTopologicalNumberingX( )
	{
		return topologicalNumberingX;
	}

	/**
	 * @return    the drawing
	 */
	public VisibilityRepresentationDrawing getDrawing( )
	{
		return drawing;
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
		case DUAL_GRAPH:
			algorithmExplanation = StringBundle.get( "visibility_representation_algorithm_step_dual" );
			break;
		case TOPO_NUM_GRAPH_Y:
			algorithmExplanation = StringBundle.get( "visibility_representation_algorithm_step_topo_num_graph_y" );
			break;
		case TOPO_NUM_DUAL_X:
			algorithmExplanation = StringBundle.get( "visibility_representation_algorithm_step_topo_num_dual_x" );
			break;
		case HORIZONTAL_VERTICES:
			algorithmExplanation = StringBundle.get( "visibility_representation_algorithm_step_horizontal_vertices" );
			break;
		case VERTICAL_EDGES:
			algorithmExplanation = StringBundle.get( "visibility_representation_algorithm_step_vertical_edges" );
			break;
		}
		
		return algorithmExplanation;
	}

	/**
	 * Ejecuta las distintas etapas del algoritmo de Representacion de Visibilidad
	 * descrito en el libro {@link "http://www.cs.brown.edu/~rt/gdbook.html"}, pag 100.
	 */
	public void executeAlgorithm ( )
	{
		this.step = Step.DUAL_GRAPH;
		this.stDualG.constructDualGraph( );
		
		this.step = Step.TOPO_NUM_GRAPH_Y;
		this.topologicalNumberingY( );
		
		this.step = Step.TOPO_NUM_DUAL_X;
		this.topologicalNumberingX( );
		
		this.step = Step.HORIZONTAL_VERTICES;
		this.constructHorizontalVertices( );
		
		this.step = Step.VERTICAL_EDGES;
		this.constructVerticalEdges( );
	}
	
	/**
	 * Calcular la numeracion topologica Y para el grafo primal G.
	 * Actualizar las etiquetas de cada vertice de G con su numeracion topologica.
	 */
	private void topologicalNumberingY ( )
	{
		this.topologicalNumberingY = GraphUtilities.getTopologicalNumbering( this.stDualG.getVertexS( ), this.stGraphG );
		
		for ( Vertex v : this.stGraphG.vertices )
		{
			this.stGraphG.suspendNotifications( true );
			v.isSelected.set( true );
			// para mostrar el label original en la representacion de visibilidad
			v.tag.set( v.label.get( ) );
			this.stGraphG.suspendNotifications( false );
			
			v.label.set( this.topologicalNumberingY.get( v ) + "" );
			
			this.stGraphG.suspendNotifications( true );
			v.isSelected.set( false );
			this.stGraphG.suspendNotifications( false );
		}
	}
	
	/**
	 * Calcular la numeracion topologica X para el grafo dual G*.
	 * Actualizar las etiquetas de cada vertice de G* con su numeracion topologica.
	 */
	private void topologicalNumberingX ( )
	{
		this.topologicalNumberingX = GraphUtilities.getTopologicalNumbering( this.stDualG.getFaceS( ), this.stDualG );
		
		for ( Vertex v : this.stDualG.vertices )
		{
			this.stDualG.suspendNotifications( true );
			v.isSelected.set( true );
			// para mostrar el label original en la representacion de visibilidad
			v.tag.set( v.label.get( ) );
			this.stDualG.suspendNotifications( false );
			
			v.label.set( this.topologicalNumberingX.get( v ) + "" );
			
			this.stDualG.suspendNotifications( true );
			v.isSelected.set( false );
			this.stDualG.suspendNotifications( false );
		}
	}

	/**
	 * Obtener la numeracion topologica Y correspondiente al vertice v.
	 * Precondition: v es un vertice (o cara) del grafo primal G
	 * @param v {@code Vertex}
	 * @return numeracion {@code Integer}
	 */
	private Integer numberY ( Vertex v )
	{
		return this.topologicalNumberingY.get( v ).intValue( );
	}
	
	/**
	 * Obtener la numeracion topologica X correspondiente al vertice v.
	 * Precondition: v es un vertice del grafo dual G*
	 * @param v {@code Vertex}
	 * @return numeracion {@code Integer}
	 */
	private Integer numberX ( Vertex v )
	{
		return this.topologicalNumberingX.get( v ).intValue( );
	}
	
	/**
	 * Determinar para cada vertice v de G las coordenadas
	 * de su segmento horizontal.
	 * Pseudo-codigo:
	 * 	Para cada vertice v de G hacer
	 * 		y(T(v)) = Y(v)
	 * 		xL(T(v)) = X(left(v))
	 * 		xR(T(v)) = X(right(v)) - 1
	 */
	private void constructHorizontalVertices ( )
	{
		VertexSegment vSegment;
		for ( Vertex v : this.stGraphG.vertices )
		{			
			vSegment = new VertexSegment(
					this.numberY( v ),
					this.numberX( this.stDualG.left( v ) ),
					this.numberX( this.stDualG.right( v ) ) - 1,
					v );
			
			this.drawing.vertexSegments.add( vSegment );
		}
	}
	
	/**
	 * Determinar para cada arista e de G las coordenadas
	 * de su segmento vertical.
	 * Pseudo-codigo:
	 * 	Para cada arista e de G hacer
	 * 		x(T(e)) = X(left(e))
	 *		yB(T(e)) = Y(orig(e))
	 *		yT(T(e)) = Y(dest(e))
	 */
	private void constructVerticalEdges ( )
	{
		EdgeSegment eSegment;
		for ( Edge e : this.stGraphG.edges )
		{
			eSegment = new EdgeSegment(
					this.numberX( this.stDualG.left( e ) ),
					this.numberY( this.stDualG.orig( e ) ),
					this.numberY( this.stDualG.dest( e ) ),
					e );
			
			this.drawing.edgeSegments.add( eSegment );
		}
	}
}