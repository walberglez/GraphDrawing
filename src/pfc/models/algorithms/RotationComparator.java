/**
 * RotationComparator.java
 * 15/07/2011 20:37:00
 */
package pfc.models.algorithms;

import java.util.Comparator;

import pfc.models.Vertex;
import pfc.utilities.GeometryUtilities;


/**
 * Clase para comparar dos vertices segun el angulo que forman
 * con el segmento formado el vertice center y el de referencia.
 * @author walber
 *
 */
public class RotationComparator implements Comparator<Vertex>
{
    /**
     * Vertex center of rotation
     */
    private final Vertex            center;
    /**
     * Vertex reference to calculate the angle
     */
    private final Vertex            reference;
    
    /**
     * @param center Vertex
     * @param reference Vertex
     */
    public RotationComparator( Vertex center, Vertex reference )
    {
        this.center = center;
        this.reference = reference;
    }

    @Override
    public int compare( Vertex v1, Vertex v2 )
    {
        double angleV1 = GeometryUtilities.getClockwiseAngle(
                this.reference.getPoint2D( ),
                this.center.getPoint2D( ),
                v1.getPoint2D( ) );
        double angleV2 = GeometryUtilities.getClockwiseAngle(
                this.reference.getPoint2D( ),
                this.center.getPoint2D( ),
                v2.getPoint2D( ) );
        
        if ( angleV1 < angleV2 )
            return -1;
        if ( angleV1 > angleV2 )
            return 1;
        return 0;
    }

	/**
	 * @return the center
	 */
	public Vertex getCenter( )
	{
		return this.center;
	}

	/**
	 * @return the reference
	 */
	public Vertex getReference( )
	{
		return this.reference;
	}
    
}