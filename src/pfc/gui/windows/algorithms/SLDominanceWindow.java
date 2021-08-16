/**
 * SLDominanceWindow.java
 * 31/07/2011 01:53:25
 */
package pfc.gui.windows.algorithms;

import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.JInternalFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pfc.controllers.algorithms.SLDominanceDisplayController;
import pfc.models.Graph;
import pfc.resources.ImageIconBundle;
import pfc.resources.StringBundle;
import pfc.settings.UserSettings;


/**
 * @author walber
 *
 */
public class SLDominanceWindow extends JInternalFrame implements ChangeListener
{

	private static final long serialVersionUID = 7936003034060193983L;
	
	/* Controlador de la ventana del algoritmo Straight-Line Dominance */
	private SLDominanceDisplayController slDominanceDC;
	/* Layout del panel */
	private GroupLayout 				slDominanceWindowLayout;
	
	public SLDominanceWindow ( Graph g )
	{
		super( "", true, true, true, true );
		// Poner nombre de ventana
		this.setTitle( String.format( "%1$s - %2$s", StringBundle.get ( "sl_dominance_window_title" ), g.name.get( ) ) );
		this.setFrameIcon( ImageIconBundle.get( "app_icon_128x128" ) );
		this.setSize( new Dimension( UserSettings.instance.graphWindowWidth.get( ), UserSettings.instance.graphWindowHeight.get( ) ) );
		
		// Elementos de la ventana
		this.slDominanceDC = new SLDominanceDisplayController( g );
		this.slDominanceDC.addExeCancelEventListener( this );
		
		// Layout de la ventana
		this.organizeLayout( );
		
		this.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		this.setVisible( true );
		this.requestFocus( );
		this.toFront( );
	}

	private void organizeLayout ( )
	{
		this.slDominanceWindowLayout = new GroupLayout( this.getContentPane( ) );
		this.getContentPane( ).setLayout( this.slDominanceWindowLayout );
		
		// Organizar grupo horizontal
		this.slDominanceWindowLayout.setHorizontalGroup(
				this.slDominanceWindowLayout.createParallelGroup( GroupLayout.Alignment.LEADING )
				.addComponent( this.slDominanceDC, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
		);

		// Organizar grupo vertical
		this.slDominanceWindowLayout.setVerticalGroup(
				this.slDominanceWindowLayout.createParallelGroup( GroupLayout.Alignment.LEADING )
				.addComponent( this.slDominanceDC, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
		);
	}

	/**
	 * @return the slDominanceDC
	 */
	public SLDominanceDisplayController getSlDominanceDC( )
	{
		return this.slDominanceDC;
	}

	/**
	 * @param slDominanceDC the slDominanceDC to set
	 */
	public void setSlDominanceDC( SLDominanceDisplayController slDominanceDC )
	{
		this.slDominanceDC = slDominanceDC;
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
