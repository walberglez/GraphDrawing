/**
 * EdgeDisplayView.java
 */
package pfc.views.display;

import java.awt.*;
import java.awt.geom.*;

import pfc.models.*;
import pfc.settings.*;
import pfc.utilities.*;


/**
 * @author Cameron Behar
 */
public class EdgeDisplayView
{
	public static void paintEdge( Graphics2D g2D, GraphSettings s, Edge e )
	{
		// polyline
		Point2D bendTo = new Point2D.Double( );
		Path2D.Double bendPath = new Path2D.Double( );
		if ( e.isPolyline( ) )
		{
			bendPath.moveTo( e.from.x.get( ), e.from.y.get( ) );
			for ( Point2D p : e.getBends( ) )
			{
				bendPath.lineTo( p.getX( ), p.getY( ) );
				bendTo = p;
			}
			bendPath.lineTo( e.to.x.get( ), e.to.y.get( ) );
		}
		
		// Decide where we should draw the handle and/or arrow head
		Point2D apparentHandleLocation;
		if ( e.isLinear( ) )
			apparentHandleLocation = GeometryUtilities.midpoint( e.from, e.to );
		else if ( e.isPolyline( ) )
			apparentHandleLocation = GeometryUtilities.midpoint( bendTo.getX( ), bendTo.getY( ), e.to.x.get( ), e.to.y.get( ) );
		else
			apparentHandleLocation = e.getHandlePoint2D( );
		
		double handleRadius = e.thickness.get( ) * UserSettings.instance.defaultEdgeHandleRadiusRatio.get( );
		
		Stroke oldStroke = g2D.getStroke( );
		
		// Set the edge-specific stroke
		g2D.setStroke( new BasicStroke( e.thickness.get( ).floatValue( ) ) );
		
		g2D.setColor( e.isSelected.get( ) ? ColorUtilities.blend( UserSettings.instance.getEdgeColor( e.color.get( ) ), UserSettings.instance.selectedEdge.get( ) ) : UserSettings.instance.getEdgeColor( e.color.get( ) ) );
		
		// Draw the edge
		if( e.isLinear( ) )
			g2D.draw( e.getLine( ) );
		else if ( e.isPolyline( ) )
			g2D.draw( bendPath );
		else
			g2D.draw( e.getArc( ) );
		
		// Return the stroke to what it was before
		g2D.setStroke( oldStroke );
		
		// Set the handle-specific color
		if( e.color.get( ) == -1 )
			g2D.setColor( e.isSelected.get( ) ? ColorUtilities.blend( UserSettings.instance.edgeHandle.get( ), UserSettings.instance.selectedEdgeHandle.get( ) ) : UserSettings.instance.edgeHandle.get( ) );
		else
			g2D.setColor( e.isSelected.get( ) ? ColorUtilities.blend( UserSettings.instance.getEdgeColor( e.color.get( ) ), UserSettings.instance.selectedEdgeHandle.get( ) ) : UserSettings.instance.getEdgeColor( e.color.get( ) ) );
				
		// Draw arrow head for directed edges
		if( e.isDirected )
		{
			if ( e.isLinear( ) )
				apparentHandleLocation = GeometryUtilities.threeOutOfFourPoint( e.from, e.to );
			else if ( e.isPolyline( ) )
				apparentHandleLocation = GeometryUtilities.midpoint( bendTo.getX( ), bendTo.getY( ), e.to.x.get( ), e.to.y.get( ) );

			Point2D.Double[] arrowPoint = new Point2D.Double[3];
			double tangentAngle;
			
			if( e.isLinear( ) )
				tangentAngle = Math.atan2( e.to.y.get( ) - e.from.y.get( ), e.to.x.get( ) - e.from.x.get( ) );
			else if ( e.isPolyline( ) )
				tangentAngle = Math.atan2( e.to.y.get( ) - bendTo.getY( ), e.to.x.get( ) - bendTo.getX( ) );
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
			
			if ( e.isLinear( ) )
				apparentHandleLocation = GeometryUtilities.midpoint( e.from, e.to );
			else if ( e.isPolyline( ) )
				apparentHandleLocation = GeometryUtilities.midpoint( bendTo.getX( ), bendTo.getY( ), e.to.x.get( ), e.to.y.get( ) );
			else
				apparentHandleLocation = e.getHandlePoint2D( );
		}
		
		// Set the label color to the edge's
		g2D.setColor( e.isSelected.get( ) ? ColorUtilities.blend( UserSettings.instance.getEdgeColor( e.color.get( ) ), UserSettings.instance.selectedEdge.get( ) ) : UserSettings.instance.getEdgeColor( e.color.get( ) ) );
		
		// Draw edge label
		if( s.showEdgeLabels.get( ) )
		{
			Font oldFont = g2D.getFont( );
			g2D.setFont( new Font( oldFont.getFamily( ), oldFont.getStyle( ), (int) Math.round( 11.0 * handleRadius / 1.5 ) ) );
			g2D.drawString( e.label.get( ), (float) ( apparentHandleLocation.getX( ) + 2.0 * handleRadius ), (float) ( apparentHandleLocation.getY( ) + 2.0 * handleRadius ) );
			g2D.setFont( oldFont );
		}
		
		// Draw edge weight label
		if( s.showEdgeWeights.get( ) )
		{
			Font oldFont = g2D.getFont( );
			g2D.setFont( new Font( oldFont.getFamily( ), oldFont.getStyle( ), (int) Math.round( 11.0 * handleRadius / 1.5 ) ) );
			g2D.drawString( String.format( "%." + UserSettings.instance.edgeWeightPrecision.get( ) + "f", e.weight.get( ) ), (float) ( apparentHandleLocation.getX( ) - 1.5 * handleRadius ), (float) ( apparentHandleLocation.getY( ) + 9.0 * handleRadius ) );
			g2D.setFont( oldFont );
		}
	}
	
	public static boolean wasClicked( Edge edge, Point point, double scale )
	{
		return ( Point2D.distance( edge.handleX.get( ), edge.handleY.get( ), point.x, point.y ) <= edge.thickness.get( ) * UserSettings.instance.defaultEdgeHandleRadiusRatio.get( ) + UserSettings.instance.edgeHandleClickMargin.get( ) / scale );
	}
	
	public static boolean wasSelected( Edge edge, Rectangle selection )
	{
		return selection.contains( edge.handleX.get( ), edge.handleY.get( ) );
	}
}
