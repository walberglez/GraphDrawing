/**
 * DominanceDrawingDisplayView.java
 * 01/08/2011 06:30:08
 */
package pfc.views.display.algorithms.dominance;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import pfc.models.Edge;
import pfc.models.Graph;
import pfc.models.Vertex;
import pfc.models.algorithms.sldominance.DominanceDrawing;
import pfc.settings.GlobalSettings;
import pfc.settings.GraphSettings;
import pfc.settings.UserSettings;
import pfc.views.display.GraphDisplayView;


/**
 * @author walber
 *
 */
public class DominanceDrawingDisplayView
{

	/**
	 * Dibujar el drawing de Dominance.
	 * @param g2D Graphics2D
	 * @param drawing DominanceDrawing
	 */
	public static void paintDrawing ( Graphics2D g2D, DominanceDrawing drawing, GraphSettings s )
	{
		// first X axe coordinate
		double firstX = getFirstX( );
		// first Y axe coordinate
		double firstY = getFirstY( drawing );

		Stroke oldStroke = g2D.getStroke( );

		// paint grid
		paintGrid( g2D, drawing );
		
		// Set the edge-specific stroke
		g2D.setStroke( new BasicStroke( (float) GlobalSettings.defaultEdgeThickness ) );
				
		// draw Axes
		paintAxes( g2D, drawing );
		
		Graph graph = new Graph( drawing.getGraph( ).toString( ) );
		
		// set vertex real point from integer coordinates
		for ( Vertex v : graph.vertices )
		{
			Point2D point = getCoordinatePoint( v.x.get( ).intValue( ), v.y.get( ).intValue( ), firstX, firstY );
			v.x.set( point.getX( ) );
			v.y.set( point.getY( ) );
		}
		
		// set bends real point from integer coordinates
		for ( Edge e : graph.edges )
		{
			if ( e.isPolyline( ) )
			{
				for ( Point2D bend : e.getBends( ) )
				{
					bend.setLocation( getCoordinatePoint( (int) bend.getX( ), (int) bend.getY( ), firstX, firstY) );
				}
			}
		}
		
		// draw the graph
		GraphDisplayView.paint( g2D, graph, s );
		
		// Return the stroke to what it was before
		g2D.setStroke( oldStroke );
	}
	
	/**
	 * get the bounds of the given drawing
	 * @param drawing DominanceDrawing
	 * @return Rectangle2D
	 */
	public static Rectangle2D getBounds( DominanceDrawing drawing )
	{
		if ( drawing.isEmpty( ) )
			return null;
		
		double minX = 0.0, minY = 0.0;
		double maxX =
			GlobalSettings.defaultCoordinateAxesGap +
			GlobalSettings.defaultHorizontalCoordinateGap +
			GlobalSettings.defaultVertexSegmentOffset +
			( drawing.getMaxXCoordinate( ) * GlobalSettings.defaultHorizontalCoordinateGap ) +
			GlobalSettings.defaultVertexSegmentOffset +
			GlobalSettings.defaultHorizontalCoordinateGap +
			GlobalSettings.defaultCoordinateAxesGap;
		double maxY =
			GlobalSettings.defaultCoordinateAxesGap +
			GlobalSettings.defaultVerticalCoordinateGap +
			GlobalSettings.defaultVertexSegmentOffset +
			( drawing.getMaxYCoordinate( ) * GlobalSettings.defaultVerticalCoordinateGap ) +
			GlobalSettings.defaultVertexSegmentOffset +
			GlobalSettings.defaultVerticalCoordinateGap +
			GlobalSettings.defaultCoordinateAxesGap;
		
		return new Rectangle2D.Double( minX, minY, maxX - minX, maxY - minY );
	}
	
	/**
	 * Obtener punto del plano que representa la coordenada con numeros enteros del drawing.
	 * @param x int
	 * @param y int
	 * @param firstX double
	 * @param firstY double
	 * @return Point2D
	 */
	public static Point2D getCoordinatePoint( int x, int y, double firstX, double firstY )
	{
		return new Point2D.Double(
				firstX + x * GlobalSettings.defaultHorizontalCoordinateGap,
				firstY - y * GlobalSettings.defaultVerticalCoordinateGap );
	}
	
