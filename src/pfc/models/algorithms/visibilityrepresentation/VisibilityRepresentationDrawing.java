/**
 * VisibilityRepresentationDrawing.java
 */
package pfc.models.algorithms.visibilityrepresentation;

import java.util.*;

import pfc.models.Edge;
import pfc.models.ObservableModel;
import pfc.models.Vertex;
import pfc.utilities.JsonUtilities;


/**
 * @author Walber Gonzalez
 * @see VertexSegment
 * @see EdgeSegment
 * 
 * UMLGraph
 * @navassoc - - "*\n-vertexSegments" VertexSegment
 * @navassoc - - "*\n-edgeSegments" EdgeSegment
 * @depend - - - JsonUtilities
 */
public class VisibilityRepresentationDrawing extends ObservableModel
{
	/**
	 * Lista de segmentos vertice {@code VertexSegment}
	 */
	public final List<VertexSegment>			vertexSegments;
	/**
	 * Lista de segmentos arista {@code EdgeSegment}
	 */
	public final List<EdgeSegment>				edgeSegments;
	/**	
	 * Map de segmentos-vertice y su vertice 
	 */
	private final Map<Vertex, VertexSegment>	vertexToSegment;
	/**
	 * Map de segmentos-arist y su arista 
	 */
	private final Map<Edge, EdgeSegment>		edgeToSegment;
	/**
	 * Un {@code Observer} para capturar los cambios en los segmentos
	 */
	private final Observer						elementObserver;
	/**
	 * Un {@code boolean} que indica si notificar a todos los {@code Observer} o no.
	 */
	private boolean								notificationsSuspended;
	/**
	 * maxima coordenada en el eje X
	 */
	private Integer								maxX = null;
	/**
	 * maxima coordenada en el eje Y
	 */
	private Integer								maxY = null;
	
	public VisibilityRepresentationDrawing( )
	{
		this.notificationsSuspended = false;
		
		this.elementObserver = new Observer( )
		{
			@Override
			public void update( Observable o, Object arg )
			{
				VisibilityRepresentationDrawing.this.setChanged( );
				
				if( VisibilityRepresentationDrawing.this.notificationsSuspended == false )
					VisibilityRepresentationDrawing.this.notifyObservers( arg );
			}
		};
		
		this.vertexSegments = new VertexSegmentList( );
		
		this.edgeSegments = new EdgeSegmentList( );
		
		this.vertexToSegment = new HashMap<Vertex, VertexSegment>( );
		this.edgeToSegment = new HashMap<Edge, EdgeSegment>( );
	}
	
	@SuppressWarnings("unchecked")
	public VisibilityRepresentationDrawing( Map<String, Object> attributes )
	{
		this( );
		
		this.suspendNotifications( true );
		
		Map<String, Vertex> idToVertexMap = new HashMap<String, Vertex>( );
		// verticesSegments
		for ( Object vertexSegment : (Iterable<?>) attributes.get( "verticesSegments" ) )
		{
			if ( vertexSegment instanceof Map<?, ?> )
			{
				Map<String, Object> vSegmentPropertyMap = (Map<String, Object>) vertexSegment;
				VertexSegment newVertexSegment = new VertexSegment( vSegmentPropertyMap );
				this.vertexSegments.add( newVertexSegment );
				idToVertexMap.put( newVertexSegment.vertex.id.get( ).toString( ) , newVertexSegment.vertex );
			}
		}
		for ( Object edgeSegment : (Iterable<?>) attributes.get( "edgeSegments" ) )
			if ( edgeSegment instanceof Map<?, ?> )
				this.edgeSegments.add( new EdgeSegment( (Map<String, Object>) edgeSegment, idToVertexMap ) );
		
		this.suspendNotifications( false );
	}
	
	public VisibilityRepresentationDrawing( String json )
	{
		this( JsonUtilities.parseObject( json ) );
	}
	
