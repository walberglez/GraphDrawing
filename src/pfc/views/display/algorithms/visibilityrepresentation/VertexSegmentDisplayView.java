package pfc.views.display.algorithms.visibilityrepresentation;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.*;

import pfc.models.algorithms.visibilityrepresentation.VertexSegment;
import pfc.settings.GlobalSettings;
import pfc.settings.UserSettings;


public class VertexSegmentDisplayView
{
	/**
	 * Paint Vertex Segment method
	 * @param g2D Graphics2D
	 * @param vSegment VertexSegment
	 * @param pOrigin Point2D
	 */
	public static void paint ( Graphics2D g2D, VertexSegment vSegment, Point2D pOrigin )
	{
		// Draw vertex center
		RectangularShape center = null;
		center = new Rectangle2D.Double(
				pOrigin.getX( ) - GlobalSettings.defaultVertexSegmentOffset,
				pOrigin.getY( ) - GlobalSettings.defaultVertexSegmentOffset,
				getW( vSegment.xRightCoordinate.get( ) - vSegment.xLeftCoordinate.get( ) ),
				getH( )
				);
		
		g2D.setColor( UserSettings.instance.uncoloredVertexFill.get( ) );
		g2D.fill( center );
		
		g2D.setColor( UserSettings.instance.vertexLine.get( ) );
		g2D.draw( center );
		
		// Draw label
		Font oldFont = g2D.getFont( );
		int fontSize = (int) Math.round( GlobalSettings.defaultVertexSegmentOffset / 1.1 );
		g2D.setFont( new Font( oldFont.getFamily( ), oldFont.getStyle( ), fontSize ) );
		g2D.setColor( UserSettings.instance.vertexLine.get( ) );
		g2D.drawString( vSegment.vertex.tag.get( ),
				(float) ( center.getCenterX( ) - Math.round( fontSize / 4 ) ),
				(float) ( center.getCenterY( ) + Math.round( fontSize / 3 ) ) );
		g2D.setFont( oldFont );
	}
	
	private static double getW ( int diff )
	{
		return ( GlobalSettings.defaultVertexSegmentOffset * 2.0 ) +
		GlobalSettings.defaultHorizontalCoordinateGap * diff;
	}
	
	private static double getH ( )
	{
		return GlobalSettings.defaultVertexSegmentOffset * 2.0;
	}
}
