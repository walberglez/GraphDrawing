/**
 * StringBundle.java
 */
package pfc.resources;

import java.util.*;

import pfc.settings.*;


/**
 * @author Cameron Behar
 */
public class StringBundle
{
	private static final ResourceBundle	instance;
	
	static
	{
		String[ ] localeParts = UserSettings.instance.language.get( ).split( "_" );
		switch( localeParts.length )
		{
			case 1:
				instance = ResourceBundle.getBundle( "pfc.resources.strings.Resources", new Locale( localeParts[0] ) );
				break;
			case 2:
				instance = ResourceBundle.getBundle( "pfc.resources.strings.Resources", new Locale( localeParts[0], localeParts[1] ) );
				break;
			case 3:
				instance = ResourceBundle.getBundle( "pfc.resources.strings.Resources", new Locale( localeParts[0], localeParts[1], localeParts[2] ) );
				break;
			default:
				instance = ResourceBundle.getBundle( "pfc.resources.strings.Resources" );
				break;
		}
	}
	
	public static String get( String key )
	{
		try
		{
			return instance.getString( key );
		}
		catch( MissingResourceException ex )
		{
			System.out.println( String.format( "An exception occurred while trying to load resource %s.", key ) );
			return '!' + key + '!';
		}
	}
	
	private StringBundle( )
	{}
}
