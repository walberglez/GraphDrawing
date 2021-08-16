package pfc.views.display.algorithms.visibilityrepresentation;

import java.awt.*;
import java.awt.geom.*;

import pfc.models.algorithms.visibilityrepresentation.EdgeSegment;
import pfc.models.algorithms.visibilityrepresentation.VertexSegment;
import pfc.models.algorithms.visibilityrepresentation.VisibilityRepresentationDrawing;
import pfc.settings.GlobalSettings;
import pfc.settings.UserSettings;


public class VisRepDrawingDisplayView
{
	/**
	 * Dibujar el drawing de Visibility Representation.
	 * @param g2D Graphics2D
	 * @param drawing VisibilityRepresentationDrawing
	 */
	public static void paintDrawing ( Graphics2D g2D, VisibilityRepresentationDrawing drawing )
	{
		// first X axe coordinate
		double firstX = VisRepDrawingDisplayView.getFirstX( );
		// first Y axe coordinate
		double firstY = VisRepDrawingDisplayView.getFirstY( drawing );

		Stroke oldStroke = g2D.getStroke( );
		
		// Set the edge-specific stroke
		g2D.setStroke( new BasicStroke( (float) GlobalSettings.defaultEdgeThickness ) );
				
		// draw X axe
		VisRepDrawingDisplayView.paintAxeX( g2D, drawing );
		// draw Y axe
		VisRepDrawingDisplayView.paintAxeY( g2D, drawing );
		
		// draw vertex segments
		for ( VertexSegment vS : drawing.vertexSegments )
		{
			VertexSegmentDisplayView.paint( g2D, vS, getVertexSegmentPoint( vS, firstX, firstY ) );
		}
		
		// draw edge segments
		for ( EdgeSegment eS : drawing.edgeSegments )
		{
			EdgeSegmentDisplayView.paint( g2D, eS, getEdgeSegmentLine( eS, firstX, firstY ) );
		}
		
		// Return the stroke to what it was before
		g2D.setStroke( oldStroke );
	}
	
	/**
	 * Dibujar el drawing de Visibility Representation pero con lineas discontinuas.
	 * @param g2D Graphics2D
	 * @param drawing VisibilityRepresentationDrawing
	 */
	public static void paintDrawingBackground ( Graphics2D g2D, VisibilityRepresentationDrawing drawing )
	{
		// first X axe coordinate
		double firstX = VisRepDrawingDisplayView.getFirstX( );
		// first Y axe coordinate
		double firstY = VisRepDrawingDisplayView.getFirstY( drawing );

		Stroke oldStroke = g2D.getStroke( );
		
		// Set the edge-specific stroke
		g2D.setStroke( new BasicStroke( (float) GlobalSettings.defaultEdgeThickness ) );
				
		// draw X axe
		VisRepDrawingDisplayView.paintAxeX( g2D, drawing );
		// draw Y axe
		VisRepDrawingDisplayView.paintAxeY( g2D, drawing );
		
		// Set the line-specific stroke
		g2D.setStroke( new BasicStroke( 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{ 7.0f }, 0.0f ) );
		// Set the specific color
		g2D.setColor( UserSettings.instance.vertexLine.get( ) );

		// draw vertex segments
		for ( VertexSegment vS : drawing.vertexSegments )
		{
			paintBackground( g2D,
					getCoordinatePoint( vS.xLeftCoordinate.get( ), vS.yCoordinate.get( ), firstX, firstY ),
					getCoordinatePoint( vS.xRightCoordinate.get( ), vS.yCoordinate.get( ), firstX, firstY )
					);
		}
		
		// draw edge segments
		for ( EdgeSegment eS : drawing.edgeSegments )
		{
			paintBackground( g2D,
					getCoordinatePoint( eS.xCoordinate.get( ), eS.yBottomCoordinate.get( ), firstX, firstY ),
					getCoordinatePoint( eS.xCoordinate.get( ), eS.yTopCoordinate.get( ), firstX, firstY )
					);
		}
		
		// Return the stroke to what it was before
		g2D.setStroke( oldStroke );
	}

