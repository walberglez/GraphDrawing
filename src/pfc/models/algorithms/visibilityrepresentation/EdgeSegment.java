package pfc.models.algorithms.visibilityrepresentation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import pfc.models.Edge;
import pfc.models.ObservableModel;
import pfc.models.Vertex;
import pfc.utilities.JsonUtilities;


/**
 * Arista Segmento (Vertical) de la Representacion de Visibilidad
 * @author    walber
 * 
 * UMLGraph
 * @navassoc - - "*\n-edge" Edge
 * @depend - - - JsonUtilities
 */
public class EdgeSegment extends ObservableModel
{
	/**
	 * coordenada x del segmento arista
	 */
	public final Property<Integer>	xCoordinate;
	/**
	 * coordenada y bottom del segmento arista
	 */
	public final Property<Integer>	yBottomCoordinate;
	/**
	 * coordenada y top del segmento arista
	 */
	public final Property<Integer>	yTopCoordinate;
	/**
	 * Vertice original que representa este segmento
	 */
	public Edge						edge;
	/**
	 * identificador unico utilizado para serializar y deserializar.
	 */
	public final Property<UUID>		id;
	
	public EdgeSegment ( )
	{
		this( 0, 0, 0 );
	}
	
	/**
	 * @param xCoordinate Integer
	 * @param yBottomCoordinate Integer
	 * @param yTopCoordinate Integer
	 */
	public EdgeSegment( Integer xCoordinate, Integer yBottomCoordinate, Integer yTopCoordinate )
	{
		this( xCoordinate, yBottomCoordinate, yTopCoordinate, null );
	}
	
	/**
	 * @param xCoordinate Integer
	 * @param yBottomCoordinate Integer
	 * @param yTopCoordinate Integer
	 * @param edge Edge
	 */
	public EdgeSegment( Integer xCoordinate, Integer yBottomCoordinate, Integer yTopCoordinate, Edge edge )
	{
		this.xCoordinate = new Property<Integer>( xCoordinate );
		this.yBottomCoordinate = new Property<Integer>( yBottomCoordinate );
		this.yTopCoordinate = new Property<Integer>( yTopCoordinate );
		this.edge = edge;
		this.id = new Property<UUID>( UUID.randomUUID( ) );
	}
	
	/**
	 * Construir un {@code EdgeSegment} a partir del {@code Map} de propiedades especificado.
	 * @param members {@code Map<String, Object>}
	 */
	public EdgeSegment( Map<String, Object> members, Map<String, Vertex> vertices )
	{
		this.id = new Property<UUID>( UUID.fromString( (String) members.get( "id" ) ) );
		// vertice
		Object edge = members.get( "edge" );
		if ( edge instanceof Map<?, ?> )
		{
			@SuppressWarnings("unchecked")
			Map<String, Object> edgePropertyMap = (Map<String, Object>) edge;
			this.edge = new Edge( edgePropertyMap, vertices );
		}
		this.xCoordinate = new Property<Integer>( (Integer) members.get( "xCoordinate" ) );
		this.yBottomCoordinate = new Property<Integer>( (Integer) members.get( "yBottomCoordinate" ) );
		this.yTopCoordinate = new Property<Integer>( (Integer) members.get( "yTopCoordinate" ) );
	}
	
	/**
	 * @return booelan si es una arista corta.
	 */
	public boolean isShortEdge( )
	{
		return Math.abs( this.yTopCoordinate.get( ) - this.yBottomCoordinate.get( ) ) == 1;
	}
	
	/**
	 * Construir un {@code EdgeSegment} a patir del texto JSON especificado 
	 * @param json String
	 * @see #toString()
	 */
	public EdgeSegment( String json, Map<String, Vertex> vertices )
	{
		this( JsonUtilities.parseObject( json),  vertices );
	}
	
	@Override
	public String toString ( )
	{
		Map<String, Object> members = new HashMap<String, Object>( );

		members.put( "id", this.id );
		members.put( "edge", this.edge );
		members.put( "xCoordinate", this.xCoordinate );
		members.put( "yBottomCoordinate", this.yBottomCoordinate );
		members.put( "yTopCoordinate", this.yTopCoordinate );

		return JsonUtilities.formatObject( members );
	}
}
