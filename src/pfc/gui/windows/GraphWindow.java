/**
 * GraphWindow.java
 */
package pfc.gui.windows;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.imageio.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

import pfc.controllers.*;
import pfc.controllers.GraphDisplayController.*;
import pfc.models.*;
import pfc.resources.*;
import pfc.settings.*;
import pfc.views.svg.*;


/**
 * @author    Cameron Behar
 */
public class GraphWindow extends JInternalFrame implements GraphChangeEventListener
{
	private static final long serialVersionUID = -8670177760366047708L;
	
	private final JFileChooser		fileChooser;
	private GraphDisplayController	gdc;
	private File					file;
	private boolean					hasChanged;
	private boolean					hasLoaded;
	
	public GraphWindow( Graph g )
	{
		super( "", true, true, true, true );
		this.setFrameIcon( ImageIconBundle.get( "app_icon_128x128" ) );
		this.file = null;
		this.setSize( new Dimension( UserSettings.instance.graphWindowWidth.get( ), UserSettings.instance.graphWindowHeight.get( ) ) );
		this.add( this.setGdc( new GraphDisplayController( g ) ) );
		this.setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
		this.hasChanged = false;
		this.hasLoaded = false;
		this.addInternalFrameListener( new InternalFrameListener( )
		{
			public void internalFrameActivated( InternalFrameEvent e )
			{
				if( !GraphWindow.this.hasLoaded )
				{
					GraphWindow.this.gdc.zoomFit( );
					GraphWindow.this.hasLoaded = true;
				}
			}
			
			public void internalFrameClosed( InternalFrameEvent e )
			{}
			
			public void internalFrameClosing( InternalFrameEvent e )
			{
				GraphWindow.this.closingWindow( e );
			}
			
			public void internalFrameDeactivated( InternalFrameEvent e )
			{}
			
			public void internalFrameDeiconified( InternalFrameEvent e )
			{}
			
			public void internalFrameIconified( InternalFrameEvent e )
			{}
			
			public void internalFrameOpened( InternalFrameEvent e )
			{}
		} );
		this.updateTitle( );
		this.fileChooser = new JFileChooser( );
		this.setVisible( true );
		this.requestFocus( );
		this.toFront( );
	}
	
	public void closingWindow( InternalFrameEvent e )
	{
		if( this.hasChanged )
		{
			try
			{
				if( this.isIcon( ) )
					this.setIcon( false );
			}
			catch( Exception ex )
			{
				return;
			}
			
			int result = JOptionPane.showInternalConfirmDialog( this, String.format( StringBundle.get( "do_you_want_to_save_changes_dialog_message" ), this.gdc.getGraph( ).name.get( ) ), GlobalSettings.applicationName, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE );
			
			switch( result )
			{
				case JOptionPane.YES_OPTION:
					try
					{
						this.save( );
						this.gdc.dispose( );
						this.dispose( );
					}
					catch( IOException ex )
					{
						System.out.println( "An exception occurred while saving graph." );
						JOptionPane.showInternalMessageDialog( this, String.format( StringBundle.get( "an_exception_occurred_while_saving_graph_dialog_message" ), this.gdc.getGraph( ).name.get( ) ) );
					}
					break;
				case JOptionPane.NO_OPTION:
					this.gdc.dispose( );
					this.dispose( );
					break;
				case JOptionPane.CANCEL_OPTION:
					break;
			}
		}
		else
		{
			this.gdc.dispose( );
			this.dispose( );
		}
	}
	
	/**
	 * @return file
	 */
	public File getFile( )
	{
		return this.file;
	}
	
	/**
	 * @return GraphDisplayController
	 */
	public GraphDisplayController getGdc( )
	{
		return this.gdc;
	}
	
	/**
	 * @return hasChanged
	 */
	public boolean getHasChanged( )
	{
		return this.hasChanged;
	}
	
	public void graphChangeEventOccurred( GraphChangeEvent e )
	{
		this.setHasChanged( true );
	}
	
	public void save( ) throws IOException
	{
		if( this.file == null )
			this.saveAs( );
		else
			this.saveFile( this.file );
	}
	
