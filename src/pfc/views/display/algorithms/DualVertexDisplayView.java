package pfc.views.display.algorithms;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

import pfc.models.Vertex;
import pfc.settings.GraphSettings;
import pfc.settings.UserSettings;
import pfc.utilities.ColorUtilities;


public class DualVertexDisplayView
{

	public static void paint( Graphics2D g2D, GraphSettings s, Vertex v )
	{
		// Draw vertex center
		RectangularShape center = null;
		center = new Rectangle2D.Double( v.x.get( ) - v.radius.get( ), v.y.get( ) - v.radius.get( ), v.radius.get( ) * 2, v.radius.get( ) * 2 );

		g2D.setColor( v.isSelected.get( ) ? ColorUtilities.blend( UserSettings.instance.getVertexColor( v.color.get( ) ), UserSettings.instance.selectedVertexFill.get( ) ) : UserSettings.instance.uncoloredDualVertexFill.get( ) );
		g2D.fill( center );
		
		// Draw vertex outline
		RectangularShape outline = null;
		outline = new Rectangle2D.Double( v.x.get( ) - v.radius.get( ), v.y.get( ) - v.radius.get( ), v.radius.get( ) * 2, v.radius.get( ) * 2 );

		g2D.setColor( v.isSelected.get( ) ? ColorUtilities.blend( UserSettings.instance.vertexLine.get( ), UserSettings.instance.selectedVertexLine.get( ) ) : UserSettings.instance.vertexLine.get( ) );
		g2D.draw( outline );
		
		// Draw label
		if( s.showVertexLabels.get( ) )
		{
			Font oldFont = g2D.getFont( );
			g2D.setFont( new Font( oldFont.getFamily( ), oldFont.getStyle( ), (int) Math.round( 11.0 * v.radius.get( ) / 5.0 ) ) );
			g2D.setColor( v.isSelected.get( ) ? ColorUtilities.blend( UserSettings.instance.vertexLine.get( ), UserSettings.instance.selectedVertexLine.get( ) ) : UserSettings.instance.uncoloredDualVertexFill.get( ) );
			g2D.drawString( v.label.get( ), (float) ( v.x.get( ) + 1.4 * v.radius.get( ) ), (float) ( v.y.get( ) + 2.0 * v.radius.get( ) ) );
			g2D.setFont( oldFont );
		}
	}
}
