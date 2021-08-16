/**
 * ImageBundle.java
 */
package pfc.resources;

import java.net.*;
import java.util.*;
import javax.swing.*;


/**
 * @author Cameron Behar
 */
public class ImageIconBundle extends ResourceBundle
{
	private static Map<String, ImageIcon>	map			= new HashMap<String, ImageIcon>( );
	private static final ResourceBundle		instance	= ResourceBundle.getBundle( "pfc.resources.ImageIconBundle" );
	
	public static ImageIcon get( String key )
	{
		try
		{
			return (ImageIcon) instance.getObject( key );
		}
		catch( MissingResourceException ex )
		{
			System.out.println( String.format( "An exception occurred while trying to load resource %s.", key ) );
			return null;
		}
	}
	
	@Override
	public Enumeration<String> getKeys( )
	{
		return ( new Vector<String>( map.keySet( ) ) ).elements( );
	}
	
	@Override
	protected final Object handleGetObject( String key )
	{
		return this.loadImageIcon( key, ".png" );
	}
	
	private ImageIcon loadImageIcon( String filename, String extension )
	{
		String imageName = filename + extension;
		
		ImageIcon icon = map.get( imageName );
		
		if( icon != null )
			return icon;
		
		URL url = this.getClass( ).getResource( "images/" + imageName );
		
		if( url == null )
			return null;
		
		icon = new ImageIcon( url );
		map.put( imageName, icon );
		
		return icon;
	}
}