	/**
	 * dibujar la red de lineas discontinuas
	 * @param g2D
	 * @param drawing
	 */
	private static void paintGrid( Graphics2D g2D, DominanceDrawing drawing )
	{
		// Set the line-specific stroke
		g2D.setStroke( new BasicStroke( 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{ 7.0f }, 0.0f ) );
		// Set the specific color
		g2D.setColor( UserSettings.instance.vertexLine.get( ) );
		
		// draw x lines
		paintLinesXGrid( g2D, drawing );
		// draw y lines
		paintLinesYGrid( g2D, drawing );
	}

	/**
	 * @param g2D
	 * @param drawing
	 */
	private static void paintLinesXGrid( Graphics2D g2D, DominanceDrawing drawing )
	{
		double firstX = getFirstX( );
		double firstY = getFirstY( drawing );
		int xL = 0;
		int xR = drawing.getMaxXCoordinate( );
		for ( int y = 0; y <= drawing.getMaxYCoordinate( ); y++ )
		{
			paintLine( g2D,
					getCoordinatePoint(xL, y, firstX, firstY), 
					getCoordinatePoint(xR, y, firstX, firstY));
		}
	}

	/**
	 * @param g2D
	 * @param drawing
	 */
	private static void paintLinesYGrid( Graphics2D g2D, DominanceDrawing drawing )
	{
		double firstX = getFirstX( );
		double firstY = getFirstY( drawing );
		int yB = 0;
		int yT = drawing.getMaxYCoordinate( );
		for ( int x = 0; x <= drawing.getMaxXCoordinate( ); x++ )
		{
			paintLine( g2D,
					getCoordinatePoint(x, yB, firstX, firstY), 
					getCoordinatePoint(x, yT, firstX, firstY));
		}
	}
	
	/**
	 * Paint Line method
	 * @param g2D Graphics2D
	 * @param pOrigin Point2D
	 * @param pDest Point2D
	 */
	private static void paintLine ( Graphics2D g2D, Point2D pOrigin, Point2D pDest )
	{
		// Draw vertex center
		Line2D lineSegment = new Line2D.Double( 
				pOrigin.getX( ), pOrigin.getY( ),
				pDest.getX( ), pDest.getY( )
				);
		g2D.draw( lineSegment );
	}
	
	/**
	 * draw both axes X and Y
	 * @param g2D Graphics2D
	 * @param drawing DominanceDrawing
	 */
	public static void paintAxes( Graphics2D g2D, DominanceDrawing drawing )
	{
		// draw X axe
		paintAxeX( g2D, drawing );
		// draw Y axe
		paintAxeY( g2D, drawing );
	}
	
	/**
	 * draw X axe
	 * @param g2D Graphics2D
	 * @param drawing DominanceDrawing
	 */
	private static void paintAxeX ( Graphics2D g2D, DominanceDrawing drawing )
	{
		// draw X axe line
		g2D.draw( new Line2D.Double( getAxeXLeftPoint( drawing ), getAxeXRightPoint( drawing ) ) );
		
		// draw axes X point
		for (double i = 0; i <= drawing.getMaxXCoordinate( ); i++ )
		{
			double despl = i * GlobalSettings.defaultHorizontalCoordinateGap;
			Line2D axe = new Line2D.Double(
					getFirstX( ) + despl,
					getAxeYBottomPoint( drawing ).getY( ) - GlobalSettings.defaultVertexSegmentOffset / 2,
					getFirstX( ) + despl,
					getAxeYBottomPoint( drawing ).getY( ) + GlobalSettings.defaultVertexSegmentOffset / 2
					);
			g2D.draw( axe );
			
			// draw String
			Font oldFont = g2D.getFont( );
			int fontSize = (int) Math.round( GlobalSettings.defaultVertexSegmentOffset / 1.1 );
			g2D.setFont( new Font( oldFont.getFamily( ), oldFont.getStyle( ), fontSize ) );
			g2D.setColor( UserSettings.instance.vertexLine.get( ) );
			g2D.drawString( (int) i + "",
					(float) ( getFirstX( ) + despl - fontSize / 2 ),
					(float) ( getAxeYBottomPoint( drawing ).getY( ) + GlobalSettings.defaultVertexSegmentOffset + fontSize ) );
			g2D.setFont( oldFont );
		}
	}
	
