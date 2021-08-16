/**
 * DualPathVertexDisplayView.java
 * 21/07/2011 01:35:01
 */
package pfc.views.display.algorithms;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import pfc.models.Vertex;
import pfc.settings.GraphSettings;
import pfc.settings.UserSettings;
import pfc.utilities.ColorUtilities;


/**
 * @author walber
 *
 */
public class DualPathVertexDisplayView
{

	public static void paint( Graphics2D g2D, GraphSettings s, Vertex v )
	{
		// Draw vertex center
		Shape center = null;
		center = getRhombusShape( v.getPoint2D( ), v.radius.get( ) );
		
		g2D.setColor( v.isSelected.get( ) ? ColorUtilities.blend( UserSettings.instance.getVertexColor( v.color.get( ) ), UserSettings.instance.selectedVertexFill.get( ) ) : UserSettings.instance.uncoloredDualVertexFill.get( ) );
		g2D.fill( center );
		
		// Draw vertex outline
		Shape outline = null;
		outline = getRhombusShape( v.getPoint2D( ), v.radius.get( ) );
		
		g2D.setColor( v.isSelected.get( ) ? ColorUtilities.blend( UserSettings.instance.vertexLine.get( ), UserSettings.instance.selectedVertexLine.get( ) ) : UserSettings.instance.vertexLine.get( ) );
		g2D.draw( outline );
		
		// Draw label
		if( s.showVertexLabels.get( ) )
		{
			Font oldFont = g2D.getFont( );
			g2D.setFont( new Font( oldFont.getFamily( ), oldFont.getStyle( ), (int) Math.round( 11.0 * v.radius.get( ) / 5.0 ) ) );
			g2D.setColor( v.isSelected.get( ) ? ColorUtilities.blend( UserSettings.instance.vertexLine.get( ), UserSettings.instance.selectedVertexLine.get( ) ) : UserSettings.instance.uncoloredDualVertexFill.get( ) );
			g2D.drawString( v.label.get( ), (float) ( v.x.get( ) + 1.4 * v.radius.get( ) ), (float) ( v.y.get( ) - 2.0 * v.radius.get( ) ) );
			g2D.setFont( oldFont );
		}
	}
	
	private static Shape getRhombusShape( Point2D center, Double radius )
	{
		Point2D.Double[] rhombusPoint = new Point2D.Double[4];
		
		rhombusPoint[0] = new Point2D.Double( center.getX( ), center.getY( ) - radius );
		rhombusPoint[1] = new Point2D.Double( center.getX( ) + radius, center.getY( ) );
		rhombusPoint[2] = new Point2D.Double( center.getX( ), center.getY( ) + radius );
		rhombusPoint[3] = new Point2D.Double( center.getX( ) - radius, center.getY( ) );
		
		Path2D.Double path = new Path2D.Double( );
		path.moveTo( rhombusPoint[0].x, rhombusPoint[0].y );
		path.lineTo( rhombusPoint[1].x, rhombusPoint[1].y );
		path.lineTo( rhombusPoint[2].x, rhombusPoint[2].y );
		path.lineTo( rhombusPoint[3].x, rhombusPoint[3].y );
		path.lineTo( rhombusPoint[0].x, rhombusPoint[0].y );
		
		return path;
	}
}