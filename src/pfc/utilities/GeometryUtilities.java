/**
 * GeometryUtilities.java
 */
package pfc.utilities;

import java.util.*;
import java.awt.geom.*;

import pfc.models.*;
import pfc.settings.*;

import static java.lang.Math.*;

/**
 * @author Cameron Behar
 * @author walber
 */
public class GeometryUtilities
{
	public static double angle( double x, double y )
	{
		return -( atan2( y, x ) - PI / 2.0 );
	}
	
	public static double angleBetween( double angle0, double angle1 )
	{
		// Going counter clock-wise
		return angle1 - ( angle0 + ( ( angle0 < angle1 ) ? 360.0 : 0.0 ) );
	}
	
	public static boolean areClose( Point2D point, Line2D line )
	{
		return line.ptSegDistSq( point ) <= UserSettings.instance.areCloseDistance.get( );
	}
	
	public static double determinant( double[ ][ ] A )
	{
		if( A.length == 1 )
			return A[0][0];
		
		if( A.length == 2 )
			return A[0][0] * A[1][1] - A[0][1] * A[1][0];
		
		double result = 0;
		for( int i = 0; i < A[0].length; i++ )
		{
			double temp[][] = new double[A.length - 1][A[0].length - 1];
			
			for( int j = 1; j < A.length; j++ )
				for( int k = 0; k < A[0].length; k++ )
					if( k < i )
						temp[j - 1][k] = A[j][k];
					else if( k > i )
						temp[j - 1][k - 1] = A[j][k];
			
			result += A[0][i] * ( ( i & 1 ) == 0 ? 1 : -1 ) * determinant( temp );
		}
		
		return result;
	}
	
	public static double distance( Vertex v0, Vertex v1 )
	{
		return Point2D.distance( v0.x.get( ), v0.y.get( ), v1.x.get( ), v1.y.get( ) );
	}
	
	public static double distanceSq( Vertex v0, Vertex v1 )
	{
		return Point2D.distanceSq( v0.x.get( ), v0.y.get( ), v1.x.get( ), v1.y.get( ) );
	}
	
	public static Rectangle2D getBounds( Graph graph )
    {
        if( graph.vertices.size( ) <= 0 )
            return null;
        
        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        
        for( Vertex vertex : graph.vertices )
        {
            minX = Math.min( minX, vertex.x.get( ) );
            maxX = Math.max( maxX, vertex.x.get( ) );
            minY = Math.min( minY, vertex.y.get( ) );
            maxY = Math.max( maxY, vertex.y.get( ) );
        }
        
        for( Edge edge : graph.edges )
            if( !edge.isLinear( ) && !edge.isPolyline( ) )
            {
                Rectangle2D rect = edge.getArc( ).getBounds2D( );
                minX = Math.min( minX, rect.getMinX( ) );
                maxX = Math.max( maxX, rect.getMaxX( ) );
                minY = Math.min( minY, rect.getMinY( ) );
                maxY = Math.max( maxY, rect.getMaxY( ) );
            }
        
        return new Rectangle2D.Double( minX, minY, maxX - minX, maxY - minY );
    }
	