	public void saveAs( )
	{
		this.fileChooser.resetChoosableFileFilters( );
		this.fileChooser.setAcceptAllFileFilterUsed( false );
		this.fileChooser.addChoosableFileFilter( new FileNameExtensionFilter( StringBundle.get( "portable_network_graphics_file_description" ), "png" ) );
		this.fileChooser.addChoosableFileFilter( new FileNameExtensionFilter( StringBundle.get( "scalable_vector_graphics_file_description" ), "svg" ) );
		this.fileChooser.addChoosableFileFilter( new FileNameExtensionFilter( StringBundle.get( "visigraph_file_description" ), "vsg" ) );
		this.fileChooser.setMultiSelectionEnabled( false );
		
		boolean success = false;
		
		while( !success )
			if( this.fileChooser.showSaveDialog( this ) == JFileChooser.APPROVE_OPTION )
				try
				{
					File selectedFile = this.fileChooser.getSelectedFile( );
					
					if( this.fileChooser.getFileFilter( ).getDescription( ).equals( StringBundle.get( "visigraph_file_description" ) ) && !selectedFile.getName( ).endsWith( ".vsg" ) )
						selectedFile = new File( selectedFile.getAbsolutePath( ) + ".vsg" );
					else if( this.fileChooser.getFileFilter( ).getDescription( ).equals( StringBundle.get( "scalable_vector_graphics_file_description" ) ) && !selectedFile.getName( ).endsWith( ".svg" ) )
						selectedFile = new File( selectedFile.getAbsolutePath( ) + ".svg" );
					else if( this.fileChooser.getFileFilter( ).getDescription( ).equals( StringBundle.get( "portable_network_graphics_file_description" ) ) && !selectedFile.getName( ).endsWith( ".png" ) )
						selectedFile = new File( selectedFile.getAbsolutePath( ) + ".png" );
					
					this.saveFile( selectedFile );
					
					success = true;
				}
				catch( IOException ex )
				{
					System.out.println( "An exception occurred while saving as." );
					success = false;
				}
			else
				success = true;
	}
	
	public void saveFile( File file ) throws IOException
	{
		Graph graph = this.gdc.getGraph( );
		GraphSettings settings = this.gdc.settings;
		
		if( file.getName( ).endsWith( ".vsg" ) )
		{
			graph.name.set( file.getName( ).substring( 0, file.getName( ).length( ) - 4 ) );
			this.updateTitle( );
			
			FileWriter fw = new FileWriter( file );
			fw.write( graph.toString( ) );
			fw.close( );
			
			this.setFile( file );
			this.setHasChanged( false );
		}
		else if( file.getName( ).endsWith( ".svg" ) )
		{
			FileWriter fw = new FileWriter( file );
			fw.write( GraphSvgView.format( graph, settings ) );
			fw.close( );
		}
		else if( file.getName( ).endsWith( ".png" ) )
			ImageIO.write( this.gdc.getImage( ), "png", file );
	}
	
	/**
	 * @param file File
	 */
	public void setFile( File file )
	{
		this.file = file;
	}
	
	/**
	 * @param gdc GraphDisplayController
	 * @return GraphDisplayController
	 */
	public GraphDisplayController setGdc( GraphDisplayController gdc )
	{
		if( gdc != null )
			gdc.removeGraphChangeListener( this );
		
		this.gdc = gdc;
		this.gdc.addGraphChangeListener( this );
		
		return gdc;
	}
	
	/**
	 * @param hasChanged boolean
	 */
	public void setHasChanged( boolean hasChanged )
	{
		if( this.hasChanged != hasChanged )
		{
			this.hasChanged = hasChanged;
			this.updateTitle( );
		}
	}
	
	public void updateTitle( )
	{
		this.setTitle( String.format( "%1$s - %2$s%3$s", StringBundle.get ( "graph_editor_window_title" ), this.getGdc( ).getGraph( ).name.get( ), ( this.hasChanged ? "*" : "" ) ) );
	}
}
