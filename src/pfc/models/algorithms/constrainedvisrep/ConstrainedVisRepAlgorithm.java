/**
 * ConstrainedVisRepAlgorithm.java
 * 20/07/2011 00:09:27
 */
package pfc.models.algorithms.constrainedvisrep;

import java.util.*;

import pfc.models.Edge;
import pfc.models.Graph;
import pfc.models.ObservableModel;
import pfc.models.Vertex;
import pfc.models.algorithms.DirectedPath;
import pfc.models.algorithms.DualPathGraph;
import pfc.models.algorithms.NonIntersectingPathList;
import pfc.models.algorithms.visibilityrepresentation.EdgeSegment;
import pfc.models.algorithms.visibilityrepresentation.VertexSegment;
import pfc.models.algorithms.visibilityrepresentation.VisibilityRepresentationDrawing;
import pfc.resources.StringBundle;
import pfc.utilities.GraphUtilities;


/**
 * @author Walber Gonzalez
 *
 * UMLGraph
 * @navassoc - - "-stGraphG" Graph
 * @navassoc - - "-paths" NonIntersectingPathList
 * @navassoc - - "-stDualG" DualPathGraph
 * @navassoc - - "-drawing" VisibilityRepresentationDrawing
 * @depend - - - Edge
 * @depend - - - Vertex
 * @depend - - - DirectedPath
 * @depend - - - EdgeSegment
 * @depend - - - VertexSegment
 * @depend - - - GraphUtilities
 * @depend - - - StringBundle
 */
