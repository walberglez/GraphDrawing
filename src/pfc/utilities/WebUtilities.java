/**
 * WebUtilities.java
 */
package pfc.utilities;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

import pfc.resources.*;
import pfc.settings.*;


/**
 * @author Cameron Behar
 */
public class WebUtilities
{
	static final String[ ]	browsers	= { "google-chrome", "firefox", "opera", "epiphany", "konqueror", "conkeror", "midori", "kazehakase", "mozilla" };
	
	public static void downloadFile( String url, String filename ) throws Exception
	{
		BufferedInputStream inputStream = new BufferedInputStream( new URL( url ).openStream( ) );
		FileOutputStream fileOutputStream = new FileOutputStream( filename );
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream( fileOutputStream, 1024 );
		byte[ ] data = new byte[1024];
		
		int x = 0;
		while( ( x = inputStream.read( data, 0, 1024 ) ) >= 0 )
			bufferedOutputStream.write( data, 0, x );
		
		bufferedOutputStream.close( );
		inputStream.close( );
	}
	
	public static void launchBrowser( String url )
	{
		try
		{
			// attempt to use Desktop library from JDK 1.6+
			Class<?> d = Class.forName( "java.awt.Desktop" );
			d.getDeclaredMethod( "browse", new Class[ ] { java.net.URI.class } ).invoke( d.getDeclaredMethod( "getDesktop" ).invoke( null ), new Object[ ] { java.net.URI.create( url ) } );
			// above code mimics java.awt.Desktop.getDesktop().browse()
		}
		catch( Exception ignore )
		{
			// library not available or failed
			String osName = System.getProperty( "os.name" );
			try
			{
				if( osName.startsWith( "Mac OS" ) )
					Class.forName( "com.apple.eio.FileManager" ).getDeclaredMethod( "openURL", new Class[ ] { String.class } ).invoke( null, new Object[ ] { url } );
				else if( osName.startsWith( "Windows" ) )
					Runtime.getRuntime( ).exec( "rundll32 url.dll,FileProtocolHandler " + url );
				else
				{
					// At this point we're simply assuming Unix or Linux
					String browser = null;
					for( String b : browsers )
						if( browser == null && Runtime.getRuntime( ).exec( new String[ ] { "which", b } ).getInputStream( ).read( ) != -1 )
							Runtime.getRuntime( ).exec( new String[ ] { browser = b, url } );
					
					if( browser == null )
						throw new Exception( Arrays.toString( browsers ) );
				}
			}
			catch( Exception ex )
			{
				System.out.println( "An exception occurred while launching the default browser." );
				JOptionPane.showMessageDialog( null, String.format( StringBundle.get( "an_exception_occurred_while_launching_browser_dialog_message" ), ex.toString( ) ), GlobalSettings.applicationName, JOptionPane.ERROR_MESSAGE );
			}
		}
	}
}