	@Override
	public String toString( )
	{
		Map<String, Object> members = new HashMap<String, Object>( );

		members.put( "verticesSegments", this.vertexSegments );
		members.put( "edgeSegments", this.edgeSegments );
		
		return JsonUtilities.formatObject( members );
	};
	
	/**
	 * Obtener el VertexSegment que representa al Vertex v.
	 * @param v Vertex
	 * @return VertexSegment
	 */
	public VertexSegment getVertexSegment( Vertex v )
	{
		return this.vertexToSegment.get( v );
	}
	
	/**
	 * Obtener el EdgeSegment que representa a la Edge e.
	 * @param e Edge
	 * @return EdgeSegment
	 */
	public EdgeSegment getEdgeSegment( Edge e )
	{
		return this.edgeToSegment.get( e );
	}
	
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
			if ( this.vertexSegments.size( ) <= 0 )
				return 0;

			this.maxY = 0;

			for ( VertexSegment vS : this.vertexSegments )
			{
				this.maxY = Math.max( this.maxY, vS.yCoordinate.get( ) );
			}
			
			this.setMaxCoordinates( );
		}
		return this.maxY;
	}
	
	private void setMaxCoordinates ( )
	{
		if ( this.edgeSegments.isEmpty( ) && this.vertexSegments.isEmpty( ) )
		{
			this.maxX = null;
			this.maxY = null;
		}
		else
		{
			this.maxX = 0;
			this.maxY = 0;
			
			for ( EdgeSegment eS : this.edgeSegments )
			{
				this.maxX = Math.max( this.maxX, eS.xCoordinate.get( ) );
				this.maxY = Math.max( this.maxY, eS.yBottomCoordinate.get( ) );
				this.maxY = Math.max( this.maxY, eS.yTopCoordinate.get( ) );
			}
			for ( VertexSegment vS : this.vertexSegments )
			{
				this.maxY = Math.max( this.maxY, vS.yCoordinate.get( ) );
				this.maxX = Math.max( this.maxX, vS.xLeftCoordinate.get( ) );
				this.maxX = Math.max( this.maxX, vS.xRightCoordinate.get( ) );
			}
		}
	}
	
	/**
	 * Sobrescritura del comportamiento por defecto de ArrayList para agregar
	 * el tratamiento de los {@code Observer} de la lista de vertices segmentos
	 * @author walber
	 */
	@SuppressWarnings("serial")
	private class VertexSegmentList extends ArrayList<VertexSegment>
	{
		public VertexSegmentList( )
		{
			super( );
		}
		
		@Override
		public void add( int index, VertexSegment element )
		{
			if( this.contains( element ) )
				return;
			
			VisibilityRepresentationDrawing.this.suspendNotifications( true );
			
			super.add( element );
			VisibilityRepresentationDrawing.this.vertexToSegment.put( element.vertex, element );
			element.addObserver( VisibilityRepresentationDrawing.this.elementObserver );
			
			VisibilityRepresentationDrawing.this.suspendNotifications( false );
			
			VisibilityRepresentationDrawing.this.setChanged( );
			VisibilityRepresentationDrawing.this.notifyObservers( this );
		}
		
		@Override
		public boolean add( VertexSegment e )
		{
			int originalSize = super.size( );
			this.add( super.size( ), e );
			return originalSize != super.size( );
		}
		
		@Override
		public VertexSegment remove( int index )
		{
			VisibilityRepresentationDrawing.this.suspendNotifications ( true );
			
			VertexSegment removedT = super.remove( index );
			VisibilityRepresentationDrawing.this.vertexToSegment.remove( removedT.vertex );
			removedT.deleteObserver( VisibilityRepresentationDrawing.this.elementObserver );
			
			VisibilityRepresentationDrawing.this.suspendNotifications( false );
			
			VisibilityRepresentationDrawing.this.setChanged( );
			VisibilityRepresentationDrawing.this.notifyObservers( this );				

			return removedT;
		}
		
		@Override
		public boolean remove( Object o )
		{
			int index = this.indexOf( o );
			
			if( index == -1 )
				return false;
			this.remove( index );
			
			return true;
		}
		
		@Override
		public VertexSegment set( int index, VertexSegment element )
		{
			if( this.get( index ) == element )
				return element;
			else if( this.contains( element ) )
				return null;
			
			VisibilityRepresentationDrawing.this.suspendNotifications( true );
			
			VertexSegment oldT = super.get( index );
			VisibilityRepresentationDrawing.this.vertexToSegment.remove( oldT.vertex );
			oldT.deleteObserver( VisibilityRepresentationDrawing.this.elementObserver );
			super.set( index, element );
			VisibilityRepresentationDrawing.this.vertexToSegment.put( element.vertex, element );
			element.addObserver( VisibilityRepresentationDrawing.this.elementObserver );

			VisibilityRepresentationDrawing.this.suspendNotifications( false );
			
			VisibilityRepresentationDrawing.this.setChanged( );
			VisibilityRepresentationDrawing.this.notifyObservers( this );
			
			return oldT;
		}
	}
	
	/**
	 * Sobrescritura del comportamiento por defecto de ArrayList para agregar
	 * el tratamiento de los {@code Observer} a la lista de aristas segmentos
	 * @author walber
	 */
	@SuppressWarnings("serial")
	private class EdgeSegmentList extends ArrayList<EdgeSegment>
	{
		public EdgeSegmentList( )
		{
			super( );
		}
		
		@Override
		public void add( int index, EdgeSegment element )
		{
			if( this.contains( element ) )
				return;
			
			VisibilityRepresentationDrawing.this.suspendNotifications( true );
			
			super.add( element );
			VisibilityRepresentationDrawing.this.edgeToSegment.put( element.edge, element );
			element.addObserver( VisibilityRepresentationDrawing.this.elementObserver );
			
			VisibilityRepresentationDrawing.this.suspendNotifications( false );
			
			VisibilityRepresentationDrawing.this.setChanged( );
			VisibilityRepresentationDrawing.this.notifyObservers( this );
		}
		
		@Override
		public boolean add( EdgeSegment e )
		{
			int originalSize = super.size( );
			this.add( super.size( ), e );
			return originalSize != super.size( );
		}

		@Override
		public EdgeSegment remove( int index )
		{
			VisibilityRepresentationDrawing.this.suspendNotifications ( true );
			
			EdgeSegment removedT = super.remove( index );
			VisibilityRepresentationDrawing.this.edgeToSegment.remove( removedT.edge );
			removedT.deleteObserver( VisibilityRepresentationDrawing.this.elementObserver );
			
			VisibilityRepresentationDrawing.this.suspendNotifications( false );
			
			VisibilityRepresentationDrawing.this.setChanged( );
			VisibilityRepresentationDrawing.this.notifyObservers( this );				

			return removedT;
		}
		
		@Override
		public boolean remove( Object o )
		{
			int index = this.indexOf( o );
			
			if( index == -1 )
				return false;
			this.remove( index );
			
			return true;
		}
		
		@Override
		public EdgeSegment set( int index, EdgeSegment element )
		{
			if( this.get( index ) == element )
				return element;
			else if( this.contains( element ) )
				return null;
			
			VisibilityRepresentationDrawing.this.suspendNotifications( true );
			
			EdgeSegment oldT = super.get( index );
			VisibilityRepresentationDrawing.this.edgeToSegment.remove( element.edge );
			oldT.deleteObserver( VisibilityRepresentationDrawing.this.elementObserver );
			super.set( index, element );
			VisibilityRepresentationDrawing.this.edgeToSegment.put( element.edge, element );
			element.addObserver( VisibilityRepresentationDrawing.this.elementObserver );

			VisibilityRepresentationDrawing.this.suspendNotifications( false );
			
			VisibilityRepresentationDrawing.this.setChanged( );
			VisibilityRepresentationDrawing.this.notifyObservers( this );
			
			return oldT;
		}
	}
}