	public static Vector<Point2D> getCrossings( Arc2D arc0, Point2D arc0Center, Arc2D arc1, Point2D arc1Center )
	{
		Vector<Point2D> ret = new Vector<Point2D>( );
		
		double distance = arc0Center.distance( arc1Center );
		double radius0Squared = arc0Center.distanceSq( arc0.getStartPoint( ) );
		double radius0 = sqrt( radius0Squared );
		double radius1Squared = arc1Center.distanceSq( arc1.getStartPoint( ) );
		double radius1 = sqrt( radius1Squared );
		
		if( distance > radius0 + radius1 )
		{
			// There are no solutions because the circles are separate.
		}
		else if( distance < abs( radius0 - radius1 ) )
		{
			// There are no solutions because one circle is contained within the other.
		}
		else if( distance == 0 && radius0 == radius1 )
		{
			// There are an infinite number of solutions because the circles are coincident.
		}
		else
		{
			// Calculate the first intersection
			double x0 = arc0Center.getX( ), y0 = arc0Center.getY( );
			double x1 = arc1Center.getX( ), y1 = arc1Center.getY( );
			
			double a = ( radius0Squared - radius1Squared + distance * distance ) / ( 2 * distance );
			double h = sqrt( radius0Squared - a * a );
			
			double x2 = x0 + a * ( x1 - x0 ) / distance;
			double y2 = y0 + a * ( y1 - y0 ) / distance;
			
			Point2D.Double intersection = new Point2D.Double( x2 + h * ( y1 - y0 ) / distance, y2 - h * ( x1 - x0 ) / distance );
			double angle0ToIntersection = toDegrees( atan2( -( intersection.y - y0 ), intersection.x - x0 ) );
			double angle1ToIntersection = toDegrees( atan2( -( intersection.y - y1 ), intersection.x - x1 ) );
			if( arc0.containsAngle( angle0ToIntersection ) && arc1.containsAngle( angle1ToIntersection ) )
				ret.add( intersection );
			
			// If the circles aren't tangential, calculate the second intersection
			if( distance != radius0 + radius1 )
			{
				intersection = new Point2D.Double( x2 - h * ( y1 - y0 ) / distance, y2 + h * ( x1 - x0 ) / distance );
				angle0ToIntersection = toDegrees( atan2( -( intersection.y - y0 ), intersection.x - x0 ) );
				angle1ToIntersection = toDegrees( atan2( -( intersection.y - y1 ), intersection.x - x1 ) );
				if( arc0.containsAngle( angle0ToIntersection ) && arc1.containsAngle( angle1ToIntersection ) )
					ret.add( intersection );
			}
		}
		
		return ret;
	}
	
	public static Vector<Point2D> getCrossings( Line2D line, Arc2D arc, Point2D arcCenter )
	{
		Vector<Point2D> ret = new Vector<Point2D>( );
		
		double x0 = line.getX1( ) - arcCenter.getX( ), y0 = line.getY1( ) - arcCenter.getY( );
		double x1 = line.getX2( ) - arcCenter.getX( ), y1 = line.getY2( ) - arcCenter.getY( );
		double xDiff = x1 - x0, yDiff = y1 - y0;
		
		// Resist the urge to use Math.signum(). It returns 0.0 when the
		// parameter is 0.0, rather than 1.0. Do not change to Math.signum(),
		// otherwise intersections with horizontal lines will be messed up.
		double yDiffSign = ( yDiff < 0 ? -1 : 1 );
		
		double arcRadiusSquared = arcCenter.distanceSq( arc.getStartPoint( ) );
		double rDiffSquared = xDiff * xDiff + yDiff * yDiff;
		
		double determinant = x0 * y1 - x1 * y0;
		double rootPart = sqrt( arcRadiusSquared * rDiffSquared - determinant * determinant );
		
		double discriminant = arcRadiusSquared * rDiffSquared - determinant * determinant;
		
		// If the line is tangent or secant to the circle...
		if( discriminant >= 0 )
		{
			// Calculate the first intersection
			Point2D.Double intersection = new Point2D.Double( );
			intersection.x = ( determinant * yDiff + yDiffSign * xDiff * rootPart ) / rDiffSquared;
			intersection.y = ( -determinant * xDiff + abs( yDiff ) * rootPart ) / rDiffSquared;
			double angleToIntersection = angle( -intersection.y, intersection.x );
			intersection.x += arcCenter.getX( );
			intersection.y += arcCenter.getY( );
			if( areClose( intersection, line ) && arc.containsAngle( toDegrees( angleToIntersection ) ) )
				ret.add( intersection );
			
			// If the line is secant to the circle
			if( discriminant > 0 )
			{
				// Calculate the other intersection
				intersection = new Point2D.Double( );
				intersection.x = ( determinant * yDiff - yDiffSign * xDiff * rootPart ) / rDiffSquared;
				intersection.y = ( -determinant * xDiff - abs( yDiff ) * rootPart ) / rDiffSquared;
				angleToIntersection = angle( -intersection.y, intersection.x );
				intersection.x += arcCenter.getX( );
				intersection.y += arcCenter.getY( );
				if( areClose( intersection, line ) && arc.containsAngle( toDegrees( angleToIntersection ) ) )
					ret.add( intersection );
			}
		}
		
		return ret;
	}
	