	/**
	 * draw Y axe
	 * @param g2D Graphics2D
	 * @param drawing DominanceDrawing
	 */
	private static void paintAxeY ( Graphics2D g2D, DominanceDrawing drawing )
	{
		// draw Y axe line
		g2D.draw( new Line2D.Double( getAxeYBottomPoint( drawing ), getAxeYTopPoint( ) ) );
		
		// draw axes Y point
		for (double i = drawing.getMaxYCoordinate( ); i >= 0; i-- )
		{
			double despl = i * GlobalSettings.defaultVerticalCoordinateGap;
			Line2D axe = new Line2D.Double(
					getAxeYBottomPoint( drawing ).getX( ) - GlobalSettings.defaultVertexSegmentOffset / 2,
					getFirstY( drawing ) - despl,
					getAxeYBottomPoint( drawing ).getX( ) + GlobalSettings.defaultVertexSegmentOffset / 2,
					getFirstY( drawing ) - despl
					);
			g2D.draw( axe );
			
			// draw String
			Font oldFont = g2D.getFont( );
			int fontSize = (int) Math.round( GlobalSettings.defaultVertexSegmentOffset / 1.1 );
			g2D.setFont( new Font( oldFont.getFamily( ), oldFont.getStyle( ), fontSize ) );
			g2D.setColor( UserSettings.instance.vertexLine.get( ) );
			g2D.drawString( (int) i + "",
					(float) ( getAxeYBottomPoint( drawing ).getX( ) - GlobalSettings.defaultVertexSegmentOffset - fontSize ),
					(float) ( getFirstY( drawing ) - despl + fontSize / 2 ) );
			g2D.setFont( oldFont );
		}
	}
	
	/**
	 * first X axe coordinate
	 * @return double
	 */
	public static double getFirstX ( )
	{
		return GlobalSettings.defaultCoordinateAxesGap + GlobalSettings.defaultHorizontalCoordinateGap +
		GlobalSettings.defaultVertexSegmentOffset;
	}
	
	/**
	 * first Y axe coordinate
	 * @param drawing DominanceDrawing
	 * @return double
	 */
	public static double getFirstY ( DominanceDrawing drawing )
	{
		return GlobalSettings.defaultCoordinateAxesGap + GlobalSettings.defaultVerticalCoordinateGap +
		GlobalSettings.defaultVertexSegmentOffset +	( drawing.getMaxYCoordinate( ) * GlobalSettings.defaultVerticalCoordinateGap );
	}

	/**
	 * Axe X top point
	 * @return Point2D
	 */
	private static Point2D getAxeYTopPoint ( )
	{
		return new Point2D.Double(
				GlobalSettings.defaultCoordinateAxesGap,
				GlobalSettings.defaultCoordinateAxesGap + GlobalSettings.defaultVerticalCoordinateGap
				);
	}
	
	/**
	 * Axe X bottom point
	 * @param drawing DominanceDrawing
	 * @return Point2D
	 */
	private static Point2D getAxeYBottomPoint ( DominanceDrawing drawing )
	{
		return new Point2D.Double(
				GlobalSettings.defaultCoordinateAxesGap,
				getFirstY( drawing ) + GlobalSettings.defaultVertexSegmentOffset + GlobalSettings.defaultVerticalCoordinateGap
				);
	}
	
	/**
	 * Axe Y left point
	 * @param drawing DominanceDrawing
	 * @return Point2D
	 */
	private static Point2D getAxeXLeftPoint ( DominanceDrawing drawing )
	{
		return getAxeYBottomPoint( drawing );
	}
	
	/**
	 * Axe Y right point
	 * @param drawing DominanceDrawing
	 * @return Point2D
	 */
	private static Point2D getAxeXRightPoint ( DominanceDrawing drawing )
	{
		return new Point2D.Double(
				GlobalSettings.defaultCoordinateAxesGap +
				GlobalSettings.defaultHorizontalCoordinateGap +
				GlobalSettings.defaultVertexSegmentOffset +
				( drawing.getMaxXCoordinate( ) * GlobalSettings.defaultHorizontalCoordinateGap ) +
				GlobalSettings.defaultVertexSegmentOffset,
				getFirstY( drawing ) + GlobalSettings.defaultVertexSegmentOffset + GlobalSettings.defaultVerticalCoordinateGap
				);
	}

}
