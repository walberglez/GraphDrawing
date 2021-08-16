/**
 * GraphDrawing.java
 */
package pfc;

import java.io.*;
import javax.swing.*;

import pfc.gui.windows.*;
import pfc.settings.*;


/**
 * @author Cameron Behar
 */
public class GraphDrawing
{
	private static void initializeLookAndFeel( )
	{
		try
		{
			System.setProperty( "java.awt.Window.locationByPlatform", "true" );
			System.setProperty( "apple.laf.useScreenMenuBar", "true" );
			System.setProperty( "com.apple.mrj.application.apple.menu.about.name", GlobalSettings.applicationName );
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName( ) );
		}
		catch( Exception ex )
		{
			System.out.println( "An exception occurred while setting the system look and feel.");
		}
	}
	
	/**
	 * @param args arguments
	 */
	public static void main( final String[ ] args )
	{
		initializeLookAndFeel( );
		
		SwingUtilities.invokeLater( new Runnable( )
		{
			@Override
			public void run( )
			{
				MainWindow window = new MainWindow( );
				for( String arg : args )
					try
					{
						window.openFile( new File( arg ) );
					}
					catch( IOException ex )
					{
						System.out.println( "An exception occurred while loading a graph from file.");
					}
			}
		} );
	}
}
