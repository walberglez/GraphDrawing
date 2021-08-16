/**
 * DominanceDrawing.java
 */
package pfc.models.algorithms.sldominance;

import java.util.*;

import pfc.models.Graph;
import pfc.models.ObservableModel;
import pfc.models.Vertex;
import pfc.utilities.JsonUtilities;


/**
 * @author Walber Gonzalez
 * 
 * UMLGraph
 * @navassoc - - "-graph" Graph
 * @depend - - - JsonUtilities
 */
public class DominanceDrawing extends ObservableModel
{	
	/**
	 * Grafo 
	 */
	private final Graph		graph;
	/**
	 * Un {@code Observer} para capturar los cambios en los segmentos
	 */
	private final Observer	elementObserver;
	/**
	 * Un {@code boolean} que indica si notificar a todos los {@code Observer} o no.
	 */
	private boolean			notificationsSuspended;
	/**
	 * maxima coordenada en el eje X
	 */
	private Integer			maxX = null;
	/**
	 * maxima coordenada en el eje Y
	 */
	private Integer			maxY = null;
	
	/**
	 * @param areLoopsAllowed {@link Boolean}
	 * @param areDirectedEdgesAllowed {@link Boolean}
	 * @param areMultipleEdgesAllowed {@link Boolean}
	 * @param areCyclesAllowed {@link Boolean}
	 */
	public DominanceDrawing(
			boolean areLoopsAllowed,
			boolean areDirectedEdgesAllowed,
			boolean areMultipleEdgesAllowed,
			boolean areCyclesAllowed )
	{
		this.notificationsSuspended = false;
		
		this.elementObserver = new Observer( )
		{
			@Override
			public void update( Observable o, Object arg )
			{
				DominanceDrawing.this.setChanged( );
				
				if( DominanceDrawing.this.notificationsSuspended == false )
					DominanceDrawing.this.notifyObservers( arg );
			}
		};
		
		this.graph = new Graph( "dominance",
				areLoopsAllowed,
				areDirectedEdgesAllowed,
				areMultipleEdgesAllowed,
				areCyclesAllowed );
		this.graph.addObserver( elementObserver );
	}
	
	/**
	 * @param graph {@link Graph}
	 */
	public DominanceDrawing( Graph graph )
	{
		this.notificationsSuspended = false;
		
		this.elementObserver = new Observer( )
		{
			@Override
			public void update( Observable o, Object arg )
			{
				DominanceDrawing.this.setChanged( );
				
				if( DominanceDrawing.this.notificationsSuspended == false )
					DominanceDrawing.this.notifyObservers( arg );
			}
		};
		
		this.graph = graph;
		this.graph.addObserver( elementObserver );
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * @param attributes Map<String, Object>
	 */
	public DominanceDrawing( Map<String, Object> attributes )
	{
		this( new Graph( (Map<String, Object>) attributes.get( "graph" ) ) );
	}
	
	/**
	 * @param json {@link String}
	 */
	public DominanceDrawing( String json )
	{
		this( JsonUtilities.parseObject( json ) );
	}
	
	/**
	 * @return the graph
	 */
	public Graph getGraph( )
	{
		return this.graph;
	}

	/**
	 * @return {@link Boolean} whether this graph is empty or not.
	 */
	public boolean isEmpty( )
	{
		return this.graph.vertices.isEmpty( ) && this.graph.edges.isEmpty( );
	}
	
	@Override
	public String toString( )
	{
		Map<String, Object> members = new HashMap<String, Object>( );

		members.put( "graph", this.graph );
		
		return JsonUtilities.formatObject( members );
	};
	
	/**
	 * Suspender las notificaciones o reactivarlas.
	 * @param suspend {@code boolean} indica suspenderlas con {@code true} y activarlas con {@code false}
	 * @return {@code true} si las notificaciones fueron antes suspendidas o {@code false} en otro caso.
	 */
	public boolean suspendNotifications( boolean suspend )
	{
		boolean wasSuspended = this.notificationsSuspended;
		this.notificationsSuspended = suspend;
		return wasSuspended;
	}
	
	/**
	 * obtener la coordenada x maxima (max topologicalNumberingX)
	 * @return maxX {@code Integer}
	 */
	public Integer getMaxXCoordinate( )
	{
		if ( this.maxX == null )
		{
			this.setMaxCoordinates( );
		}
		return this.maxX;
	}
	
	/**
	 * obtener la coordenada Y maxima (max topologicalNumberingY)
	 * @return maxY {@code Integer}
	 */
	public Integer getMaxYCoordinate( )
	{
		if ( this.maxY == null )
		{			
			this.setMaxCoordinates( );
		}
		return this.maxY;
	}
	
	private void setMaxCoordinates ( )
	{
		if ( this.isEmpty( ) )
		{
			this.maxX = null;
			this.maxY = null;
		}
		else
		{
			this.maxX = 0;
			this.maxY = 0;

			for ( Vertex v : this.graph.vertices )
			{
				this.maxX = Math.max( this.maxX, v.x.get( ).intValue( ) );
				this.maxY = Math.max( this.maxY, v.y.get( ).intValue( ) );
			}
		}
	}
}
