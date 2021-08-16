package pfc.views.display.algorithms;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import pfc.models.Edge;
import pfc.settings.GraphSettings;
import pfc.settings.UserSettings;
import pfc.utilities.ColorUtilities;
import pfc.utilities.GeometryUtilities;


public class DualEdgeDisplayView {

	public static void paintEdge( Graphics2D g2D, GraphSettings s, Edge e )
	{
		// Decide where we should draw the handle and/or arrow head
		Point2D apparentHandleLocation = e.isLinear( ) ? GeometryUtilities.midpoint( e.from, e.to ) : e.getHandlePoint2D( );
		
		Stroke oldStroke = g2D.getStroke( );
		
		// Set the edge-specific stroke
		g2D.setStroke( new BasicStroke( 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{ 10.0f }, 0.0f ) );
		
		g2D.setColor( e.isSelected.get( ) ? ColorUtilities.blend( UserSettings.instance.getEdgeColor( e.color.get( ) ), UserSettings.instance.selectedEdge.get( ) ) : UserSettings.instance.uncoloredDualEdgeLine.get( ) );
		
		// Draw the edge
		if( e.isLinear( ) )
			g2D.draw( e.getLine( ) );
		else
			g2D.draw( e.getArc( ) );
		
		// Return the stroke to what it was before
		g2D.setStroke( oldStroke );

		// Draw arrow head for directed edges
		if( e.isDirected )
		{
			apparentHandleLocation = e.isLinear( ) ? GeometryUtilities.threeOutOfFourPoint( e.from, e.to ) : apparentHandleLocation;
			Point2D.Double[] arrowPoint = new Point2D.Double[3];
			double tangentAngle;
			
			if( e.isLinear( ) )
				tangentAngle = Math.atan2( e.to.y.get( ) - e.from.y.get( ), e.to.x.get( ) - e.from.x.get( ) );
			else
			{
				tangentAngle = Math.atan2( e.handleY.get( ) - e.getCenter( ).getY( ), e.handleX.get( ) - e.getCenter( ).getX( ) );
				
				// We need to calculate these angles so that we know in which order whether to flip the angle by 180 degrees
				double fromAngle = Math.atan2( e.from.y.get( ) - e.getCenter( ).getY( ), e.from.x.get( ) - e.getCenter( ).getX( ) );
				double toAngle = Math.atan2( e.to.y.get( ) - e.getCenter( ).getY( ), e.to.x.get( ) - e.getCenter( ).getX( ) );
				
				if( GeometryUtilities.angleBetween( fromAngle, tangentAngle ) >= GeometryUtilities.angleBetween( fromAngle, toAngle ) )
					tangentAngle += Math.PI;
				
				tangentAngle += Math.PI / 2;
			}
			
			for( int i = 0; i < 3; ++i )
			{
				double theta = tangentAngle + i * 2.0 * Math.PI / 3.0;
				arrowPoint[i] = new Point2D.Double( e.thickness.get( ) * UserSettings.instance.directedEdgeArrowRatio.get( ) * Math.cos( theta ) + apparentHandleLocation.getX( ), e.thickness.get( ) * UserSettings.instance.directedEdgeArrowRatio.get( ) * Math.sin( theta ) + apparentHandleLocation.getY( ) );
			}
			
			Path2D.Double path = new Path2D.Double( );
			path.moveTo( arrowPoint[0].x, arrowPoint[0].y );
			path.lineTo( arrowPoint[1].x, arrowPoint[1].y );
			path.lineTo( arrowPoint[2].x, arrowPoint[2].y );
			path.lineTo( arrowPoint[0].x, arrowPoint[0].y );
			g2D.fill( path );
			
			apparentHandleLocation = e.isLinear( ) ? GeometryUtilities.midpoint( e.from, e.to ) : apparentHandleLocation;
		}
	}
}
