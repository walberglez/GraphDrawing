/**
 * VertexDisplayView.java
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
public class VertexDisplayView
{
	public static void paint( Graphics2D g2D, GraphSettings s, Vertex v )
	{
		// Draw vertex center
		RectangularShape center = null;
		center = new Ellipse2D.Double( v.x.get( ) - v.radius.get( ), v.y.get( ) - v.radius.get( ), v.radius.get( ) * 2, v.radius.get( ) * 2 );
		g2D.setColor( v.isSelected.get( ) ? ColorUtilities.blend( UserSettings.instance.getVertexColor( v.color.get( ) ), UserSettings.instance.selectedVertexFill.get( ) ) : UserSettings.instance.getVertexColor( v.color.get( ) ) );
		g2D.fill( center );
		
		// Draw vertex outline
		RectangularShape outline = null;
		outline = new Ellipse2D.Double( v.x.get( ) - v.radius.get( ), v.y.get( ) - v.radius.get( ), v.radius.get( ) * 2, v.radius.get( ) * 2 );
		g2D.setColor( v.isSelected.get( ) ? ColorUtilities.blend( UserSettings.instance.vertexLine.get( ), UserSettings.instance.selectedVertexLine.get( ) ) : UserSettings.instance.vertexLine.get( ) );
		g2D.draw( outline );
		
		// Draw label
		if( s.showVertexLabels.get( ) )
		{
			Font oldFont = g2D.getFont( );
			g2D.setFont( new Font( oldFont.getFamily( ), oldFont.getStyle( ), (int) Math.round( 11.0 * v.radius.get( ) / 5.0 ) ) );
			g2D.setColor( v.isSelected.get( ) ? ColorUtilities.blend( UserSettings.instance.vertexLine.get( ), UserSettings.instance.selectedVertexLine.get( ) ) : UserSettings.instance.vertexLabelColor.get( ) );
			g2D.drawString( v.label.get( ), (float) ( v.x.get( ) + 1.4 * v.radius.get( ) ), (float) ( v.y.get( ) + 2.0 * v.radius.get( ) ) );
			g2D.setFont( oldFont );
		}
		
		// Draw weight
		if( s.showVertexWeights.get( ) )
		{
			Font oldFont = g2D.getFont( );
			g2D.setFont( new Font( oldFont.getFamily( ), oldFont.getStyle( ), (int) Math.round( 11.0 * v.radius.get( ) / 5.0 ) ) );
			g2D.drawString( String.format( "%." + UserSettings.instance.vertexWeightPrecision.get( ) + "f", v.weight.get( ) ), (float) ( v.x.get( ) + 1.4 * v.radius.get( ) ), v.y.get( ).floatValue( ) );
			g2D.setFont( oldFont );
		}
	}
	
	public static boolean wasClicked( Vertex vertex, Point point, double scale )
	{
		return ( Point2D.distance( vertex.x.get( ), vertex.y.get( ), point.x, point.y ) <= vertex.radius.get( ) + UserSettings.instance.vertexClickMargin.get( ) / scale );
	}
	
	public static boolean wasSelected( Vertex vertex, Rectangle selection )
	{
		return selection.contains( vertex.x.get( ), vertex.y.get( ) );
	}
}