public class ConstrainedVisRepAlgorithm extends ObservableModel
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
	private final Graph						stGraphG;
	/**
	 * Paths definidos por el usuario
	 */
	private final NonIntersectingPathList	paths;
	/**
	 * Grafo Dual G* de G.
	 */
	private DualPathGraph					stDualG;
	/**
	 * numeracion topologica Y del grafo G
	 */
	private Map<Vertex, Float>				topologicalNumberingY;
	/**
	 * numeracion topologica X del grafo G
	 */
	private Map<Vertex, Float>				topologicalNumberingX;
	/**
	 * modelo de la Representacion de Visibilidad
	 */
	private VisibilityRepresentationDrawing	drawing;
	/**
	 * estado actual de la ejecucion del algoritmo
	 */
	private	Step							step;
	/**
	 * objeto observador del grafo dual 
	 */
	private Observer						elementObserver;
	
	/**
	 * @param stGraphG grafo al que se le aplicara el algoritmo.
	 */
	public ConstrainedVisRepAlgorithm ( Graph stGraphG, NonIntersectingPathList paths )
	{		
		this.stGraphG = new Graph( stGraphG.toString( ) );
		
		this.paths = paths;
		
		this.stDualG = new DualPathGraph( this.stGraphG, this.paths );

		this.drawing = new VisibilityRepresentationDrawing( );

		this.elementObserver = new Observer( )
		{
			@Override
			public void update(Observable o, Object arg)
			{
				ConstrainedVisRepAlgorithm.this.setChanged( );
				ConstrainedVisRepAlgorithm.this.notifyObservers( );
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
	 * Obtener la numeracion topologica Y correspondiente al vertice v.
	 * Precondition: v es un vertice (o cara) del grafo primal G
	 * @param v {@code Vertex}
	 * @return numeracion {@code Integer}
	 */
	public Integer numberY ( Vertex v )
	{
		return this.topologicalNumberingY.get( v ).intValue( );
	}
	
	/**
	 * Obtener la numeracion topologica X correspondiente al vertice v.
	 * Precondition: v es un vertice del grafo dual G*
	 * @param v {@code Vertex}
	 * @return numeracion {@code Integer}
	 */
	public Integer numberX ( Vertex v )
	{
		return this.topologicalNumberingX.get( v ).intValue( );
	}
	
	/**
	 * @param path {@link DirectedPath}
	 * @return {@code Vertex} face que representa al path dado.
	 */
	public Vertex getPathFace( DirectedPath path )
	{
		return this.stDualG.getPathFace( path );
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
			algorithmExplanation = StringBundle.get( "constrained_vis_rep_algorithm_step_dual" );
			break;
		case TOPO_NUM_GRAPH_Y:
			algorithmExplanation = StringBundle.get( "constrained_vis_rep_algorithm_step_topo_num_graph_y" );
			break;
		case TOPO_NUM_DUAL_X:
			algorithmExplanation = StringBundle.get( "constrained_vis_rep_algorithm_step_topo_num_dual_x" );
			break;
		case HORIZONTAL_VERTICES:
			algorithmExplanation = StringBundle.get( "constrained_vis_rep_algorithm_step_horizontal_vertices" );
			break;
		case VERTICAL_EDGES:
			algorithmExplanation = StringBundle.get( "constrained_vis_rep_algorithm_step_vertical_edges" );
			break;
		}
		
		return algorithmExplanation;
	}

	/**
	 * Ejecuta las distintas etapas del algoritmo de Representacion de Visibilidad con Restricciones
	 * descrito en el libro {@link "http://www.cs.brown.edu/~rt/gdbook.html"}, pag 105.
	 */
	public void executeAlgorithm ( )
	{
		this.step = Step.DUAL_GRAPH;
		this.stDualG.constructDualPathGraph( );
		
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
		this.topologicalNumberingX = GraphUtilities.getTopologicalNumbering(
				this.stDualG.getFaceS( ), this.stDualG,
				-0.5f, 0.5f  );
		
		for ( Vertex v : this.stDualG.vertices )
		{
			this.stDualG.suspendNotifications( true );
			v.isSelected.set( true );
			this.stDualG.suspendNotifications( false );
			
			v.label.set( this.topologicalNumberingX.get( v ) + "" );
			
			this.stDualG.suspendNotifications( true );
			v.isSelected.set( false );
			this.stDualG.suspendNotifications( false );
		}
	}
	
	/**
	 * Determinar para cada vertice v de G las coordenadas
	 * de su segmento horizontal.
	 * Pseudo-codigo:
	 * 	Para cada vértice v de G hacer:
	 * 		dibujar un segmento horizontal (vértice) con
	 * 		y(T(v)) = Y(v)
	 * 		xL(T(v)) = min {X(p)} para todos paths p pertenece v 
	 * 		xR(T(v)) = max {X(p)} para todos paths p pertenece v
	 */
	private void constructHorizontalVertices ( )
	{
		VertexSegment vSegment;
		for ( Vertex v : this.stGraphG.vertices )
		{			
			List<Integer> numberXPaths = this.getNumberXPaths( v );
			vSegment = new VertexSegment(
					this.numberY( v ),
					Collections.min( numberXPaths ),
					Collections.max( numberXPaths ),
					v );
			
			this.drawing.vertexSegments.add( vSegment );
		}
	}
	
	/**
	 * Determinar para cada arista e de G las coordenadas
	 * de su segmento vertical.
	 * Pseudo-codigo:
	 * 	Para cada path p de PI hacer:
	 * 		Para cada arista e de p hacer:
	 * 			dibujar un segmento vertical (arista) con
	 * 			x(T(e)) = X(p)
	 * 			yB(T(e)) = Y(orig(e))
	 * 			yT(T(e)) = Y(dest(e))
	 */
	private void constructVerticalEdges ( )
	{
		EdgeSegment eSegment;
		for ( DirectedPath p : this.paths ) {
			for ( Edge e : p.getEdges( ) )
			{
				eSegment = new EdgeSegment(
						this.numberX( this.stDualG.getPathFace( p ) ),
						this.numberY( this.stDualG.orig( e ) ),
						this.numberY( this.stDualG.dest( e ) ),
						e );

				this.drawing.edgeSegments.add( eSegment );
			}
		}
	}

	/**
	 * Obtener la lista con la numeracion topologica X
	 * de cada vertice path en el grafo dual G* que contiene
	 * el vertice v del grafo primal G.
	 * List no puede estar empty, los paths cubren todo las aristas del grafo,
	 * por tanto, cubre todos los vertices. 
	 * @param v Vertex
	 * @return list List<Integer>
	 */
	private List<Integer> getNumberXPaths( Vertex v )
	{
		List<Integer> list = new ArrayList<Integer>( );
		Set<DirectedPath> allPaths = this.paths.getAll( v );
		for ( DirectedPath p : allPaths )
		{
			list.add( this.numberX( this.stDualG.getPathFace( p ) ) );
		}
		return list;
	}
}
