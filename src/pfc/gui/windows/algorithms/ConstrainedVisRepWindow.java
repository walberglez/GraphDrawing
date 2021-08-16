/**
 * ConstrainedVisRepWindow.java
 */
package pfc.gui.windows.algorithms;

import java.awt.Dimension;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pfc.controllers.algorithms.ConstrainedVisRepDisplayController;
import pfc.models.Graph;
import pfc.models.algorithms.NonIntersectingPathList;
import pfc.resources.ImageIconBundle;
import pfc.resources.StringBundle;
import pfc.settings.UserSettings;


/**
 * @author    Walber González
 */
public class ConstrainedVisRepWindow extends JInternalFrame implements ChangeListener {

    private static final long serialVersionUID = -491148100284453452L;
    
    /* Controlador de la ventana del algoritmo de representacion de visibilidad con restricciones */
	private ConstrainedVisRepDisplayController     constrainedVisRepDC;
	/* Layout del panel */
	private GroupLayout                            constrainedVisRepWindowLayout;
	
	public ConstrainedVisRepWindow ( Graph g, NonIntersectingPathList paths )
	{
		super( "", true, true, true, true );
		// Poner nombre de ventana
		this.setTitle( String.format( "%1$s - %2$s", StringBundle.get ( "constrained_vis_rep_window_title" ), g.name.get( ) ) );
		this.setFrameIcon( ImageIconBundle.get( "app_icon_128x128" ) );
		this.setSize( new Dimension( UserSettings.instance.graphWindowWidth.get( ), UserSettings.instance.graphWindowHeight.get( ) ) );
		
		// Elementos de la ventana
		this.constrainedVisRepDC = new ConstrainedVisRepDisplayController( g, paths );
		this.constrainedVisRepDC.addExeCancelEventListener( this );
		
		// Layout de la ventana
		this.organizeLayout( );
		
		this.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		this.setVisible( true );
		this.requestFocus( );
		this.toFront( );
	}

	private void organizeLayout ( )
	{
		this.constrainedVisRepWindowLayout = new GroupLayout( this.getContentPane( ) );
		this.getContentPane( ).setLayout( this.constrainedVisRepWindowLayout );
		
		// Organizar grupo horizontal
		this.constrainedVisRepWindowLayout.setHorizontalGroup(
				this.constrainedVisRepWindowLayout.createParallelGroup( GroupLayout.Alignment.LEADING )
				.addComponent( this.constrainedVisRepDC, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
		);

		// Organizar grupo vertical
		this.constrainedVisRepWindowLayout.setVerticalGroup(
				this.constrainedVisRepWindowLayout.createParallelGroup( GroupLayout.Alignment.LEADING )
				.addComponent( this.constrainedVisRepDC, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
		);
	}
	
	/**
	 * @return    the constrainedVisRepDC
	 */
	public ConstrainedVisRepDisplayController getConstrainedVisRepDC( )
	{
		return constrainedVisRepDC;
	}

	/**
     * @param constrainedVisRepDC the constrainedVisRepDC to set
     */
    public void setConstrainedVisRepDC(
            ConstrainedVisRepDisplayController constrainedVisRepDC) {
        this.constrainedVisRepDC = constrainedVisRepDC;
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
