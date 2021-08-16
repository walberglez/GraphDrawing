/**
 * Rotation.java
 * 15/07/2011 15:53:48
 */
package pfc.models.algorithms;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import pfc.models.Vertex;
import pfc.utilities.GeometryUtilities;


/**
 * @author Walber Gonzalez
 * 
 * UMLGraph
 * @navassoc - - "*\n" Vertex
 * @depend - - - RotationComparator
 */
public class Rotation extends TreeSet<Vertex>
{
    private static final long serialVersionUID = -297457897744190066L;
    
    /**
     * Constructor definiendo el vertice centro de la rotacion.
     * El vertice referencia sera tomado como el punto ( x=center.x, y=0).
     * @param center Vertex
     */
    public Rotation( Vertex center )
    {
        this( center, new Vertex( center.x.get( ), 0 ) );
    }
    
    /**
     * Constructor definiendo el vertice centro de la rotacion y el vertice referencia.
     * @param center Vertex
     * @param reference Vertex
     */
    public Rotation( Vertex center, Vertex reference )
    {
        super( new RotationComparator( center, reference ) );
    }
    
    /**
     * Constructor definiendo el vertice centro de la rotacion y los vertices vecinos.
     * @param center Vertex
     * @param neighbors Set<Vertex>
     */
    public Rotation( Vertex center, Set<Vertex> neighbors )
    {
        this( center, new Vertex( center.x.get( ), 0 ), neighbors );
    }
    
    /**
     * Constructor definiendo el vertice centro de la rotacion, el vertice referencia
     * y el conjunto de vecinos.
     * @param center Vertex
     * @param reference Vertex
     * @param neighbors Set<Vertex>
     */
    public Rotation( Vertex center, Vertex reference, Set<Vertex> neighbors )
    {
        super( new RotationComparator( center, reference ) );
        this.addAll( neighbors );
    }
    
    /**
     * Constructor definiendo el vertice centro de la rotacion, el conjunto de vecinos
     * y determinando el vertice referencia.
     * @param center {@link Vertex}
     * @param neighbors {@link Set}
     * @param reference {@link Boolean}
     */
    public Rotation( Vertex center, Set<Vertex> neighbors, boolean reference )
    {
        this( center, determineReference( center, neighbors ), neighbors );
    }
    
    /**
     * Determinar el vertice referencia de la rotacion.
     * Seleccionamos el vertice referencia como el que se encuentra mas a la izquierda
     * formando el menor angulo con el vertice mas alejado de su rotacion.
	 * @param center {@link Vertex}
	 * @param neighbors {@link Set}
	 * @return {@link Vertex}
	 */
	private static Vertex determineReference( Vertex center, Set<Vertex> neighbors )
	{
		TreeSet<Rotation> rotations = new TreeSet<Rotation>( new Comparator<Rotation>( ) {

			@Override
			public int compare( Rotation r1, Rotation r2 )
			{
				RotationComparator rc1 = (RotationComparator) r1.comparator( );
				RotationComparator rc2 = (RotationComparator) r2.comparator( );
				double angleR1 = GeometryUtilities.getClockwiseAngle(
		                rc1.getReference( ).getPoint2D( ),
		                rc1.getCenter( ).getPoint2D( ),
		                r1.last( ).getPoint2D( ) );
				double angleR2 = GeometryUtilities.getClockwiseAngle(
		                rc2.getReference( ).getPoint2D( ),
		                rc2.getCenter( ).getPoint2D( ),
		                r2.last( ).getPoint2D( ) );
		        
		        if ( angleR1 < angleR2 )
		            return -1;
		        if ( angleR1 > angleR2 )
		            return 1;
		        return 0;
			}
		} );
		// construir las rotaciones tomando cada vertice vecino como referencia
		for ( Vertex n : neighbors )
		{
			rotations.add( new Rotation( center, n, neighbors) );
		}
		if ( rotations.isEmpty( ) == false )
		{
			return ( (RotationComparator) rotations.first( ).comparator( ) ).getReference( );
		}
		return new Vertex( center.x.get( ), 0 );
	}

	/**
     * Siguiente vertice en el sentido Clockwise (N->E->S->O) desde el vertice v.
     * @param v Vertex
     * @return Vertex
     */
    public Vertex getClockwiseVertex( Vertex v )
    {
        Vertex higher = this.higher( v );
        if ( higher == null )
            return this.first( );
        return higher;
    }
    
    /**
     * Siguiente vertice en el sentido CounterClockwise (N->O->S->E) desde el vertice v.
     * @param v Vertex
     * @return Vertex
     */
    public Vertex getCounterClockwiseVertex( Vertex v )
    {
        Vertex lower = this.lower( v );
        if ( lower == null )
            return this.last( );
        return lower;
    }

    @Override
    public Vertex higher( Vertex v )
    {
    	for ( Iterator<Vertex> it = this.iterator( ); it.hasNext( ); )
    	{
    		if ( it.next( ).equals( v ) && it.hasNext( ) )
    			return it.next( );
    	}
    	return null;
    }

    @Override
    public Vertex lower( Vertex v )
    {
    	for ( Iterator<Vertex> it = this.descendingIterator( ); it.hasNext( ); )
    	{
    		if ( it.next( ).equals( v ) && it.hasNext( ) )
    			return it.next( );
    	}
    	return null;
    }
}