	public static Vector<Point2D> getCrossings( Line2D line0, Line2D line1 )
	{
		Vector<Point2D> ret = new Vector<Point2D>( );
		
		if( line0.intersectsLine( line1 ) )
		{
			Point2D.Double intersection = new Point2D.Double( 0.0, 0.0 );
			
			double xDiff0 = line0.getX2( ) - line0.getX1( );
			double xDiff1 = line1.getX2( ) - line1.getX1( );
			double yDiff0 = line0.getY2( ) - line0.getY1( );
			double yDiff1 = line1.getY2( ) - line1.getY1( );
			double xDiff2 = line0.getX1( ) - line1.getX1( );
			double yDiff2 = line0.getY1( ) - line1.getY1( );
			
			double div = yDiff1 * xDiff0 - xDiff1 * yDiff0;
			double u = ( xDiff1 * yDiff2 - yDiff1 * xDiff2 ) / div;
			intersection.x = line0.getX1( ) + u * xDiff0;
			intersection.y = line0.getY1( ) + u * yDiff0;
			
			ret.add( intersection );
		}
		
		return ret;
	}
	
	public static boolean isBetween( Point2D a, Point2D b, Point2D c )
	{
		double u = c.getX( ) - a.getX( );
		double v = c.getY( ) - a.getY( );
		double tx = ( b.getX( ) - a.getX( ) ) / u;
		double ty = ( b.getY( ) - a.getY( ) ) / v;
		
		return ( tx > 0 && tx < 1 ) || ( ty > 0 && ty < 1 );
	}
	
	public static Point2D midpoint( double x0, double y0, double x1, double y1 )
	{
		return new Point2D.Double( ( x1 + x0 ) / 2.0, ( y1 + y0 ) / 2.0 );
	}
	
	public static Point2D midpoint( Vertex v0, Vertex v1 )
	{
		return midpoint( v0.x.get( ), v0.y.get( ), v1.x.get( ), v1.y.get( ) );
	}
	
	public static Point2D threeOutOfFourPoint ( double x0, double y0, double x1, double y1 )
	{
		return new Point2D.Double( ( ( 3.0 * x1 ) + x0 ) / 4.0, ( ( 3.0 * y1 ) +  y0 ) / 4.0 );
	}
	
	public static Point2D threeOutOfFourPoint( Vertex v0, Vertex v1 )
	{	
		return threeOutOfFourPoint( v0.x.get( ), v0.y.get( ), v1.x.get( ), v1.y.get( ) );
	}

	public static double slope( double x0, double y0, double x1, double y1 )
	{
		return ( y1 - y0 ) / ( x1 - x0 );
	}
	
	public static double slope( Line2D line )
	{
		return slope( line.getX1( ), line.getY1( ), line.getX2( ), line.getY2( ) );
	}
	
	/**
	 * Determinar el angulo formado por los tres puntos p1, p2 y p3,
	 * siendo p2 punto origen de las dos rectas. El angulo determinado
	 * esta en el sentido clockwise.
	 * @param p1 Point2D
	 * @param p2 Point2D
	 * @param p3 Point2D
	 * @return angulo double
	 */
	public static double getClockwiseAngle ( Point2D p1, Point2D p2, Point2D p3 )
	{
		 // Version Walber
		 double angle = getAngle( p1, p2, p3, p2 );
		 Line2D refLine = new Line2D.Double( p1, p2 );
		 if ( refLine.relativeCCW( p3 ) == -1 )
			 angle = 360 - angle;
		 return angle;
		 
//		return Math.toDegrees( 
//				-angleBetween( 
//						angle( p2.getX() - p1.getX(), p2.getY() - p1.getY() ),
//						angle( p2.getX() - p3.getX(), p2.getY() - p3.getY() ) )
//		);
	}
	
