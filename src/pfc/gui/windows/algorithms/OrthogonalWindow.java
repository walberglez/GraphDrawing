/**
 * OrthogonalWindow.java
 * 26/07/2011 14:59:59
 */
package pfc.gui.windows.algorithms;

import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.JInternalFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pfc.controllers.algorithms.OrthogonalDisplayController;
import pfc.models.Graph;
import pfc.resources.ImageIconBundle;
import pfc.resources.StringBundle;
import pfc.settings.UserSettings;


/**
 * @author    Walber González
 */
public class OrthogonalWindow extends JInternalFrame implements ChangeListener {

	private static final long serialVersionUID = 7936003034060193983L;
	
	/* Controlador de la ventana del algoritmo Orthogonal */
	private OrthogonalDisplayController orthogonalDC;
	/* Layout del panel */
	private GroupLayout 				orthogonalWindowLayout;
	
	public OrthogonalWindow ( Graph g )
	{
		super( "", true, true, true, true );
		// Poner nombre de ventana
		this.setTitle( String.format( "%1$s - %2$s", StringBundle.get ( "orthogonal_window_title" ), g.name.get( ) ) );
		this.setFrameIcon( ImageIconBundle.get( "app_icon_128x128" ) );
		this.setSize( new Dimension( UserSettings.instance.graphWindowWidth.get( ), UserSettings.instance.graphWindowHeight.get( ) ) );
		
		// Elementos de la ventana
		this.orthogonalDC = new OrthogonalDisplayController( g );
		this.orthogonalDC.addExeCancelEventListener( this );
		
		// Layout de la ventana
		this.organizeLayout( );
		
		this.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		this.setVisible( true );
		this.requestFocus( );
		this.toFront( );
	}

	private void organizeLayout ( )
	{
		this.orthogonalWindowLayout = new GroupLayout( this.getContentPane( ) );
		this.getContentPane( ).setLayout( this.orthogonalWindowLayout );
		
		// Organizar grupo horizontal
		this.orthogonalWindowLayout.setHorizontalGroup(
				this.orthogonalWindowLayout.createParallelGroup( GroupLayout.Alignment.LEADING )
				.addComponent( this.orthogonalDC, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
		);

		// Organizar grupo vertical
		this.orthogonalWindowLayout.setVerticalGroup(
				this.orthogonalWindowLayout.createParallelGroup( GroupLayout.Alignment.LEADING )
				.addComponent( this.orthogonalDC, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
		);
	}

	/**
	 * @return the orthogonalDC
	 */
	public OrthogonalDisplayController getOrthogonalDC( )
	{
		return this.orthogonalDC;
	}

	/**
	 * @param orthogonalDC the orthogonalDC to set
	 */
	public void setOrthogonalDC( OrthogonalDisplayController orthogonalDC )
	{
		this.orthogonalDC = orthogonalDC;
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
