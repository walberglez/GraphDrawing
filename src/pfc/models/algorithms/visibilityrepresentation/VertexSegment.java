package pfc.models.algorithms.visibilityrepresentation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import pfc.models.ObservableModel;
import pfc.models.Vertex;
import pfc.utilities.JsonUtilities;


/**
 * Vertice Segmento (Horizontal) de la Representacion de Visibilidad
 * @author    Walber Gonzalez
 * 
 * UMLGraph
 * @navassoc - - "*\n-vertex" Vertex
 * @depend - - - JsonUtilities
 */
public class VertexSegment extends ObservableModel
{
	/**
	 * coordenada y del segmento vertice
	 */
	public final Property<Integer>	yCoordinate;
	/**
	 * coordenada x left del segmento vertice
	 */
	public final Property<Integer>	xLeftCoordinate;
	/**
	 * coordenada x right del segmento vertice
	 */
	public final Property<Integer>	xRightCoordinate;
	/**
	 * Vertice original que representa este segmento
	 */
	public Vertex					vertex;
	/**
	 * identificador unico utilizado para serializar y deserializar.
	 */
	public final Property<UUID>		id;
	
	public VertexSegment ( )
	{
		this( 0, 0, 0);
	}

	/**
	 * @param yCoordinate Integer
	 * @param xLeftCoordinate Integer
	 * @param xRightCoordinate Integer
	 */
	public VertexSegment( Integer yCoordinate, Integer xLeftCoordinate,	Integer xRightCoordinate )
	{
		this( yCoordinate, xLeftCoordinate, xRightCoordinate, null );
	}
	
	/**
	 * @param yCoordinate Integer
	 * @param xLeftCoordinate Integer
	 * @param xRightCoordinate Integer
	 * @param vertex Vertex
	 */
	public VertexSegment( Integer yCoordinate, Integer xLeftCoordinate,	Integer xRightCoordinate, Vertex vertex )
	{
		this.yCoordinate = new Property<Integer>( yCoordinate );
		this.xLeftCoordinate = new Property<Integer>( xLeftCoordinate );
		this.xRightCoordinate = new Property<Integer>( xRightCoordinate );
		this.vertex = vertex;		
		this.id = new Property<UUID>( UUID.randomUUID( ) );
	}
	
	/**
	 * Construir un {@code VertexSegment} a partir del {@code Map} de propiedades especificado.
	 * @param members {@code Map<String, Object>}
	 */
	public VertexSegment ( Map<String, Object> members )
	{
		this.id = new Property<UUID>( UUID.fromString( (String) members.get( "id" ) ) );
		// vertice
		Object vertex = members.get( "vertex" );
		if ( vertex instanceof Map<?, ?> )
		{
			@SuppressWarnings("unchecked")
			Map<String, Object> vertexPropertyMap = (Map<String, Object>) vertex;
			this.vertex = new Vertex( vertexPropertyMap );
		}
		this.yCoordinate = new Property<Integer>( (Integer) members.get( "yCoordinate" ) );
		this.xLeftCoordinate = new Property<Integer>( (Integer) members.get( "xLeftCoordinate" ) );
		this.xRightCoordinate = new Property<Integer>( (Integer) members.get( "xRightCoordinate" ) );
	}
	
	/**
	 * Construir un {@code VertexSegment} a patir del texto JSON especificado 
	 * @param json String
	 * @see #toString()
	 */
	public VertexSegment( String json )
	{
		this( JsonUtilities.parseObject( json ) );
	}
	
	@Override
	public String toString ( )
	{
		Map<String, Object> members = new HashMap<String, Object>( );
		
		members.put( "id", this.id );
		members.put( "vertex", this.vertex );
		members.put( "yCoordinate", this.yCoordinate );
		members.put( "xLeftCoordinate", this.xLeftCoordinate );
		members.put( "xRightCoordinate", this.xRightCoordinate );

		return JsonUtilities.formatObject( members );
	}
}