	/**
	 * Calcula el angulo entre dos rectas l1(p1, p2) y l2(p3, p4) 
	 * @param p1 Point2D punto destino del vector direccion de l1
	 * @param p2 Point2D punto origen del vector direccion de l1
	 * @param p3 Point2D punto destino del vector direccion de l2
	 * @param p4 Point2D punto origen del vector direccion de l2
	 * @return double angulo en grados
	 * @see #getClockwiseAngle(Point2D, Point2D, Point2D)
	 */
	public static double getAngle ( Point2D p1, Point2D p2, Point2D p3, Point2D p4 )
	{
		double u1 = p1.getX( ) - p2.getX( );
		double u2 = p1.getY( ) - p2.getY( );
		double v1 = p3.getX( ) - p4.getX( );
		double v2 = p3.getY( ) - p4.getY( );

		// u*v producto escalar en R2
		double dot = u1*v1 + u2*v2;
		// |u|*|v| modulo de u por modulo de v
		double lenght = Math.sqrt( u1*u1 + u2*u2 ) * Math.sqrt( v1*v1 + v2*v2 );

		double div = dot / lenght;
		div = Math.rint( div * 100000 ) / 100000;
		
		// If the argument is NaN or its absolute value is greater than 1, then the result is NaN		
		return Math.toDegrees( Math.acos( div ) );
	}
	
	/**
     * Obtener el vertice del grafo mas cercano al punto origen (0, 0) 
     * Realmente, beneficia vertice con menor y. 
     * @param graph Graph
     * @return Vertex
     */
    public static Vertex getTheClosestVertexToOrigin ( Graph graph )
    {
        Vertex closestToOrigin = null;
        for ( Vertex v : graph.vertices )
            if ( closestToOrigin == null || isCloserToOrigin( v, closestToOrigin ) )
                closestToOrigin = v;
        
        return closestToOrigin;
    }
    
    /**
     * @param v1 Vertex
     * @param v2 Vertex
     * @return boolean si v1 esta mas cerca del origen que v2.
     */
    public static boolean isCloserToOrigin ( Vertex v1, Vertex v2 )
    {
        if ( v1.y.get( ) < v2.y.get( ) )
            return true;
        if ( v1.y.get( ) == v2.y.get( ) && v1.x.get( ) < v2.x.get( ))
            return true;
        return false; 
    }
    
    /**
     * Obtener el vertice del grafo mas alejado del punto origen (0, 0)
     * Realmente, beneficia vertice con mayor y. 
     * @param graph Graph
     * @return Vertex
     */
    public static Vertex getTheFarthestVertexToOrigin ( Graph graph )
    {
        Vertex farthestToOrigin = null;
        for ( Vertex v : graph.vertices )
            if ( farthestToOrigin == null || ( isCloserToOrigin( v, farthestToOrigin ) == false ) )
                farthestToOrigin = v;
        
        return farthestToOrigin;
    }

    /** 
	 * Method to calculate the area of a polygon
	 * @param polygon List lista de puntos que forma el poligono
	 * @return double area
	 */
	public static double calculateSignedPolygonArea ( List<Point2D> polygon )
	{
		int i, j;
		double area = 0;

		for ( i = 0; i < polygon.size( ); i++ )
		{
			j = (i + 1) % polygon.size( );
			area += polygon.get( i ).getX( ) * polygon.get( j ).getY( );
			area -= polygon.get( i ).getY( ) * polygon.get( j ).getX( );
		}
		area /= 2.0;

		return( area );
		//return(area < 0 ? -area : area); for unsigned
	}

	/**
	 * Method to obtaing the center point of a polygon
	 * @param polygon List lista de puntos que forma el poligono
	 * @return Point2D point
	 */
	public static Point2D getPolygonCentroid( List<Point2D> polygon )
	{
		double cx = 0, cy = 0;
		double area = calculateSignedPolygonArea ( polygon );
		Point2D center = new Point2D.Double( );
		int i, j;

		double factor=0;
		for ( i = 0; i < polygon.size( ); i++ )
		{
			j = ( i + 1 ) % polygon.size( );
			factor = ( polygon.get( i ).getX( ) * polygon.get( j ).getY( ) - polygon.get( j ).getX( ) * polygon.get( i ).getY( ) );
			cx += ( polygon.get( i ).getX( ) + polygon.get( j ).getX( ) ) * factor;
			cy += ( polygon.get( i ).getY( ) + polygon.get( j ).getY( ) ) * factor;
		}
		area *= 6.0f;
		factor = 1 / area;
		cx *= factor;
		cy *= factor;
		center.setLocation(cx, cy);
		
		return center;
	}
	
}
