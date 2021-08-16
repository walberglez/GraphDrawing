/**
 * PolylineDominanceWindow.java
 * 03/08/2011 23:37:46
 */
package pfc.gui.windows.algorithms;

import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.JInternalFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pfc.controllers.algorithms.PolylineDominanceDisplayController;
import pfc.models.Graph;
import pfc.resources.ImageIconBundle;
import pfc.resources.StringBundle;
import pfc.settings.UserSettings;

/**
 * @author walber
 *
 */
public class PolylineDominanceWindow extends JInternalFrame implements ChangeListener
{

	private static final long serialVersionUID = 7936003034060193983L;
	
	/* Controlador de la ventana del algoritmo Polyline Dominance */
	private PolylineDominanceDisplayController	polyDominanceDC;
	/* Layout del panel */
	private GroupLayout 						polyDominanceWindowLayout;
	
	public PolylineDominanceWindow ( Graph g )
	{
		super( "", true, true, true, true );
		// Poner nombre de ventana
		this.setTitle( String.format( "%1$s - %2$s", StringBundle.get ( "polyline_dominance_window_title" ), g.name.get( ) ) );
		this.setFrameIcon( ImageIconBundle.get( "app_icon_128x128" ) );
		this.setSize( new Dimension( UserSettings.instance.graphWindowWidth.get( ), UserSettings.instance.graphWindowHeight.get( ) ) );
		
		// Elementos de la ventana
		this.polyDominanceDC = new PolylineDominanceDisplayController( g );
		this.polyDominanceDC.addExeCancelEventListener( this );
		
		// Layout de la ventana
		this.organizeLayout( );
		
		this.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		this.setVisible( true );
		this.requestFocus( );
		this.toFront( );
	}

	private void organizeLayout ( )
	{
		this.polyDominanceWindowLayout = new GroupLayout( this.getContentPane( ) );
		this.getContentPane( ).setLayout( this.polyDominanceWindowLayout );
		
		// Organizar grupo horizontal
		this.polyDominanceWindowLayout.setHorizontalGroup(
				this.polyDominanceWindowLayout.createParallelGroup( GroupLayout.Alignment.LEADING )
				.addComponent( this.polyDominanceDC, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
		);

		// Organizar grupo vertical
		this.polyDominanceWindowLayout.setVerticalGroup(
				this.polyDominanceWindowLayout.createParallelGroup( GroupLayout.Alignment.LEADING )
				.addComponent( this.polyDominanceDC, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
		);
	}

	/**
	 * @return the polyDominanceDC
	 */
	public PolylineDominanceDisplayController getPolyDominanceDC( )
	{
		return this.polyDominanceDC;
	}

	/**
	 * @param polyDominanceDC the polyDominanceDC to set
	 */
	public void setPolyDominanceDC(	PolylineDominanceDisplayController polyDominanceDC )
	{
		this.polyDominanceDC = polyDominanceDC;
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

