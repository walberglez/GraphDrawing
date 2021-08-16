package pfc.views.display.algorithms.visibilityrepresentation;

import java.awt.*;
import java.awt.geom.*;

import pfc.models.algorithms.visibilityrepresentation.EdgeSegment;
import pfc.settings.GlobalSettings;
import pfc.settings.UserSettings;
import pfc.utilities.GeometryUtilities;


public class EdgeSegmentDisplayView
{
	/**
	 * paint edge segment method
	 * @param g2D Graphics2D
	 * @param eSegment EdgeSegment
	 * @param line Line2D
	 */
	public static void paint ( Graphics2D g2D, EdgeSegment eSegment, Line2D line )
	{
		Line2D lineSegment = null;
		double offset = GlobalSettings.defaultVertexSegmentOffset + GlobalSettings.defaultEdgeThickness;
		
		if ( eSegment.yTopCoordinate.get( ) > eSegment.yBottomCoordinate.get( ) )
			lineSegment = new Line2D.Double( 
					line.getX1( ), line.getY1( ) - offset,
					line.getX2( ), line.getY2( ) + offset
					);
		else
			lineSegment = new Line2D.Double( 
					line.getX1( ), line.getY1( ) + offset,
					line.getX2( ), line.getY2( ) - offset
					);
		
		Stroke oldStroke = g2D.getStroke( );
		
		// Set the edge-specific stroke
		g2D.setStroke( new BasicStroke( (float) GlobalSettings.defaultEdgeThickness ) );
		
		g2D.setColor( GlobalSettings.defaultUncoloredEdgeLine );
		
		// draw the segment
		g2D.draw( lineSegment );
		
		paintEdgeSegmentArrow( g2D, lineSegment );
		
		// Return the stroke to what it was before
		g2D.setStroke( oldStroke );
		
	}

	/**
	 * Pintar la flecha de la arista segmento.
	 * @param g2D
	 * @param lineSegment
	 */
	private static void paintEdgeSegmentArrow ( Graphics2D g2D, Line2D lineSegment )
	{
		Point2D.Double[] arrowPoint = new Point2D.Double[3];
		
		Point2D apparentHandleLocation = GeometryUtilities.threeOutOfFourPoint(
				lineSegment.getX1( ),
				lineSegment.getY1( ),
				lineSegment.getX2( ),
				lineSegment.getY2( ) );
					
		double tangentAngle = Math.atan2(
				lineSegment.getY2( ) - lineSegment.getY1( ),
				lineSegment.getX2( ) - lineSegment.getX1( ) );

		for( int i = 0; i < 3; ++i )
		{
			double theta = tangentAngle + i * 2.0 * Math.PI / 3.0;
			arrowPoint[i] = new Point2D.Double(
					GlobalSettings.defaultEdgeThickness * UserSettings.instance.directedEdgeArrowRatio.get( ) * Math.cos( theta ) + apparentHandleLocation.getX( ),
					GlobalSettings.defaultEdgeThickness * UserSettings.instance.directedEdgeArrowRatio.get( ) * Math.sin( theta ) + apparentHandleLocation.getY( )
					);
		}
		
		Path2D.Double path = new Path2D.Double( );
		path.moveTo( arrowPoint[0].x, arrowPoint[0].y );
		path.lineTo( arrowPoint[1].x, arrowPoint[1].y );
		path.lineTo( arrowPoint[2].x, arrowPoint[2].y );
		path.lineTo( arrowPoint[0].x, arrowPoint[0].y );
		g2D.fill( path );
	}
}
