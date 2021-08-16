/**
 * UpwardPolylineWindow.java
 * 24/07/2011 02:43:56
 */
package pfc.gui.windows.algorithms;

import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.JInternalFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pfc.controllers.algorithms.UpwardPolylineDisplayController;
import pfc.models.Graph;
import pfc.resources.ImageIconBundle;
import pfc.resources.StringBundle;
import pfc.settings.UserSettings;


/**
 * @author    Walber González
 */
public class UpwardPolylineWindow extends JInternalFrame implements ChangeListener
{

	private static final long serialVersionUID = 7936003034060193983L;
	
	/* Controlador de la ventana del algoritmo Upward Polyline */
	private UpwardPolylineDisplayController 	polylineDC;
	/* Layout del panel */
	private GroupLayout 						polylineWindowLayout;
	
	public UpwardPolylineWindow ( Graph g )
	{
		super( "", true, true, true, true );
		// Poner nombre de ventana
		this.setTitle( String.format( "%1$s - %2$s", StringBundle.get ( "upward_polyline_window_title" ), g.name.get( ) ) );
		this.setFrameIcon( ImageIconBundle.get( "app_icon_128x128" ) );
		this.setSize( new Dimension( UserSettings.instance.graphWindowWidth.get( ), UserSettings.instance.graphWindowHeight.get( ) ) );
		
		// Elementos de la ventana
		this.polylineDC = new UpwardPolylineDisplayController( g );
		this.polylineDC.addExeCancelEventListener( this );
		
		// Layout de la ventana
		this.organizeLayout( );
		
		this.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		this.setVisible( true );
		this.requestFocus( );
		this.toFront( );
	}

	private void organizeLayout ( )
	{
		this.polylineWindowLayout = new GroupLayout( this.getContentPane( ) );
		this.getContentPane( ).setLayout( this.polylineWindowLayout );
		
		// Organizar grupo horizontal
		this.polylineWindowLayout.setHorizontalGroup(
				this.polylineWindowLayout.createParallelGroup( GroupLayout.Alignment.LEADING )
				.addComponent( this.polylineDC, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
		);

		// Organizar grupo vertical
		this.polylineWindowLayout.setVerticalGroup(
				this.polylineWindowLayout.createParallelGroup( GroupLayout.Alignment.LEADING )
				.addComponent( this.polylineDC, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
		);
	}
	
	/**
	 * @return    the polylineDC
	 */
	public UpwardPolylineDisplayController getPolylineDC( )
	{
		return polylineDC;
	}

	/**
	 * @param polylineDC    the polylineDC to set
	 */
	public void setPolylineDC( UpwardPolylineDisplayController polylineDC )
	{
		this.polylineDC = polylineDC;
	}

	/**
     * Evento que informa de la cancelacion de la ejecucion del algoritmo.
     */
	@Override
	public void stateChanged( ChangeEvent e )
	{
		this.dispose( );		
	}
}
