/**
 * ConstrainedUpwardPolylineWindow.java
 * 24/07/2011 02:44:04
 */
package pfc.gui.windows.algorithms;

import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.JInternalFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pfc.controllers.algorithms.ConstrainedPolylineDisplayController;
import pfc.models.Graph;
import pfc.models.algorithms.NonIntersectingPathList;
import pfc.resources.ImageIconBundle;
import pfc.resources.StringBundle;
import pfc.settings.UserSettings;


/**
 * @author    Walber González
 */
public class ConstrainedUpwardPolylineWindow extends JInternalFrame implements ChangeListener {

    private static final long serialVersionUID = -491148100284453452L;
    
    /* Controlador de la ventana del algoritmo Constrained Upward Polyline */
	private ConstrainedPolylineDisplayController	constrainedPolylineDC;
	/* Layout del panel */
	private GroupLayout                            	constrainedPolylineWindowLayout;
	
	public ConstrainedUpwardPolylineWindow ( Graph g, NonIntersectingPathList paths )
	{
		super( "", true, true, true, true );
		// Poner nombre de ventana
		this.setTitle( String.format( "%1$s - %2$s", StringBundle.get ( "constrained_upward_polyline_window_title" ), g.name.get( ) ) );
		this.setFrameIcon( ImageIconBundle.get( "app_icon_128x128" ) );
		this.setSize( new Dimension( UserSettings.instance.graphWindowWidth.get( ), UserSettings.instance.graphWindowHeight.get( ) ) );
		
		// Elementos de la ventana
		this.constrainedPolylineDC = new ConstrainedPolylineDisplayController( g, paths );
		this.constrainedPolylineDC.addExeCancelEventListener( this );
		
		// Layout de la ventana
		this.organizeLayout( );
		
		this.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		this.setVisible( true );
		this.requestFocus( );
		this.toFront( );
	}

	private void organizeLayout ( )
	{
		this.constrainedPolylineWindowLayout = new GroupLayout( this.getContentPane( ) );
		this.getContentPane( ).setLayout( this.constrainedPolylineWindowLayout );
		
		// Organizar grupo horizontal
		this.constrainedPolylineWindowLayout.setHorizontalGroup(
				this.constrainedPolylineWindowLayout.createParallelGroup( GroupLayout.Alignment.LEADING )
				.addComponent( this.constrainedPolylineDC, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
		);

		// Organizar grupo vertical
		this.constrainedPolylineWindowLayout.setVerticalGroup(
				this.constrainedPolylineWindowLayout.createParallelGroup( GroupLayout.Alignment.LEADING )
				.addComponent( this.constrainedPolylineDC, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
		);
	}
	
	/**
	 * @return    the constrainedPolylineDC
	 */
	public ConstrainedPolylineDisplayController getConstrainedPolylineDC( )
	{
		return constrainedPolylineDC;
	}

	/**
     * @param constrainedPolylineDC the constrainedPolylineDC to set
     */
    public void setConstrainedVisRepDC( ConstrainedPolylineDisplayController constrainedPolylineDC )
    {
        this.constrainedPolylineDC = constrainedPolylineDC;
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

