/**
 * VisibilityRepWindow.java
 */
package pfc.gui.windows.algorithms;

import java.awt.Dimension;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pfc.controllers.algorithms.VisibilityRepDisplayController;
import pfc.models.Graph;
import pfc.resources.ImageIconBundle;
import pfc.resources.StringBundle;
import pfc.settings.UserSettings;


/**
 * @author    Walber González
 */
public class VisibilityRepWindow extends JInternalFrame implements ChangeListener {

	private static final long serialVersionUID = 7936003034060193983L;
	
	/* Controlador de la ventana del algoritmo de representacion de visibilidad */
	private VisibilityRepDisplayController 	visRepDC;
	/* Layout del panel */
	private GroupLayout 					visibilityRepWindowLayout;
	
	public VisibilityRepWindow ( Graph g )
	{
		super( "", true, true, true, true );
		// Poner nombre de ventana
		this.setTitle( String.format( "%1$s - %2$s", StringBundle.get ( "visibility_representation_window_title" ), g.name.get( ) ) );
		this.setFrameIcon( ImageIconBundle.get( "app_icon_128x128" ) );
		this.setSize( new Dimension( UserSettings.instance.graphWindowWidth.get( ), UserSettings.instance.graphWindowHeight.get( ) ) );
		
		// Elementos de la ventana
		this.visRepDC = new VisibilityRepDisplayController( g );
		this.visRepDC.addExeCancelEventListener( this );
		
		// Layout de la ventana
		this.organizeLayout( );
		
		this.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		this.setVisible( true );
		this.requestFocus( );
		this.toFront( );
	}

	private void organizeLayout ( )
	{
		this.visibilityRepWindowLayout = new GroupLayout( this.getContentPane( ) );
		this.getContentPane( ).setLayout( this.visibilityRepWindowLayout );
		
		// Organizar grupo horizontal
		this.visibilityRepWindowLayout.setHorizontalGroup(
				this.visibilityRepWindowLayout.createParallelGroup( GroupLayout.Alignment.LEADING )
				.addComponent( this.visRepDC, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
		);

		// Organizar grupo vertical
		this.visibilityRepWindowLayout.setVerticalGroup(
				this.visibilityRepWindowLayout.createParallelGroup( GroupLayout.Alignment.LEADING )
				.addComponent( this.visRepDC, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
		);
	}
	
	/**
	 * @return    the visRepDC
	 */
	public VisibilityRepDisplayController getVisRepDC( )
	{
		return visRepDC;
	}

	/**
	 * @param visRepDC    the visRepDC to set
	 */
	public void setVisRepDC( VisibilityRepDisplayController visRepDC )
	{
		this.visRepDC = visRepDC;
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