	/**
	 * Dibujar el drawing de Visibility Representation pero con lineas discontinuas
	 * y sin los ejes de coordenadas.
	 * @param g2D Graphics2D
	 * @param drawing VisibilityRepresentationDrawing
	 */
	public static void paintDrawingBackgroundWithoutAxes ( Graphics2D g2D, VisibilityRepresentationDrawing drawing )
	{
		// first X axe coordinate
		double firstX = VisRepDrawingDisplayView.getFirstX( );
		// first Y axe coordinate
		double firstY = VisRepDrawingDisplayView.getFirstY( drawing );

		Stroke oldStroke = g2D.getStroke( );

		// Set the line-specific stroke
		g2D.setStroke( new BasicStroke( 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{ 7.0f }, 0.0f ) );
		// Set the specific color
		g2D.setColor( UserSettings.instance.vertexLine.get( ) );

		// draw vertex segments
		for ( VertexSegment vS : drawing.vertexSegments )
		{
			paintBackground( g2D,
					getCoordinatePoint( vS.xLeftCoordinate.get( ), vS.yCoordinate.get( ), firstX, firstY ),
					getCoordinatePoint( vS.xRightCoordinate.get( ), vS.yCoordinate.get( ), firstX, firstY )
					);
		}
		
		// draw edge segments
		for ( EdgeSegment eS : drawing.edgeSegments )
		{
			paintBackground( g2D,
					getCoordinatePoint( eS.xCoordinate.get( ), eS.yBottomCoordinate.get( ), firstX, firstY ),
					getCoordinatePoint( eS.xCoordinate.get( ), eS.yTopCoordinate.get( ), firstX, firstY )
					);
		}
		
		// Return the stroke to what it was before
		g2D.setStroke( oldStroke );
	}
	
	/**
	 * Paint Vertex Segment method
	 * @param g2D Graphics2D
	 * @param pOrigin Point2D
	 * @param pDest Point2D
	 */
	private static void paintBackground ( Graphics2D g2D, Point2D pOrigin, Point2D pDest )
	{
		// Draw vertex center
		Line2D lineSegment = new Line2D.Double( 
				pOrigin.getX( ), pOrigin.getY( ),
				pDest.getX( ), pDest.getY( )
				);
		g2D.draw( lineSegment );
	}
	
	/**
	 * get the bounds of the given drawing
	 * @param drawing VisibilityRepresentationDrawing
	 * @return Rectangle2D
	 */
	public static Rectangle2D getBounds( VisibilityRepresentationDrawing drawing )
	{
		if ( drawing.vertexSegments.size( ) <= 0 )
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
	 * draw X axe
	 * @param g2D Graphics2D
	 * @param drawing VisibilityRepresentationDrawing
	 */
	private static void paintAxeX ( Graphics2D g2D, VisibilityRepresentationDrawing drawing )
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
	 * @param drawing VisibilityRepresentationDrawing
	 */
	private static void paintAxeY ( Graphics2D g2D, VisibilityRepresentationDrawing drawing )
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
	 * punto origen de un segmento vertice
	 * @param vS VertexSegment
	 * @param firstX double
	 * @param firstY double
	 * @return Point2D
	 */
	public static Point2D getVertexSegmentPoint ( VertexSegment vS, double firstX, double firstY )
	{
		return getCoordinatePoint( vS.xLeftCoordinate.get( ), vS.yCoordinate.get( ), firstX, firstY );
	}
	
	/**
	 * linea de un segmento arista
	 * @param eS EdgeSegment
	 * @param firstX double
	 * @param firstY double
	 * @return Line2D
	 */
	public static Line2D getEdgeSegmentLine ( EdgeSegment eS, double firstX, double firstY )
	{
		Point2D pOrigin = getCoordinatePoint( eS.xCoordinate.get( ), eS.yBottomCoordinate.get( ), firstX, firstY);
		
		Point2D pDest = new Point2D.Double(
				pOrigin.getX( ),
				pOrigin.getY( ) - GlobalSettings.defaultVerticalCoordinateGap *
				( eS.yTopCoordinate.get( ) - eS.yBottomCoordinate.get( ) ) 
				);
		
		return new Line2D.Double( pOrigin, pDest );
	}
	
	/**
	 * Obtener punto del plano que representa la coordenada con numeros enteros del drawing.
	 * @param x int
	 * @param y int
	 * @param drawing VisibilityRepresentationDrawing
	 * @return Point2D
	 */
	public static Point2D getCoordinatePoint( int x, int y, VisibilityRepresentationDrawing drawing )
	{
		return getCoordinatePoint( x, y, getFirstX( ), getFirstY( drawing ) );
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
	 * @param drawing VisibilityRepresentationDrawing
	 * @return double
	 */
	public static double getFirstY ( VisibilityRepresentationDrawing drawing )
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
	 * @param drawing VisibilityRepresentationDrawing
	 * @return Point2D
	 */
	private static Point2D getAxeYBottomPoint ( VisibilityRepresentationDrawing drawing )
	{
		return new Point2D.Double(
				GlobalSettings.defaultCoordinateAxesGap,
				getFirstY( drawing ) + GlobalSettings.defaultVertexSegmentOffset + GlobalSettings.defaultVerticalCoordinateGap
				);
	}
	
	/**
	 * Axe Y left point
	 * @param drawing VisibilityRepresentationDrawing
	 * @return Point2D
	 */
	private static Point2D getAxeXLeftPoint ( VisibilityRepresentationDrawing drawing )
	{
		return getAxeYBottomPoint( drawing );
	}
	
	/**
	 * Axe Y right point
	 * @param drawing VisibilityRepresentationDrawing
	 * @return Point2D
	 */
	private static Point2D getAxeXRightPoint ( VisibilityRepresentationDrawing drawing )
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
