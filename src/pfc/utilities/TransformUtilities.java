package pfc.utilities;

import java.awt.geom.*;

import pfc.settings.UserSettings;


public class TransformUtilities {

	public static void zoomFit ( AffineTransform transform, Rectangle2D objectRectangle, Rectangle2D componentRectangle )
	{
		// First we need to reset and translate the graph to the viewport's center
		transform.setToIdentity( );
		transform.translate( Math.round( componentRectangle.getWidth( ) / 2.0 ), Math.round( componentRectangle.getHeight( ) / 2.0 ) );
		
		// We need to fit it to the viewport. So we want to scale according to the lowest viewport-to-graph dimension ratio.
		double widthRatio = ( componentRectangle.getWidth( ) - UserSettings.instance.zoomGraphPadding.get( ) ) / objectRectangle.getWidth( );
		double heightRatio = ( componentRectangle.getHeight( ) - UserSettings.instance.zoomGraphPadding.get( ) ) / objectRectangle.getHeight( );
		double minRatio = Math.min( widthRatio, heightRatio );
		
		if( minRatio < 1 )
			transform.scale( minRatio, minRatio );
		
		// Only now that we've properly scaled can we translate to the graph's center
		Point2D.Double graphCenter = new Point2D.Double( objectRectangle.getCenterX( ), objectRectangle.getCenterY( ) );
		transform.translate( Math.round( -graphCenter.x ), Math.round( -graphCenter.y ) );
	}
	
	public static void zoomCenter( AffineTransform transform, Point2D.Double center, double factor )
	{
		transform.translate( Math.round( center.x ), Math.round( center.y ) );
		
		transform.scale( factor, factor );
		if( transform.getScaleX( ) > UserSettings.instance.maximumZoomFactor.get( ) )
			zoomMax( transform );
		
		transform.translate( Math.round( -center.x ), Math.round( -center.y ) );
	}
	
	public static void zoomMax( AffineTransform transform )
	{
		transform.setTransform( UserSettings.instance.maximumZoomFactor.get( ), transform.getShearY( ), transform.getShearX( ), UserSettings.instance.maximumZoomFactor.get( ), Math.round( transform.getTranslateX( ) ), Math.round( transform.getTranslateY( ) ) );
	}
	
	public static void zoomOneToOne( AffineTransform transform )
	{
		transform.setTransform( 1, transform.getShearY( ), transform.getShearX( ), 1, (int) transform.getTranslateX( ), (int) transform.getTranslateY( ) );
	}
}
